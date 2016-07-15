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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lps.lpsapp.R;
import com.lps.lpsapp.services.IBeaconServiceListener;
import com.lps.lpsapp.services.InDoorPositionService;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.lpsapp.viewModel.BeaconData;
import com.lps.webapi.services.WebApiService;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class BeaconListActivity extends BaseActivity {
    ListView listView;
    MyArrayAdapter adapter;
    ArrayList<Beacon> listItems = new ArrayList<Beacon>();
    boolean mBound = false;
    IBeaconServiceListener listener;
    InDoorPositionService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView)this.findViewById(R.id.beaconListView);
        adapter=new MyArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                listItems);
        listView.setAdapter(adapter);

       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Beacon b = (Beacon) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getApplicationContext(), MeasurementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", b.getId3().toString());
                startActivity(intent);
            }
        });*/

        listener = new IBeaconServiceListener() {
            @Override
            public void beaconsInRange(Collection<Beacon> beacons,Region region) {
                if(region.getUniqueId().equals(mService.backgroundRegion.getUniqueId())) {
                    fillList(beacons);
                }
            }

        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, InDoorPositionService.class);
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
            mService.monitoreBackgroundRegion(false);
            mService = null;
            mBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_beacon_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                List<BeaconData> result = new ArrayList<>();
                for(Beacon beacon:this.listItems)
                {
                    BeaconData data = new BeaconData();
                    data.id1 = beacon.getId1().toUuid();
                    data.id2 = beacon.getId2().toInt();
                    data.id3 = beacon.getId3().toInt();
                    result.add(data);
                }

                if(result.size() > 0)
                {
                    WebApiService service = new WebApiService(BeaconData.class,false);
                    service.performPost(WebApiActions.SaveBackgroundBeacons(), result);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            InDoorPositionService.LocalBinder binder = (InDoorPositionService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.setBeaconServiceListener(listener);
            mService.monitoreBackgroundRegion(true);
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
            String txt = beacon.getId3().toString() + "\nDistance " + beacon.getDistance() +
                    "\nP " + beacon.getTxPower() + "\nRssi " + beacon.getRssi() + "\nId1 " + beacon.getId1() +
                    "\nId2 " + beacon.getId2() + "\nId3 " + beacon.getId3();
            v.setText(txt);
            return v;
        }

    }
}
