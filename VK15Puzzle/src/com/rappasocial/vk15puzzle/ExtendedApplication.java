package com.rappasocial.vk15puzzle;


import java.util.ArrayList;

import com.perm.kate.api.User;

import android.app.Application;

public class ExtendedApplication extends Application {

	
	
	
	public ArrayList<User> arFriends;
	public int frNumber;
	
	
	@Override
    public void onCreate() {
       
        
        super.onCreate();
   
        arFriends = new ArrayList<User>();
      

        
    }
 
	
	

}
