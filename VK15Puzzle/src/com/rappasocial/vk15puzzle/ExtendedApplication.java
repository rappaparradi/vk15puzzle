package com.rappasocial.vk15puzzle;


import java.util.ArrayList;
import java.util.Collection;

import com.perm.kate.api.Api;
import com.perm.kate.api.User;
import com.perm.kate.api.sample.Account;

import android.app.Application;
import android.graphics.Bitmap;

public class ExtendedApplication extends Application {

	
	
	
	public ArrayList<User> arFriends;
	public int frNumber;
	public Bitmap scoreImage, original;
	public String result_time;
	public String result_moves;
	public Account account = new Account();
	public Api api;
	public String friend_name_dat;
	public int level;
	public String url;
	Collection<Long> uids = new ArrayList<Long>();
	
	
	@Override
    public void onCreate() {
       
        
        super.onCreate();
   
        arFriends = new ArrayList<User>();
      

        
    }
 
	
	

}
