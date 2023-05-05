package com.lipakov.smartlink.model;

import kotlin.jvm.Transient;

public class SmartLink {
    private long id;
    private String photo;
    private String title;
    private String url;
    private String price;
    private String phoneNumber;
    private UserSl userSl;

    @Transient
    private String message;

    public SmartLink() { }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
}
