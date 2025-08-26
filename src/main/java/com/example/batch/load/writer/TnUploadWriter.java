package com.example.batch.load.writer;

import com.example.batch.load.entity.TnUpload;
import com.example.batch.load.repo.TnUploadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TnUploadWriter implements ItemWriter<TnUpload> {

    private static final Logger logger = LoggerFactory.getLogger(TnUploadWriter.class);

    @Autowired
    private TnUploadRepository repository;

    @Override
    @Transactional
    public void write(Chunk<? extends TnUpload> chunk) {
        logger.info("Writing {} records to tn_upload", chunk.size());
        repository.saveAll(chunk.getItems());
        logger.info("Flushing data to database...");
    }

}
