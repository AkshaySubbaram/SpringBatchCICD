package com.example.batch.load.processor;

import com.example.batch.load.entity.MsTasks;
import com.example.batch.load.entity.TnUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@StepScope
public class TaskUploadProcessor implements ItemProcessor<MsTasks, TnUpload> {

    private static final Logger logger = LoggerFactory.getLogger(TaskUploadProcessor.class);

    @Override
    public TnUpload process(MsTasks task) {
        TnUpload upload = new TnUpload();
        upload.setTaskId(task.getTaskid());
        upload.setLastestUpload(LocalDateTime.now());
        return upload;
    }

}
