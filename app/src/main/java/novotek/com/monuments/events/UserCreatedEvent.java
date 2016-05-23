package novotek.com.monuments.events;

import novotek.com.monuments.model.MonumentUser;

/**
 * Created by BX on 5/23/2016.
 */
public class UserCreatedEvent {
    public MonumentUser user;

    public  UserCreatedEvent(MonumentUser user) {
        this.user = user;
    }
}
