package com.rappasocial.vk15puzzle;


import cz.destil.sliderpuzzle.ui.GameBoardView;

import java.util.LinkedList;

import android.content.Intent;
import android.os.Bundle;

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
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_activity);
		extApp = (ExtendedApplication) getApplicationContext();
		try {
			gameBoard = (GameBoardView) findViewById(R.id.gameboard);
			gameBoard.PutURL(extApp.arFriends.get(extApp.frNumber).photo_big,
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
}