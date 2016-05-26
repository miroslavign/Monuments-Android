/*
 * Monument.java
 * Monuments-Android
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */

package novotek.com.monuments.model;

import android.net.Uri;


public class Monument {
    String uuid;
    String name;
    String description;
    MonumentType type;
    Uri imageUri;
    MonumentUser userAdded;
    long updated;

    public Monument() {

    }
    public Monument(String uuid, String name, String description, Uri imageUri) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.imageUri = imageUri;
    }

    public String getUuid() {
        return uuid;
    }

    public void setuuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName(){
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setMonumentType(MonumentType type) {
        this.type = type;
    }

    public Long getMonumentType(){
        if (type != null)
            return  type.id;
        else
            return null;
    }

    public String getMonumentStringType(){
        if (type != null) {
            return type.typeName;
        }
        return "";
    }

    public void setUser(MonumentUser user) {
        this.userAdded = user;
    }

    public String getUserNameAdded() {
        if (userAdded != null)
            return userAdded.getUserName();
        else
            return "";
    }

    public Long getUserAddedId() {
        if (userAdded != null)
            return userAdded.getUserId();
        else
            return null;
    }

    public Uri getMonumentUri() {
        return imageUri;
    }

    public void setUpdated(long updatedTime) {
        this.updated = updatedTime;
    }
}
