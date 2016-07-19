package com.lps.lpsapp.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.lps.lpsapp.LpsApplication;
import com.lps.lpsapp.R;
import com.lps.lpsapp.activities.BookingHistoryActivity;
import com.lps.lpsapp.activities.ChatActivity;
import com.lps.lpsapp.viewModel.booking.BookingJoinRoomData;
import com.lps.lpsapp.viewModel.booking.BookingStateEnum;
import com.lps.lpsapp.viewModel.chat.Actor;
import com.lps.lpsapp.viewModel.chat.ChatMessage;
import com.lps.lpsapp.viewModel.chat.DevicePosition;
import com.lps.webapi.AccessToken;
import com.lps.webapi.JsonSerializer;
import com.lps.webapi.OAuth2Credentials;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.StateChangedCallback;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.Subscription;
import microsoft.aspnet.signalr.client.transport.NegotiationException;

/**
 * Created by dle on 06.10.2015.
 */
public class PushService extends Service {
    private static String TAG = "PushService";
    private final IBinder mBinder = new LocalBinder();
    private HubProxy proxy;
    private HubConnection conn;
    private SignalRFuture<Void> sf;
    private String deviceId;
    private List<DevicePositionNotifier> positionConsumers = new ArrayList<DevicePositionNotifier>();
    private List<BookingStateNotofier> bookingStateConsumers = new ArrayList<BookingStateNotofier>();
    private List<ChatNotifier> chatConsumers = new ArrayList<ChatNotifier>();
    private Action<JsonElement[]> onJoinChat = new Action<JsonElement[]>() {
        @Override
        public void run(JsonElement[] jsonElements) throws Exception {
            if (positionConsumers.isEmpty()) {
                return;
            }

            try {
                Actor actor = JsonSerializer.deserialize(jsonElements[0].toString(), Actor.class);
                if (chatConsumers.size() > 0) {
                    for (ChatNotifier consumer : chatConsumers) {
                        consumer.joinChat(actor);
                    }
                } else {
                    //notifyNewChatMessage(msg);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

        }


    };
    private Action<JsonElement[]> onLeaveChat = new Action<JsonElement[]>() {
        @Override
        public void run(JsonElement[] jsonElements) throws Exception {
            if (positionConsumers.isEmpty()) {
                return;
            }

            try {
                Actor actor = JsonSerializer.deserialize(jsonElements[0].toString(), Actor.class);
                if (chatConsumers.size() > 0) {
                    for (ChatNotifier consumer : chatConsumers) {
                        consumer.leaveChat(actor);
                    }
                } else {
                    //notifyNewChatMessage(msg);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

        }


    };
    private Action<JsonElement[]> onPositionChanged = new Action<JsonElement[]>() {
        @Override
        public void run(JsonElement[] jsonElements) throws Exception {
            if (positionConsumers.isEmpty()) {
                return;
            }

            try {
                DevicePosition position = JsonSerializer.deserialize(jsonElements[0].toString(), DevicePosition.class);
                if (position.deviceId.equals(deviceId)) {
                    return;
                }
                for (DevicePositionNotifier consumer : positionConsumers) {
                    consumer.positionChanged(position);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

        }


    };
    private Action<JsonElement[]> onBookingStateChanged = new Action<JsonElement[]>() {
        @Override
        public void run(JsonElement[] jsonElements) throws Exception {

            try {
                if (jsonElements.length > 0) {
                    BookingJoinRoomData model = JsonSerializer.deserialize(jsonElements[0].toString(), BookingJoinRoomData.class);
                    if (model.getBookingState() == BookingStateEnum.Accepted) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(PushService.this, "Ваш заказ принят. Вы можете отправить план помещения с выбранным Вами столиком Вашим друзьям.", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
                    } else if (model.getBookingState() == BookingStateEnum.Rejected) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(PushService.this, "Ваш заказ rejected.", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
                    }
                    if (bookingStateConsumers.size() == 0) {
                        notifyBookingStateChanged(model);
                    } else {
                        for (BookingStateNotofier consumer : bookingStateConsumers) {
                            consumer.bookingStateChanged();
                        }
                    }

                } else {
                    if (bookingStateConsumers.size() > 0) {
                        for (BookingStateNotofier consumer : bookingStateConsumers) {
                            consumer.bookingStateChanged();
                        }
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

        }


    };
    private Action<JsonElement[]> onReservationModelChanged = new Action<JsonElement[]>() {
        @Override
        public void run(JsonElement[] jsonElements) throws Exception {

            try {

                if (bookingStateConsumers.size() > 0) {
                    for (BookingStateNotofier consumer : bookingStateConsumers) {
                        consumer.bookingStateChanged();
                    }
                }


            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

        }


    };
    private Action<JsonElement[]> onNewChatMessage = new Action<JsonElement[]>() {
        @Override
        public void run(JsonElement[] jsonElements) throws Exception {

            try {
                ChatMessage msg = JsonSerializer.deserialize(jsonElements[0].toString(), ChatMessage.class);
                if (chatConsumers.size() > 0) {
                    for (ChatNotifier consumer : chatConsumers) {
                        consumer.messageResived(msg);
                    }
                } else {
                    //notifyNewChatMessage(msg);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

        }


    };

    public void setActorPositionListener(DevicePositionNotifier consumer) {
        this.positionConsumers.add(consumer);
    }

    public void removeActorPositionListener(DevicePositionNotifier consumer) {
        this.positionConsumers.remove(consumer);
    }

    public void setBookingStateListener(BookingStateNotofier consumer) {
        this.bookingStateConsumers.add(consumer);
    }

    public void removeBookingStateListener(BookingStateNotofier consumer) {
        this.bookingStateConsumers.remove(consumer);
    }

    public void setChatListener(ChatNotifier consumer) {
        this.chatConsumers.add(consumer);
    }

    public void removeChatMessageListener(ChatNotifier consumer) {
        this.chatConsumers.remove(consumer);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // The service is being created

        this.deviceId = ((LpsApplication) getApplication()).getAndroidId();
        // Connect to the server
        conn = new HubConnection(WebApiActions.Subscribe(), "", true, new Logger() {

            @Override
            public void log(String message, LogLevel level) {
                Log.d(TAG, message);
            }
        });
        if (AccessToken.CurrentToken != null) {
            OAuth2Credentials credentials = new OAuth2Credentials(AccessToken.CurrentToken.access_token);
            conn.setCredentials(credentials);
        }
        // Create the hub proxy
        proxy = conn.createHubProxy("LpsHub");


        // Subscribe to the error event
        conn.error(new ErrorCallback() {

            @Override
            public void onError(Throwable error) {
                ((LpsApplication) getApplication()).HandleError(new Exception(error));
            }
        });

        // Subscribe to the connected event
        conn.connected(new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, "CONNECTED");
            }
        });

        // Subscribe to the closed event
        conn.closed(new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, "DISCONNECTED");
            }
        });

        conn.stateChanged(new StateChangedCallback() {
            @Override
            public void stateChanged(ConnectionState connectionState, ConnectionState connectionState1) {
                Log.d(TAG, "oldState:" + connectionState + " newState: " + connectionState1);
            }
        });


        // Subscribe to the received event
        /*conn.received(new MessageReceivedHandler() {

            @Override
            public void onMessageReceived(JsonElement json) {
                Log.d(TAG, "RAW received message: " + json.toString());
            }
        });*/

        Subscription positionchangedSubscription = proxy.subscribe("devicePositionChanged");
        positionchangedSubscription.addReceivedHandler(onPositionChanged);

        Subscription bookingSubscription = proxy.subscribe("bookingStateChanged");
        bookingSubscription.addReceivedHandler(onBookingStateChanged);

        Subscription reservationModelSubscription = proxy.subscribe("changeTableReservationModel");
        reservationModelSubscription.addReceivedHandler(onReservationModelChanged);

        Subscription chatSubscription = proxy.subscribe("newchatmessage");
        chatSubscription.addReceivedHandler(onNewChatMessage);

        Subscription joinChatSubscription = proxy.subscribe("joinchat");
        joinChatSubscription.addReceivedHandler(onJoinChat);

        Subscription leaveChatSubscription = proxy.subscribe("leavechat");
        leaveChatSubscription.addReceivedHandler(onLeaveChat);

        sf = conn.start();

        sf.onError(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                if (throwable instanceof NegotiationException) {
                    sf.cancel(true);
                    ((LpsApplication) getApplication()).HandleError(new Exception(throwable));

                } else {
                    Log.e(TAG, "sf: " + throwable.toString(), throwable);
                    ((LpsApplication) getApplication()).HandleError(new Exception(throwable));
                }
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        // All clients have unbound with unbindService()
        //proxy.removeSubscription("devicepositionchanged");
        //proxy.removeSubscription("bookingstatechanged");
        //proxy.removeSubscription("newchatmessage");
        //if(conn.getState() != ConnectionState.Disconnected) {
        //    conn.stop();
        //}
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind");
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        proxy.removeSubscription("devicepositionchanged");
        proxy.removeSubscription("bookingstatechanged");
        proxy.removeSubscription("newchatmessage");
        proxy.removeSubscription("joinchat");
        proxy.removeSubscription("leavechat");
        proxy.removeSubscription("changetablereservationmodel");
        if (conn.getState() != ConnectionState.Disconnected) {
            try {
                conn.stop();
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }

        }
        super.onDestroy();
    }

    public void resetCredentials() {
        this.onDestroy();
        this.onCreate();
//        if(AccessToken.CurrentToken != null) {
//            OAuth2Credentials credentials = new OAuth2Credentials(AccessToken.CurrentToken.access_token);
//            conn.setCredentials(credentials);
//            conn.disconnect();
//            this.sf = conn.start();
//        }
    }

    public void joinPositionConsumerGroup(final UUID mapId) {
        try {
            if (this.sf.isDone()) {
                String groupName = "PositionConsumer" + ":" + mapId.toString();
                proxy.invoke("joinGroup", groupName);
            } else {
                this.sf.done(new Action<Void>() {

                    @Override
                    public void run(Void obj) throws Exception {

                        String groupName = "PositionConsumer" + ":" + mapId.toString();
                        proxy.invoke("joinGroup", groupName);
                    }
                });
            }

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    public void leavePositionConsumerGroup(final UUID mapId) {
        try {
            if (this.sf.isDone()) {
                String groupName = "PositionConsumer" + ":" + mapId.toString();
                proxy.invoke("leaveGroup", groupName);
            } else {
                this.sf.done(new Action<Void>() {

                    @Override
                    public void run(Void obj) throws Exception {
                        String groupName = "PositionConsumer" + ":" + mapId.toString();
                        proxy.invoke("leaveGroup", groupName);
                    }
                });
            }

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    public void joinReservationModelGroup(final UUID mapId) {
        try {
            if (this.sf.isDone()) {
                if (conn.getState() == ConnectionState.Disconnected) {
                    conn.start();
                }
                String groupName = "ReservationModel" + ":" + mapId.toString();
                proxy.invoke("joinGroup", groupName);
            } else if (sf.isCancelled()) {
                return;
            } else {
                this.sf.done(new Action<Void>() {

                    @Override
                    public void run(Void obj) throws Exception {
                        String groupName = "ReservationModel" + ":" + mapId.toString();
                        proxy.invoke("joinGroup", groupName);
                    }
                });
            }


        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    public void leaveReservationModelGroup(final UUID mapId) {
        try {
            if (sf.isDone()) {
                if (conn.getState() == ConnectionState.Disconnected) {
                    conn.start();
                }
                String groupName = "ReservationModel" + ":" + mapId.toString();
                proxy.invoke("leaveGroup", groupName);
            } else if (sf.isCancelled()) {
                return;
            } else {
                this.sf.done(new Action<Void>() {

                    @Override
                    public void run(Void obj) throws Exception {
                        String groupName = "ReservationModel" + ":" + mapId.toString();
                        proxy.invoke("leaveGroup", groupName);
                    }
                });
            }


        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    private void notifyNewChatMessage(final ChatMessage msg) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(PushService.this, "New chat message", Toast.LENGTH_LONG);
                toast.show();
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(msg.message)
                                .setContentText(msg.message);
                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
                Actor actor = new Actor();
                //actor.userId = msg.
                //resultIntent.putExtra("id",model.roomId);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());

                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(ChatActivity.class);

                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                mNotificationManager.notify(1, mBuilder.build());

            }
        });
    }

    private void notifyBookingStateChanged(final BookingJoinRoomData model) {
        try {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(PushService.this, "Booking State changed", Toast.LENGTH_LONG);
                    toast.show();
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setContentTitle(model.name)
                                    .setContentText(model.getBookingState().toString());
                    // Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(getApplicationContext(), BookingHistoryActivity.class);
                    resultIntent.putExtra("id", model.roomId);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());

                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(BookingHistoryActivity.class);

                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(1, mBuilder.build());

                }
            });


        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }


    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public PushService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PushService.this;
        }
    }
}
