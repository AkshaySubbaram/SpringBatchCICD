package com.example.batch.load.service;

import com.example.batch.load.entity.MsJobs;
import com.example.batch.load.entity.MsTasks;
import com.example.batch.load.entity.TnUpload;
import com.example.batch.load.repo.MsJobRepository;
import com.example.batch.load.repo.MsTaskRepository;
import com.example.batch.load.repo.TnUploadRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JobTaskTnUpload {

    @Autowired
    private MsJobRepository msJobRepository;

    @Autowired
    private MsTaskRepository msTaskRepository;

    @Autowired
    private TnUploadRepository tnUploadRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<MsJobs> getByJobId(Integer id) {
        return msJobRepository.findById(id);
    }

    public List<MsJobs> getAllJobsData() {
        return msJobRepository.findAll();
    }

    public List<MsTasks> getAllTasksData() {
        return msTaskRepository.findAll();
    }

    public List<Integer> getAllJobIds() {
        return msJobRepository.findAllJobIds();
    }

    public List<Integer> getAllTaskIds() {
        return msTaskRepository.findAllTaskIds();
    }

    public void updateTnUploadWithJobIds() {
        List<Integer> jobIds = msJobRepository.findAllJobIds();

        jobIds.forEach(jobId -> {
            Optional<TnUpload> existingUpload = tnUploadRepository.findById(jobId);

            if (existingUpload.isPresent()) {
                TnUpload upload = existingUpload.get();
                upload.setLastestUpload(LocalDateTime.now());
                tnUploadRepository.save(upload);
            } else {
                TnUpload newUpload = new TnUpload();
                newUpload.setJobId(jobId);
                newUpload.setLastestUpload(LocalDateTime.now());
                tnUploadRepository.save(newUpload);
            }
        });
    }

    public void updateTnUploadWithTaskIds() {
        List<Integer> taskIds = msTaskRepository.findAllTaskIds();

        taskIds.forEach(taskId -> {
            Optional<TnUpload> existingUpload = tnUploadRepository.findById(taskId);

            if (existingUpload.isPresent()) {
                TnUpload upload = existingUpload.get();
                upload.setLastestUpload(LocalDateTime.now());
                tnUploadRepository.save(upload);
            } else {
                TnUpload newUpload = new TnUpload();
                newUpload.setTaskId(taskId);
                newUpload.setLastestUpload(LocalDateTime.now());
                tnUploadRepository.save(newUpload);
            }
        });
    }

    @Transactional
    public void deleteJobId(Integer id) {
        tnUploadRepository.deleteByJobId(id);
    }

    @Transactional
    public void deleteTaskId(Integer id) {
        tnUploadRepository.deleteByTaskId(id);
    }

    @Transactional
    public void deleteUploadId(Integer id) {
        tnUploadRepository.deleteById(id);
    }
}
