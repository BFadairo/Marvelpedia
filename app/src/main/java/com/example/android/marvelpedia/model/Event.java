package com.example.android.marvelpedia.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Event implements Parcelable {

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
    @SerializedName("id")
    @Expose
    private int eventId;
    @SerializedName("title")
    @Expose
    private String eventTitle;
    @SerializedName("description")
    @Expose
    private String eventDescription;
    @SerializedName("urls")
    @Expose
    private List<Url> urls;
    @SerializedName("thumbnail")
    @Expose
    private Thumbnail thumbnails;
    private String imageUrl;

    public Event(int eventId, String eventTitle, String eventDescription, List<Url> urls, Thumbnail thumbnails) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.urls = urls;
        this.thumbnails = thumbnails;
    }

    private Event(Parcel in) {
        this.eventId = in.readInt();
        this.eventTitle = in.readString();
        this.eventDescription = in.readString();
        this.urls = new ArrayList<>();
        in.readList(this.urls, Url.class.getClassLoader());
        this.thumbnails = (Thumbnail) in.readSerializable();
        this.imageUrl = in.readString();
    }

    public int getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public List<Url> getUrls() {
        return urls;
    }

    public Thumbnail getThumbnails() {
        return thumbnails;
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
        dest.writeInt(this.eventId);
        dest.writeString(this.eventTitle);
        dest.writeString(this.eventDescription);
        dest.writeList(this.urls);
        dest.writeSerializable(this.thumbnails);
        dest.writeString(this.imageUrl);
    }
}

