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
public class Areas extends ArrayList<Area> {

    SharedPreferences instancePrefs;

    public Areas(SharedPreferences instancePrefs) {
        super();
        this.instancePrefs = instancePrefs;
    }

    public Areas getSelectedAreas() {
        Areas retval = new Areas(instancePrefs);
        String[] selectedareas_str = instancePrefs.getString("selectedareas", "").split(":");
        ArrayList<Integer> selectedAreas = new ArrayList<Integer>();
        for (String s : selectedareas_str) {
            if (!s.equals("")) {
                selectedAreas.add(Integer.parseInt(s));
            }
        }

        for (Integer i : selectedAreas) {
            if (getById(i) != null) {
                retval.add(getById(i));
            }
        }
        return retval;
    }

    public boolean isSelected(int areapos) {
        try {
            Area myarea = this.get(areapos);
            if (getSelectedAreas().getById(myarea.getId()) != null) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public void setSelectedArea(Area area, boolean value) {
        Area myarea = getById(area.getId());
        if (myarea == null) {
            return;
        }
        String selectedAreasString = instancePrefs.getString("selectedareas", "");
        if (value) {
            //Shall be selected
            if (getSelectedAreas().getById(myarea.getId()) == null) {
                //is not selected
                selectedAreasString = selectedAreasString + ":" + myarea.getId();
                LQFBInstances.selectionUpdatesForRefresh = true;
            } else {
                //is already selected - change noting
            }
        } else {
            //Shall not be selected
            if (getSelectedAreas().getById(myarea.getId()) == null) {
                //is not selected do nothing
            } else {
                //is selected, unselect

                String newselectedareas = ":";
                for (String snippet : selectedAreasString.split(":")) {
                    if (!snippet.equals(myarea.getId() + "") && (!snippet.equals(""))) {
                        newselectedareas += snippet + ":";
                    }
                }
                LQFBInstances.selectionUpdatesForRefresh = true;

                selectedAreasString = newselectedareas;
            }

        }
        SharedPreferences.Editor editor = instancePrefs.edit();
        selectedAreasString = selectedAreasString.replaceAll("::", ":");
        if (selectedAreasString.startsWith(":")) {
            selectedAreasString = selectedAreasString.substring(1);
        }
        if (selectedAreasString.endsWith(":")) {
            selectedAreasString = selectedAreasString.substring(0, selectedAreasString.length() - 1);
        }
        editor.putString("selectedareas", selectedAreasString);
        editor.commit();
    }

    public Area getByName(String searchname) {

        for (Area area : this) {
            if (area.getName().equals(searchname)) {
                return area;
            }
        }
        return null;
    }

    public Area getById(int search) {

        for (Area area : this) {
            if (area != null) {
                if (area.getId() == search) {
                    return area;
                }
            }
        }
        return null;
    }
}
