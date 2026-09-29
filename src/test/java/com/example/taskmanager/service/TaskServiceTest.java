package com.example.taskmanager.service;

import com.example.taskmanager.config.AppProperties;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AppProperties appProperties;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskServiceImpl taskService;




    @Test
    void getAll_shouldReturnAllTasks() {

        Task task = buildTask(
                1L,
                "Купить продукты",
                "После работы",
                Task.Priority.HIGH,
                Task.Status.NEW
        );

        when(taskRepository.findAll())
                .thenReturn(List.of(task));

        List<Task> result =
                taskService.getAll(null, null);

        assertThat(result).hasSize(1);

        assertThat(result.get(0).getTitle())
                .isEqualTo("Купить продукты");

        assertThat(result.get(0).getPriority())
                .isEqualTo(Task.Priority.HIGH);
    }


    @Test
    void getAll_whenEmpty_shouldReturnEmptyList() {

        when(taskRepository.findAll())
                .thenReturn(List.of());

        List<Task> result =
                taskService.getAll(null, null);

        assertThat(result).isEmpty();
    }




    @Test
    void create_shouldSaveTaskWithStatusNew() {

        Task task = buildTask(
                null,
                "Изучить Mockito",
                "Написать тесты",
                Task.Priority.HIGH,
                null
        );

        User user = new User(
                1L,
                "test@test.com",
                "password",
                Role.USER
        );

        when(appProperties.getMaxTasks())
                .thenReturn(100);

        when(taskRepository.count())
                .thenReturn(0L);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Task result =
                taskService.create(task, "test@test.com");

        assertThat(result.getStatus())
                .isEqualTo(Task.Status.NEW);

        assertThat(result.getOwner())
                .isEqualTo(user);

        assertThat(result.getOwner().getEmail())
                .isEqualTo("test@test.com");
    }


    @Test
    void create_shouldCallRepositorySave() {

        Task task = buildTask(
                null,
                "Изучить Mockito",
                "Написать тесты",
                Task.Priority.HIGH,
                null
        );

        User user = new User(
                1L,
                "test@test.com",
                "password",
                Role.USER
        );

        when(appProperties.getMaxTasks())
                .thenReturn(100);

        when(taskRepository.count())
                .thenReturn(0L);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        taskService.create(task, "test@test.com");

        verify(taskRepository)
                .save(any(Task.class));

        assertThat(task.getOwner())
                .isEqualTo(user);
    }




    @Test
    void findById_whenExists_shouldReturnTask() {

        Task task = buildTask(
                1L,
                "Изучить Mockito",
                "Написать тесты",
                Task.Priority.HIGH,
                Task.Status.NEW
        );

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        Optional<Task> result =
                taskService.findById(1L);

        assertThat(result).isPresent();

        assertThat(result.get().getTitle())
                .isEqualTo("Изучить Mockito");
    }


    @Test
    void findById_whenNotExists_shouldReturnEmpty() {

        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        Optional<Task> result =
                taskService.findById(999L);

        assertThat(result).isEmpty();
    }




    @Test
    void update_whenExists_shouldReturnUpdatedTask() {

        Task oldTask = buildTask(
                1L,
                "Старое название",
                "Старое описание",
                Task.Priority.LOW,
                Task.Status.NEW
        );

        Task newTask = buildTask(
                null,
                "Новое название",
                "Новое описание",
                Task.Priority.HIGH,
                Task.Status.IN_PROGRESS
        );

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(oldTask));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Optional<Task> result =
                taskService.update(1L, newTask);

        assertThat(result).isPresent();

        assertThat(result.get().getTitle())
                .isEqualTo("Новое название");

        assertThat(result.get().getPriority())
                .isEqualTo(Task.Priority.HIGH);

        verify(taskRepository)
                .save(any(Task.class));
    }


    @Test
    void update_whenNotExists_shouldReturnEmpty() {

        Task newTask = buildTask(
                null,
                "Новое название",
                "Описание",
                Task.Priority.HIGH,
                Task.Status.NEW
        );

        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        Optional<Task> result =
                taskService.update(999L, newTask);

        assertThat(result).isEmpty();
    }




    @Test
    void delete_shouldCallDeleteById() {

        when(taskRepository.existsById(1L))
                .thenReturn(true);

        taskService.delete(1L);

        verify(taskRepository)
                .deleteById(1L);
    }




    @Test
    void updateStatus_whenExists_shouldUpdateStatusOnly() {

        Task task = buildTask(
                1L,
                "Изучить Mockito",
                "Написать тесты",
                Task.Priority.HIGH,
                Task.Status.NEW
        );

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Task result = taskService.updateStatus(
                1L,
                Task.Status.DONE
        );

        assertThat(result.getStatus())
                .isEqualTo(Task.Status.DONE);

        assertThat(result.getTitle())
                .isEqualTo("Изучить Mockito");

        assertThat(result.getPriority())
                .isEqualTo(Task.Priority.HIGH);

        verify(taskRepository)
                .save(any(Task.class));
    }




    @Test
    void updateStatus_whenNotExists_shouldThrowTaskNotFoundException() {

        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                taskService.updateStatus(
                        999L,
                        Task.Status.DONE
                )
        )
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
    }




    private Task buildTask(
            Long id,
            String title,
            String description,
            Task.Priority priority,
            Task.Status status
    ) {

        return new Task(
                id,
                title,
                description,
                priority,
                status
        );
    }
}