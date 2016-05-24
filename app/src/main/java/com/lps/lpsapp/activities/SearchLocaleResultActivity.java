package com.lps.lpsapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lps.webapi.IWebApiResultListener;
import com.lps.webapi.JsonSerializer;
import com.lps.lpsapp.R;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.webapi.services.WebApiService;
import com.lps.lpsapp.viewModel.rooms.RequestLocationData;
import com.lps.lpsapp.viewModel.rooms.RoomInfo;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SearchLocaleResultActivity extends BaseActivity  implements GoogleMap.OnInfoWindowClickListener {
    private static String TAG = "SearchLocaleResultActivity";

    // Activity Parameters
    private RequestLocationData mParameters;

    private Menu menu;


    private GoogleMap mMap;
    private List<RoomInfo> mRooms;
    private Map<String, RoomInfo> mRoomsMap;
    private MyArrayAdapter adapter;

    private static SupportMapFragment myMapFragment;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_locale_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRoomsMap = new HashMap<>();
        String params = getIntent().getStringExtra("parameters");
        if(params != null) {
            try {
                this.mParameters = JsonSerializer.deserialize(params, RequestLocationData.class);
//                SharedPreferences settings = getSharedPreferences("parameters", 0);
//                SharedPreferences.Editor editor = settings.edit();
//                editor.putString("search", params);
//                editor.commit();
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        } else {
//            SharedPreferences settings = getSharedPreferences("parameters", 0);
//            String search = settings.getString("search", null);
//            if(search != null)
//            {
//                try {
//                    this.mParameters = JsonSerializer.deserialize(search, RequestLocationData.class);
//                } catch (IOException ex) {
//                    Log.e(TAG, ex.getMessage(), ex);
//                }
//            }
        }
        this.loadLocales();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    protected void onDestroy() {

        if (mMap != null) {
            mMap.setMyLocationEnabled(false);
            mMap.setOnInfoWindowClickListener(null);
            mMap.setInfoWindowAdapter(null);
            mMap.clear();
            mMap = null;
        }
        myMapFragment = null;

        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_locale_result, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorit) {
            MyArrayAdapter.isFavoritMode = true;
            this.adapter.notifyDataSetInvalidated();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        RoomInfo item = this.mRoomsMap.get(marker.getId());
        Intent intent = new Intent(getApplicationContext(), BookingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("id", item.id);
        startActivity(intent);
    }

    private void setGoogleMap(GoogleMap map)
    {
        this.mMap = map;
        this.mMap.setOnInfoWindowClickListener(this);
        this.mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        this.mMap.setMyLocationEnabled(true);


        for (RoomInfo roomInfo : this.mRooms) {
            LatLng position = new LatLng(roomInfo.lat, roomInfo.lng);
            MarkerOptions option = new MarkerOptions().position(position);
            option.title(roomInfo.id.toString());
            Marker marker = mMap.addMarker(option);
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_local_bar_black_24dp));
            mRoomsMap.put(marker.getId(), roomInfo);
        }
        if(this.mParameters != null) {
            LatLng position = new LatLng(this.mParameters.latitude, this.mParameters.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
        }

    }

    private void loadLocales() {
        if (this.mParameters != null) {
            WebApiService service = new WebApiService(RoomInfo.class,true);
            service.performPostList(WebApiActions.GetRooms(), this.mParameters, new IWebApiResultListener<List>() {
                @Override
                public void onResult(List objResult) {
                    loadingFinished(objResult);
                }
            });
        } else {
            WebApiService service = new WebApiService(RoomInfo.class,true);
            service.performGetList(WebApiActions.GetRooms(), new IWebApiResultListener<List>() {
                @Override
                public void onResult(List objResult) {
                    loadingFinished(objResult);
                }
            });
        }
    }

    private void loadingFinished(List<RoomInfo> rooms)
    {
        this.mRooms = rooms;

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                menu.getItem(0).setVisible(position == 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

     /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LIST";
                case 1:
                    return "MAP";

            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final SearchLocaleResultActivity activity = (SearchLocaleResultActivity)getActivity();
            int tabNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            if(tabNumber == 2)
            {
                View rootView = inflater.inflate(R.layout.fragment_search_locale_result_map, container, false);

                myMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
                myMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        activity.setGoogleMap(googleMap);
                    }
                });
                return rootView;
            }
            else
            {
                View rootView = inflater.inflate(R.layout.fragment_search_locale_result_list, container, false);
                ListView listView = (ListView)rootView.findViewById(R.id.lvLocales);

                activity.adapter = new MyArrayAdapter(getContext().getApplicationContext(),R.layout.list_item_locale,activity.mRooms);
                listView.setAdapter(activity.adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final RoomInfo item = (RoomInfo) adapterView.getItemAtPosition(i);
                        Intent intent = new Intent(getContext().getApplicationContext(), BookingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("id", item.id);
                        startActivity(intent);
                    }
                });
                return rootView;
            }

        }
    }



    private static class MyArrayAdapter extends ArrayAdapter
    {
        public static boolean isFavoritMode = false;
        private LayoutInflater inflater = null;
        public MyArrayAdapter(Context context, int resource, List<RoomInfo> objects) {
            super(context, resource,objects);
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
            {
                convertView = inflater.inflate(R.layout.list_item_locale, null);
            }

            final RoomInfo info =  (RoomInfo)this.getItem(position);

            CheckBox chb = (CheckBox) convertView.findViewById(R.id.localecheck);
            if(isFavoritMode)
            {
                chb.setVisibility(View.VISIBLE);
            }
            else
            {
                chb.setVisibility(View.GONE);
            }

            final ImageView ivFavorit = (ImageView) convertView.findViewById(R.id.favorit);
            ivFavorit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info.isFavorite = !info.isFavorite;
                    if(info.isFavorite)
                    {
                        ivFavorit.setImageResource(R.drawable.ic_favorite_black_24dp);
                        new WebApiService(UUID.class,true).performGet(WebApiActions.InsertFavorite() + "/" + info.id.toString(), null);
                    }
                    else
                    {
                        ivFavorit.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        new WebApiService(UUID.class,true).performGet(WebApiActions.DeleteFavorite() + "/" + info.id.toString(), null);
                    }

                }
            });
            if(info.isFavorite) {
               ivFavorit.setImageResource(R.drawable.ic_favorite_black_24dp);
            }

            ImageView ivChat = (ImageView) convertView.findViewById(R.id.chatIcon);
            if(info.isChatExist) {

                ivChat.setVisibility(View.VISIBLE);
            }
            else
            {
                ivChat.setVisibility(View.INVISIBLE);
            }

            TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
            tvTitle.setText(info.name);

            LinearLayout ratings = (LinearLayout) convertView.findViewById(R.id.ratings);
            ratings.removeAllViews();
            for(int i = 0; i < info.rating; i++)
            {
                ImageView iv = (ImageView)inflater.inflate(R.layout.imageview_rating_full,null);
                ratings.addView(iv);
            }

            TextView tvText = (TextView) convertView.findViewById(R.id.text);
            tvText.setText(info.city);


            final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            String iPath = info.imageFileName;
            if(iPath != null && !iPath.isEmpty())
            {
                String path = WebApiActions.GetImage() + "/" + iPath;
                Picasso.with(getContext()).load(path).resize(200,200).into(imageView);
            }






           return convertView;
        }
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private LayoutInflater inflater = null;
        private final View myContentsView;
        final TextView tvTitle;
        final TextView tvSnippet;
        final ImageView imageView;
        final LinearLayout ratings;

        MyInfoWindowAdapter() {
            inflater = getLayoutInflater();
            myContentsView = inflater.inflate(R.layout.custom_info_contents, null);
            tvTitle = ((TextView) myContentsView.findViewById(R.id.title));
            tvSnippet = ((TextView) myContentsView.findViewById(R.id.snippet));
            imageView = (ImageView) myContentsView.findViewById(R.id.imageView);
            ratings = (LinearLayout) myContentsView.findViewById(R.id.ratings);
        }

        @Override
        public View getInfoContents(final Marker marker) {
            final RoomInfo info = mRoomsMap.get(marker.getId());
            String iPath = info.imageFileName;
            if(iPath != null && !iPath.isEmpty())
            {
                String path = WebApiActions.GetImage() + "/" + iPath;
                Picasso.with(SearchLocaleResultActivity.this.getApplicationContext()).load(path).resize(200,200).into(imageView);
            }
            tvTitle.setText(info.name);
            tvSnippet.setText(info.name);


            ratings.removeAllViews();
            for(int i = 0; i < info.rating; i++)
            {
                ImageView iv = (ImageView)inflater.inflate(R.layout.imageview_rating_full,null);
                ratings.addView(iv);
            }
            return myContentsView;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            return null;
        }

    }
}
