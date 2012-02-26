/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.app.Application;
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


    public LiqoidApplication() {
        super();
  
    }

    @Override
    public void onCreate(){
        Thread.setDefaultUncaughtExceptionHandler(new CrashLog(new File(getExternalFilesDir(null), "liqoid.log")));

        lqfbInstances = new LQFBInstances();
        lqfbInstances.initFromFileOrDefaults(new File(getExternalFilesDir(null), "liqoid.xml"));
           lqfbInstanceChangeListeners=new ArrayList<LQFBInstanceChangeListener>();


    }
    public File getApplicationFile() {
        return new File(getExternalFilesDir(null), "liqoid.xml");
    }


    public void addLQFBInstancesChangeListener(LQFBInstanceChangeListener l){
    lqfbInstanceChangeListeners.add(l);
    }

    public void removeLQFBInstancesChangeListener(LQFBInstanceChangeListener l){
    lqfbInstanceChangeListeners.remove(l);
    }


    void fireLQFBInstanceChangedEvent() {
        for (LQFBInstanceChangeListener l : lqfbInstanceChangeListeners) {
            l.lqfbInstanceChanged();
        }
    }

}
