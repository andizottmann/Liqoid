/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import de.quadrillenschule.liquidroid.model.Area;
import de.quadrillenschule.liquidroid.model.CachedAPI1Queries;
import de.quadrillenschule.liquidroid.model.Initiative;
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
        cachedAPI1Queries=new CachedAPI1Queries(getCacheDir());
        lqfbInstances = new LQFBInstances(this);
        lqfbInstanceChangeListeners = new ArrayList<LQFBInstanceChangeListener>();


    }





    public void saveSelectedIssuesToPrefs() {
        for (LQFBInstance lin : lqfbInstances) {
            SharedPreferences prefs = getSharedPreferences(lin.getPrefsName(), MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String selectedinis = "";
            for (Area a : lin.areas) {
                for (Initiative ini : a.getInitiativen()) {
                    if (ini.isSelected()) {
                        selectedinis += ini.issue_id + ":";
                    }
                }
            }
            editor.putString("selectedissues", selectedinis);
            editor.commit();
        }
    }

    public void loadSelectedIssuesFromPrefs() {
        for (LQFBInstance lin : lqfbInstances) {
            SharedPreferences prefs = getSharedPreferences(lin.getPrefsName(), MODE_PRIVATE);
            String[] selectedInis_str = prefs.getString("selectedissues", "0").split(":", 0);
            ArrayList<Integer> selectedInis = new ArrayList<Integer>();
            for (String s : selectedInis_str) {
                try {
                    selectedInis.add(Integer.parseInt(s));
                } catch (Exception e) {
                }
            }
            for (Integer i : selectedInis) {
                for (Area a:lin.areas){

                try {   
                    for (Initiative ini:a.getInitiativen().findByIssueID(i)){
                    ini.setSelected(true);
                    }
                } catch (Exception e) {
                }}
            }
        }
    }

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

    public void toast(Context context, CharSequence charseq) {
        Toast mytoast = Toast.makeText(context, charseq, Toast.LENGTH_LONG);
        mytoast.show();
    }
}
