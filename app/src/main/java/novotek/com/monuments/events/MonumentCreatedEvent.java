/*
 * MonumentCreatedEvent.java
 * Monuments-Android
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */


package novotek.com.monuments.events;

import novotek.com.monuments.model.Monument;

public class MonumentCreatedEvent {
    public Monument monument;
    public MonumentCreatedEvent(Monument monument) {
        this.monument = monument;
    }
}
