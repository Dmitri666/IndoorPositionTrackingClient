package com.lps.lpsapp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.lps.lpsapp.BuildConfig;
import com.lps.lpsapp.LpsApplication;
import com.lps.lpsapp.activities.ActorsActivity;
import com.lps.lpsapp.altbeacon.DefaultDistanceCalculator;
import com.lps.lpsapp.management.AppManager;
import com.lps.lpsapp.positions.BeaconGroupsModel;
import com.lps.lpsapp.positions.Point2D;
import com.lps.lpsapp.positions.PositionCalculator;
import com.lps.lpsapp.viewModel.BeaconData;
import com.lps.lpsapp.viewModel.Measurement;
import com.lps.lpsapp.viewModel.PositionLogData;
import com.lps.lpsapp.viewModel.RangingData;
import com.lps.lpsapp.viewModel.chat.BeaconModel;
import com.lps.lpsapp.viewModel.chat.DevicePosition;
import com.lps.webapi.IWebApiResultListener;
import com.lps.webapi.services.WebApiService;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BleNotAvailableException;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.logging.Loggers;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by dle on 27.07.2015.
 */
public class InDoorPositionService extends Service implements BootstrapNotifier, BeaconConsumer {
    private static final String TAG = "InDoorPositionService";
    private final IBinder mBinder = new LocalBinder();
    public boolean bound;
    public PositionCalculator mPositionCalculator;
    public DevicePositionNotifier devicePositionListener;
    public Region backgroundRegion;
    List<IBeaconServiceListener> consumers;
    private BeaconManager beaconManager;
    private List<Region> mRegions;
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;
    private Boolean regionBootstrapInitialised = false;
    private BeaconGroupsModel beaconGroupsModel;


    @Override
    public void onCreate() {
        super.onCreate();

        this.mRegions = new ArrayList<>();
        backgroundRegion = new Region("backgroundRegion", null, null, null);
        this.consumers = new ArrayList<IBeaconServiceListener>();

        LpsApplication app = (LpsApplication) this.getApplicationContext();
        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(app);
        if (BuildConfig.DEBUG) {
            LogManager.setVerboseLoggingEnabled(true);
            LogManager.setLogger(Loggers.verboseLogger());
        }

        //BeaconManager.setDistanceModelUpdateUrl(getResources().getString(R.string.modelDistanceDalculationsUrl));

        boolean avalable = true;
        try {
            avalable = beaconManager.checkAvailability();
        } catch (BleNotAvailableException ex) {
            avalable = false;
        }
        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
        // find a different type of beacon, you must specify the byte layout for that beacon's
        // advertisement with a line like below.  The example shows how to find a beacon with the
        // same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb.  To find the proper
        // layout expression for other beacon types, do a web search for "setBeaconLayout"
        // including the quotes.
        //
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=aabb,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        //
        if (avalable) {
            beaconManager.getBeaconParsers().clear();
            beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")); // Estimotes
            beaconManager.getBeaconParsers().add(new BeaconParser().
                    setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));  // iBeacons
            beaconManager.getBeaconParsers().add(new BeaconParser().
                    setBeaconLayout("m:0-3=a7ae2eb7,i:4-19,i:20-21,i:22-23,p:24-24"));  // easiBeacons
        }

        //beaconManager.setForegroundScanPeriod(5000);
        //beaconManager.setForegroundBetweenScanPeriod(5000);
        //beaconManager.setBackgroundScanPeriod(BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD);
        //beaconManager.setBackgroundBetweenScanPeriod(10000);
        this.backgroundPowerSaver = new BackgroundPowerSaver(app);

        beaconManager.bind(this);
        Log.d(TAG, "Created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroyed");
        beaconManager.unbind(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        bound = true;
        Log.d(TAG, "Binded");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        bound = false;
        Log.d(TAG, "Unbinded");
        // All clients have unbound with unbindService()
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        Log.d(TAG, "onRebind");
        bound = true;
    }

