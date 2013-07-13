package com.rappasocial.vk15puzzle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LevelActivity extends Activity implements OnClickListener {

	Button btLevelEasy, btLevelMedium, btLevelHard;
	ExtendedApplication extApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.level_activity);
		btLevelEasy = (Button) findViewById(R.id.btLevelEasy);
		btLevelEasy.setOnClickListener(this);
		btLevelMedium = (Button) findViewById(R.id.btLevelMedium);
		btLevelMedium.setOnClickListener(this);
		btLevelHard = (Button) findViewById(R.id.btLevelHard);
		btLevelHard.setOnClickListener(this);
		extApp = (ExtendedApplication) getApplicationContext();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.btLevelEasy:

			extApp.level = 3;

			Intent intent = new Intent(this, GameActivity.class);
			startActivity(intent);

			break;

		case R.id.btLevelMedium:

			extApp.level = 4;

			intent = new Intent(this, GameActivity.class);
			startActivity(intent);

			break;
		case R.id.btLevelHard:

			extApp.level = 5;

			intent = new Intent(this, GameActivity.class);
			startActivity(intent);

			break;

		}

	}

}
