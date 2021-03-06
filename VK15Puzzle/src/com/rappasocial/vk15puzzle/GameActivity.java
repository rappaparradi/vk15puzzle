package com.rappasocial.vk15puzzle;


import cz.destil.sliderpuzzle.ui.GameBoardView;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ProgressDialog;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.google.ads.AdRequest;
import com.google.ads.AdView;



import com.perm.kate.api.sample.ImageLoader;
import com.rappasocial.vk15puzzle.R;

/**
 * 
 * Main activity where the game is played.
 * 
 * @author David Vavra
 * 
 */



public class GameActivity extends SherlockActivity implements OnClickListener {

	

	private GameBoardView gameBoard;
	
	ExtendedApplication extApp;
	Thread timerThread;
	long secondCounter,hourCounter, minuteCounter;
	boolean continueThread;
	TextView secTextView, minTextView, hourTextView, tvKolDviz;
	int KolDvizCounter;
	Date dateStart;
	Button btRefreshGame, btShowSuccess;
	private ShareActionProvider mShareActionProvider;
	public static final String ACTION_MOVE_DONE = "com.rappasocial.vk15puzzle.ACTION_MOVE_DONE";
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			countMovesIncr();

		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_activity);
		extApp = (ExtendedApplication) getApplicationContext();
//		randomizeFriendNum();
		
		initgameBoard(true);
		
		Toast.makeText(
				this,
				extApp.arFriends.get(extApp.frNumber).first_name + " "
						+ extApp.arFriends.get(extApp.frNumber).last_name,
				Toast.LENGTH_LONG).show();
		
		secTextView = (TextView) findViewById(R.id.secTextView);
		minTextView = (TextView) findViewById(R.id.minTextView);
		hourTextView = (TextView) findViewById(R.id.hourTextView);
		tvKolDviz = (TextView) findViewById(R.id.tvKolDviz);
		btRefreshGame = (Button) findViewById(R.id.btRefreshGame);
		btRefreshGame.setOnClickListener(this);
