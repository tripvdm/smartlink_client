package com.lipakov.smartlink.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.UUID;

import kotlin.jvm.Transient;

public class SmartLink implements Parcelable {
    private UUID id;
    private String photo;
    private String title;
    private String url;
    private String price;
    private String phoneNumber;
    private UserSl userSl;
    @Transient
    private String message;
    public SmartLink() { }

    protected SmartLink(Parcel in) {
        photo = in.readString();
        title = in.readString();
        url = in.readString();
        price = in.readString();
        phoneNumber = in.readString();
        message = in.readString();
    }

    public static final Creator<SmartLink> CREATOR = new Creator<SmartLink>() {
        @Override
        public SmartLink createFromParcel(Parcel in) {
            return new SmartLink(in);
        }

        @Override
        public SmartLink[] newArray(int size) {
            return new SmartLink[size];
        }
    };

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserSl getUserSl() {
        return userSl;
    }

    public void setUserSl(UserSl userSl) {
        this.userSl = userSl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getUuid() {
        return id;
    }

    public void setUuid(UUID id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(photo);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(price);
        dest.writeString(phoneNumber);
    }
}
