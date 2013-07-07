package com.rappasocial.vk15puzzle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class ScoresDialog extends Activity {
	
	ImageView scoreImage;
	 ExtendedApplication extApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_dialog);
		scoreImage = (ImageView) findViewById(R.id.scoreImage);
		extApp = (ExtendedApplication) getApplicationContext();
		
		scoreImage.setImageBitmap(extApp.scoreImage);
		
	}

}
