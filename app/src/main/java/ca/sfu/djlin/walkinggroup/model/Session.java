package ca.sfu.djlin.walkinggroup.model;

import android.content.Context;

import ca.sfu.djlin.walkinggroup.app.LoginActivity;
import ca.sfu.djlin.walkinggroup.app.SignupActivity;
import ca.sfu.djlin.walkinggroup.app.WelcomeActivity;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;

public class Session {
    User user;
    WGServerProxy proxy;

    //singleton instance
    private static Session session;
    private Session(){
        //private to prevent public instantiating
    }

    public static Session getSession(Context context){
        if(session==null){
            session=new Session();
        }
        session= SignupActivity.sendUser(context, session);
        if(session==null){
            session= WelcomeActivity.sendUser(context, session);
        }
        return session;
    }



    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setProxy(WGServerProxy proxy) {
        this.proxy = proxy;
    }

    public WGServerProxy getProxy() {
        return proxy;
    }
}
