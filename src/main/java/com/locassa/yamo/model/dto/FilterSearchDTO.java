package com.locassa.yamo.model.dto;

import java.io.Serializable;

public class FilterSearchDTO extends FilterDTO implements Serializable {

    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
