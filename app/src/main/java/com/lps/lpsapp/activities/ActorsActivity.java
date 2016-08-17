package com.lps.lpsapp.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.lps.lpsapp.LpsApplication;
import com.lps.lpsapp.R;
import com.lps.lpsapp.management.AppManager;
import com.lps.lpsapp.map.CustomerMapView;
import com.lps.lpsapp.map.GuiDevice;
import com.lps.lpsapp.positions.BeaconData;
import com.lps.lpsapp.positions.PositionCalculatorNotifier;
import com.lps.lpsapp.services.ChatNotifier;
import com.lps.lpsapp.services.DevicePositionNotifier;
import com.lps.lpsapp.services.InDoorPositionService;
import com.lps.lpsapp.services.PushService;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.lpsapp.viewModel.chat.Actor;
import com.lps.lpsapp.viewModel.chat.ChatMessage;
import com.lps.lpsapp.viewModel.chat.DevicePosition;
import com.lps.lpsapp.viewModel.rooms.RoomModel;
import com.lps.webapi.IWebApiResultListener;
import com.lps.webapi.JsonSerializer;
import com.lps.webapi.services.WebApiService;
import com.squareup.picasso.Picasso;

import org.altbeacon.beacon.BeaconManager;

import java.util.ArrayList;
import java.util.List;


