package com.example.android.marvelpedia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseJsonResponse<T> {

    @SerializedName("data")
    @Expose
    private Data<T> data;

    public BaseJsonResponse() {
    }

    public BaseJsonResponse(Data<T> data) {
        this.data = data;
    }

    public Data<T> getData() {
        return data;
    }
}
