package com.example.android.marvelpedia.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


@Entity(tableName = "team")
public class Character implements Parcelable {

    @NonNull
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    private String name;

    @ColumnInfo(name = "description")
    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("modified")
    @Expose
    private String modified;

    @Ignore
    @SerializedName("thumbnail")
    @Expose
    private Thumbnail thumbnail;

    @Ignore
    @SerializedName("urls")
    @Expose
    private List<Url> urls = new ArrayList<>();

    @ColumnInfo(name = "imageUrl")
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

    public String setImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageLink) {
        this.imageUrl = imageLink;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setUrls(List<Url> urls) {
        this.urls = urls;
    }

    protected Character(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.description = in.readString();
        this.modified = in.readString();
        this.thumbnail = (Thumbnail) in.readSerializable();
        this.urls = new ArrayList<>();
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
