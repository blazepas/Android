package pl.jacek.jablonka.android.tpp.utilities;

import pl.jacek.jablonka.android.tpp.MainActivity;
import pl.jacek.jablonka.android.tpp.R;
import pl.jacek.jablonka.android.tpp.TPPApp;
import pl.jacek.jablonka.android.tpp.verification.PremiumUtilities;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ReminderService extends IntentService implements FinalVariables {
    private static final int NOTIF_ID = 1;
    CommonUtilities utilities = TPPApp.getUtilities();

    public ReminderService(){
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
       
        if (!PremiumUtilities.APP_VERSION_NONE) {
        	runNotification(intent);
        }
    }
    
    private void runNotification(Intent intent) {
    	 NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
         long when = System.currentTimeMillis();         // notification time
         MainActivity.notification = true;
         
         String productName = intent.getStringExtra("productName");
 		String productId = intent.getStringExtra("productId");
         String endTime = intent.getStringExtra("endDate");
         
         String productTxt = getString(R.string.message_product);
         String passesFor = getString(R.string.message_passes_for);
         long endTimeInMillis;
 		try {
 			endTimeInMillis = utilities.parseDate(endTime);
 		} catch (Exception e) {
 			endTimeInMillis = 0;
 		}
         String peroid =  utilities.dateToWords(when, endTimeInMillis);
         String message = productTxt + ": " + productName + " " + passesFor + " " + peroid;
         
         Notification notification = new Notification(R.drawable.icon, "TPP", when);
         notification.defaults |= Notification.DEFAULT_SOUND;
         notification.flags |= notification.FLAG_AUTO_CANCEL;
         
         Intent notificationIntent = new Intent(this, MainActivity.class);
         notificationIntent.putExtra("productId", productId);
         PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent , 0);
        
         notification.setLatestEventInfo(getApplicationContext(), "TPP", message, contentIntent);
         nm.notify(NOTIF_ID, notification);
    }

}
