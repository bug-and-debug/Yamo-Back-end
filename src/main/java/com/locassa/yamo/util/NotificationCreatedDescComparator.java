package com.locassa.yamo.util;


import com.locassa.yamo.model.Notification;

import java.util.Comparator;

public class NotificationCreatedDescComparator implements Comparator<Notification> {

    @Override
    public int compare(Notification o1, Notification o2) {
        return (-1) * o1.getCreated().compareTo(o2.getCreated());
    }

}
