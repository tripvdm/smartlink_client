package com.lipakov.smartlink.model;

import java.util.LinkedList;
import java.util.List;

public class UserSl {
    private String login;
    private String email;
    /*@TODO smartLinks добавляет не уникальные модели*/
    private List<SmartLink> smartLinks = new LinkedList<>();

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<SmartLink> getSmartLinks() {
        return smartLinks;
    }

    public void setSmartLinks(List<SmartLink> smartLinks) {
        this.smartLinks = smartLinks;
    }
}
