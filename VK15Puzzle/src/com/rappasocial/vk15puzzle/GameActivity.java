package com.rappasocial.vk15puzzle;


import cz.destil.sliderpuzzle.ui.GameBoardView;

import java.util.Date;
import java.util.LinkedList;

import org.holoeverywhere.app.AlertDialog;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;


import com.rappasocial.vk15puzzle.R;

/**
 * 
 * Main activity where the game is played.
 * 
 * @author David Vavra
 * 
 */



public class GameActivity extends SherlockActivity {

	

	private GameBoardView gameBoard;
	ExtendedApplication extApp;
	Thread timerThread;
	long secondCounter,hourCounter, minuteCounter;
	boolean continueThread;
	TextView secTextView, minTextView, hourTextView, tvKolDviz;
	int KolDvizCounter;
	Date dateStart;
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
		
		secTextView = (TextView) findViewById(R.id.secTextView);
		minTextView = (TextView) findViewById(R.id.minTextView);
		hourTextView = (TextView) findViewById(R.id.hourTextView);
		tvKolDviz = (TextView) findViewById(R.id.tvKolDviz);
		dateStart = new Date(System.currentTimeMillis());
		continueThread = true;
		timeUpdate();
		KolDvizCounter = 0;
		tvKolDviz.setText(String.valueOf(KolDvizCounter));
		
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

//			intent = new Intent(Intent.ACTION_VIEW);
//		    //Try Google play
//		    intent.setData(Uri.parse("market://details?id=com.rappasocial.eyesbreak"));
//		    if (MyStartActivity(intent) == false) {
//		        //Market (Google play) app seems not installed, let's try to open a webbrowser
//		        intent.setData(Uri.parse("https://play.google.com/store/apps/details?com.rappasocial.eyesbreak"));
//		        if (MyStartActivity(intent) == false) {
//		            //Well if this also fails, we have run out of options, inform the user.
//		            Toast.makeText(this, getString(R.string.gplaynotfound), Toast.LENGTH_SHORT).show();
//		        }
//		    }
			return true;
		
		case R.id.menu_item_getpro:

//			intent = new Intent(Intent.ACTION_VIEW);
//		    //Try Google play
//		    intent.setData(Uri.parse("market://details?id=com.rappasocial.eyesbreakpro"));
//		    if (MyStartActivity(intent) == false) {
//		        //Market (Google play) app seems not installed, let's try to open a webbrowser
//		        intent.setData(Uri.parse("https://play.google.com/store/apps/details?com.rappasocial.eyesbreakpro"));
//		        if (MyStartActivity(intent) == false) {
//		            //Well if this also fails, we have run out of options, inform the user.
//		            Toast.makeText(this, getString(R.string.gplaynotfound), Toast.LENGTH_SHORT).show();
//		        }
//		    }
			return true;
			
		case R.id.menu_item_about:

//			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//			alertDialog.setTitle(getString(R.string.about));
//			alertDialog.setMessage(getString(R.string.abouttext));
//			alertDialog.show();
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
	                        mHandlerUpdateMinute.post(mUpdateMinute);
	                        secondCounter = 0;
	                        if(minuteCounter > 59){
	                            hourCounter = hourCounter + 1;
	                            mHandlerUpdateHour.post(mUpdateHour);
	                            minuteCounter = 0;
	                        }
	                    }
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
	
}