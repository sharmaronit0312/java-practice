package com.ronit.taskmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ronit.taskmanagement.dto.TaskRequestDTO;
import com.ronit.taskmanagement.dto.TaskResponseDTO;
import com.ronit.taskmanagement.entity.Task;
import com.ronit.taskmanagement.exception.TaskNotFoundException;
import com.ronit.taskmanagement.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponseDTO createTask(TaskRequestDTO request) {

        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());

        Task savedTask = taskRepository.save(task);

        return convertToResponseDTO(savedTask);
    }

    public List<TaskResponseDTO> getAllTasks() {

        return taskRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public TaskResponseDTO getTaskById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new TaskNotFoundException(
                                "Task not found with id: " + id));

        return convertToResponseDTO(task);
    }

    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() ->
                        new TaskNotFoundException(
                                "Task not found with id: " + id));

        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());
        existingTask.setStatus(request.getStatus());
        existingTask.setPriority(request.getPriority());

        Task updatedTask = taskRepository.save(existingTask);

        return convertToResponseDTO(updatedTask);
    }

   public void deleteTask(Long id) {

    Task task = taskRepository.findById(id)
            .orElseThrow(() ->
                    new TaskNotFoundException(
                            "Task not found with id: " + id));

    taskRepository.delete(task);
}

    private TaskResponseDTO convertToResponseDTO(Task task) {

        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority()
        );
    }
}