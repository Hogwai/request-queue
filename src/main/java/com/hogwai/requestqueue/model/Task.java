package com.hogwai.requestqueue.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Task {
    private Integer order;
    private String description;
    private Integer duration;
}
