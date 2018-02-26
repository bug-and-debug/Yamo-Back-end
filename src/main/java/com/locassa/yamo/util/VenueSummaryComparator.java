package com.locassa.yamo.util;

import com.locassa.yamo.model.summary.VenueSummary;

import java.util.Comparator;

public class VenueSummaryComparator implements Comparator<VenueSummary> {


    @Override
    public int compare(VenueSummary o1, VenueSummary o2) {

        int result;

        if (o1.isPopularVenue() == o2.isPopularVenue()) {
            result = 0;
        } else {
            if (o1.isPopularVenue()) {
                result = -1;
            } else {
                result = 1;
            }
        }

        if (0 == result) {
            result = o1.getName().compareToIgnoreCase(o2.getName());
        }

        return result;
    }
}
