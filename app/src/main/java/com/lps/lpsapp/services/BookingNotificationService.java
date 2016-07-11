package com.lps.lpsapp.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.lps.lpsapp.R;
import com.lps.lpsapp.activities.BookingHistoryActivity;
import com.lps.lpsapp.viewModel.booking.BookingJoinRoomData;
import com.lps.webapi.AccessToken;
import com.lps.webapi.JsonSerializer;
import com.lps.webapi.OAuth2Credentials;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.Subscription;
import microsoft.aspnet.signalr.client.transport.NegotiationException;

/**
 * Created by dle on 06.10.2015.
 */
public class BookingNotificationService extends Service {
    private static String TAG = "BookingNotificationService";
    private HubProxy proxy;
    private HubConnection conn;
    private Logger logger;
    private int startId;

    @Override
    public void onCreate() {
        super.onCreate();
        // The service is being created

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startId = startId;
        // If we get killed, after returning from here, restart
        logger = new Logger() {

            @Override
            public void log(String message, LogLevel level) {
                Log.d(TAG, message);
            }
        };

        ErrorCallback errorCallback = new ErrorCallback() {

            @Override
            public void onError(Throwable error) {
                if(error instanceof NegotiationException)
                {
                    try {

                    }
                    catch (Exception ex)
                    {
                        Log.e(TAG, ex.getMessage(), ex);
                    }
                }
                Log.e(TAG, error.getMessage(), error);
            }
        };
        // Connect to the server
        conn = new HubConnection(WebApiActions.Subscribe(), "", true, logger);
        if(AccessToken.CurrentToken != null) {
            OAuth2Credentials credentials = new OAuth2Credentials(AccessToken.CurrentToken.access_token);
            conn.setCredentials(credentials);
        }
        // Create the hub proxy
        proxy = conn.createHubProxy("LpsHub");


        // Subscribe to the error event
        conn.error(errorCallback);

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



        // Subscribe to the received event
        conn.received(new MessageReceivedHandler() {
            @Override
            public void onMessageReceived(JsonElement json) {
                //Log.d(TAG, "RAW received message: " + json.toString());
            }
        });



        Subscription bookingSubscription = proxy.subscribe("bookingStateChanged");
        bookingSubscription.addReceivedHandler(onBookingStateChanged);

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.conn.disconnect();
    }

    private Action<JsonElement[]> onBookingStateChanged =  new Action<JsonElement[]>() {
        @Override
        public void run(JsonElement[] jsonElements) throws Exception {

            try {
                final BookingJoinRoomData model = JsonSerializer.deserialize(jsonElements[0].toString(), BookingJoinRoomData.class);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(BookingNotificationService.this, "Booking State changed", Toast.LENGTH_LONG);
                        toast.show();
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentTitle(model.name)
                                        .setContentText(model.getBookingState().toString());
                        // Creates an explicit intent for an Activity in your app
                        Intent resultIntent = new Intent(getApplicationContext(), BookingHistoryActivity.class);
                        resultIntent.putExtra("id",model.roomId);
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

                stopSelf(startId);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

        }


    };
}