//		btShowSuccess = (Button) findViewById(R.id.btShowSuccess);
//		btShowSuccess.setOnClickListener(this);
		
		dateStart = new Date(System.currentTimeMillis());
		continueThread = true;
		timeUpdate();
		KolDvizCounter = 0;
		tvKolDviz.setText(String.valueOf(KolDvizCounter));
		
		
		
		AdRequest adRequest = new AdRequest();

		// test mode on DEVICE (this example code must be replaced with your
		// device uniquq ID)
		adRequest.addTestDevice(Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID));

		AdView adView = (AdView) findViewById(R.id.ad);

		// Initiate a request to load an ad in test mode.
		// You can keep this even when you release your app on the market,
		// because
		// only emulators and your test device will get test ads. The user will
		// receive real ads.
		adView.loadAd(adRequest);
		
	}
	
	private Runnable getFriendNameDat = new Runnable() {
	    public void run() {
	        backgroundGetFriendNameDat();
	    }
	};
	// ??????????, ?????????????? ?????????????????? ??????????-???? ???????????????? ?? ?????????????? ????????????.
	private void backgroundGetFriendNameDat() {

		 extApp.uids.clear();
		 extApp.uids.add(extApp.arFriends.get(extApp.frNumber).uid);
		 try {
			 extApp.friend_name_dat = extApp.api.getProfiles(extApp.uids, null, "first_name", "dat", null, null).get(0).first_name;
		 } catch (Exception e) {
				e.printStackTrace();
			}

	}
	
	public void randomizeFriendNum() {
		
		Random random = new Random();
		extApp.frNumber = showRandomInteger(0, extApp.arFriends.size() - 1,
				random);
		while (extApp.arFriends.get(extApp.frNumber).photo_max_orig == null) {
			
			extApp.frNumber = showRandomInteger(0, extApp.arFriends.size() - 1,
					random);

		}

		Toast.makeText(
				this,
				extApp.arFriends.get(extApp.frNumber).first_name + " "
						+ extApp.arFriends.get(extApp.frNumber).last_name,
				Toast.LENGTH_LONG).show();
		Thread thread = new Thread(null, getFriendNameDat, "Background");
		thread.start();

	}

	public void initgameBoard(boolean fromOnCreate)
	{
		 
//		 initializeDialog();
		 if (!fromOnCreate){
		  randomizeFriendNum();	
		  initializeDialog();
		 AsyncTask<Void, Void, Void> task = new LoadImgTask(this);
     task.execute((Void[])null);}
     while (extApp.original == null){}
     try {
			gameBoard = (GameBoardView) findViewById(R.id.gameboard);
			
			gameBoard.PutURL(extApp.arFriends.get(extApp.frNumber).photo_max_orig,
					GameActivity.this);
			gameBoard.setTileOrder(null);
			// use preserved tile locations when orientation changed
			@SuppressWarnings({ "deprecation", "unchecked" })
			final LinkedList<Integer> tileOrder = (LinkedList<Integer>) getLastNonConfigurationInstance();
			if (tileOrder != null) {
				gameBoard.setTileOrder(tileOrder);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
    
	public ProgressDialog pd;
	public void initializeDialog() {
                  pd = ProgressDialog.show(GameActivity.this, "", "????????????????...");

         }
	
	public void dismissDialog() {
		pd.dismiss();
}
	 public class LoadImgTask extends AsyncTask<Void, Void, Void> {
         private ProgressDialog pd;
         private Context context;
         
         public  LoadImgTask(Context ctx){
        	 
        	 this.context = ctx;
        	 
         }
//         
//         @Override
//         protected void onPreExecute() {
//        	 ((GameActivity)this.context).initializeDialog();
//
//         }
         @Override
         protected Void doInBackground(Void... arg0) {
        	 
        	
        	 
//        	 ImageLoader imageLoader = new ImageLoader(this.context);
//        	 extApp.original = imageLoader.getBitmap(extApp.arFriends.get(extApp.frNumber).photo_max_orig);
        	 extApp.original = getBitmapFromURL(extApp.arFriends.get(extApp.frNumber).photo_max_orig);   
        	
                
                 return null;
          }
          @Override
          protected void onPostExecute(Void result) {
        	  ((GameActivity)this.context).dismissDialog();
                  
          }
 };
	
	private static int showRandomInteger(int aStart, int aEnd, Random aRandom){
	    if ( aStart > aEnd ) {
	      throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	    //get the range, casting to long to avoid overflow problems
	    long range = (long)aEnd - (long)aStart + 1;
	    // compute a fraction of the range, 0 <= frac < range
	    long fraction = (long)(range * aRandom.nextDouble());
	    int randomNumber =  (int)(fraction + aStart);    
	    return randomNumber;
	  }
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_MOVE_DONE);

		this.registerReceiver(this.receiver, filter);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		this.unregisterReceiver(this.receiver);
	}

	void countMovesIncr(){
		
		KolDvizCounter++;
		tvKolDviz.setText(String.valueOf(KolDvizCounter));
		extApp.result_moves = KolDvizCounter == 0 ? "0": String.valueOf(KolDvizCounter);
		
	}
	
	public static Bitmap getBitmapFromURL(String src) {
	    try {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.settings_menu, menu);
		// Fetch and store ShareActionProvider
		MenuItem item = menu.findItem(R.id.menu_item_share);
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();

		return true;

		// menu.add("Settings")
		// .setIcon(R.drawable.cogs)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		//
		// menu.add("Search")
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM |
		// MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		//
		// menu.add("Share")
		// .setIcon(R.drawable.share)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		//
		// menu.add("Fave")
		// .setIcon(R.drawable.thumbs_up)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		//
		// menu.add("MenuLLL")
		// .setIcon(R.drawable.menu)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		//
		// return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_item_share:

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharetext));
//			Uri uriToImage = Uri.parse("android.resource://com.rappasocial.eyesbreak/drawable/eyesbreak_shareimage.png");
			sendIntent.setType("text/plain");
//			sendIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
//			sendIntent.setType("image/*");
			setShareIntent(sendIntent);
			return true;

		case R.id.menu_item_settings:

//			Intent intent = new Intent(MainScreen.this, SettingsActivity.class);
//			startActivity(intent);
			return true;
			
		case R.id.menu_item_rate:

			Intent intent = new Intent(Intent.ACTION_VIEW);
		    //Try Google play
		    intent.setData(Uri.parse("market://details?id=com.rappasocial.vk15puzzle"));
		    if (MyStartActivity(intent) == false) {
		        //Market (Google play) app seems not installed, let's try to open a webbrowser
		        intent.setData(Uri.parse("https://play.google.com/store/apps/details?com.rappasocial.vk15puzzle"));
		        if (MyStartActivity(intent) == false) {
		            //Well if this also fails, we have run out of options, inform the user.
		            Toast.makeText(this, getString(R.string.gplaynotfound), Toast.LENGTH_SHORT).show();
		        }
		    }
			return true;
		
		case R.id.menu_item_getpro:

			intent = new Intent(Intent.ACTION_VIEW);
		    //Try Google play
		    intent.setData(Uri.parse("market://details?id=com.rappasocial.vk15puzzlepro"));
		    if (MyStartActivity(intent) == false) {
		        //Market (Google play) app seems not installed, let's try to open a webbrowser
		        intent.setData(Uri.parse("https://play.google.com/store/apps/details?com.rappasocial.vk15puzzlepro"));
		        if (MyStartActivity(intent) == false) {
		            //Well if this also fails, we have run out of options, inform the user.
		            Toast.makeText(this, getString(R.string.gplaynotfound), Toast.LENGTH_SHORT).show();
		        }
		    }
			return true;
			
		case R.id.menu_item_about:

			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle(getString(R.string.about));
			alertDialog.setMessage(getString(R.string.abouttext));
			alertDialog.show();
			return true;
			
			

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private boolean MyStartActivity(Intent aIntent) {
	    try
	    {
	        startActivity(aIntent);
	        return true;
	    }
	    catch (ActivityNotFoundException e)
	    {
	        return false;
	    }
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// preserve state when rotated
		return gameBoard.getTileOrder();
	}
	
	private void setShareIntent(Intent shareIntent) {
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(shareIntent);
		}
	}
	
	public void timeUpdate()
	{
	    timerThread = new Thread(new Runnable() {

	        @Override
	        public void run() {
	            while(continueThread){
	                Date newDate = new Date();
	                if(((newDate.getTime()) - dateStart.getTime()) > 1000){
	                    secondCounter = secondCounter+1;
	                    mHandlerUpdateSec.post(mUpdateSec);
	                    System.out.println("Inside the Theread ..."+secondCounter);
	                    if(secondCounter > 59){
	                        minuteCounter = minuteCounter + 1;
	                        
	                        secondCounter = 0;
	                        if(minuteCounter > 59){
	                            hourCounter = hourCounter + 1;
	                            
	                            minuteCounter = 0;
	                        }
	                    }
	                    
	                    mHandlerUpdateMinute.post(mUpdateMinute);
	                    mHandlerUpdateHour.post(mUpdateHour);
	                    String t_result_time = "";
	                    String thourCounter = String.valueOf(hourCounter);
	        	        if(thourCounter.length() == 1)
	        	        	t_result_time = t_result_time + "0" + thourCounter;
	        	        else
	        	        	t_result_time = t_result_time + "" + thourCounter;
	        	        
	        	        String tminuteCounter = String.valueOf(minuteCounter);
	        	        if(tminuteCounter.length() == 1)
	        	        	t_result_time = t_result_time + ":0" + tminuteCounter;
	        	        else
	        	        	t_result_time = t_result_time + ":" + tminuteCounter;
	        	        
	        	        String tsecondCounter = String.valueOf(secondCounter);
	        	        if(tsecondCounter.length() == 1)
	        	        	t_result_time = t_result_time + ":0" + tsecondCounter;
	        	        else
	        	        	t_result_time = t_result_time + ":" + tsecondCounter;
	                    
	                    extApp.result_time = String.valueOf(t_result_time);
	                    
	                }
	                try{
	                    timerThread.sleep(1000);
	                }catch (Exception e) {
	                    // TODO: handle exception
	                }
	            }
	        }
	    });
	    timerThread.start();
	}
	
	final Handler mHandlerUpdateSec = new Handler();
	final Runnable mUpdateSec = new Runnable() {
	    public void run() {
	        String temp = "" + secondCounter;
	        System.out.println("Temp second counter length: " + temp.length());
	        if(temp.length() == 1)
	            secTextView.setText("0" + secondCounter);
	        else
	            secTextView.setText("" + secondCounter);
	    }
	};
	final Handler mHandlerUpdateMinute = new Handler();
	final Runnable mUpdateMinute= new Runnable() {
	    public void run() {
	        String temp = "" + minuteCounter;
	        System.out.println("Temp second counter length: " + temp.length());
	        if(temp.length() == 1)
	            minTextView.setText("0" + minuteCounter);
	        else
	            minTextView.setText("" + minuteCounter);
	    }
	};
	final Handler mHandlerUpdateHour = new Handler();
	final Runnable mUpdateHour = new Runnable() {
	    public void run() {
	        String temp = "" + hourCounter;
	        System.out.println("Temp second counter length: " + temp.length());
	        if(temp.length() == 1)
	            hourTextView.setText("0" + hourCounter);
	        else
	            hourTextView.setText("" + hourCounter);
	    }
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
//		case R.id.btShowSuccess:
//
//			Bitmap src = extApp.scoreImage; // the original file yourimage.jpg i added in resources
//		    Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
//		    Canvas cs = new Canvas(dest);
//		    cs.drawBitmap(src, 0f, 0f, null);
//
//		    extApp.scoreImage = dest;
//		    Intent intent = new Intent(this,
//		    		ScoresDialog.class);
//			
//		    startActivity(intent);
//			
//			
//			break;
		
		case R.id.btRefreshGame:

			Animation animRotate = AnimationUtils.loadAnimation(this,
					R.anim.anim_rotate);
			v.startAnimation(animRotate);
			
			
//			    	  randomizeFriendNum();
						
						gameBoard.boardCreated = false;
						extApp.original = null;
						initgameBoard(false);
						
						dateStart = new Date(System.currentTimeMillis());
						secondCounter = 0; 
						hourCounter = 0;
						minuteCounter = 0;
						KolDvizCounter = 0;
						tvKolDviz.setText(String.valueOf(KolDvizCounter));
			      
		
			
//			Thread t = new Thread(new Runnable() {
//			      public void run() {
//			    	  
//			    	  randomizeFriendNum();
//						
//						gameBoard.boardCreated = false;
//						initgameBoard();
//						
//						dateStart = new Date(System.currentTimeMillis());
//						secondCounter = 0; 
//						hourCounter = 0;
//						minuteCounter = 0;
//						KolDvizCounter = 0;
//						tvKolDviz.setText(String.valueOf(KolDvizCounter));
//			      
//			      }
//			    });
//			    t.start();
			 

			
			
			
			break;

		

		}

	
	}
	
}