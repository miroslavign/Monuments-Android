/*
 * UserLoginEvent.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */

package novotek.com.monuments.events;

import novotek.com.monuments.model.MonumentUser;

public class UserLoginEvent {
    public MonumentUser user;
    public boolean successLogin;
    public  UserLoginEvent(MonumentUser user, boolean success){
        this.user = user;
        this.successLogin = success;
    }
}
