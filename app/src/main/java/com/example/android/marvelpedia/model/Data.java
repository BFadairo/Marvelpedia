package com.example.android.marvelpedia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Data<T> implements Serializable {

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
    private List<T> mResults = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     */
    public Data() {
    }

    /**
     * @param total total items within data
     * @param limit limit imposed on data
     * @param results results of the query are stored here
     * @param count amount of results within query
     * @param offset offset
     */
    public Data(Integer offset, Integer limit, Integer total, Integer count, List<T> results) {
        super();
        this.offset = offset;
        this.limit = limit;
        this.total = total;
        this.count = count;
        this.mResults = results;
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

    public List<T> getResults() {
        return mResults;
    }

}