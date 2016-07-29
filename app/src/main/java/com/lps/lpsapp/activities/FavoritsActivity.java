package com.lps.lpsapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lps.lpsapp.LpsApplication;
import com.lps.lpsapp.R;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.lpsapp.viewModel.rooms.RoomInfo;
import com.lps.webapi.IWebApiResultListener;
import com.lps.webapi.services.WebApiService;

import java.util.ArrayList;
import java.util.List;

public class FavoritsActivity extends BaseActivity {
    private static String TAG = "FavoritsActivity";
    ListView listView;
    MyArrayAdapter adapter;
    ArrayList<RoomInfo> listItems = new ArrayList<RoomInfo>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorits);

        listView = (ListView) this.findViewById(R.id.lvLocales);

        adapter = new MyArrayAdapter(this,
                R.layout.list_item_locale,
                listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final RoomInfo item = (RoomInfo) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getApplicationContext(), BookingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("id", item.id);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onResume() {
        super.onResume();
        WebApiService service = new WebApiService(RoomInfo.class, true);
        service.performGetList(WebApiActions.GetRooms(), new IWebApiResultListener<List>() {
            @Override
            public void onResult(List objResult) {
                setRooms(objResult);
            }

            @Override
            public void onError(Exception err) {
                ((LpsApplication) getApplicationContext()).HandleError(err);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setRooms(List<RoomInfo> rooms) {
        adapter.clear();
        for (RoomInfo info : rooms) {
            adapter.add(info);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private class MyArrayAdapter extends ArrayAdapter {
        private LayoutInflater inflater = null;

        public MyArrayAdapter(Context context, int resource, List<RoomInfo> objects) {
            super(context, resource, objects);
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.list_item_locale, null);
            TextView tvText = (TextView) convertView.findViewById(R.id.text);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.title);

            RoomInfo info = (RoomInfo) this.getItem(position);
            tvTitle.setText(info.name);
            tvText.setText(info.name);

            return convertView;
        }

    }
}
