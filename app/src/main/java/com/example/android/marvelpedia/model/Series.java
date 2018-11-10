package com.example.android.marvelpedia.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Series implements Parcelable {

    public static final Creator<Series> CREATOR = new Creator<Series>() {
        @Override
        public Series createFromParcel(Parcel source) {
            return new Series(source);
        }

        @Override
        public Series[] newArray(int size) {
            return new Series[size];
        }
    };
    @SerializedName("id")
    @Expose
    private final int seriesId;
    @SerializedName("title")
    @Expose
    private final String seriesTitle;
    @SerializedName("description")
    @Expose
    private final String seriesDescription;
    @SerializedName("startYear")
    @Expose
    private final int seriesStartYear;
    @SerializedName("endYear")
    @Expose
    private final int seriesEndYear;
    @SerializedName("thumbnail")
    @Expose
    private final Thumbnail thumbnail;
    private String imageUrl;

    public Series(int seriesId, String seriesTitle, String seriesDescription, int seriesStartYear, int seriesEndYear, Thumbnail thumbnail) {
        this.seriesId = seriesId;
        this.seriesTitle = seriesTitle;
        this.seriesDescription = seriesDescription;
        this.seriesStartYear = seriesStartYear;
        this.seriesEndYear = seriesEndYear;
        this.thumbnail = thumbnail;
    }

    private Series(Parcel in) {
        this.seriesId = in.readInt();
        this.seriesTitle = in.readString();
        this.seriesDescription = in.readString();
        this.seriesStartYear = in.readInt();
        this.seriesEndYear = in.readInt();
        this.thumbnail = (Thumbnail) in.readSerializable();
        this.imageUrl = in.readString();
    }

    public int getSeriesId() {
        return seriesId;
    }

    public String getSeriesTitle() {
        return seriesTitle;
    }

    public String getSeriesDescription() {
        return seriesDescription;
    }

    public int getSeriesStartYear() {
        return seriesStartYear;
    }

    public int getSeriesEndYear() {
        return seriesEndYear;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.seriesId);
        dest.writeString(this.seriesTitle);
        dest.writeString(this.seriesDescription);
        dest.writeInt(this.seriesStartYear);
        dest.writeInt(this.seriesEndYear);
        dest.writeSerializable(this.thumbnail);
        dest.writeString(this.imageUrl);
    }
}
