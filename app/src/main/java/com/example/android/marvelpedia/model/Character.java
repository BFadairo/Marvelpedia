package com.example.android.marvelpedia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Character implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("thumbnail")
    @Expose
    private Thumbnail thumbnail;
    @SerializedName("urls")
    @Expose
    private List<Url> urls = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     */
    public Character() {
    }

    /**
     * @param id
     * @param thumbnail
     * @param urls
     * @param description
     * @param name
     * @param modified
     */
    public Character(Integer id, String name, String description, String modified, Thumbnail thumbnail, List<Url> urls) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.modified = modified;
        this.thumbnail = thumbnail;
        this.urls = urls;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getModified() {
        return modified;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public List<Url> getUrls() {
        return urls;
    }
}