    @Override
    public void didEnterRegion(final Region region) {
        Log.i(TAG, "did enter region." + region.getUniqueId());
        if (region.getUniqueId().equals(this.backgroundRegion.getUniqueId())) {
            try {
                beaconManager.startRangingBeaconsInRegion(region);
            } catch (RemoteException ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        } else {

            final Context ctx = this;
            final LpsApplication app = (LpsApplication) this.getApplicationContext();

            String path = WebApiActions.GetBeaconsInLocale() + "/" + region.getUniqueId();
            WebApiService service = new WebApiService(BeaconModel.class, true);
            service.performGet(path, new IWebApiResultListener<BeaconModel>() {
                @Override
                public void onResult(BeaconModel objResult) {
                    beaconGroupsModel = new BeaconGroupsModel(objResult);
                    mPositionCalculator = new PositionCalculator(beaconGroupsModel);
                    try {
                        beaconManager.startRangingBeaconsInRegion(region);
                    } catch (RemoteException ex) {
                        Log.e(TAG, ex.getMessage(), ex);
                    }
                }

                @Override
                public void onError(Exception err) {
                    ((LpsApplication) getApplicationContext()).HandleError(err);
                }

            });

            path = WebApiActions.SetPosition();
            DevicePosition param = new DevicePosition();
            param.deviceId = app.getAndroidId();
            param.roomId = UUID.fromString(region.getUniqueId());
            param.x = 0;
            param.y = 0;
            service = new WebApiService(DevicePosition.class, true);
            service.performPost(path, param);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast toast1 = Toast.makeText(ctx, "Willkommen " + region.getUniqueId(), Toast.LENGTH_LONG);
                    toast1.show();
                }
            });

            AppManager.getInstance().AppState.setCurrentLocaleId(param.roomId);

