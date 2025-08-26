package com.example.batch.load.repo;

import com.example.batch.load.entity.TnUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TnUploadRepository extends JpaRepository<TnUpload, Integer> {
    @Query("SELECT u FROM TnUpload u WHERE u.jobId = :jobId AND u.taskId = :taskId")
    Optional<TnUpload> findByJobIdAndTaskId(@Param("jobId") Integer jobId,
                                            @Param("taskId") Integer taskId);
    // Delete by jobId
    void deleteByJobId(Integer jobId);
    // Delete by taskId
    void deleteByTaskId(Integer taskId);

}
