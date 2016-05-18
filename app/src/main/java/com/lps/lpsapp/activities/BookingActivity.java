package com.lps.lpsapp.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import com.lps.core.webapi.IWebApiResultListener;
import com.lps.lpsapp.R;
import com.lps.lpsapp.dialogs.DatePickerFragment;
import com.lps.lpsapp.dialogs.NumberPickerFragment;
import com.lps.lpsapp.dialogs.TimePickerFragment;
import com.lps.lpsapp.helper.DataTimeUtil;
import com.lps.lpsapp.map.CustomerMapView;
import com.lps.lpsapp.services.IBookingStateChangedListener;
import com.lps.lpsapp.services.PushService;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.lpsapp.services.WebApiService;
import com.lps.lpsapp.viewModel.booking.BookingRequest;
import com.lps.lpsapp.viewModel.booking.TableReservationModelRequest;
import com.lps.lpsapp.viewModel.booking.TableState;
import com.lps.lpsapp.viewModel.booking.TableStateEnum;
import com.lps.lpsapp.viewModel.rooms.RoomModel;
import com.lps.lpsapp.viewModel.rooms.Table;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

public class BookingActivity extends BaseActivity  implements DatePickerFragment.TheListener,TimePickerFragment.TheListener ,NumberPickerFragment.TheListener {
    private static String TAG = "BookingActivity";
    protected UUID roomId;

    private IBookingStateChangedListener bookingStateListener;
    private GregorianCalendar mDate;
    private boolean mBound = false;
    protected PushService mPushService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.roomId = (UUID) getIntent().getExtras().get("id");

        mDate = new GregorianCalendar();
        final CustomerMapView view = (CustomerMapView) this.findViewById(R.id.CustomerMapView);
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                String path = WebApiActions.GetTableModel() + "/" + roomId.toString();
                WebApiService service = new WebApiService(RoomModel.class, true);
                service.performGet(path, new IWebApiResultListener<RoomModel>() {
                    @Override
                    public void onResult(RoomModel objResult) {
                        view.setmRoomModel(objResult);
                        BookingActivity.this.setDate();
                        BookingActivity.this.mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        bookingStateListener = new IBookingStateChangedListener() {
            @Override
            public void bookingStateChanged() {
                onBookingStateChanged();
            }
        };

        EditText tv = (EditText) findViewById(R.id.txtDate);
        if(tv != null) {
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment picker = new DatePickerFragment();
                    Bundle args = new Bundle();
                    args.putLong("date", mDate.getTime().getTime());
                    picker.setArguments(args);
                    picker.show(getSupportFragmentManager(), "datePicker");
                }
            });
        };

        EditText tvTime= (EditText) findViewById(R.id.txtTime);
        if(tvTime != null) {
            tvTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment timePicker = new TimePickerFragment();
                    Bundle args = new Bundle();
                    args.putLong("time", mDate.getTime().getTime());
                    timePicker.setArguments(args);
                    timePicker.show(getSupportFragmentManager(), "timePicker");
                }
            });
        };

        EditText tvCount= (EditText) findViewById(R.id.txtCount);

        /*tvCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment numberPicker = new NumberPickerFragment();
                numberPicker.show(getSupportFragmentManager(), "numberPicker");
            }
        });*/

        FloatingActionButton btn = (FloatingActionButton)this.findViewById(R.id.btnBooking);
        if(btn != null) {
            btn.setVisibility(View.INVISIBLE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendBookingRequest();
                    Snackbar.make(view, "send booking ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        CustomerMapView view = (CustomerMapView) this.findViewById(R.id.CustomerMapView);
        if(view.hasRoomModel()) {
            this.getReservationModel();
        }
        bindService(new Intent(this, PushService.class), mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            mPushService.removeBookingStateListener(bookingStateListener);
            mPushService.leaveReservationModelGroup(this.roomId);
            unbindService(mConnection);
            mBound = false;
            mPushService = null;
            bookingStateListener = null;
        }
    }

    @Override
    public void returnDate(GregorianCalendar date) {
        this.mDate.set(Calendar.YEAR, date.get(Calendar.YEAR));
        this.mDate.set(Calendar.MONTH, date.get(Calendar.MONTH));
        this.mDate.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

        setDate();
    }

    @Override
    public void returnTime(GregorianCalendar time) {
        this.mDate.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        this.mDate.set(Calendar.MINUTE, time.get(Calendar.MINUTE));

        setDate();
    }

    @Override
    public void returnNumber(int number) {
        EditText tv= (EditText) findViewById(R.id.txtCount);
        tv.setText(number);

    }

    private void setDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(this.mDate.getTime());
        EditText tv= (EditText) findViewById(R.id.txtDate);
        tv.setText(formattedDate);

        sdf = new SimpleDateFormat("HH:mm");
        String formattedTime = sdf.format(this.mDate.getTime());
        tv= (EditText) findViewById(R.id.txtTime);
        tv.setText(formattedTime);

        this.getReservationModel();
    }
    public void validateBooking()
    {
        CustomerMapView view = (CustomerMapView) this.findViewById(R.id.CustomerMapView);
        FloatingActionButton btn = (FloatingActionButton)this.findViewById(R.id.btnBooking);
        if(!view.getSelectedTables().isEmpty())
        {
            btn.setVisibility(View.VISIBLE);
        }
        else
        {
            btn.setVisibility(View.INVISIBLE);
        }
    }


    private void getReservationModel()
    {
        try {
            TableReservationModelRequest request = new TableReservationModelRequest();
            request.roomId = roomId;
            Calendar bookingTime = DataTimeUtil.convertToGTM(mDate);
            request.time = bookingTime.getTime();

            WebApiService service = new WebApiService(TableState.class,true);
            service.performPostList(WebApiActions.GetBookingState(), request, new IWebApiResultListener<List>() {
                @Override
                public void onResult(List objResult) {
                    CustomerMapView view = (CustomerMapView) findViewById(R.id.CustomerMapView);
                    view.setBooking(objResult);
                }
            });
        }
        catch (Exception ex)
        {
            Log.e(TAG,ex.getMessage(),ex);
        }
    }



    public void onBookingStateChanged() {
        this.getReservationModel();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PushService.LocalBinder binder = (PushService.LocalBinder) service;
            mPushService = binder.getService();
            mBound = true;

            mPushService.setBookingStateListener(bookingStateListener);
            mPushService.joinReservationModelGroup(roomId);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;


        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_booking, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu:
                return true;
            case R.id.home:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void sendBookingRequest()
    {
        CustomerMapView view = (CustomerMapView) this.findViewById(R.id.CustomerMapView);

        List<Table> selected = new ArrayList<>(view.getSelectedTables());
        String path =  WebApiActions.SendBookingRequest();
        BookingRequest request = new BookingRequest();
        for(Table table:selected) {
            request.tables.add(table.id);
        }

        request.time = DataTimeUtil.convertToGTM(this.mDate).getTime();
        WebApiService service = new WebApiService(BookingRequest.class,true);
        service.performPost(path, request);

        view.clearSelectedTables();
        for(Table table:selected) {
            TableState state = table.getBookingState();
            state.setTableState(TableStateEnum.Waiting);
            table.setBookingState(state);
        }


        view.invalidate();

        FloatingActionButton btn = (FloatingActionButton)this.findViewById(R.id.btnBooking);
        btn.setVisibility(View.INVISIBLE);
    }
}