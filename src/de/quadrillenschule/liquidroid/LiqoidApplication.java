/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;
import de.quadrillenschule.liquidroid.model.CachedAPI1Queries;
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
            l.lqfbInstanceChanged();

        }
    }

    public SharedPreferences getGlobalPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
      //  return getSharedPreferences("liqoid", android.content.Context.MODE_PRIVATE);
    }
    public static final String VIBRATE_PREF = "vibrate", REDLIMIT_PREF = "redlimit", ORANGELIMIT_PREF = "orangelimit", YELLOWLIMIT_PREF = "yellowlimit";

    public void setDefaultGlobalPreferencesIfNotExists() {
        long oneday = 1000 * 60 * 60 * 24;
        if (!getGlobalPreferences().contains(VIBRATE_PREF)) {
            getGlobalPreferences().edit().putBoolean(VIBRATE_PREF, true).commit();
        }
        if (!getGlobalPreferences().contains(REDLIMIT_PREF)) {
            getGlobalPreferences().edit().putLong(REDLIMIT_PREF, oneday).commit();
        }
        if (!getGlobalPreferences().contains(ORANGELIMIT_PREF)) {
            getGlobalPreferences().edit().putLong(ORANGELIMIT_PREF, oneday * 3).commit();
        }
        if (!getGlobalPreferences().contains(YELLOWLIMIT_PREF)) {
            getGlobalPreferences().edit().putLong(YELLOWLIMIT_PREF, oneday * 5).commit();
        }
    }

    public void toast(Context context, CharSequence charseq) {
        Toast mytoast = Toast.makeText(context, charseq, Toast.LENGTH_LONG);
        mytoast.show();
    }
}
