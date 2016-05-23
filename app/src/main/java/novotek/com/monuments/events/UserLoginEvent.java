package novotek.com.monuments.events;

import novotek.com.monuments.model.MonumentUser;

/**
 * Created by BX on 5/23/2016.
 */
public class UserLoginEvent {
    public MonumentUser user;
    public boolean successLogin;
    public  UserLoginEvent(MonumentUser user, boolean success){
        this.user = user;
        this.successLogin = success;
    }
}
