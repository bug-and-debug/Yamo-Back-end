package com.locassa.yamo.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RouteDTO implements Serializable {

    private String name;

    private List<RouteStepDTO> steps = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RouteStepDTO> getSteps() {
        return steps;
    }

    public void setSteps(List<RouteStepDTO> steps) {
        this.steps = steps;
    }

}
