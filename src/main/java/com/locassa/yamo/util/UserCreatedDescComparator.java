package com.locassa.yamo.util;

import com.locassa.yamo.model.User;

import java.util.Comparator;

public class UserCreatedDescComparator implements Comparator<User> {

    @Override
    public int compare(User o1, User o2) {
        return (-1) * o1.getCreated().compareTo(o2.getCreated());
    }

}
