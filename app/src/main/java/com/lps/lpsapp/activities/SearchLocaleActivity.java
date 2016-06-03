package com.lps.lpsapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.lps.lpsapp.network.ConnectionDetector;
import com.lps.webapi.IWebApiResultListener;
import com.lps.webapi.JsonSerializer;
import com.lps.lpsapp.R;
import com.lps.lpsapp.helper.ComboBoxItem;
import com.lps.lpsapp.helper.SimpleComboBoxAdapter;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.webapi.services.WebApiService;
import com.lps.lpsapp.viewModel.rooms.RequestLocationData;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SearchLocaleActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks ,GoogleApiClient.OnConnectionFailedListener{
    private static String TAG = "SearchLocaleActivity";

    private RequestLocationData mSearchParameters;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_locale);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.mSearchParameters = new RequestLocationData();

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            grantSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient != null) {
            this.mGoogleApiClient.disconnect();
            this.mGoogleApiClient.unregisterConnectionCallbacks(this);
            this.mGoogleApiClient.unregisterConnectionFailedListener(this);
            this.mGoogleApiClient = null;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected Boolean InitialiseActivity() {
        if(new ConnectionDetector(getApplicationContext()).isConnectedToNetwork()) {
            this.loadFilterContent();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_locale, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(!super.onOptionsItemSelected(item)) {
            switch (item.getItemId()) {
                case R.id.search:


                    Spinner spinner = (Spinner)this.findViewById(R.id.spnRadius);
                    String strDistance =  (String)spinner.getSelectedItem();
                    int distance = Integer.parseInt(strDistance);
                    this.mSearchParameters.radius = distance;

                    spinner = (Spinner)this.findViewById(R.id.spnLocaleName);
                    ComboBoxItem name =  (ComboBoxItem)spinner.getSelectedItem();
                    if(!name.equals("All")) {
                        mSearchParameters.locationName = name.text;
                    }

                    spinner = (Spinner)this.findViewById(R.id.spnCity);
                    String city =  (String)spinner.getSelectedItem();
                    mSearchParameters.locationCity = city;
                    if(!city.equals("All")) {
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<Address> adresses = geocoder.getFromLocationName(city, 1);

                            if (!adresses.isEmpty()) {
                                mSearchParameters.latitude = adresses.get(0).getLatitude();
                                mSearchParameters.longitude = adresses.get(0).getLongitude();
                                this.StartResultActivity();
                            }
                        } catch (IOException ex) {
                            Log.e(TAG, ex.getMessage(), ex);
                        }
                    }
                    else
                    {
                        this.GetMyLocation();
                    }

                    return true;
                default:
                    return false;
            }
        }
        return true;
    }

    private void StartResultActivity()
    {
        Intent intent = new Intent(getApplicationContext(), SearchLocaleResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            intent.putExtra("parameters", JsonSerializer.serialize(this.mSearchParameters));
            startActivity(intent);
        }
        catch (IOException ex)
        {
            Log.e(TAG,ex.getMessage(),ex);
        }

    }

    private void GetMyLocation() {
        if(this.mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }

        if(!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }

    }

    private void loadFilterContent()
    {
        WebApiService service = new WebApiService(ComboBoxItem.class,true);
        service.performGetList(WebApiActions.GetLocaleNames(), new IWebApiResultListener<List<ComboBoxItem>>() {
            @Override
            public void onResult(List<ComboBoxItem> items) {
                items.add(0,new ComboBoxItem(new UUID(0L,0L),"All"));

                Spinner spinner = (Spinner) SearchLocaleActivity.this.findViewById(R.id.spnLocaleName);
                ArrayAdapter adapter = new SimpleComboBoxAdapter(SearchLocaleActivity.this, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                if(mSearchParameters.locationName != null)
                {
                    for(int i = 0; i < adapter.getCount();i++)
                    {
                        if(((ComboBoxItem)adapter.getItem(i)).text.equals(mSearchParameters.locationName))
                        {
                            spinner.setSelection(i);
                            break;
                        }
                    }

                }

            }

        });

        service = new WebApiService(String.class,true);
        service.performGetList(WebApiActions.GetCities(), new IWebApiResultListener<List<String>>() {
            @Override
            public void onResult(List<String> cities) {
                cities.add(0,"All");
                Spinner spinner = (Spinner) SearchLocaleActivity.this.findViewById(R.id.spnCity);
                ArrayAdapter<String> adapter = new ArrayAdapter(SearchLocaleActivity.this,android.R.layout.simple_spinner_item, cities);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                if(mSearchParameters.locationCity != null)
                {
                    for(int i = 0; i < adapter.getCount();i++)
                    {
                        if((adapter.getItem(i)).equals(mSearchParameters.locationCity))
                        {
                            spinner.setSelection(i);
                            break;
                        }
                    }

                }
            }

        });

        Spinner spinner = (Spinner)SearchLocaleActivity.this.findViewById(R.id.spnRadius);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this, R.array.radius_count_arrays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if(mSearchParameters.radius != 0)
        {
            for(int i = 0; i < adapter.getCount();i++)
            {
                if(Integer.parseInt((String)adapter.getItem(i)) == mSearchParameters.radius)
                {
                    spinner.setSelection(i);
                    break;
                }
            }

        }

        service = new WebApiService(ComboBoxItem.class,true);
        service.performGetList(WebApiActions.GetLocaleTypes(), new IWebApiResultListener<List<ComboBoxItem>>() {
            @Override
            public void onResult(List<ComboBoxItem> items) {
                items.add(0, new ComboBoxItem(new UUID(0L, 0L), "All"));

                Spinner spinner = (Spinner) SearchLocaleActivity.this.findViewById(R.id.spnType);
                ArrayAdapter adapter = new SimpleComboBoxAdapter(SearchLocaleActivity.this, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(myLocation == null)
        {
            Log.d(TAG, "My Location not found");
            myLocation = new Location(LocationManager.GPS_PROVIDER);
            myLocation.setLatitude(51.23469);
            myLocation.setLongitude(6.83989999999994);

        }
        this.mSearchParameters.longitude = myLocation.getLongitude();
        this.mSearchParameters.latitude = myLocation.getLatitude();
        this.StartResultActivity();


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //this.loadCityFilterContent();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.


                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }







    private void getLocationByCity() {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            //geocoder.getFromLocation()
            /*List<Address> adresses = geocoder.getFromLocationName(this.mCity.getText().toString(), 10);
            if(adresses.size() > 0)
            {
                mLastLocation = new Location(LocationManager.GPS_PROVIDER);
                mLastLocation.setLatitude(adresses.get(0).getLatitude());
                mLastLocation.setLongitude(adresses.get(0).getLongitude());
                this.startMapsActivity();
            }*/
        }
        catch (Exception ex)
        {
            Log.e(TAG,ex.getMessage(),ex);
        }

        mProgressBar.setVisibility(View.GONE);
    }
}
