package com.thoughtworks.studios.driod.citracker.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.thoughtworks.studios.driod.citracker.R;
import com.thoughtworks.studios.driod.citracker.activity.PipelineList;

public class LocalService extends Service {
    private NotificationManager mNM;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public LocalService getService() {
            return LocalService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    Runnable refreshFeedsTask = new Runnable() {
        public void run() {
            // Normally we would do some work here...  for our sample, we will
            // just sleep for 30 seconds.
            long endTime = System.currentTimeMillis() + 15*1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (mBinder) {
                    try {
                        mBinder.wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }

// Done with our work...  stop the service!
            LocalService.this.stopSelf();
            showNotification();
        }
    };

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(R.string.local_service_stopped);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.icon, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, PipelineList.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                text, contentIntent);

        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNM.notify(R.string.local_service_started, notification);
    }
}