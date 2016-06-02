package com.lps.lpsapp.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.lps.lpsapp.LpsApplication;
import com.lps.lpsapp.R;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by user on 16.08.2015.
 */
public class BaseActivity extends AppCompatActivity {

    protected ProgressBar mProgressBar;

    public final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    public final int MY_PERMISSIONS_READ_CONTACTS = 2;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LpsApplication mMyApp = (LpsApplication)this.getApplicationContext();

        // Create a progress bar to display while the list loads
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content).getRootView();
        mProgressBar = new ProgressBar(this);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);

        RelativeLayout rl = new RelativeLayout(this);
        rl.setGravity(Gravity.CENTER);
        rl.addView(mProgressBar);
        root.addView(rl, params);

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        Object toolbar = findViewById(R.id.toolbar);
        if(toolbar != null) {
            Toolbar myToolbar = (Toolbar)toolbar;
                    setSupportActionBar(myToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = LpsApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                return true;

            case R.id.action_about:

                return true;
            default:

        }
        return false;

    }

    public void grantSelfPermission(String permisions,int response)
    {
        int permissionCheck = ContextCompat.checkSelfPermission(this,permisions);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,permisions)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,new String[]{permisions},response);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,new String[]{permisions},response);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }
    }





}
