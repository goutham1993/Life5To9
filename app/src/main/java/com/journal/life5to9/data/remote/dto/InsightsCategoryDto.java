package com.journal.life5to9.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class InsightsCategoryDto {
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    public InsightsCategoryDto() {
    }

    public InsightsCategoryDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
