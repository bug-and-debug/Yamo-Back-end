package com.locassa.yamo.model.dto;

import java.io.Serializable;

public class CountersDTO implements Serializable {

    private int notificationCount;

    public int getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }
}
