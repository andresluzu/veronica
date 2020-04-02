package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rolandopalermo.facturacion.ec.common.util.Constants;
import com.rolandopalermo.facturacion.ec.dto.v1_0.ComprobanteDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.sri.RespuestaSolicitudDTO;
import com.rolandopalermo.facturacion.ec.persistence.entity.Document;
import com.rolandopalermo.facturacion.ec.persistence.repository.DocumentRepository;
import com.rolandopalermo.facturacion.ec.service.v1_0.impl.GenericSRIServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ValidationTask extends AbstractTask {

    private static final Logger LOGGER = LogManager.getLogger(ValidationTask.class);
    private static final long DELAY = 5 * 60000; // 5 minutes

    protected ValidationTask(GenericSRIServiceImpl sriService, DocumentRepository repository,
                             ThreadPoolTaskScheduler taskScheduler, Document document, MessagePublisher publisher) {
        super(sriService, repository, taskScheduler, document, publisher);
    }

    @Override
    public void run() {
        LOGGER.info("Running validation task on document {}", document.getId());
        RespuestaSolicitudDTO result = null;
        try {
            result = sriService.post(document.getAccessKey());
        } catch (Exception ex) {
            LOGGER.error("Cannot validate SRI document", ex);
            document.setErrorMessage(ex.getMessage());
            document.setState(ProcessingState.ERROR.name());
            publishMessage(ex);
        }

        if (Objects.nonNull(result)) {
            String state = Optional.ofNullable(result.getEstado()).orElse(Constants.SRI_REJECTED);
            if (state.equals(Constants.SRI_RECEIVED)) {
                LOGGER.info("Document validated with accessKey {}", document.getAccessKey());
                document.setPhase(ProcessingPhase.AUTHORIZATION.name());
                taskScheduler.schedule(new AuthorizationTask(sriService, repository, taskScheduler, document, publisher),
                        Date.from(Instant.now().plusMillis(DELAY)));
            } else {
                LOGGER.error("Document rejected from validation with accessKey {}", document.getAccessKey());
                document.setState(ProcessingState.INVALID.name());
                ObjectMapper mapper = new ObjectMapper();
                try {
                    document.setErrorMessage(mapper.writeValueAsString(result));
                } catch (JsonProcessingException e) {
                    LOGGER.error("Cannot create json from {}", result, e);
                    document.setState(ProcessingState.ERROR.name());
                    document.setErrorMessage(e.getMessage());
                }
                publishMessage(result);
            }
        } else {
            document.setState(ProcessingState.ERROR.name());
            publishMessage("Invalid RespuestaSolicitudDTO");
        }
        document.setDateTime(new Date());
        repository.save(document);
    }
}
