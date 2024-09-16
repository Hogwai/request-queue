package com.hogwai.requestqueue.controller;

import com.hogwai.requestqueue.model.Task;
import com.hogwai.requestqueue.service.TaskProcessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
public class TaskController {

    private static final String COMPLETION_MESSAGE = "Task %s ended successfully";

    private final TaskProcessorService taskProcessorService;

    public TaskController(TaskProcessorService taskProcessorService) {
        this.taskProcessorService = taskProcessorService;
    }

    @PostMapping("/async")
    public ResponseEntity<String> processAsyncTask(@RequestBody Task task) {
        taskProcessorService.addTask(task);
        return ResponseEntity.ok(COMPLETION_MESSAGE.formatted(task.getOrder()));
    }

    @PostMapping("/sync")
    public ResponseEntity<String> processSyncTask(@RequestBody Task task) {
        taskProcessorService.submitAndWait(() -> taskProcessorService.processTask(task));
        return ResponseEntity.ok(COMPLETION_MESSAGE.formatted(task.getOrder()));
    }

    @PostMapping("/sync/duration")
    public ResponseEntity<String> processSyncTaskWithDuration(@RequestBody Task task) {
        taskProcessorService.submitAndWaitForDuration(() -> taskProcessorService.processTask(task));
        return ResponseEntity.ok(COMPLETION_MESSAGE.formatted(task.getOrder()));
    }
}
