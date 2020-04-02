package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rolandopalermo.facturacion.ec.dto.v1_0.sri.AutorizacionDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.sri.RespuestaComprobanteDTO;
import com.rolandopalermo.facturacion.ec.persistence.entity.Document;
import com.rolandopalermo.facturacion.ec.persistence.repository.DocumentRepository;
import com.rolandopalermo.facturacion.ec.service.v1_0.impl.GenericSRIServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.rolandopalermo.facturacion.ec.common.util.Constants.SRI_APPLIED;
import static com.rolandopalermo.facturacion.ec.common.util.Constants.SRI_INVALID;

public class AuthorizationTask extends AbstractTask {

    private static final Logger LOGGER = LogManager.getLogger(AuthorizationTask.class);

    protected AuthorizationTask(GenericSRIServiceImpl sriService, DocumentRepository repository,
                                ThreadPoolTaskScheduler taskScheduler, Document document,
                                MessagePublisher publisher) {
        super(sriService, repository, taskScheduler, document, publisher);
    }

    @Override
    public void run() {
        LOGGER.info("Running authorization task on document {}", document.getId());
        RespuestaComprobanteDTO result = null;
        try {
            result = sriService.apply(document.getAccessKey());
        } catch (Exception ex) {
            LOGGER.error("Cannot authorize SRI document", ex);
            document.setErrorMessage(ex.getMessage());
            document.setState(ProcessingState.ERROR.name());
            publishMessage(ex);
        }

        if (Objects.nonNull(result)) {
            AutorizacionDTO authorization = result.getAutorizaciones().get(0);
            String state = Optional.ofNullable(authorization.getEstado()).orElse(SRI_INVALID);
            if (state.equals(SRI_APPLIED)) {
                LOGGER.info("Document authorized with accessKey {}", document.getAccessKey());
                document.setState(ProcessingState.COMPLETE.name());
            } else {
                LOGGER.error("Document not authorized with accessKey {}", document.getAccessKey());
                document.setState(ProcessingState.INVALID.name());
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    document.setErrorMessage(mapper.writeValueAsString(result));
                } catch (JsonProcessingException e) {
                    LOGGER.error("Cannot create json from {}", result, e);
                    document.setState(ProcessingState.ERROR.name());
                    document.setErrorMessage(e.getMessage());
                }
            }
            publishMessage(result);
        } else {
            document.setState(ProcessingState.ERROR.name());
            publishMessage("Invalid RespuestaComprobanteDTO");
        }
        document.setDateTime(new Date());
        repository.save(document);
    }
}