public class ActorsActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "ActorsActivity";

    private DevicePositionNotifier devicePositionListener;
    private boolean mPushServiceBound = false;
    private PushService mPushService;
    private boolean mBeaconServiceBound = false;
    private InDoorPositionService mBeaconService;
    private MyArrayAdapter mActorListAdapter;
    private ChatNotifier chatListener;
    private ActionMode mActionMode;
    private ServiceConnection mPushServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PushService.LocalBinder binder = (PushService.LocalBinder) service;
            mPushService = binder.getService();
            mPushServiceBound = true;

            mPushService.setActorPositionListener(devicePositionListener);
            mPushService.setChatListener(chatListener);
            mPushService.joinPositionConsumerGroup(AppManager.getInstance().AppState.getCurrentLocaleId());
            Log.d(TAG, "onServiceConnected");

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mPushServiceBound = false;
            Log.d(TAG, "onServiceDisconnected");


        }
    };
    private ServiceConnection mBeaconServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            InDoorPositionService.LocalBinder binder = (InDoorPositionService.LocalBinder) service;
            mBeaconService = binder.getService();
            mBeaconServiceBound = true;

            mBeaconService.devicePositionListener = devicePositionListener;
            if (mBeaconService.mPositionCalculator != null) {
                mBeaconService.mPositionCalculator.positionCalculatorListener = new PositionCalculatorNotifier() {
                    @Override
                    public void onCalculationResult(List<BeaconData> beaconDatas, Rect bounds, Path path) {
                        setCalculationResult(beaconDatas, bounds,path);
                    }
                };
            }


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBeaconServiceBound = false;


        }
    };
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_context_actors, menu);
            mActionMode = mode;
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_chat:
                    Actor actor = (Actor) mode.getTag();
                    mode.finish();
                    try {
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        String serActor = JsonSerializer.serialize(actor);
                        intent.putExtra("actor", serActor);
                        startActivity(intent);
                    } catch (JsonProcessingException ex) {
                        Log.e(TAG, ex.getMessage(), ex);
                    }

                    return true;
                case R.id.action_showProfile:
                    Intent intent = new Intent(getApplicationContext(), ActorProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Actor actor = (Actor) mode.getTag();
            actor.guiElement.setSelected(false);
            mActionMode.setTag(null);
            mActionMode = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_actors);

        Log.d(TAG, "onCreate");
        if (AppManager.getInstance().AppState.getCurrentLocaleId() == null) {
            this.finish();
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        devicePositionListener = new DevicePositionNotifier() {
            @Override
            public void positionChanged(DevicePosition position) {
                actorPositionChanged(position);
            }
        };

        chatListener = new ChatNotifier() {
            @Override
            public void messageResived(ChatMessage chatMessage) {

            }

            @Override
            public void joinChat(Actor actor) {
                actorJoined(actor);
            }

            @Override
            public void leaveChat(Actor actor) {
                actorLeaved(actor);
            }
        };
        // Create the mActorListAdapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections mActorListAdapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!mPushServiceBound) {
            bindService(new Intent(this, PushService.class), mPushServiceConnection, Context.BIND_AUTO_CREATE);
        }
        if (!mBeaconServiceBound) {
            bindService(new Intent(this, InDoorPositionService.class), mBeaconServiceConnection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        mPushService.removeActorPositionListener(devicePositionListener);
        mPushService.removeChatMessageListener(chatListener);
        mPushService.leavePositionConsumerGroup(AppManager.getInstance().AppState.getCurrentLocaleId());
        if (mPushServiceBound) {
            unbindService(mPushServiceConnection);
            mPushServiceBound = false;
            mPushService = null;
        }

        mBeaconService.devicePositionListener = null;
        if (mBeaconService.mPositionCalculator != null) {
            mBeaconService.mPositionCalculator.positionCalculatorListener = null;
        }

        if (mBeaconServiceBound) {
            unbindService(mBeaconServiceConnection);
            mBeaconServiceBound = false;
            mBeaconService = null;
        }


    }

    public void setRoomModel(final CustomerMapView view, RoomModel map) {
        view.setmRoomModel(map);
        String path = WebApiActions.GetActorsInLocale() + "/" + map.id.toString();
        WebApiService service = new WebApiService(Actor.class, true);

        service.performGetList(path, new IWebApiResultListener<List>() {
            @Override
            public void onResult(List objResult) {
                setActors(view, objResult);
            }

            @Override
            public void onError(Exception err) {
                ((LpsApplication) getApplicationContext()).HandleError(err);
            }


        });
    }

    private void setActors(CustomerMapView view, List<Actor> model) {
        this.mActorListAdapter.clear();
        view.clearActors();
        for (Actor actor : model) {
            view.addActor(actor);
            if (!actor.position.deviceId.equals(((LpsApplication) getApplicationContext()).getAndroidId())) {
                this.mActorListAdapter.add(actor);
            }
        }


    }

    private void actorJoined(final Actor actor) {
        final CustomerMapView view = (CustomerMapView) this.findViewById(R.id.CustomerMapView);
        boolean notExist = true;
        for (int i = 0; i < mActorListAdapter.getCount(); i++) {
            if (((Actor) mActorListAdapter.getItem(i)).userId.equals(actor.userId)) {
                notExist = false;
                break;
            }
        }

        if (notExist) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (!actor.position.deviceId.equals(((LpsApplication) getApplicationContext()).getAndroidId())) {
                        mActorListAdapter.add(actor);
                        mActorListAdapter.notifyDataSetInvalidated();
                    }

                    view.addActor(actor);
                }

            });
        }


    }

    private void actorLeaved(final Actor actor) {
        final CustomerMapView view = (CustomerMapView) this.findViewById(R.id.CustomerMapView);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mActorListAdapter.getCount(); i++) {
                    if (((Actor) mActorListAdapter.getItem(i)).userId.equals(actor.userId)) {
                        mActorListAdapter.remove(mActorListAdapter.getItem(i));
                        view.removeActor(actor.position.deviceId);
                        mActorListAdapter.notifyDataSetInvalidated();
                        break;
                    }
                }
            }

        });
    }

    public void actorPositionChanged(final DevicePosition position) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                CustomerMapView view = (CustomerMapView) findViewById(R.id.CustomerMapView);
                if (view.hasRoomModel()) {
                    if(mBeaconService != null) {
                        view.positionChanged(position, mBeaconService.getInterval());
                    } else {
                        view.positionChanged(position, BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD + BeaconManager.DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD);
                    }
                }
            }
        });

    }

    public void setCalculationResult(final List<BeaconData> beaconDatas, final Rect bounds,final Path path) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                CustomerMapView view = (CustomerMapView) findViewById(R.id.CustomerMapView);
                if (view != null && view.hasRoomModel()) {
                    view.setCalculationResult(beaconDatas, bounds,path);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        GuiDevice gDevice = (GuiDevice) view;
        if (gDevice.deviceId.equals(((LpsApplication) getApplication()).getAndroidId())) {
            return;
        }

        if (mActionMode != null) {
            return;
        }

        // Start the CAB using the ActionMode.Callback defined above
        mActionMode = startActionMode(mActionModeCallback);
        CustomerMapView map = (CustomerMapView) this.findViewById(R.id.CustomerMapView);
        Actor actor = map.findActorByDeviceId(gDevice.deviceId);
        mActionMode.setTag(actor);
        view.setSelected(true);


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

        public PlaceholderFragment() {
        }

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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final ActorsActivity activity = (ActorsActivity) getActivity();
            int tabNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            if (tabNumber == 1) {
                View rootView = inflater.inflate(R.layout.fragment_actors_map, container, false);

                final CustomerMapView view = (CustomerMapView) rootView.findViewById(R.id.CustomerMapView);
                ViewTreeObserver vto = view.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        String path = WebApiActions.GetTableModel() + "/" + AppManager.getInstance().AppState.getCurrentLocaleId().toString();
                        WebApiService service = new WebApiService(RoomModel.class, true);
                        service.performGet(path, new IWebApiResultListener<RoomModel>() {
                            @Override
                            public void onResult(RoomModel objResult) {
                                objResult.id = AppManager.getInstance().AppState.getCurrentLocaleId();
                                activity.setRoomModel(view, objResult);
                            }

                            @Override
                            public void onError(Exception err) {
                                ((LpsApplication) getContext().getApplicationContext()).HandleError(err);
                            }


                        });


                    }
                });
                FloatingActionButton btnZoomIn = (FloatingActionButton) rootView.findViewById(R.id.btnZoomPlus);
                if (btnZoomIn != null) {

                    btnZoomIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            view.zoomIn();
                        }
                    });
                }
                ;

                FloatingActionButton btnZoomOut = (FloatingActionButton) rootView.findViewById(R.id.btnZoomMinus);
                if (btnZoomOut != null) {

                    btnZoomOut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            view.zoomOut();
                        }
                    });
                }
                ;
                return rootView;
            } else {
                View rootView = inflater.inflate(R.layout.fragment_actors_list, container, false);
                ListView listView = (ListView) rootView.findViewById(R.id.lvActors);

                activity.mActorListAdapter = new MyArrayAdapter(getContext().getApplicationContext(), R.layout.list_item_chat, new ArrayList<Actor>());
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

    private static class MyArrayAdapter extends ArrayAdapter<Actor> {
        private Context context;
        private LayoutInflater inflater = null;

        public MyArrayAdapter(Context context, int resource, List<Actor> objects) {
            super(context, resource, objects);
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

            Actor actor = this.getItem(position);
            tvText.setText(actor.userName);
            tvTitle.setText(actor.userName);
            String iPath = actor.photoPath;
            if (iPath != null && !iPath.isEmpty()) {
                String path = WebApiActions.GetImage() + "/" + iPath;
                Picasso.with(context).load(path).resize(200, 200).into(iv);
            }
            return convertView;
        }

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
}