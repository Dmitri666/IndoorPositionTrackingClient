package com.lps.lpsapp.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.lps.lpsapp.R;
import com.lps.lpsapp.services.AltBeaconService;
import com.lps.lpsapp.services.IBeaconServiceListener;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;
import java.util.UUID;

/**
 * 
 * @author dyoung
 * @author Matt Tyler
 */
public class MenuActivity extends BaseActivity {
	protected static final String TAG = "MenuActivity";
	AltBeaconService mService;
	boolean mBound = false;
	IBeaconServiceListener listener;
	private UUID mLocaleId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"Created");
		setContentView(R.layout.activity_menu);

		this.listener = new IBeaconServiceListener() {
			@Override
			public void beaconsInRange(Collection<Beacon> beacon) {

			}

			@Override
			public void deviceInLocale(final UUID localeId,final boolean isInLocale) {
				MenuActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						View btn =  MenuActivity.this.findViewById(R.id.btnChat);
						btn.setEnabled(isInLocale);
						MenuActivity.this.mLocaleId = localeId;
					}
				});

			}
		};

		this.mProgressBar.setVisibility(View.GONE);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");

	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		if (mBound) {
			mService.removeBeaconServiceListener(listener);
			unbindService(mConnection);
			mService = null;
			mBound = false;
		}
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		// Bind to LocalService
		Intent intent = new Intent(this, AltBeaconService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");

	}

	public void onNewsClicked(View view) {
		Intent myIntent = new Intent(this, LoginActivity.class);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(myIntent);
	}

	public void onChatClicked(View view) {
		Intent myIntent = new Intent(this, ActorsActivity.class);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		myIntent.putExtra("id", this.mLocaleId);
		this.startActivity(myIntent);
	}

	public void onSearchButtonClicked(View view) {
		Intent myIntent = new Intent(this, SearchLocaleActivity.class);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(myIntent);
	}

	public void onFavoritButtonClicked(View view) {
		Intent myIntent = new Intent(this, FavoritsActivity.class);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(myIntent);
	}


	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
									   IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			AltBeaconService.LocalBinder binder = (AltBeaconService.LocalBinder) service;
			mService = binder.getService();
			mBound = true;
			mService.setBeaconServiceListener(listener);
			Log.d(TAG, "onServiceConnected");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
			Log.d(TAG, "onServiceDisconnected");

		}
	};

}
