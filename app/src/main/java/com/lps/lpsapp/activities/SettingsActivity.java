package com.lps.lpsapp.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.lps.lpsapp.LpsApplication;
import com.lps.lpsapp.R;
import com.lps.lpsapp.altbeacon.TimedBeaconSimulator;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.lpsapp.viewModel.chat.BeaconModel;
import com.lps.lpsapp.viewModel.rooms.Point;
import com.lps.lpsapp.viewModel.rooms.RoomInfo;
import com.lps.webapi.IWebApiResultListener;
import com.lps.webapi.services.WebApiService;

import org.altbeacon.beacon.BeaconManager;

import java.util.List;

public class SettingsActivity extends BaseActivity {
    public static Boolean ShowCircles = false;
    public static Boolean UseBeaconSimulator = false;
    public static String WebApiUrl;
    public static Point TestPosition;
    public static Integer BeaconGroupCount;

    private CompoundButton.OnCheckedChangeListener mUseBeaconSimulatorChangeListener;
    private CompoundButton.OnCheckedChangeListener mShowCirclesChangeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUseBeaconSimulatorChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UseBeaconSimulator = isChecked;
                if (isChecked) {
                    String path = WebApiActions.GetRooms();
                    WebApiService service = new WebApiService(RoomInfo.class, true);
                    service.performGetList(path, new IWebApiResultListener<List<RoomInfo>>() {
                        @Override
                        public void onResult(List<RoomInfo> objResult) {
                            if (!objResult.isEmpty()) {
                                String path = WebApiActions.GetBeaconsInLocale() + "/" + objResult.get(0).id;
                                WebApiService service = new WebApiService(BeaconModel.class, true);
                                service.performGet(path, new IWebApiResultListener<BeaconModel>() {
                                    @Override
                                    public void onResult(BeaconModel objResult) {
                                        BeaconManager.setBeaconSimulator(new TimedBeaconSimulator());
                                        ((TimedBeaconSimulator) BeaconManager.getBeaconSimulator()).createTimedSimulatedBeacons(objResult);

                                    }

                                    @Override
                                    public void onError(Exception err) {
                                        ((LpsApplication) getApplicationContext()).HandleError(err);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(Exception err) {
                            ((LpsApplication) getApplicationContext()).HandleError(err);
                        }
                    });

                } else {
                    BeaconManager.setBeaconSimulator(null);
                }

            }
        };

        Switch swUseSimulator = (Switch) this.findViewById(R.id.switch_use_beacon_simulator);
        swUseSimulator.setChecked(UseBeaconSimulator);
        swUseSimulator.setOnCheckedChangeListener(mUseBeaconSimulatorChangeListener);

        mShowCirclesChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ShowCircles = isChecked;


            }
        };
        Switch swShowCircles = (Switch) this.findViewById(R.id.switch_show_circles);
        swShowCircles.setOnCheckedChangeListener(mShowCirclesChangeListener);


        EditText url = (EditText) this.findViewById(R.id.tbWebapiurl);
        url.setText(WebApiUrl);
        url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                WebApiUrl = s.toString();
            }
        });


        EditText testX = (EditText) this.findViewById(R.id.txtX);
        EditText testY = (EditText) this.findViewById(R.id.txtY);

        if (TestPosition != null) {
            testX.setText(Float.toString(TestPosition.x));
            testY.setText(Float.toString(TestPosition.y));
        }

        testX.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setTestPosition();
            }
        });


        testY.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setTestPosition();
            }
        });


        EditText bgc = (EditText) this.findViewById(R.id.txtBeaconGroupCount);
        if (BeaconGroupCount != null) {
            bgc.setText(Integer.toString(BeaconGroupCount));

        }

        bgc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    BeaconGroupCount = 1;
                } else {
                    BeaconGroupCount = Integer.parseInt(s.toString());
                }
            }
        });

    }


    private void setTestPosition() {
        EditText testX = (EditText) this.findViewById(R.id.txtX);
        EditText testY = (EditText) this.findViewById(R.id.txtY);

        if (testX.getText().toString().length() == 0 || testY.getText().toString().length() == 0) {
            TestPosition = null;
            return;
        }

        float x = Float.parseFloat(testX.getText().toString());
        float y = Float.parseFloat(testY.getText().toString());

        TestPosition = new Point(x, y);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Switch swSendToServer = (Switch) this.findViewById(R.id.switch_show_circles);
        swSendToServer.setOnCheckedChangeListener(null);

        Switch swUseSimulator = (Switch) this.findViewById(R.id.switch_use_beacon_simulator);
        swUseSimulator.setOnCheckedChangeListener(null);

    }
}
