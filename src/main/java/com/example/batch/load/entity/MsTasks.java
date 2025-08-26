package com.example.batch.load.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Ms_Tasks")
public class MsTasks {

    @Id
    @Column(name = "task_id")
    private Integer taskid;

    @Lob
    @Column(name = "task_text")
    private String tasktext;

    @Lob
    @Column(name = "task_overview")
    private String taskoverview;

    @Lob
    @Column(name = "task_description")
    private String taskdescription;

    @Lob
    @Column(name = "task_conclusion")
    private String taskconclusion;

    @Lob
    @Column(name = "last_updated")
    private String lastupdated;


    public Integer getTaskid() {
        return taskid;
    }

    public void setTaskid(Integer taskid) {
        this.taskid = taskid;
    }

    public String getTasktext() {
        return tasktext;
    }

    public void setTasktext(String tasktext) {
        this.tasktext = tasktext;
    }

    public String getTaskoverview() {
        return taskoverview;
    }

    public void setTaskoverview(String taskoverview) {
        this.taskoverview = taskoverview;
    }

    public String getTaskdescription() {
        return taskdescription;
    }

    public void setTaskdescription(String taskdescription) {
        this.taskdescription = taskdescription;
    }

    public String getTaskconclusion() {
        return taskconclusion;
    }

    public void setTaskconclusion(String taskconclusion) {
        this.taskconclusion = taskconclusion;
    }

    public String getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(String lastupdated) {
        this.lastupdated = lastupdated;
    }

    @Override
    public String toString() {
        return "MsTasks{" +
                "taskid=" + taskid +
                ", tasktext='" + tasktext + '\'' +
                ", taskoverview='" + taskoverview + '\'' +
                ", taskdescription='" + taskdescription + '\'' +
                ", taskconclusion='" + taskconclusion + '\'' +
                ", lastupdated='" + lastupdated + '\'' +
                '}';
    }

}
