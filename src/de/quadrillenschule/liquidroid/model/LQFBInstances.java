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

    private ArrayList<LQFBInstance> lockedInstances;
    LiqoidApplication liqoidApplication;
    public static boolean selectionUpdatesForRefresh = false;

    public LQFBInstances(LiqoidApplication liqoidApplication) {
        super();
        lockedInstances = new ArrayList();
        this.liqoidApplication = liqoidApplication;
        initInstances();
        //  initFromFileOrDefaults();
    }

    @Override
    public boolean add(LQFBInstance toAdd) {
        for (LQFBInstance li : this) {
            if (li.getShortName().equals(toAdd.getShortName())) {
                //No duplicates allowed
                return false;
            }
        }
        if (toAdd.getDeveloperkey().equals("")) {
            for (LQFBInstance lockedi : getLockedInstances()) {
                if (lockedi.getShortName().equals(toAdd.getShortName())) {
                    return false;
                }
            }
            return getLockedInstances().add(toAdd);
        } else {
            LQFBInstance toRemove = null;
            for (LQFBInstance lockedi : getLockedInstances()) {
                if (lockedi.getShortName().equals(toAdd.getShortName())) {
                    toRemove = lockedi;
                }
            }
            if (toRemove != null) {
                getLockedInstances().remove(toRemove);
            }
            return super.add(toAdd);
        }

    }

    public void initInstances() {

        /*       this.add(new LQFBInstance(liqoidApplication, "PP Bund 2.0", "DE_PIRATEN_BUND_2",
        "Piraten Bund (2.0)",
        "http://apitest.liquidfeedback.org:25520/",
        "https://lqfb.piratenpartei.de/lf/",
        "gV4LWzzXrHQTfPFwN6p7", LQFBInstance.API2));

        this.add(new LQFBInstance(liqoidApplication, "PP Bund (alt)", "DE_PIRATEN_BUND",
        "Piraten Bund (alt)",
        "https://lqfb.piratenpartei.de/pp/api/",
        "https://lqfb.piratenpartei.de/pp/",
        "6Bw8HGL8Bp2z4wK6L3Zw", LQFBInstance.API1));*/
        this.add(new LQFBInstance(liqoidApplication, "PP Deutschland", "DE_PIRATEN_BUND_2",
                "Piraten Deutschland",
                "https://lqfb.piratenpartei.de/api/",
                "https://lqfb.piratenpartei.de/lf/",
                "anonymous", LQFBInstance.API2));
        this.add(new LQFBInstance(liqoidApplication, "PP Österreich", "AT_PIRATEN_BUND_2",
                "Piraten Österreich",
                "http://88.198.24.116:25520/",
                "http://lqfb.piratenpartei.at/",
                "anonymous", LQFBInstance.API2));
        /*
        this.add(new LQFBInstance(liqoidApplication, "PP Berlin", "DE_PIRATEN_BE",
        "Piraten Berlin",
        "https://lqpp.de/be/api/",
        "https://lqpp.de/be/", "Y5jJ3mzf9MN23Q7zRpWs", LQFBInstance.API1));
         */

  /*      this.add(new LQFBInstance(liqoidApplication, "PP BB", "DE_PIRATEN_BB",
                "Piraten Brandenburg",
                "https://lqpp.de/bb/api/",
                "https://lqpp.de/bb/", "5vvw7LD6P5FShL4yFNQK", LQFBInstance.API1));
*/
        this.add(new LQFBInstance(liqoidApplication, "PP HS", "DE_PIRATEN_HS",
                "Piraten Hessen",
                "https://lqfb.piratenpartei-hessen.de/api/",
                "https://lqfb.piratenpartei-hessen.de/", "B8xrGhySfKXTFXpqJxYZ", LQFBInstance.API1));
// "MjXNjRD3qSYbgjKrhYgC"
     /*   this.add(new LQFBInstance(liqoidApplication, "PP MV", "DE_PIRATEN_MV",
        "Piraten Mecklenburg-Vorpommern",
        "https://lqpp.de/mv/api/",
        "https://lqpp.de/mv/",
        "VpmyJGYbqTQPcc9wyzzk", LQFBInstance.API2));*/

        /*    this.add(new LQFBInstance(liqoidApplication, "PP NRW", "DE_PIRATEN_NRW",
        "Piraten Nordrhein-Westfalen",
        "https://lqpp.de/nw/api/",
        "https://lqpp.de/nw/",
        "pxzcNHJWMNKRs5Kr3HnX", LQFBInstance.API1));*/
        /*
        this.add(new LQFBInstance(liqoidApplication, "PP LSA", "DE_PIRATEN_LSA",
        "Piraten Sachsen-Anhalt 2",
        "http://api.lqfb.piraten-lsa.de/",
        "http://lqfb.piraten-lsa.de/lsa/",
        "anonymous", LQFBInstance.API2));*/

        this.add(new LQFBInstance(liqoidApplication, "PP HH", "HH",
                "Piraten Hamburg",
                "https://lqpp.de/hh/api/",
                "https://lqpp.de/hh/",
                "", LQFBInstance.API1));
        this.add(new LQFBInstance(liqoidApplication, "PP NDS", "DE_PIRATEN_NDS",
                "Piraten NDS",
                "https://lqpp.de/ni/api/",
                "https://lqpp.de/ni/",
                "", LQFBInstance.API1));
        //YJNdCRv64rRyb3DkW8XH
      /*  this.add(new LQFBInstance(liqoidApplication, "HSG Potsdam", "DE_HSG_Potsdam",
        "Hochschulgr. Potsdam",
        "http://potsdam.lquni.de/api/",
        "http://potsdam.lquni.de/",
        "YJNdCRv64rRyb3DkW8XH", LQFBInstance.API1));*/
        this.add(new LQFBInstance(liqoidApplication, "Test LF2", "DE_LF_TEST_2",
                "LQFB Test 2",
                "http://apitest.liquidfeedback.org:25520/",
                "http://dev.liquidfeedback.org/lf2/",
                "anonymous", LQFBInstance.API2));

    }

    public LQFBInstance getSelectedInstance() {

        SharedPreferences prefs = liqoidApplication.getGlobalPreferences();
        return this.get(prefs.getInt("selectedinstance", 0));

    }

    public void setSelectedInstance(String shortName) {
        int i = 0;
        for (LQFBInstance l : this) {
            if (l.getShortName().equals(shortName)) {
                setSelectedInstance(i);
            }
            i++;
        }
    }

    private void setSelectedInstance(int id) {
        SharedPreferences prefs = liqoidApplication.getGlobalPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("selectedinstance", id);
        editor.commit();

    }

    /**
     * @return the lockedInstances
     */
    public ArrayList<LQFBInstance> getLockedInstances() {
        return lockedInstances;
    }

    public CharSequence[] getLockedInstancesNames() {
        CharSequence[] retval = new String[lockedInstances.size()];
        int i = 0;
        for (LQFBInstance l : lockedInstances) {
            retval[i] = (l.getName());
            i++;
        }
        return retval;
    }

    public CharSequence[] getUnLockedInstancesNames() {
        CharSequence[] retval = new String[this.size()];
        int i = 0;
        for (LQFBInstance l : this) {
            retval[i] = (l.getShortName());
            i++;
        }
        return retval;
    }
}
