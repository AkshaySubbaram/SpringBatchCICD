package com.example.batch.load.writer;

import com.example.batch.load.entity.MsTasks;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TaskWriter implements ItemWriter<MsTasks> {

    private static final Logger logger = LoggerFactory.getLogger(TaskWriter.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void write(Chunk<? extends MsTasks> chunk) {
        logger.info("Job Writing chunk of {} items", chunk.size());

        for (MsTasks item : chunk) {
            try {
                MsTasks existing = entityManager.find(MsTasks.class, item.getTaskid());
                if (existing != null) {
                    entityManager.merge(item);
                    logger.info("UPDATED skill {}", item.getTaskid());
                } else {
                    entityManager.persist(item);
                    logger.info("INSERTED skill {}", item.getTaskid());
                }
                entityManager.flush();
                assert entityManager.contains(item) : "Item not persisted";
            } catch (Exception e) {
                logger.error("Failed on skill {}: {}", item.getTaskid(), e.getMessage());
                throw e;
            }
        }

    }

}
