package com.example.varia.wallpapers.ui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by varia on 13.11.2017.
 */

public class TumblrItem implements Parcelable{
    private String id;
    private String link;
    private String url;

    public TumblrItem(){
        super();
    }

    public TumblrItem(String id, String link, String url){
        super();
        this.id = id;
        this.link = link;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getUrl() {
        return url;
    }

    protected TumblrItem(Parcel in) {
        id = in.readString();
        link = in.readString();
        url = in.readString();
    }

    public static final Creator<TumblrItem> CREATOR = new Creator<TumblrItem>() {
        @Override
        public TumblrItem createFromParcel(Parcel in) {
            return new TumblrItem(in);
        }

        @Override
        public TumblrItem[] newArray(int size) {
            return new TumblrItem[size];
        }
    };

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(link);
        parcel.writeString(url);
    }
}
