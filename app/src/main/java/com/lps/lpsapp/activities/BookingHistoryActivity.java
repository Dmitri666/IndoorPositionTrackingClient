package com.lps.lpsapp.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lps.lpsapp.LpsApplication;
import com.lps.webapi.IWebApiResultListener;
import com.lps.lpsapp.R;
import com.lps.lpsapp.services.BookingStateNotofier;
import com.lps.lpsapp.services.PushService;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.webapi.services.WebApiService;
import com.lps.lpsapp.viewModel.booking.BookingJoinRoomData;
import com.lps.lpsapp.viewModel.booking.BookingStateEnum;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingHistoryActivity extends BaseActivity {
    ListView listView;
    MyArrayAdapter adapter;
    ArrayList<BookingJoinRoomData> listItems=new ArrayList<BookingJoinRoomData>();
    private BookingStateNotofier bookingStateListener;
    private boolean mBound = false;
    protected PushService mPushService;
    private UUID mRoomId;

    public BookingHistoryActivity()
    {
        bookingStateListener = new BookingStateNotofier() {
            @Override
            public void bookingStateChanged() {
                onBookingStateChanged();
            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.mRoomId = (UUID) getIntent().getExtras().get("id");
        listView = (ListView)this.findViewById(R.id.bookingListView);

        adapter=new MyArrayAdapter(this,
                R.layout.list_item_booking_history,
                listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final BookingJoinRoomData item = (BookingJoinRoomData) adapterView.getItemAtPosition(i);
                //Intent intent = new Intent(getApplicationContext(), BookingParametersActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.putExtra("id", item.id);
                //startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.refeshBookingHistory();
        bindService(new Intent(this, PushService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    private void refeshBookingHistory()
    {
        String path = WebApiActions.GetBookingHistory() + "/" + this.mRoomId.toString();
        WebApiService service = new WebApiService(BookingJoinRoomData.class, true);
        service.performGetList(path, new IWebApiResultListener<List<BookingJoinRoomData>>() {
            @Override
            public void onResult(List<BookingJoinRoomData> objResult) {
                adapter.clear();
                for (BookingJoinRoomData info : objResult) {
                    adapter.add(info);
                }
            }
            @Override
            public void onError(Exception err) {
                ((LpsApplication)getApplicationContext()).HandleError(err);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            mPushService.removeBookingStateListener(bookingStateListener);
            unbindService(mConnection);
            mBound = false;
            mPushService = null;
        }

    }

    public void onBookingStateChanged() {
        this.refeshBookingHistory();
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PushService.LocalBinder binder = (PushService.LocalBinder) service;
            mPushService = binder.getService();
            mPushService.setBookingStateListener(bookingStateListener);
            mBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;


        }
    };

    private class MyArrayAdapter extends ArrayAdapter
    {
        private LayoutInflater inflater = null;
        public MyArrayAdapter(Context context, int resource, List<BookingJoinRoomData> objects) {
            super(context, resource, objects);
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_booking_history, parent, false);
            }
            TextView textView1 = (TextView) convertView.findViewById(R.id.firstLine);
            TextView textView2 = (TextView) convertView.findViewById(R.id.secondLine);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
            View loadingPanel = convertView.findViewById(R.id.loadingPanel);
            BookingJoinRoomData info =  (BookingJoinRoomData)this.getItem(position);
            textView1.setText(info.name);
            String timeString = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(info.time);
            textView2.setText(timeString);
            if (info.getBookingState() == BookingStateEnum.Waiting) {
                imageView.setVisibility(View.INVISIBLE);
                loadingPanel.setVisibility(View.VISIBLE);
            } else if (info.getBookingState() == BookingStateEnum.Accepted){
                loadingPanel.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.ic_mood_black_24dp);
            }
            else if (info.getBookingState() == BookingStateEnum.Rejected || info.getBookingState() == BookingStateEnum.Canceled){
                loadingPanel.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.ic_mood_bad_black_24dp);
            }

            return convertView;
        }

        public void updateList(){
            notifyDataSetChanged();
        }

    }
}
