/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;
import de.quadrillenschule.liquidroid.model.Area;
import de.quadrillenschule.liquidroid.model.CachedAPI1Queries;
import de.quadrillenschule.liquidroid.model.LQFBInstance;
import de.quadrillenschule.liquidroid.model.LQFBInstances;
import de.quadrillenschule.liquidroid.tools.CrashLog;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author andi
 */
public class LiqoidApplication extends Application {

    public LQFBInstances lqfbInstances;
    ArrayList<LQFBInstanceChangeListener> lqfbInstanceChangeListeners;
    public CachedAPI1Queries cachedAPI1Queries;

    public LiqoidApplication() {
        super();

    }

    @Override
    public void onCreate() {
        Thread.setDefaultUncaughtExceptionHandler(new CrashLog(new File(getExternalFilesDir(null), "liqoid.log")));
        cachedAPI1Queries = new CachedAPI1Queries(getExternalCacheDir());
        lqfbInstances = new LQFBInstances(this);
        lqfbInstanceChangeListeners = new ArrayList<LQFBInstanceChangeListener>();
    }

    public void statusLineText(String text) {
        if (statusLine != null) {
            statusLine.setText(text);
        }
    }
    public TextView statusLine = null;

    public void addLQFBInstancesChangeListener(LQFBInstanceChangeListener l) {
        lqfbInstanceChangeListeners.add(l);
    }

    public void removeLQFBInstancesChangeListener(LQFBInstanceChangeListener l) {
        lqfbInstanceChangeListeners.remove(l);
    }

    void fireLQFBInstanceChangedEvent() {
        for (LQFBInstanceChangeListener l : lqfbInstanceChangeListeners) {
            l.onlyUpdateAreasListFromMemory();

        }
    }
 boolean dataIntegrityCheck() {
        try {
            for (LQFBInstance myInstance:lqfbInstances) {
                for (Area a : myInstance.areas) {
                    if (a.getInitiativen().size() > 0) {
                        return true;
                    }
                }
            }
        } catch (NullPointerException npe) {
            return false;
        }
        return false;
    }
    public SharedPreferences getGlobalPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
     }
    public static final String REDLIMIT_PREF = "redlimit", ORANGELIMIT_PREF = "orangelimit", YELLOWLIMIT_PREF = "yellowlimit";

    public void toast(Context context, CharSequence charseq) {
        Toast mytoast = Toast.makeText(context, charseq, Toast.LENGTH_LONG);
        mytoast.show();
    }

    public AlertDialog aboutDialog(final Activity context){
     AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.fullcredits)).setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).setNeutralButton(R.string.projecthome, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.projecthomeurl)));
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        context.startActivity(myIntent);
                    }
                }).setPositiveButton(R.string.userguide, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.userguideurl)));
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        context.startActivity(myIntent);
                    }
                });
                AlertDialog alert = builder.create();
                return alert;
    }
}
