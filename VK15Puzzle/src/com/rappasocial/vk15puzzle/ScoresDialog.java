package com.rappasocial.vk15puzzle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ScoresDialog extends Activity implements OnClickListener {
	
	ImageView scoreImage;
	 ExtendedApplication extApp;
	 LinearLayout llRoot;
	 Button btGetScreen;
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_dialog);
		scoreImage = (ImageView) findViewById(R.id.scoreImage);
		extApp = (ExtendedApplication) getApplicationContext();
		llRoot = (LinearLayout) findViewById(R.id.llRoot);
		scoreImage.setImageBitmap(extApp.scoreImage);
		btGetScreen = (Button) findViewById(R.id.btGetScreen);
		btGetScreen.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
switch (v.getId()) {
		
		case R.id.btGetScreen:

			View v1 = llRoot.getRootView();
			v1.setDrawingCacheEnabled(true);
            Bitmap b = v1.getDrawingCache();             
            String extr = Environment.getExternalStorageDirectory().toString();
            File myPath = new File(extr, "ffff1.jpg");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(myPath);
                b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                MediaStore.Images.Media.insertImage(getContentResolver(), b, "Screen", "screen");
            }catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
			
			break;
		
		case R.id.btRefreshGame:


			
			
			
			break;

		

		}
		
	}

}
