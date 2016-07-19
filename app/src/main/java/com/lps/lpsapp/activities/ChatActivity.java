package com.lps.lpsapp.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lps.lpsapp.LpsApplication;
import com.lps.lpsapp.R;
import com.lps.lpsapp.helper.ChatAdapter;
import com.lps.lpsapp.services.ChatNotifier;
import com.lps.lpsapp.services.PushService;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.lpsapp.viewModel.chat.Actor;
import com.lps.lpsapp.viewModel.chat.ChatMessage;
import com.lps.lpsapp.viewModel.chat.ConversationsData;
import com.lps.webapi.IWebApiResultListener;
import com.lps.webapi.JsonSerializer;
import com.lps.webapi.services.WebApiService;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ChatActivity extends BaseActivity {
    private static String TAG = "ChatActivity";
    private UUID conversationId;
    private Actor actor;
    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private ChatNotifier chatMessageListener;
    private boolean mBound = false;
    private PushService mPushService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PushService.LocalBinder binder = (PushService.LocalBinder) service;
            mPushService = binder.getService();
            mBound = true;

            mPushService.setChatListener(chatMessageListener);


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chatMessageListener = new ChatNotifier() {
            @Override
            public void messageResived(ChatMessage chatMessage) {
                newChatMessageRecived(chatMessage);
            }

            @Override
            public void joinChat(Actor actor) {

            }

            @Override
            public void leaveChat(Actor actor) {

            }
        };

        try {
            String serActor = getIntent().getExtras().getString("actor");
            this.actor = JsonSerializer.deserialize(serActor, Actor.class);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
            this.finish();
        }
        initControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBound) {
            bindService(new Intent(this, PushService.class), mConnection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mPushService.removeChatMessageListener(chatMessageListener);
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
            mPushService = null;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_actors, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_history:
                Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText(this.actor.userName);
        loadConversation();


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.conversationMessageId = UUID.randomUUID();//dummy
                chatMessage.message = messageText;
                chatMessage.conversationId = conversationId;
                chatMessage.time = new Date();


                messageET.setText("");

                displayMessage(chatMessage);
                WebApiService service = new WebApiService(ChatMessage.class, true);
                service.performPost(WebApiActions.PostChatMessage(), chatMessage);


            }
        });


    }

    public void displayMessage(final ChatMessage message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                adapter.add(message);
                adapter.notifyDataSetChanged();
                scroll();
            }
        });

    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }


    private void loadConversation() {

        chatHistory = new ArrayList<ChatMessage>();
        String path = WebApiActions.GetConversation() + "/" + this.actor.userId.toString();
        WebApiService service = new WebApiService(ConversationsData.class, true);
        service.performGet(path, new IWebApiResultListener<ConversationsData>() {
            @Override
            public void onResult(ConversationsData objResult) {
                setConversation(objResult);
            }

            @Override
            public void onError(Exception err) {
                ((LpsApplication) getApplicationContext()).HandleError(err);
            }
        });
    }

    private void setConversation(ConversationsData data) {
        if (data != null) {
            this.conversationId = data.conversationId;
            for (ChatMessage msg : data.messages) {
                chatHistory.add(msg);
            }
        }


        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        for (int i = 0; i < chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }
        this.mProgressBar.setVisibility(View.GONE);
    }

    private void newChatMessageRecived(ChatMessage chatMessage) {
        displayMessage(chatMessage);
    }
}
