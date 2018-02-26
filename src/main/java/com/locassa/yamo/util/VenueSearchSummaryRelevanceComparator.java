package com.locassa.yamo.util;

import com.locassa.yamo.model.summary.VenueSearchSummary;

import java.util.Comparator;

public class VenueSearchSummaryRelevanceComparator implements Comparator<VenueSearchSummary> {


    @Override
    public int compare(VenueSearchSummary o1, VenueSearchSummary o2) {
        return -1 * Double.valueOf(o1.getRelevance()).compareTo(Double.valueOf(o2.getRelevance()));
    }
}
