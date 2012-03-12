/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import android.content.SharedPreferences;
import de.quadrillenschule.liquidroid.LiqoidApplication;
import java.util.ArrayList;

/**
 *
 * @author andi
 */
public class LQFBInstances extends ArrayList<LQFBInstance> {

    ArrayList<LQFBInstance> instances;
    LiqoidApplication liqoidApplication;
    public static boolean selectionUpdatesForRefresh=false;

    public LQFBInstances(LiqoidApplication liqoidApplication) {
        super();
        this.liqoidApplication = liqoidApplication;
        initInstances();
        //  initFromFileOrDefaults();
    }

    private void initInstances() {

        if (isEmpty()) {
             this.add(new LQFBInstance(liqoidApplication,"PP Bund","DE_PIRATEN_BUND",
                    "Piraten Bund",
                    "https://lqfb.piratenpartei.de/pp/api/",
                    "https://lqfb.piratenpartei.de/pp/",
                    "6Bw8HGL8Bp2z4wK6L3Zw", "1.x"));
              this.add(new LQFBInstance(liqoidApplication,"PP MV","DE_PIRATEN_MV",
                    "Piraten Mecklenburg-Vorpommern",
                    "https://lqpp.de/mv/api/",
                    "https://lqpp.de/mv/",
                    "VpmyJGYbqTQPcc9wyzzk", "1.x"));


            this.add(new LQFBInstance(liqoidApplication,"PP SA","DE_PIRATEN_SA",
                    "Piraten Sachsen-Anhalt",
                    "http://lqfb.piraten-lsa.de/lsa/api/",
                    "http://lqfb.piraten-lsa.de/lsa/",
                    "jXKWm5rFLQXQ8f6LMf92", "1.x"));
         /*   this.add(new LQFBInstance(liqoidApplication,"TEST1","DE_TESTINSTANZ_VALID",
                    "Testinstanz (valid URL)",
                    "http://dev.liquidfeedback.org/test/api/",
                    "http://dev.liquidfeedback.org/test/",
                    "GTR8MjH6x98w6mztGB7J", "1.x"));
           this.add(new LQFBInstance(liqoidApplication,"TEST2","DE_TESTINSTANZ_INVALID",
                    "Testinstanz (invalid URL)",
                    "https://lqfb.piratenptei.de/pp/api/",
                    "https://lqfb.piratenptei.de/pp/",
                    "6Bw8HGL8Bp2z4wzhL3Zw", "1.x"));*/

        }
    }

    public LQFBInstance getSelectedInstance() {

        SharedPreferences prefs = liqoidApplication.getSharedPreferences("liqoid", android.content.Context.MODE_PRIVATE);
        return this.get(prefs.getInt("selectedinstance", 0));

    }

    public void setSelectedInstance(int id) {
        SharedPreferences prefs = liqoidApplication.getSharedPreferences("liqoid", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("selectedinstance", id);
        editor.commit();
       
    }

}
