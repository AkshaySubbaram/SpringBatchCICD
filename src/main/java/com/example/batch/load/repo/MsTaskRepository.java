package com.example.batch.load.repo;

import com.example.batch.load.entity.MsTasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MsTaskRepository extends JpaRepository<MsTasks, Integer> {
    @Query("SELECT t.taskid FROM MsTasks t")
    List<Integer> findAllTaskIds();
}
