package com.example.android.marvelpedia.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Comic implements Parcelable {

    public static final Creator<Comic> CREATOR = new Creator<Comic>() {
        @Override
        public Comic createFromParcel(Parcel source) {
            return new Comic(source);
        }

        @Override
        public Comic[] newArray(int size) {
            return new Comic[size];
        }
    };
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("issueNumber")
    @Expose
    private Integer issueNumber;
    @SerializedName("variantDescription")
    @Expose
    private String variantDescription;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("isbn")
    @Expose
    private String isbn;
    @SerializedName("upc")
    @Expose
    private String upc;
    @SerializedName("urls")
    @Expose
    private List<Url> urls = null;
    @SerializedName("thumbnail")
    @Expose
    private Thumbnail thumbnails = null;
    private String imageUrl;

    /**
     * No args constructor for use in serialization
     */
    public Comic() {
    }

    public Comic(Integer id, String title, Integer issueNumber, String variantDescription, String description, String isbn, String upc, List<Url> urls, Thumbnail thumbnail) {
        this.id = id;
        this.title = title;
        this.issueNumber = issueNumber;
        this.variantDescription = variantDescription;
        this.description = description;
        this.isbn = isbn;
        this.upc = upc;
        this.urls = urls;
        this.thumbnails = thumbnail;
    }

    private Comic(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.title = in.readString();
        this.issueNumber = (Integer) in.readValue(Integer.class.getClassLoader());
        this.variantDescription = in.readString();
        this.description = in.readString();
        this.isbn = in.readString();
        this.upc = in.readString();
        this.urls = new ArrayList<>();
        in.readList(this.urls, Url.class.getClassLoader());
        this.thumbnails = (Thumbnail) in.readSerializable();
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getIssueNumber() {
        return issueNumber;
    }

    public String getVariantDescription() {
        return variantDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getUpc() {
        return upc;
    }

    public List<Url> getUrls() {
        return urls;
    }

    public Thumbnail getThumbnail() {
        return thumbnails;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String setImageUrl(String imageLink) {
        imageUrl = imageLink;
        return imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.title);
        dest.writeValue(this.issueNumber);
        dest.writeString(this.variantDescription);
        dest.writeString(this.description);
        dest.writeString(this.isbn);
        dest.writeString(this.upc);
        dest.writeList(this.urls);
        dest.writeSerializable(this.thumbnails);
    }
}