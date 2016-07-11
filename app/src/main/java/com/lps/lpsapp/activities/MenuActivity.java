package com.lps.lpsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.lps.lpsapp.ServiceManager;
import com.lps.lpsapp.R;
import com.lps.lpsapp.network.AppState;
import com.lps.lpsapp.network.IAppStateListener;

/**
 * 
 * @author dyoung
 * @author Matt Tyler
 */
public class MenuActivity extends BaseActivity {
	protected static final String TAG = "MenuActivity";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"Created");
		setContentView(R.layout.activity_menu);
		this.mProgressBar.setVisibility(View.GONE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");


	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		// Bind to LocalService
		ServiceManager.CheckSeviceAvalability(new IAppStateListener() {
			@Override
			public void StateChanged(AppState state) {
				View btn =  MenuActivity.this.findViewById(R.id.btnSearch);
				btn.setEnabled(state.IsAuthenticated);

				View btn1 =  MenuActivity.this.findViewById(R.id.btnNews);
				btn1.setEnabled(state.IsAuthenticated);
			}
		});


		View btn =  MenuActivity.this.findViewById(R.id.btnChat);
		btn.setEnabled(ServiceManager.AppState.LocaleId != null);


	}



	public void onNewsClicked(View view) {
		Intent myIntent = new Intent(this, LoginActivity.class);
		//Intent myIntent = new Intent(this, BeaconListActivity.class);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(myIntent);
	}

	public void onChatClicked(View view) {
		Intent myIntent = new Intent(this, ActorsActivity.class);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(myIntent);
	}

	public void onSearchButtonClicked(View view) {
		Intent myIntent = new Intent(this, SearchLocaleActivity.class);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(myIntent);
	}

	public void onFavoritButtonClicked(View view) {
		Intent myIntent = new Intent(this, BeaconListActivity.class);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(myIntent);
	}




}
