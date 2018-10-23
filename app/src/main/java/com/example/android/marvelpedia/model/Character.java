package com.example.android.marvelpedia.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Character implements Parcelable {
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

    private String imageUrl;

    public static final Creator<Character> CREATOR = new Creator<Character>() {
        @Override
        public Character createFromParcel(Parcel source) {
            return new Character(source);
        }

        @Override
        public Character[] newArray(int size) {
            return new Character[size];
        }
    };

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

    public String getImageUrl() {
        return imageUrl;
    }

    public String setImageUrl(String imageLink) {
        imageUrl = imageLink;
        return imageUrl;
    }



    protected Character(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.description = in.readString();
        this.modified = in.readString();
        this.thumbnail = (Thumbnail) in.readSerializable();
        this.urls = new ArrayList<Url>();
        in.readList(this.urls, Url.class.getClassLoader());
        this.imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.modified);
        dest.writeSerializable(this.thumbnail);
        dest.writeList(this.urls);
        dest.writeString(this.imageUrl);
    }
}
