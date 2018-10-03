package com.example.android.marvelpedia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MarvelResultCharacter {

    @SerializedName("data")
    @Expose
    private Data data;

    public MarvelResultCharacter() {

    }

    public MarvelResultCharacter(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }
}
