package com.lps.lpsapp.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lps.lpsapp.R;
import com.lps.lpsapp.services.IBeaconServiceListener;
import com.lps.lpsapp.services.InDoorPositionService;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

public class MeasurementActivity extends BaseActivity {
    private final ReentrantLock lock = new ReentrantLock();
    String beaconId2;
    boolean mBound = false;
    IBeaconServiceListener listener;
    InDoorPositionService mService;
    int mTxPower;
    List<Integer> mesuaredRssi;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            InDoorPositionService.LocalBinder binder = (InDoorPositionService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.setBeaconServiceListener(listener);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public MeasurementActivity() {
        this.mesuaredRssi = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        mProgressBar.setVisibility(View.GONE);
        listener = new IBeaconServiceListener() {
            @Override
            public void beaconsInRange(Collection<Beacon> beacons, Region region) {
                lock.lock();
                try {
                    for (Beacon beacon : beacons) {
                        if (beacon.getId3().toString().equals(beaconId2)) {
                            mTxPower = beacon.getTxPower();
                            mesuaredRssi.add(beacon.getRssi());
                            String message = "The beacon " + beacon.getId3().toString() + " Rssi " + beacon.getRssi() + " Power " + beacon.getTxPower() + "\nDistance " + beacon.getDistance();
                            logToDisplay(message);

                        }
                    }
                } finally {
                    lock.unlock();
                }

            }


        };


        Button mStartButton = (Button) findViewById(R.id.btnStartMessungen);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        lock.lock();
                        try {
                            submitMesurement();
                        } finally {
                            lock.unlock();
                        }
                    }
                }, 120 * 1000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStart() {
        super.onStart();
        beaconId2 = (String) getIntent().getExtras().get("id");
        Intent intent = new Intent(this, InDoorPositionService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            mService.removeBeaconServiceListener(listener);
            unbindService(mConnection);
            mService = null;
            mBound = false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                EditText editText = (EditText) MeasurementActivity.this.findViewById(R.id.rangingText);
                editText.setText(line + "\n");
            }
        });
    }

    private void submitMesurement() {
        final Button mStartButton = (Button) findViewById(R.id.btnStartMessungen);
        EditText tbDistance = (EditText) findViewById(R.id.txtDistance);
        double distance = Double.parseDouble(tbDistance.getText().toString());
        double sumRssi = 0.0;
        for (int rssi : this.mesuaredRssi) {
            sumRssi += rssi;
        }
        ;
        mService.submitMeasurement(distance, mTxPower, sumRssi / this.mesuaredRssi.size());
        this.mesuaredRssi.clear();
        this.runOnUiThread(new Runnable() {
            public void run() {
                mStartButton.setEnabled(true);
            }
        });


    }
}
