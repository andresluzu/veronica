package com.rolandopalermo.facturacion.ec.service.pubsub;

import com.rolandopalermo.facturacion.ec.dto.ComprobanteIdDTO;
import com.rolandopalermo.facturacion.ec.dto.v1_0.ComprobanteDTO;
import com.rolandopalermo.facturacion.ec.persistence.entity.Document;
import com.rolandopalermo.facturacion.ec.persistence.repository.DocumentRepository;
import com.rolandopalermo.facturacion.ec.service.v1_0.impl.GenericSRIServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.Objects;

public class CreationTask extends AbstractTask {

    private static final Logger LOGGER = LogManager.getLogger(CreationTask.class);

    protected CreationTask(GenericSRIServiceImpl sriService, DocumentRepository repository,
                           ThreadPoolTaskScheduler taskScheduler, Document document, ComprobanteDTO dto,
                           MessagePublisher publisher) {
        super(sriService, repository, taskScheduler, document, dto, publisher);
    }

    @Override
    public void run() {
        LOGGER.info("Running creation task on document {}", document.getId());
        ComprobanteIdDTO result = null;
        try {
            result = sriService.create(dto);
        } catch (Exception ex) {
            LOGGER.error("Cannot create SRI document", ex);
            document.setErrorMessage(ex.getMessage());
            document.setState(ProcessingState.ERROR.name());
            publish(() -> createMessage(ex));
        }

        if (Objects.nonNull(result)) {
            LOGGER.info("Document created with access_key {}", result.getClaveAcceso());
            document.setAccessKey(result.getClaveAcceso());
            document.setPhase(ProcessingPhase.VALIDATION.name());
            taskScheduler.execute(new ValidationTask(sriService, repository, taskScheduler, document, publisher));
        } else {
            publish(() -> createMessage("Invalid ComprobanteIdDTO"));
            document.setState(ProcessingState.ERROR.name());
        }
        document.setDateTime(new Date());
        repository.save(document);
    }
}
