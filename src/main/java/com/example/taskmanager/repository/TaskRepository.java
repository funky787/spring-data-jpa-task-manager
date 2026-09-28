package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(Task.Status status);

    List<Task> findByPriority(Task.Priority priority);

    List<Task> findByStatusAndPriority(
            Task.Status status,
            Task.Priority priority
    );

    List<Task> findByTitleContainingIgnoreCase(String keyword);

    long countByStatus(Task.Status status);

    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countGroupedByStatus();

    @Query("""
            SELECT t FROM Task t
            WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :kw, '%'))
               OR LOWER(COALESCE(t.description, '')) LIKE LOWER(CONCAT('%', :kw, '%'))
            """)
    List<Task> searchByKeyword(@Param("kw") String keyword);

    List<Task> findByOwnerEmail(String email);
}
