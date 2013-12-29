package utilities;

import pl.mareklatuszek.tpp.MainActivity;
import pl.mareklatuszek.tpp.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ReminderService extends IntentService implements FinalVariables {
    private static final int NOTIF_ID = 1;

    public ReminderService(){
        super("ReminderService");
    }

    @Override
      protected void onHandleIntent(Intent intent) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        long when = System.currentTimeMillis();         // notification time
        MainActivity.notification = true;
        
        String message = intent.getStringExtra("message");
		String productId = intent.getStringExtra("productId");
        String timeInMillis = intent.getStringExtra("timeInMillis");
        int intentId = 0;
        
        Notification notification = new Notification(R.drawable.ic_launcher, "TPP", when);
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.flags |= notification.FLAG_AUTO_CANCEL;
        
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("productId", productId);
        notificationIntent.putExtra("timeInMillis", timeInMillis);
        PendingIntent contentIntent = PendingIntent.getActivity(this, intentId, notificationIntent , 0);
       
        notification.setLatestEventInfo(getApplicationContext(), "Test", message, contentIntent);
        nm.notify(NOTIF_ID, notification);
        
    }

}
