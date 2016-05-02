package com.lps.lpsapp.altbeacon;

import android.util.Log;

import com.lps.lpsapp.activities.SettingsActivity;
import com.lps.lpsapp.viewModel.rooms.Point;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Matt Tyler on 4/18/14.
 */
public class TimedBeaconSimulator implements org.altbeacon.beacon.simulator.BeaconSimulator {
	protected static final String TAG = "TimedBeaconSimulator";
	private List<Beacon> beacons;
	private Point currentPoint;
	private double angle = 0;
    private int i = 0;
	private Timer timer;
    private boolean isRunning;
	/*
	 * You may simulate detection of beacons by creating a class like this in your project.
	 * This is especially useful for when you are testing in an Emulator or on a device without BluetoothLE capability.
	 * 
	 * Uncomment the lines in LpsApplication starting with:
	 *     // If you wish to test beacon detection in the Android Emulator, you can use code like this:
	 * Then set USE_SIMULATED_BEACONS = true to initialize the sample code in this class.
	 * If using a Bluetooth incapable test device (i.e. Emulator), you will want to comment
	 * out the verifyBluetooth() in MenuActivity.java as well.
	 * 
	 * Any simulated beacons will automatically be ignored when building for production.
	 */
	public boolean USE_SIMULATED_BEACONS = true;

	/**
	 *  Creates empty beacons ArrayList.
	 */
	public TimedBeaconSimulator(){
		beacons = new ArrayList<Beacon>();
        currentPoint = new Point(2,2);

		ScheduledExecutorService distancesSimulator= Executors.newScheduledThreadPool(5);

		// This schedules an beacon to appear every 10 seconds:
		distancesSimulator.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try{
					//angle += 20;
					//angle = angle%360d;
					//double radian = angle * Math.PI / 180d;
					//currentPoint = new Point(2f + 2f*(float)Math.cos(radian),2f + 2f*(float)Math.sin(radian));
				}catch(Exception e){
					Log.e(TAG,e.getMessage(),e);
				}
			}
		}, 0, 5,TimeUnit.SECONDS);
	}
	
	/**
	 * Required getter method that is called regularly by the Android Beacon Library.
	 * Any beacons returned by this method will appear within your test environment immediately.
	 */
	public List<Beacon> getBeacons(){
		if(!SettingsActivity.UseBeaconSimulator) {
			beacons.clear();
			return new ArrayList<>();
		}
		Random rnd = new Random(new Date().getTime());
        i++;
        if(i%5 == 0) {
            angle += 20.0;
            angle = angle % 360.0;
            //angle = 180.0;
            double radian = Math.toRadians(angle);
            currentPoint = new Point(4.0f + 3.0f * (float) Math.cos(radian), 4.0f + 3.0f * (float) Math.sin(radian));
        }
		for (Beacon b:beacons) {
			double distance = 0.0;
			if(b.getId3().toString() == "1") {
				distance = Math.sqrt(Math.pow(currentPoint.x - 1.0, 2.0) + Math.pow(currentPoint.y - 7.0, 2.0));
						}
			else if(b.getId3().toString() == "2")
			{
				distance = Math.sqrt(Math.pow(currentPoint.x - 1.0,2.0) + Math.pow(currentPoint.y - 1.0,2.0));
			}
			else if(b.getId3().toString() == "3")
			{
				distance = Math.sqrt(Math.pow(7.0 - currentPoint.x,2.0) + Math.pow(1.0 - currentPoint.y,2.0));
			}
			else if(b.getId3().toString() == "4")
			{
				distance = Math.sqrt(Math.pow(7.0 - currentPoint.x,2.0) + Math.pow(currentPoint.y - 7.0,2.0));
			}
			//int rndInt = rnd.nextInt(10);
			//double factor = ( 1.0 + (5.0 - rndInt)/ 100d);
			//distance = distance * factor;
			b.setRssi((int)(-55 * distance ));
		}


		return beacons;
	}
	
	/**
	 * Creates simulated beacons all at once.
	 */
	public void createBasicSimulatedBeacons(){
		if (USE_SIMULATED_BEACONS) {
            Beacon beacon1 = new AltBeacon.Builder().setId1("00000000-0000-0000-0000-000000000000")
                    .setId2("0").setId3("1").setRssi(-55).setTxPower(-55).build();
            Beacon beacon2 = new AltBeacon.Builder().setId1("00000000-0000-0000-0000-000000000000")
                    .setId2("0").setId3("2").setRssi(-55).setTxPower(-55).build();
            Beacon beacon3 = new AltBeacon.Builder().setId1("00000000-0000-0000-0000-000000000000")
                    .setId2("0").setId3("3").setRssi(-55).setTxPower(-55).build();
            Beacon beacon4 = new AltBeacon.Builder().setId1("00000000-0000-0000-0000-000000000000")
                    .setId2("0").setId3("4").setRssi(-55).setTxPower(-55).build();
			beacons.add(beacon1);
			beacons.add(beacon2);
			beacons.add(beacon3);
			beacons.add(beacon4);


		}
	}
	
	
	private ScheduledExecutorService scheduleTaskExecutor;


	/**
	 * Simulates a new beacon every 10 seconds until it runs out of new ones to add.
	 */
	public void createTimedSimulatedBeacons(){
		if (USE_SIMULATED_BEACONS){



			beacons = new ArrayList<Beacon>();
            Beacon beacon1 = new AltBeacon.Builder().setId1("00900000-0000-0000-0000-000000000000")
                    .setId2("1").setId3("1").setRssi(-55).setTxPower(-55).build();
            Beacon beacon2 = new AltBeacon.Builder().setId1("00900000-0000-0000-0000-000000000000")
                    .setId2("1").setId3("2").setRssi(-55).setTxPower(-55).build();
            Beacon beacon3 = new AltBeacon.Builder().setId1("00900000-0000-0000-0000-000000000000")
                    .setId2("1").setId3("3").setRssi(-55).setTxPower(-55).build();
            Beacon beacon4 = new AltBeacon.Builder().setId1("00900000-0000-0000-0000-000000000000")
                    .setId2("1").setId3("4").setRssi(-55).setTxPower(-55).build();
			beacons.add(beacon1);
			beacons.add(beacon2);
			beacons.add(beacon3);
			beacons.add(beacon4);
			
			final List<Beacon> finalBeacons = new ArrayList<Beacon>(beacons);

			//Clearing beacons list to prevent all beacons from appearing immediately.
			//These will be added back into the beacons list from finalBeacons later.
			beacons.clear();

			scheduleTaskExecutor= Executors.newScheduledThreadPool(5);

			// This schedules an beacon to appear every 10 seconds:
			scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
				public void run() {
					try{
						//putting a single beacon back into the beacons list.
						if (finalBeacons.size() > beacons.size())
							beacons.add(finalBeacons.get(beacons.size()));
						else 
							scheduleTaskExecutor.shutdown();
						
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}, 4, 10, TimeUnit.SECONDS);
		} 
	}

}