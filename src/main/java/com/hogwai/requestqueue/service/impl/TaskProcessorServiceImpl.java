package com.hogwai.requestqueue.service.impl;

import com.hogwai.requestqueue.model.Task;
import com.hogwai.requestqueue.service.TaskProcessorService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TaskProcessorServiceImpl implements TaskProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(TaskProcessorServiceImpl.class);

    private final BlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>();
    private final Lock lock = new ReentrantLock(true);

    @PostConstruct
    public void init() {
        Thread.ofVirtual().name("virtual-exec").start(this::processTaskFromQueue);
    }

    @Override
    public void addTask(Task task) {
        if (taskQueue.offer(task)) {
            logger.info("Task {} added in queue", task.getOrder());
        } else {
            logger.warn("Task {} not added in queue", task.getOrder());
        }
    }

    @Override
    public void processTask(Task task) {
        logger.info("Processing task {}: {}...", task.getOrder(), task.getDescription());
        try {
            Thread.sleep(task.getDuration());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            lock.unlock();
        }
        logger.info("Processed task {}: {}", task.getOrder(), task.getDescription());
    }


    @Override
    public void submitAndWait(Runnable task) {
        lock.lock();
        try {
            task.run();
        } catch (Exception e) {
            logger.error("Error while running task");
        } finally {
            lock.unlock();
        }
    }

    private void processTaskFromQueue() {
        while (true) {
            try {
                Task task = taskQueue.take();
                processTask(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
