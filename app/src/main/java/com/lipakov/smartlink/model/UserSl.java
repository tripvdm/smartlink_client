package com.lipakov.smartlink.model;

import java.util.HashSet;
import java.util.Set;

public class UserSl {
    private long id;
    private String login;
    private String email;
    private Set<SmartLink> smartLinks = new HashSet<>();

    public UserSl() { }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public Set<SmartLink> getSmartLinks() {
        return smartLinks;
    }

    public void setSmartLinks(Set<SmartLink> smartLinks) {
        this.smartLinks = smartLinks;
    }
}
