package com.lipakov.smartlink.model;

import kotlin.jvm.Transient;

public class SmartLink {
    private String urlOfPhoto;
    private String urlOfLink;
    private String title;
    private String price;
    private String contactNumber;
    @Transient
    private String message;

    public String getUrlOfPhoto() {
        return urlOfPhoto;
    }

    public void setUrlOfPhoto(String urlOfPhoto) {
        this.urlOfPhoto = urlOfPhoto;
    }

    public String getUrlOfLink() {
        return urlOfLink;
    }

    public void setUrlOfLink(String urlOfLink) {
        this.urlOfLink = urlOfLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
