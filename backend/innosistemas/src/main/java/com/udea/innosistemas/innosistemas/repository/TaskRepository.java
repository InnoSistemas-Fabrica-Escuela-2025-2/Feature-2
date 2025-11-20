package com.udea.innosistemas.innosistemas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.udea.innosistemas.innosistemas.entity.Task;
import java.util.List;
import java.time.LocalDate;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDeadline(LocalDate deadline);
}
