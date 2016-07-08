package com.lps.lpsapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lps.lpsapp.LpsApplication;
import com.lps.webapi.IWebApiResultListener;
import com.lps.webapi.JsonSerializer;
import com.lps.lpsapp.R;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.webapi.services.WebApiService;
import com.lps.lpsapp.viewModel.chat.Actor;
import com.lps.lpsapp.viewModel.chat.ConversationsData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatListActivity extends BaseActivity {
    private static String TAG = "ChatListActivity";
    private ListView listView;
    private MyArrayAdapter adapter;
    private ArrayList<ConversationsData> listItems =new ArrayList<ConversationsData>();
    private UUID mRoomId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        if(getIntent().getExtras().containsKey("roomId")) {
            this.mRoomId = (UUID) getIntent().getExtras().get("roomId");
        }

        listView = (ListView)this.findViewById(R.id.listView);

        adapter=new MyArrayAdapter(this,
                R.layout.list_item_chat,
                listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final ConversationsData item = (ConversationsData) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                Actor actor = new Actor();
                actor.userId = item.userId;
                actor.userName = item.userName;
                try {
                    String serActor = JsonSerializer.serialize(actor);
                    intent.putExtra("actor", serActor);
                    startActivity(intent);
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage(), ex);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String path = WebApiActions.GetRoomConversations();
        if(this.mRoomId != null) {
            path += "/" + this.mRoomId.toString();
        }

        WebApiService service = new WebApiService(ConversationsData.class,true);
        service.performGetList(path, new IWebApiResultListener<List>() {
            @Override
            public void onResult(List objResult) {
                setConversations(objResult);
            }
            @Override
            public void onError(Exception err) {
                ((LpsApplication)getApplicationContext()).HandleError(err);
            }
        });

    }

    private void setConversations(List<ConversationsData> data)
    {
        adapter.clear();
        for (ConversationsData conversation : data) {
            adapter.add(conversation);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private class MyArrayAdapter extends ArrayAdapter
    {
        private LayoutInflater inflater = null;
        public MyArrayAdapter(Context context, int resource, List<ConversationsData> objects) {
            super(context, resource,objects);
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.list_item_chat, null);
            TextView tvText = (TextView) convertView.findViewById(R.id.text);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
            ConversationsData info =  (ConversationsData)this.getItem(position);
            tvText.setText(info.userName);
            tvTitle.setText(info.userName);
            return convertView;
        }

    }
}
