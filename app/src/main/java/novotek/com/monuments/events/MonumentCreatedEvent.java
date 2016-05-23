package novotek.com.monuments.events;

import novotek.com.monuments.model.Monument;

/**
 * Created by BX on 5/23/2016.
 */
public class MonumentCreatedEvent {
    public Monument monument;
    public MonumentCreatedEvent(Monument monument) {
        this.monument = monument;
    }
}
