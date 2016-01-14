package com.lps.lpsapp.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lps.lpsapp.R;
import com.lps.lpsapp.services.AltBeaconService;
import com.lps.lpsapp.services.IBeaconServiceListener;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class BeaconListActivity extends BaseActivity {
    ListView listView;
    MyArrayAdapter adapter;
    ArrayList<Beacon> listItems=new ArrayList<Beacon>();
    boolean mBound = false;
    IBeaconServiceListener listener;
    AltBeaconService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_list);

        listView = (ListView)this.findViewById(R.id.beaconListView);
        adapter=new MyArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Beacon b = (Beacon) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getApplicationContext(), MeasurementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", b.getId3().toString());
                startActivity(intent);
            }
        });

        listener = new IBeaconServiceListener() {
            @Override
            public void beaconsInRange(Collection<Beacon> beacons) {
                fillList(beacons);
            }
            @Override
            public void deviceInLocale(UUID localeId, boolean isInLocale) {

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, AltBeaconService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mService.removeBeaconServiceListener(listener);
            mService = null;
            mBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;

        }
    };

    private void fillList(final Collection<Beacon> beacon) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                adapter.clear();

                Beacon[] array = beacon.toArray(new Beacon[beacon.size()]);
                Arrays.sort(array, new Comparator<Beacon>() {
                    @Override
                    public int compare(Beacon lhs, Beacon rhs) {
                        return lhs.getId3().compareTo(rhs.getId3());
                    }
                });

                for(Beacon b:array)
                {
                    adapter.add(b);

                }
                mProgressBar.setVisibility(View.GONE);
            }
        });

    }

    private class MyArrayAdapter extends ArrayAdapter
    {
        public MyArrayAdapter(Context context, int resource, List<Beacon> objects) {
            super(context, resource,objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView v = (TextView)super.getView(position, convertView, parent);
            Beacon beacon =  (Beacon)this.getItem(position);
            //double  avaregeRssi = new BeaconAccessor(beacon).getRunningAverageRssi();
            String txt = beacon.getId3().toString() + "\nDistance " + beacon.getDistance() + "\nP " + beacon.getTxPower() + "\nRssi " + beacon.getRssi();
            v.setText(txt);
            return v;
        }

    }
}
