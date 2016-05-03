package com.lps.lpsapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.lps.core.webapi.IWebApiResultListener;
import com.lps.lpsapp.LpsApplication;
import com.lps.lpsapp.R;
import com.lps.lpsapp.altbeacon.TimedBeaconSimulator;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.lpsapp.services.WebApiService;
import com.lps.lpsapp.viewModel.chat.BeaconModel;
import com.lps.lpsapp.viewModel.rooms.RoomInfo;

import org.altbeacon.beacon.BeaconManager;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    public static Boolean SendToServer = false;
    public static Boolean ShowCircles = false;
    public static Boolean UseBeaconSimulator = false;

    private TextWatcher mTextWatcher;
    private CompoundButton.OnCheckedChangeListener mSendToServerChangeListener;
    private CompoundButton.OnCheckedChangeListener mUseBeaconSimulatorChangeListener;
    private CompoundButton.OnCheckedChangeListener mShowCirclesChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText tbBeaconCount =  (EditText)this.findViewById(R.id.tbBeaconCount);
        tbBeaconCount.getText().clear();
        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        tbBeaconCount.addTextChangedListener(mTextWatcher);

        mSendToServerChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SendToServer = isChecked;
                ((LpsApplication) getApplicationContext()).saveSendToServerSetting(SendToServer);
            }
        };
        Switch swSendToServer =  (Switch)this.findViewById(R.id.switch_send_to_server);
        swSendToServer.setChecked(SendToServer);
        swSendToServer.setOnCheckedChangeListener(mSendToServerChangeListener);


        mUseBeaconSimulatorChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UseBeaconSimulator = isChecked;
                if (isChecked) {
                    String path = WebApiActions.GetRooms();
                    WebApiService service = new WebApiService(RoomInfo.class,true);
                    service.performGetList(path, new IWebApiResultListener<List<RoomInfo>>() {
                        @Override
                        public void onResult(List<RoomInfo> objResult) {
                            if(!objResult.isEmpty())
                            {
                                String path = WebApiActions.GetBeaconsInLocale() + "/" + objResult.get(0).id;
                                WebApiService service = new WebApiService(BeaconModel.class,true);
                                service.performGet(path, new IWebApiResultListener<BeaconModel>() {
                                    @Override
                                    public void onResult(BeaconModel objResult) {
                                        BeaconManager.setBeaconSimulator(new TimedBeaconSimulator());
                                        ((TimedBeaconSimulator) BeaconManager.getBeaconSimulator()).createTimedSimulatedBeacons(objResult);
                                    }
                                });
                            }
                        }
                    });

                } else {
                    BeaconManager.setBeaconSimulator(null);
                }

            }
        };
        Switch swUseSimulator =  (Switch)this.findViewById(R.id.switch_use_beacon_simulator);
        swUseSimulator.setChecked(UseBeaconSimulator);
        swUseSimulator.setOnCheckedChangeListener(mUseBeaconSimulatorChangeListener);

        mShowCirclesChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ShowCircles = isChecked;


            }
        };
        Switch swShowCircles =  (Switch)this.findViewById(R.id.switch_show_circles);
        swShowCircles.setOnCheckedChangeListener(mShowCirclesChangeListener);
    }

    @Override
    protected void onDestroy() {
        EditText tbBeaconCount =  (EditText)this.findViewById(R.id.tbBeaconCount);
        tbBeaconCount.removeTextChangedListener(mTextWatcher);

        Switch swSendToServer =  (Switch)this.findViewById(R.id.switch_send_to_server);
        swSendToServer.setOnCheckedChangeListener(null);

        Switch swUseSimulator =  (Switch)this.findViewById(R.id.switch_use_beacon_simulator);
        swUseSimulator.setOnCheckedChangeListener(null);
        super.onDestroy();
    }
}
