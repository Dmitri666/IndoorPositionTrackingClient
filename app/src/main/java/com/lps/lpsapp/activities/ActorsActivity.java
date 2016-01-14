package com.lps.lpsapp.activities;

import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lps.core.webapi.IWebApiResultListener;
import com.lps.core.webapi.JsonSerializer;
import com.lps.lpsapp.R;
import com.lps.lpsapp.map.CustomerMapView;
import com.lps.lpsapp.map.GuiDevice;
import com.lps.lpsapp.services.AltBeaconService;
import com.lps.lpsapp.services.AuthenticationService;
import com.lps.lpsapp.services.IChatListener;
import com.lps.lpsapp.services.IDevicePositionListener;
import com.lps.lpsapp.services.PushService;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.lpsapp.services.WebApiService;
import com.lps.lpsapp.viewModel.chat.Actor;
import com.lps.lpsapp.viewModel.chat.ChatMessage;
import com.lps.lpsapp.viewModel.chat.DevicePosition;
import com.lps.lpsapp.viewModel.rooms.RoomModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ActorsActivity extends BaseActivity implements View.OnLongClickListener{
    private static String TAG = "ActorsActivity";
    private UUID roomId;
    private IDevicePositionListener actorPositionListener;
    private boolean mPushServiceBound = false;
    private PushService mPushService;
    private boolean mBeaconServiceBound = false;
    private AltBeaconService mBeaconService;
    private MyArrayAdapter mActorListAdapter;
    private IChatListener chatListener;

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
        setContentView(R.layout.activity_chat_actors);

        Log.d(TAG, "onCreate");
        this.roomId = (UUID) getIntent().getExtras().get("id");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actorPositionListener = new IDevicePositionListener() {
            @Override
            public void positionChanged(DevicePosition position) {
                actorPositionChanged(position);
            }
        };

        chatListener = new IChatListener() {
            @Override
            public void messageResived(ChatMessage chatMessage) {

            }

            @Override
            public void joinChat(Actor actor)
            {
                actorJoined(actor);
            }

            @Override
            public void leaveChat(Actor actor)
            {
                actorLeaved(actor);
            }
        };
        // Create the mActorListAdapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections mActorListAdapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        if (!mPushServiceBound) {
            bindService(new Intent(this, PushService.class), mPushServiceConnection, Context.BIND_AUTO_CREATE);
        }
        if (!mBeaconServiceBound) {
            bindService(new Intent(this, AltBeaconService.class), mBeaconServiceConnection, Context.BIND_AUTO_CREATE);
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"onStop");
        mPushService.removeActorPositionListener(actorPositionListener);
        mPushService.removeChatMessageListener(chatListener);
        mPushService.leavePositionConsumerGroup(roomId);
        if (mPushServiceBound) {
            unbindService(mPushServiceConnection);
            mPushServiceBound = false;
            mPushService = null;
        }
        mBeaconService.devicePositionListener = null;
        if (mBeaconServiceBound) {
            unbindService(mBeaconServiceConnection);
            mBeaconServiceBound = false;
            mBeaconService = null;
        }
        super.onStop();

    }

    public void setRoomModel(final CustomerMapView view,RoomModel map)
    {
        view.setmRoomModel(map);
        String path =  WebApiActions.GetActorsInLocale() + "/" + this.roomId.toString();
        WebApiService service = new WebApiService(Actor.class,true);
        service.performGetList(path, new IWebApiResultListener<List>() {
            @Override
            public void onResult(List objResult) {
                setActors(view, objResult);
            }
        });
    }

    private void setActors(CustomerMapView view,List<Actor> model)
    {
        for(Actor actor:model) {
            view.addActor(actor);
        }
        this.mActorListAdapter.clear();
        this.mActorListAdapter.addAll(model);
    }

    private void actorJoined(Actor actor)
    {
        boolean notExist  = true;
        for(int i = 0;i < this.mActorListAdapter.getCount();i++)
        {
            if(((Actor)this.mActorListAdapter.getItem(i)).userId == actor.userId)
            {
                notExist = false;
                break;
            }
        }

        if(notExist) {
            mActorListAdapter.add(actor);
            mActorListAdapter.notifyDataSetInvalidated();
        }
    }

    private void actorLeaved(Actor actor)
    {
        for(int i = 0;i < this.mActorListAdapter.getCount();i++)
        {
            if(((Actor)this.mActorListAdapter.getItem(i)).userId == actor.userId)
            {
                this.mActorListAdapter.remove(this.mActorListAdapter.getItem(i));
                this.mActorListAdapter.notifyDataSetInvalidated();
                break;
            }
        }

    }

    public Integer getOptionsMenuId()
    {
        return R.menu.menu_actors;
    }


    public Integer getContextMenuId() {
        return R.menu.menu_context_actors;
    }


    public boolean contextMenuItemClicked(ActionMode mode, MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_chat:
                GuiDevice actor = (GuiDevice)mode.getTag();
                String path = WebApiActions.GetActorByDevice() + "/" + actor.devicePosition.deviceId;
                WebApiService service = new WebApiService(Actor.class,true);
                service.performGet(path, new IWebApiResultListener() {
                    @Override
                    public void onResult(Object objResult) {
                        try {
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            String serActor = JsonSerializer.serialize(objResult);
                            intent.putExtra("actor", serActor);
                            startActivity(intent);
                        } catch (JsonProcessingException ex) {
                            Log.e(TAG, ex.getMessage(), ex);
                        }
                    }
                });
                mode.finish();
                return true;
            case R.id.action_showProfile:

                mode.finish(); // Action picked, so close the CAB
                return true;
            default:
                return false;
        }
    }


    public boolean optionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_history:
                Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("roomId",roomId );
                startActivity(intent);

                return true;

        }
        return super.onOptionsItemSelected(item);
    }



    public void actorPositionChanged(final DevicePosition position) {
        this.runOnUiThread(new Runnable() {
            public void run() {
               CustomerMapView view = (CustomerMapView) findViewById(R.id.CustomerMapView);
               if(view.hasRoomModel()) {
                   view.positionChanged(position);
               }
            }
        });

    }

    private ServiceConnection mPushServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PushService.LocalBinder binder = (PushService.LocalBinder) service;
            mPushService = binder.getService();
            mPushServiceBound = true;

            mPushService.setActorPositionListener(actorPositionListener);
            mPushService.setChatListener(chatListener);
            mPushService.joinPositionConsumerGroup(roomId);
            Log.d(TAG,"onServiceConnected");

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mPushServiceBound = false;
            Log.d(TAG,"onServiceDisconnected");


        }
    };

    private ServiceConnection mBeaconServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            AltBeaconService.LocalBinder binder = (AltBeaconService.LocalBinder) service;
            mBeaconService = binder.getService();
            mBeaconServiceBound = true;

            mBeaconService.devicePositionListener = actorPositionListener;


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBeaconServiceBound = false;


        }
    };

    @Override
    public boolean onLongClick(View view) {
        GuiDevice gDevice = (GuiDevice)view;
        //todo nur für test
        if(AuthenticationService.authenticationData.userName == "admin")
        {
            if (gDevice.devicePosition.deviceId.equals("000")) {
                return false;
            }
        }
        else {
            if (gDevice.devicePosition.deviceId.equals(mMyApp.getAndroidId())) {
                return false;
            }
        }
//        if (mActionMode != null) {
//            return false;
//        }
//
//        // Start the CAB using the ActionMode.Callback defined above
//        mActionMode = startActionMode(mActionModeCallback);
//        mActionMode.setTag(view);
        view.setSelected(true);
        return true;

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
            final ActorsActivity activity = (ActorsActivity)getActivity();
            int tabNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            if(tabNumber == 2)
            {
                View rootView = inflater.inflate(R.layout.fragment_actors_map, container, false);

                final CustomerMapView view = (CustomerMapView) rootView.findViewById(R.id.CustomerMapView);
                ViewTreeObserver vto = view.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        String path = WebApiActions.GetRooms() + "/" + activity.roomId.toString();
                        WebApiService service = new WebApiService(RoomModel.class, true);
                        service.performGet(path, new IWebApiResultListener<RoomModel>() {
                            @Override
                            public void onResult(RoomModel objResult) {
                                activity.setRoomModel(view,objResult);
                            }
                        });
                    }
                });
                return rootView;
            }
            else
            {
                View rootView = inflater.inflate(R.layout.fragment_actors_list, container, false);
                ListView listView = (ListView)rootView.findViewById(R.id.lvActors);

                activity.mActorListAdapter = new MyArrayAdapter(getContext().getApplicationContext(),R.layout.list_item_chat,new ArrayList<Actor>());
                listView.setAdapter(activity.mActorListAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final Actor actor = (Actor) adapterView.getItemAtPosition(i);
                        Intent intent = new Intent(activity.getApplicationContext(), ChatActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            String serActor = JsonSerializer.serialize(actor);
                            intent.putExtra("actor", serActor);
                            startActivity(intent);
                        } catch (Exception ex) {
                            Log.e(TAG, ex.getMessage(), ex);
                        }
                    }
                });
                return rootView;
            }

        }
    }

    private static class MyArrayAdapter extends ArrayAdapter
    {
        private Context context;
        private LayoutInflater inflater = null;
        public MyArrayAdapter(Context context, int resource, List<Actor> objects) {
            super(context, resource,objects);
            this.context = context;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.list_item_chat, null);
            TextView tvText = (TextView) convertView.findViewById(R.id.text);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
            ImageView iv = (ImageView) convertView.findViewById(R.id.imageView);

            Actor actor =  (Actor)this.getItem(position);
            tvText.setText(actor.userName);
            tvTitle.setText(actor.userName);
            String iPath = actor.photoPath;
            if(iPath != null && !iPath.isEmpty())
            {
                String path = WebApiActions.GetImage() + "/" + iPath;
                Picasso.with(context).load(path).resize(200,200).into(iv);
            }
            return convertView;
        }

    }
}