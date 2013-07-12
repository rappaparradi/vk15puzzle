package com.rappasocial.vk15puzzle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.holoeverywhere.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import com.perm.kate.api.Photo;

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
import android.widget.TextView;

public class ScoresDialog extends Activity implements OnClickListener {

	ImageView scoreImage;
	TextView tvTimeRes, tvMovesRes; 
	ExtendedApplication extApp;
	LinearLayout llRoot;
	Button btPostFriend, btPostSelf;
	String upload_url_owner;
	String post_result;
	JSONObject json;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_dialog);
		scoreImage = (ImageView) findViewById(R.id.scoreImage);
		extApp = (ExtendedApplication) getApplicationContext();
		llRoot = (LinearLayout) findViewById(R.id.llRoot);
		scoreImage.setImageBitmap(extApp.scoreImage);
		btPostFriend = (Button) findViewById(R.id.btPostFriend);
		btPostFriend.setOnClickListener(this);
		
		btPostSelf = (Button) findViewById(R.id.btPostSelf);
		btPostSelf.setOnClickListener(this);
		
		tvTimeRes = (TextView) findViewById(R.id.tvTimeRes);
		tvMovesRes = (TextView) findViewById(R.id.tvMovesRes);
		
		
		extApp = (ExtendedApplication) getApplicationContext();
		btPostFriend.setText("На стену " + extApp.friend_name_dat);
		
		tvTimeRes.setText(extApp.result_time);
		tvMovesRes.setText(extApp.result_moves);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btPostSelf:

			
			Thread threadPostSelf = new Thread(null, postToWallSelf,
		            "postToWallSelf");
			threadPostSelf.start();
			

			break;

		case R.id.btPostFriend:
			
			Thread threadPostFriend = new Thread(null, postToWallFriend,
		            "postToWallFriend");
			threadPostFriend.start();

			break;

		}

	}
	
	private Runnable postToWallSelf = new Runnable() {
	    public void run() {
	        backgroundpostToWallSelf();
	    }
	};
	// Метод, который выполняет какие-то действия в фоновом режиме.
	private void backgroundpostToWallSelf() {

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
			MediaStore.Images.Media.insertImage(getContentResolver(), b,
					"Screen", "screen");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			upload_url_owner = extApp.api.photosGetWallUploadServer(
					extApp.account.user_id, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Bundle params = new Bundle();

		int size = (int) myPath.length();
		byte[] bytes = new byte[size];
		try {
			BufferedInputStream buf = new BufferedInputStream(
					new FileInputStream(myPath));
			buf.read(bytes, 0, bytes.length);
			buf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		params.putByteArray("photo", bytes);

		try {
			post_result = openUrl(upload_url_owner, "POST", params);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<Photo> res_saveWallPhoto;
		long res_createWallPost;
		 try {
             //создали читателя json объектов и отдали ему строку - result
             json = new JSONObject(post_result);
             //дальше находим вход в наш json им является ключевое слово data
  
             res_saveWallPhoto = extApp.api.saveWallPhoto(json.get("server").toString(), json.get("photo").toString(), json.get("hash").toString(), extApp.account.user_id, null);
           
	
			 
			 Collection<String> photosColl = new ArrayList<String>();
			 photosColl.add("photo" + String.valueOf(extApp.account.user_id) + "_" + String.valueOf(res_saveWallPhoto.get(0).pid));
			 
		 res_createWallPost = extApp.api.createWallPost(extApp.account.user_id, "Мой результат:", photosColl, null,
					false, false, false, null, null, null, null);
		 Toast.makeText(this, String.valueOf(res_createWallPost), Toast.LENGTH_SHORT).show();
		 } catch (Exception e) {
				e.printStackTrace();
			}

	}
	
	private Runnable postToWallFriend = new Runnable() {
	    public void run() {
	    	backgroundpostToWallFriend();
	    }
	};
	// Метод, который выполняет какие-то действия в фоновом режиме.
	private void backgroundpostToWallFriend() {

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
			MediaStore.Images.Media.insertImage(getContentResolver(), b,
					"Screen", "screen");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			upload_url_owner = extApp.api.photosGetWallUploadServer(
					extApp.arFriends.get(extApp.frNumber).uid, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Bundle params = new Bundle();

		int size = (int) myPath.length();
		byte[] bytes = new byte[size];
		try {
			BufferedInputStream buf = new BufferedInputStream(
					new FileInputStream(myPath));
			buf.read(bytes, 0, bytes.length);
			buf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		params.putByteArray("photo", bytes);

		try {
			post_result = openUrl(upload_url_owner, "POST", params);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<Photo> res_saveWallPhoto;
		long res_createWallPost;
		 try {
             //создали читателя json объектов и отдали ему строку - result
             json = new JSONObject(post_result);
             //дальше находим вход в наш json им является ключевое слово data
  
             res_saveWallPhoto = extApp.api.saveWallPhoto(json.get("server").toString(), json.get("photo").toString(), json.get("hash").toString(), extApp.account.user_id, null);
           
	
			 
			 Collection<String> photosColl = new ArrayList<String>();
			 photosColl.add("photo" + String.valueOf(extApp.account.user_id) + "_" + String.valueOf(res_saveWallPhoto.get(0).pid));
			 
		 res_createWallPost = extApp.api.createWallPost(extApp.arFriends.get(extApp.frNumber).uid, "", photosColl, null,
					false, false, false, null, null, null, null);
		 Toast.makeText(this, String.valueOf(res_createWallPost), Toast.LENGTH_SHORT).show();
		 } catch (Exception e) {
				e.printStackTrace();
			}

	}

	public static String openUrl(String url, String method, Bundle params)
			throws MalformedURLException, IOException {
		String boundary = "Asrf456BGe4h";
		String endLine = "\r\n";
		String twoHyphens = "--";

		OutputStream os;
		HttpURLConnection connection = (HttpURLConnection) new URL(url)
				.openConnection();

		if (!method.equals("GET")) {
			Bundle dataparams = new Bundle();
			for (String key : params.keySet()) {
				Object parameter = params.get(key);
				if (parameter instanceof byte[]) {
					dataparams.putByteArray(key, (byte[]) parameter);
				}
			}
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + boundary);
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.connect();

			os = new BufferedOutputStream(connection.getOutputStream());
			os.write((twoHyphens + boundary + endLine).getBytes());

			if (!dataparams.isEmpty()) {
				for (String key : dataparams.keySet()) {
					os.write(("Content-Disposition: form-data; name=\"photo\"; filename=\"filename\"")
							.getBytes());
					os.write(("Content-Type: image/jpeg" + endLine + endLine)
							.getBytes());
					os.write(dataparams.getByteArray(key));
					os.write((endLine + twoHyphens + boundary + twoHyphens + endLine)
							.getBytes());
				}
			}
			os.flush();
			// os.close();
		}

		String response = read(connection.getInputStream());
		return response;
	}

	private static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

}
