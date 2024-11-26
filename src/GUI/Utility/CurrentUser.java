package GUI.Utility;

import BE.User;

public class CurrentUser {

    private static final CurrentUser instance = new CurrentUser();
    private User currentuser;

    private CurrentUser(){

    }

    public static CurrentUser getInstance(){
        return instance;
    }

    public void setCurrentuser(User user){
        this.currentuser = user;
    }


    public User getCurrentuser(){
        return this.currentuser;
    }

}
