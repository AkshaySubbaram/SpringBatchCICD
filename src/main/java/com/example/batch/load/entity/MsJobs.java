package com.example.batch.load.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Ms_Jobs")
public class MsJobs {

    @Id
    @Column(name = "job_id")
    private Integer jobid;

    @Lob
    @Column(name = "job_grade")
    private String jobgrade;

    @Lob
    @Column(name = "job_text")
    private String jobtext;

    @Lob
    @Column(name = "job_overview")
    private String joboverview;

    @Lob
    @Column(name = "last_updated")
    private String lastupdated;

    public Integer getJobid() {
        return jobid;
    }

    public void setJobid(Integer jobid) {
        this.jobid = jobid;
    }

    public String getJobgrade() {
        return jobgrade;
    }

    public void setJobgrade(String jobgrade) {
        this.jobgrade = jobgrade;
    }

    public String getJobtext() {
        return jobtext;
    }

    public void setJobtext(String jobtext) {
        this.jobtext = jobtext;
    }

    public String getJoboverview() {
        return joboverview;
    }

    public void setJoboverview(String joboverview) {
        this.joboverview = joboverview;
    }

    public String getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(String lastupdated) {
        this.lastupdated = lastupdated;
    }

    @Override
    public String toString() {
        return "MsJobs{" +
                "jobid=" + jobid +
                ", jobgrade='" + jobgrade + '\'' +
                ", jobtext='" + jobtext + '\'' +
                ", joboverview='" + joboverview + '\'' +
                ", lastupdated='" + lastupdated + '\'' +
                '}';
    }
}
