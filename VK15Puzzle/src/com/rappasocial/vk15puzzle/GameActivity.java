package com.rappasocial.vk15puzzle;


import cz.destil.sliderpuzzle.ui.GameBoardView;

import java.util.Date;
import java.util.LinkedList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

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
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_MOVE_DONE);

		this.registerReceiver(this.receiver, filter);
	}

	void countMovesIncr(){
		
		KolDvizCounter++;
		tvKolDviz.setText(String.valueOf(KolDvizCounter));
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getSupportMenuInflater();
//		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		if (item.getItemId() == R.id.new_game) {
//		
//			gameBoard.setTileOrder(null);
//			gameBoard.fillTiles();
//			return true; }
//		else if(item.getItemId() ==R.id.about) {
//			startActivity(new Intent(this, AboutActivity.class));
//			return true;}
//		else{
			return super.onOptionsItemSelected(item);
//		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// preserve state when rotated
		return gameBoard.getTileOrder();
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