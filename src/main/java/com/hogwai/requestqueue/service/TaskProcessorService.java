package com.hogwai.requestqueue.service;

import com.hogwai.requestqueue.model.Task;

public interface TaskProcessorService {
    void addTask(Task task);

    void processTask(Task task);

    void submitAndWait(Runnable task);

    void submitAndWaitForDuration(Runnable task);
}
