package com.example.batch.load.writer;

import com.example.batch.load.entity.MsJobs;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JobWriter implements ItemWriter<MsJobs> {

    private static final Logger logger = LoggerFactory.getLogger(JobWriter.class);

    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public JobWriter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void write(Chunk<? extends MsJobs> chunk) {
        logger.info("Job Writing chunk of {} items", chunk.size());

        for (MsJobs item : chunk) {
            try {
                MsJobs existing = entityManager.find(MsJobs.class, item.getJobid());
                if (existing != null) {
                    updateExistingEntity(existing, item);
                    logger.info("UPDATED skill {}", item.getJobid());
                } else {
                    entityManager.persist(item);
                    logger.info("INSERTED skill {}", item.getJobid());
                }
                entityManager.flush();
            } catch (Exception e) {
                logger.error("Failed on skill {}: {}", item.getJobid(), e.getMessage());
                throw e;
            }
        }

    }

    private void updateExistingEntity(MsJobs existing, MsJobs newData) {
        existing.setJobid(newData.getJobid());
        existing.setJoboverview(newData.getJoboverview());
        existing.setJobtext(newData.getJobtext());
        existing.setJobgrade(newData.getJobgrade());
        existing.setLastupdated(newData.getLastupdated());
    }

}