            Intent intent = new Intent(this, ActorsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Important:  make sure to add android:launchMode="singleInstance" in the manifest
            // to keep multiple copies of this activity from getting created if the user has
            // already manually launched the app.
            this.startActivity(intent);


        }
    }

    @Override
    public void didExitRegion(final Region region) {
        Log.d(TAG, "did exit region." + region.getUniqueId());
        beaconGroupsModel = null;
        if (region.getUniqueId().equals(this.backgroundRegion.getUniqueId())) {
            try {
                beaconManager.stopRangingBeaconsInRegion(region);
            } catch (RemoteException ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        } else {
            LpsApplication app = (LpsApplication) this.getApplicationContext();
            String path = WebApiActions.RemovePosition();
            DevicePosition param = new DevicePosition();
            param.deviceId = app.getAndroidId();
            param.roomId = UUID.fromString(region.getUniqueId());
            param.x = 0;
            param.y = 0;
            WebApiService service = new WebApiService(DevicePosition.class, true);
            service.performPost(path, param);

            final Context ctx = this;

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast toast1 = Toast.makeText(ctx, "Sie habed das Raum " + region.getUniqueId() + " verlassen", Toast.LENGTH_LONG);
                    toast1.show();
                }
            });
            AppManager.getInstance().AppState.setCurrentLocaleId(null);
            try {
                beaconManager.stopRangingBeaconsInRegion(region);
                if (mPositionCalculator != null) {
                    mPositionCalculator.positionCalculatorListener = null;
                    mPositionCalculator = null;
                }
            } catch (RemoteException ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {

    }

    @Override
    public void onBeaconServiceConnect() {
        Log.d(TAG, "BeaconServiceConnect.");
        Beacon.setDistanceCalculator(new DefaultDistanceCalculator());

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    for (IBeaconServiceListener consumer : consumers) {
                        consumer.beaconsInRange(beacons, region);
                    }

                    if (!region.getUniqueId().equals(InDoorPositionService.this.backgroundRegion.getUniqueId()) && mPositionCalculator != null) {
                        new PositionsThread(beacons, UUID.fromString(region.getUniqueId())).run();
                    }

                }
            }

        });
    }


    public void monitoreBackgroundRegion(boolean mode) {
        try {
            if (mode) {
                beaconManager.startMonitoringBeaconsInRegion(this.backgroundRegion);
            } else {
                beaconManager.stopMonitoringBeaconsInRegion(this.backgroundRegion);
            }
        } catch (RemoteException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    public void setBeaconServiceListener(IBeaconServiceListener consumer) {
        this.consumers.add(consumer);
    }

    public void removeBeaconServiceListener(IBeaconServiceListener consumer) {
        this.consumers.remove(consumer);
    }

    private void setRegions(List<com.lps.lpsapp.viewModel.Region> regions) {
        for (com.lps.lpsapp.viewModel.Region r : regions) {
            Region roomRegion = new Region(r.roomId.toString(), Identifier.fromUuid(r.identifirer1), Identifier.fromInt(r.identifirer2), null);
            this.mRegions.add(roomRegion);

        }
        if (this.mRegions.size() > 0) {
            this.regionBootstrap = new RegionBootstrap(this, this.mRegions);

        }


    }

    public void InitRegionBootstrap() {
        if (!regionBootstrapInitialised) {
            WebApiService service = new WebApiService(com.lps.lpsapp.viewModel.Region.class, false);
            service.performGetList(WebApiActions.GetRegions(), new IWebApiResultListener<List<com.lps.lpsapp.viewModel.Region>>() {
                @Override
                public void onResult(List<com.lps.lpsapp.viewModel.Region> objResult) {
                    setRegions(objResult);
                }

                @Override
                public void onError(Exception err) {
                    ((LpsApplication) getApplicationContext()).HandleError(err);
                }

            });
        }
    }

    private void postDeviceCoordinate(Collection<Beacon> beacons) {
        LpsApplication app = (LpsApplication) this.getApplicationContext();
        ArrayList<BeaconData> list = new ArrayList<BeaconData>();
        for (Beacon beacon : beacons) {
            BeaconData data = new BeaconData();
            data.id1 = beacon.getId1().toUuid();
            data.id2 = beacon.getId2().toInt();
            data.id3 = beacon.getId3().toInt();
            data.bluetothAddress = beacon.getBluetoothAddress();
            data.averageRssiLevel = beacon.getDistance();
            data.txPower = beacon.getTxPower();

            if (data.averageRssiLevel != Double.NaN) {
                list.add(data);
            }
        }
        RangingData rangingData = new RangingData();
        rangingData.deviceId = app.getAndroidId();
        rangingData.beaconDataList = list;


        WebApiService service = new WebApiService(RangingData.class, false);
        service.performPost(WebApiActions.PostBeaconData(), rangingData);


    }


    public void submitMeasurement(double distance, int txPower, double runningAverageRssi) {
        LpsApplication app = (LpsApplication) this.getApplicationContext();


        try {
            Measurement measurement = new Measurement();
            measurement.distance = distance;
            measurement.txPower = txPower;
            measurement.runningAverageRssi = runningAverageRssi;
            measurement.deviceId = app.getAndroidId();

            WebApiService service = new WebApiService(Measurement.class, false);
            service.performPost(WebApiActions.PostMeasurement(), measurement);


        } catch (Exception ex) {
            Log.e(this.getClass().getName(), ex.getMessage(), ex);
        }
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public InDoorPositionService getService() {
            // Return this instance of LocalService so clients can call public methods
            return InDoorPositionService.this;
        }
    }

    private class PositionsThread implements Runnable {
        Collection<Beacon> beacons;
        UUID roomId;

        public PositionsThread(Collection<Beacon> beacons, UUID roomId) {
            this.beacons = beacons;
            this.roomId = roomId;
        }

        @Override
        public void run() {
            Point2D position = mPositionCalculator.calculatePosition(beacons);
            if (position != null) {

                LpsApplication app = (LpsApplication) getApplicationContext();
                String path = WebApiActions.SetPosition();
                DevicePosition param = new DevicePosition();
                param.deviceId = app.getAndroidId();
                param.roomId = this.roomId;
                param.x = position.x;
                param.y = position.y;
                WebApiService service = new WebApiService(DevicePosition.class, true);
                service.performPost(path, param);

                if (devicePositionListener != null) {
                    devicePositionListener.positionChanged(param);
                }
                path = WebApiActions.SavePositionLog();
                PositionLogData log = new PositionLogData();
                log.deviceId = app.getAndroidId();
                log.roomId = this.roomId;
                log.x = position.x;
                log.y = position.y;


                service = new WebApiService(PositionLogData.class, true);
                service.performPost(path, log);
            }
        }
    }
}
