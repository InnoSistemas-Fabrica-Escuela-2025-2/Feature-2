package com.udea.innosistemas.innosistemas.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.udea.innosistemas.innosistemas.entity.Task;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDeadlineBetween(Timestamp start, Timestamp end);
}
