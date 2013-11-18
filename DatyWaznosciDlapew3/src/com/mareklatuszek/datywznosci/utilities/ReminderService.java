package com.mareklatuszek.datywznosci.utilities;

import com.mareklatuszek.datywaznosci.MainActivity;
import com.mareklatuszek.datywaznosci.R;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ReminderService extends IntentService {
    private static final int NOTIF_ID = 1;

    public ReminderService(){
        super("ReminderService");
    }

    @Override
      protected void onHandleIntent(Intent intent) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        long when = System.currentTimeMillis();         // notification time
        Notification notification = new Notification(R.drawable.ic_launcher, "reminder", when);
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.flags |= notification.FLAG_AUTO_CANCEL;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent , 0);
        notification.setLatestEventInfo(getApplicationContext(), "Test", "Testowy", contentIntent);
        nm.notify(NOTIF_ID, notification);
    }

}
