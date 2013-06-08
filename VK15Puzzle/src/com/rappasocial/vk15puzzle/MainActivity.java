package com.rappasocial.vk15puzzle;

import java.util.ArrayList;
import java.util.LinkedList;
import com.perm.kate.api.Api;
import com.perm.kate.api.User;
import com.perm.kate.api.sample.Account;
import com.perm.kate.api.sample.Constants;
import com.perm.kate.api.sample.ImageLoader;
import com.perm.kate.api.sample.LoginActivity;


import cz.destil.sliderpuzzle.ui.GameBoardView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

	private final int REQUEST_LOGIN = 1;

	Button authorizeButton;
	Button logoutButton;
	Button postButton;
	ImageView ivBig;
	Button btStartGame;
	BoxAdapterFriends boxAdapter;
	EditText messageEditText;
	ListView lvFriends;
	Account account = new Account();
	Api api;
	ExtendedApplication extApp;
	private GameBoardView gameBoard;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		extApp = (ExtendedApplication) getApplicationContext();

		setupUI();

		// Восстановление сохранённой сессии
		account.restore(this);

		// Если сессия есть создаём API для обращения к серверу
		if (account.access_token != null) {
			api = new Api(account.access_token, Constants.API_ID);

			showButtons();

			try {

				extApp.arFriends = api.getFriends(account.user_id, null, 20,
						null, null, null);
				// Показать сообщение в UI потоке
				runOnUiThread(successRunnable);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ImageLoader imageLoader = new ImageLoader(MainActivity.this);
			imageLoader.DisplayImage(extApp.arFriends.get(0).photo_max_orig,
					ivBig);

			boxAdapter = new BoxAdapterFriends(extApp.arFriends,
					MainActivity.this);

			lvFriends = (ListView) findViewById(R.id.lvFriends);

			lvFriends.setAdapter(boxAdapter);
		}
		
//		try {
//			gameBoard = (GameBoardView) findViewById(R.id.gameboard);
//			gameBoard.setTileOrder(null);
//			gameBoard.fillTiles(extApp.arFriends.get(0).photo_big,
//					MainActivity.this);
//			// use preserved tile locations when orientation changed
//			@SuppressWarnings({ "deprecation", "unchecked" })
//			final LinkedList<Integer> tileOrder = (LinkedList<Integer>) getLastNonConfigurationInstance();
//			if (tileOrder != null) {
//				gameBoard.setTileOrder(tileOrder);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}

	private void setupUI() {
		authorizeButton = (Button) findViewById(R.id.authorize);
		logoutButton = (Button) findViewById(R.id.logout);
		postButton = (Button) findViewById(R.id.post);
		messageEditText = (EditText) findViewById(R.id.message);
		authorizeButton.setOnClickListener(authorizeClick);
		logoutButton.setOnClickListener(logoutClick);
		postButton.setOnClickListener(postClick);
		btStartGame = (Button) findViewById(R.id.btStartGame);
		btStartGame.setOnClickListener(btStartGameClick);
		ivBig = (ImageView) findViewById(R.id.ivBig);
	}

	private OnClickListener authorizeClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startLoginActivity();
		}
	};
	
	private OnClickListener btStartGameClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startGameActivity();
		}
	};

	private OnClickListener logoutClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			logOut();
		}
	};

	private OnClickListener postClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			postToWall();
		}
	};
	


	private void startLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}
	
	private void startGameActivity() {
		Intent intent = new Intent(MainActivity.this,
				GameActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_LOGIN) {
			if (resultCode == RESULT_OK) {
				// авторизовались успешно
				account.access_token = data.getStringExtra("token");
				account.user_id = data.getLongExtra("user_id", 0);
				account.save(MainActivity.this);
				api = new Api(account.access_token, Constants.API_ID);
				showButtons();
			}
		}
	}

	private void postToWall() {
		// Общение с сервером в отдельном потоке чтобы не блокировать UI поток
		new Thread() {
			@Override
			public void run() {
				try {
					String text = messageEditText.getText().toString();
					api.createWallPost(account.user_id, text, null, null,
							false, false, false, null, null, null, null);
					// Показать сообщение в UI потоке
					runOnUiThread(successRunnable);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	Runnable successRunnable = new Runnable() {
		@Override
		public void run() {
			Toast.makeText(getApplicationContext(), "Запись успешно добавлена",
					Toast.LENGTH_LONG).show();
		}
	};

	private void logOut() {
		api = null;
		account.access_token = null;
		account.user_id = 0;
		account.save(MainActivity.this);
		showButtons();
	}

	void showButtons() {
		if (api != null) {
			authorizeButton.setVisibility(View.GONE);
			logoutButton.setVisibility(View.VISIBLE);
			postButton.setVisibility(View.VISIBLE);
			messageEditText.setVisibility(View.VISIBLE);
		} else {
			authorizeButton.setVisibility(View.VISIBLE);
			logoutButton.setVisibility(View.GONE);
			postButton.setVisibility(View.GONE);
			messageEditText.setVisibility(View.GONE);
		}
	}

	private class ViewHolder {
		public ImageView ivFriendAva;
		public TextView tvFriendName;
		public TextView tvIsOnline;
		public int position;

	}

	public class BoxAdapterFriends extends ArrayAdapter<User> implements
			OnClickListener, OnItemClickListener {
		Context ctx;
		LayoutInflater lInflater;
		ArrayList<User> objects;
		public ImageLoader imageLoader;
		// private LayoutInflater inflater = null;

		// ExtendedApplication extApp;
		LinearLayout llRoutineEditBG, llRoutineEditClickAble;

		public BoxAdapterFriends(ArrayList<User> objects, Context ctx) {
			super(MainActivity.this, R.layout.friend_list_item,
					R.id.tvFriendName, objects);
			// extApp = (ExtendedApplication) ctx.getApplicationContext();
			// inflater =
			// (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			imageLoader = new ImageLoader(ctx);
			this.ctx = ctx;
		}

		// BoxAdapterRoutine(Context context, ArrayList<Routine> routines) {
		// ctx = context;
		// objects = routines;
		// lInflater = (LayoutInflater) ctx
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// extApp = (ExtendedApplication)ctx.getApplicationContext();
		// }

		// public int getCount() {
		// return objects.size();
		// }
		//
		//
		// public Routine getItem(int position) {
		// return objects.get(position);
		// }
		//
		//
		// public long getItemId(int position) {
		// return position;
		// }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// View view = convertView;
			// if (view == null) {
			// view = lInflater.inflate(R.layout.routine_list_item, parent,
			// false);
			// }
			//
			// Routine p = getRoutine(position);
			//
			//
			// ((TextView)
			// view.findViewById(R.id.tvRoutineName)).setText(p.name);
			// Button btEditRoutine = (Button)
			// view.findViewById(R.id.btEditRoutine);
			// btEditRoutine.setTag(position);
			// btEditRoutine.setOnClickListener(this);
			// // ((Button)
			// view.findViewById(R.id.btEditRoutine)).setOnClickListener(this);
			// // btEditRoutine.setOnClickListener(this);
			//
			// return view;

			View v = super.getView(position, convertView, parent);

			if (v != convertView && v != null) {
				ViewHolder holder = new ViewHolder();

				ImageView ivFriendAva = (ImageView) v
						.findViewById(R.id.ivFriendAva);
				TextView tvFriendName = (TextView) v
						.findViewById(R.id.tvFriendName);
				TextView tvIsOnline = (TextView) v
						.findViewById(R.id.tvIsOnline);

				holder.ivFriendAva = ivFriendAva;

				holder.tvFriendName = tvFriendName;
				holder.tvIsOnline = tvIsOnline;

				v.setTag(holder);
			}

			//

			ViewHolder holder = (ViewHolder) v.getTag();

			imageLoader.DisplayImage(getItem(position).photo_medium,
					holder.ivFriendAva);

			holder.tvFriendName.setText(getItem(position).first_name + " "
					+ getItem(position).last_name);
			holder.tvIsOnline.setText(String.valueOf(getItem(position).online));

			v.setOnClickListener(this);

			holder.position = position;

			return v;

		}

		Drawable drawable_from_url(String url, String src_name)
				throws java.net.MalformedURLException, java.io.IOException {
			return Drawable.createFromStream(
					((java.io.InputStream) new java.net.URL(url).getContent()),
					src_name);
		}

		User getRoutine(int position) {
			return getItem(position);
		}

		@Override
		public void onClick(View v) {

//			switch (v.getId()) {
//
//			case R.id.btEditRoutine:
//			//
//			break;
//
//			}
			extApp.frNumber = ((ViewHolder) v.getTag()).position;

		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

		}

	}
}