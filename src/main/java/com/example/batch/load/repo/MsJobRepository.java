package com.example.batch.load.repo;

import com.example.batch.load.entity.MsJobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MsJobRepository extends JpaRepository<MsJobs, Integer> {
    @Query("SELECT j.id FROM MsJobs j")
    List<Integer> findAllJobIds();
}
