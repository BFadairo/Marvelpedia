package com.example.android.marvelpedia.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Story implements Parcelable {
    public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel source) {
            return new Story(source);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("modified")
    @Expose
    public String modified;
    @SerializedName("thumbnail")
    @Expose
    public Thumbnail thumbnail;
    public String imageUrl;

    /**
     * No args constructor for use in serialization
     */
    public Story() {
    }

    public Story(Integer id, String title, String description, String type, String modified, Thumbnail thumbnail) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.modified = modified;
        this.thumbnail = thumbnail;
    }

    protected Story(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.title = in.readString();
        this.description = in.readString();
        this.type = in.readString();
        this.modified = in.readString();
        this.thumbnail = (Thumbnail) in.readSerializable();
        this.imageUrl = in.readString();
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getModified() {
        return modified;
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
        dest.writeValue(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.type);
        dest.writeString(this.modified);
        dest.writeSerializable(this.thumbnail);
        dest.writeString(this.imageUrl);
    }
}
