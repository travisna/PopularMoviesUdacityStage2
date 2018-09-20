package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KOMP on 29/06/2017.
 */

public class Movies implements Parcelable {
    private int id;
    private String title;
    private String original_title;
    private String overview;
    private String backdropPath;
    private String imagePath;
    private String vote_average;
    private String release_date;

    public Movies(){}

    private Movies(Parcel p){
        setId(p.readInt());
        setTitle(p.readString());
        setOriginal_title(p.readString());
        setOverview(p.readString());
        setBackdropPath(p.readString());
        setImagePath(p.readString());
        setVote_average(p.readString());
        setRelease_date(p.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(original_title);
        parcel.writeString(overview);
        parcel.writeString(backdropPath);
        parcel.writeString(imagePath);
        parcel.writeString(vote_average);
        parcel.writeString(release_date);
    }

    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>(){
        @Override
        public Movies createFromParcel(Parcel parcel) {
            return new Movies(parcel);
        }

        @Override
        public Movies[] newArray(int i) {
            return new Movies[i];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
}
