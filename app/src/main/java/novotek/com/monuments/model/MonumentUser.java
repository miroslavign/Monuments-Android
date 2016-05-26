/*
 * MonumentUser.java
 * Monuments-Android
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */

package novotek.com.monuments.model;

public class MonumentUser {
    Long userId;
    private String userName;
    private String email;
    private String password;

    public MonumentUser() {
    }

    public MonumentUser(String name, String email, String pass) {
        this.userName = name;
        this.email = email;
        this.password = pass;
    }

    public void setUserId(long id) {
        this.userId = id;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserName(String uName) {
        this.userName = uName;
    }
    public String getUserName() {
        return this.userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return this.email;
    }

    public void setPassword(String pass) {
        this.password = pass;
    }
    public String getPassword() {
        return this.password;
    }
}
