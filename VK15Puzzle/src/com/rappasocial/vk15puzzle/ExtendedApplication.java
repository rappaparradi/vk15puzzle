package com.rappasocial.vk15puzzle;


import java.util.ArrayList;

import com.perm.kate.api.Api;
import com.perm.kate.api.User;
import com.perm.kate.api.sample.Account;

import android.app.Application;
import android.graphics.Bitmap;

public class ExtendedApplication extends Application {

	
	
	
	public ArrayList<User> arFriends;
	public int frNumber;
	public Bitmap scoreImage;
	String result_time;
	String result_moves;
	Account account = new Account();
	Api api;
	String friend_name_dat;
	
	
	@Override
    public void onCreate() {
       
        
        super.onCreate();
   
        arFriends = new ArrayList<User>();
      

        
    }
 
	
	

}
