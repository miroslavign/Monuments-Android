/*
 * UserCreatedEvent.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */


package novotek.com.monuments.events;

import novotek.com.monuments.model.MonumentUser;

public class UserCreatedEvent {
    public MonumentUser user;

    public  UserCreatedEvent(MonumentUser user) {
        this.user = user;
    }
}
