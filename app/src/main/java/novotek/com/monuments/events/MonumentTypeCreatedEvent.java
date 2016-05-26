/*
 * MonumentTypeCreatedEvent.java
 * Monuments-Android
 *
 * Created by Miroslav Ignjatovic on 5/26/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */

package novotek.com.monuments.events;

import novotek.com.monuments.model.MonumentType;

public class MonumentTypeCreatedEvent {
    public MonumentType newType;
    public MonumentTypeCreatedEvent(MonumentType aType) {
        this.newType = aType;
    }
}
