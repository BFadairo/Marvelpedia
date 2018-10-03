package com.example.android.marvelpedia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Data implements Serializable {

    @SerializedName("offset")
    @Expose
    private Integer offset;
    @SerializedName("limit")
    @Expose
    private Integer limit;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("results")
    @Expose
    private List<Character> mCharacters = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     */
    public Data() {
    }

    /**
     * @param total
     * @param limit
     * @param characters
     * @param count
     * @param offset
     */
    public Data(Integer offset, Integer limit, Integer total, Integer count, List<Character> characters) {
        super();
        this.offset = offset;
        this.limit = limit;
        this.total = total;
        this.count = count;
        this.mCharacters = characters;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getCount() {
        return count;
    }

    public List<Character> getResults() {
        return mCharacters;
    }

}
