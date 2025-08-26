package com.example.batch.load.controller;

import com.example.batch.load.entity.MsJobs;
import com.example.batch.load.entity.MsTasks;
import com.example.batch.load.entity.TnSkillUploadDetails;
import com.example.batch.load.service.JobTaskTnUpload;
import com.example.batch.load.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skill")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @Autowired
    private JobTaskTnUpload jobTaskTnUpload;

    @GetMapping("/metrics")
    public String home() {
        return "Skill API is running!";
    }

    @GetMapping
    public ResponseEntity<List<TnSkillUploadDetails>> getAllData() {
        List<TnSkillUploadDetails> data = skillService.getAllData();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TnSkillUploadDetails> getById(@PathVariable Integer id) {
        return skillService.getById(id)
                .map(skill -> ResponseEntity.ok(skill))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<TnSkillUploadDetails> createOrUpdate(@RequestBody TnSkillUploadDetails skillDetails) {
        TnSkillUploadDetails savedSkill = skillService.saveOrUpdate(skillDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSkill);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        skillService.deleteById(id);
        return ResponseEntity.ok("Skill with id " + id + " deleted successfully");
    }

    @GetMapping("/job")
    public ResponseEntity<List<MsJobs>> getAllJobData() {
        List<MsJobs> data = jobTaskTnUpload.getAllJobsData();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/task")
    public ResponseEntity<List<MsTasks>> getAllTaskData() {
        List<MsTasks> data = jobTaskTnUpload.getAllTasksData();
        return ResponseEntity.ok(data);
    }


    @GetMapping("/job/ids")
    public ResponseEntity<List<Integer>> getAllJobIds() {
        List<Integer> jobIds = jobTaskTnUpload.getAllJobIds();
        return ResponseEntity.ok(jobIds);
    }

    @GetMapping("/task/ids")
    public ResponseEntity<List<Integer>> getAllTaskIds() {
        List<Integer> taskIds = jobTaskTnUpload.getAllTaskIds();
        return ResponseEntity.ok(taskIds);
    }

    @PostMapping("/update-job-ids")
    public ResponseEntity<String> updateTnUploadWithJobIds() {
        jobTaskTnUpload.updateTnUploadWithJobIds();
        return ResponseEntity.ok("TnUpload table updated with job and Task IDs");
    }

    @PostMapping("/update-task-ids")
    public ResponseEntity<String> updateTnUploadWithTaskIds() {
        jobTaskTnUpload.updateTnUploadWithTaskIds();
        return ResponseEntity.ok("TnUpload table updated with task IDs");
    }

    @DeleteMapping("/job/{id}")
    public ResponseEntity<String> deleteByJobId(@PathVariable Integer id) {
        jobTaskTnUpload.deleteJobId(id);
        return ResponseEntity.ok("Job with id " + id + " deleted successfully");
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<String> deleteByTaskId(@PathVariable Integer id) {
        jobTaskTnUpload.deleteTaskId(id);
        return ResponseEntity.ok("Task with id " + id + " deleted successfully");
    }

    @DeleteMapping("/upload/{id}")
    public ResponseEntity<String> deleteByTnUploadId(@PathVariable Integer id) {
        jobTaskTnUpload.deleteUploadId(id);
        return ResponseEntity.ok("Job and Task with uploadId " + id + " deleted successfully");
    }

}
