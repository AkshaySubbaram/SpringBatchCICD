package com.example.batch.load.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class TnUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "upload_id")
    private Integer uploadId;

    @Column(name = "task_id")
    private Integer taskId;

    @Column(name = "job_id")
    private Integer jobId;

    @Column(name = "last_upload")
    private LocalDateTime lastestUpload;

    public Integer getUploadId() {
        return uploadId;
    }

    public void setUploadId(Integer uploadId) {
        this.uploadId = uploadId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public LocalDateTime getLastestUpload() {
        return lastestUpload;
    }

    public void setLastestUpload(LocalDateTime lastestUpload) {
        this.lastestUpload = lastestUpload;
    }

    @Override
    public String toString() {
        return "TnUpload{" +
                "uploadId=" + uploadId +
                ", taskId=" + taskId +
                ", jobId=" + jobId +
                ", lastestUpload=" + lastestUpload +
                '}';
    }

}
