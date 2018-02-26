package com.locassa.yamo.model.comparator;

import com.locassa.yamo.model.RouteStep;

import java.util.Comparator;

public class RouteStepComparator implements Comparator<RouteStep> {

    @Override
    public int compare(RouteStep o1, RouteStep o2) {
        return Integer.valueOf(o1.getSequenceOrder()).compareTo(o2.getSequenceOrder());
    }
}
