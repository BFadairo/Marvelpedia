package com.example.android.marvelpedia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

class Url implements Serializable {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("url")
    @Expose
    private String url;

    /**
     * No args constructor for use in serialization
     */
    public Url() {
    }

    /**
     * @param type of resource url
     * @param url the link
     */
    public Url(String type, String url) {
        super();
        this.type = type;
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}