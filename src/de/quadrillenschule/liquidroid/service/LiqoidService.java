/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import de.quadrillenschule.liquidroid.LiqoidApplication;
import de.quadrillenschule.liquidroid.LiqoidMainActivity;
import de.quadrillenschule.liquidroid.R;
import de.quadrillenschule.liquidroid.model.RefreshInisListThread;
import de.quadrillenschule.liquidroid.model.RefreshInisListThread.RefreshInisListListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andi
 */
public class LiqoidService extends IntentService {

    public RefreshInisListThread ralt;

    public LiqoidService() {
        super("LiqoidService");

    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        notification(getString(R.string.updating) + "...");

        Handler handler = new Handler() {
// public static int FINISH_OK = 0, DOWNLOADING = 1, DOWNLOAD_ERROR = -1, DOWNLOAD_RETRY = 2, UPDATING = 4, DOWNLOADING_INSTANCE = 5, READ_CACHE = 6;

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == ralt.FINISH_OK) {
                //    notification(getString(R.string.refreshedcontent));

                } else {
                    //         notification(getString(R.string.updating) + ": " + msg.what);
                }

            }
        };
        LiqoidApplication la = ((LiqoidApplication) getApplication());
        ralt = new RefreshInisListThread(true, la.refreshInisListListeners, handler, la);

        ralt.start();
        try {
            ralt.join(1000 * 60 * 15);
            notification(getString(R.string.refreshedcontent));

        } catch (InterruptedException ex) {
            //    notification("interrupted");
            //      Logger.getLogger(LiqoidService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    Notification notification;

    public void notification(String text) {
        LiqoidApplication la = ((LiqoidApplication) getApplication());

        if (!la.getGlobalPreferences().getBoolean("serviceiconenabled", true)) {
            return;
        }
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        int icon = R.drawable.lqfb;
        CharSequence tickerText = text;
        long when = System.currentTimeMillis();
        notification = new Notification(icon, tickerText, when);
        Context context = getApplicationContext();
        CharSequence contentTitle = getString(R.string.app_name);
        CharSequence contentText = tickerText;
        Intent notificationIntent = new Intent(this, LiqoidMainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        mNotificationManager.notify(1, notification);
    }
}
