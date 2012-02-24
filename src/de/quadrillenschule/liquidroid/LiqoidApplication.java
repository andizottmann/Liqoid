/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.app.Application;
import de.quadrillenschule.liquidroid.model.LQFBInstances;
import java.io.File;

/**
 *
 * @author andi
 */
public class LiqoidApplication extends Application {

    public LQFBInstances lqfbInstances;

    public LiqoidApplication() {
        super();
  
    }

    @Override
    public void onCreate(){
        lqfbInstances = new LQFBInstances();
        lqfbInstances.initFromFileOrDefaults(new File(getExternalFilesDir(null), "liqoid.xml"));

    }
    public File getApplicationFile() {
        return new File(getExternalFilesDir(null), "liqoid.xml");
    }
}
