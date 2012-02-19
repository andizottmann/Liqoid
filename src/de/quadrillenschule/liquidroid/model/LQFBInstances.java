/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 *
 * @author andi
 */
public class LQFBInstances extends ArrayList<LQFBInstance> {

    ArrayList<LQFBInstance> instances;
    Activity mainActivity;

    public LQFBInstances(Activity mainActivity) {
        this.mainActivity = mainActivity;
        this.load();
        if (size() <= 0) {
            this.add(new LQFBInstance(
                    "Piraten Bund",
                    "https://lqfb.piratenpartei.de/pp/api/",
                    "https://lqfb.piratenpartei.de/pp/",
                    "6Bw8HGL8Bp2z4wK6L3Zw", "1.x", true));
            this.add(new LQFBInstance(
                    "Testinstanz",
                    "https://lqfb.piratenptei.de/pp/api/",
                    "https://lqfb.piratenptei.de/pp/",
                    "6Bw8HGL8Bp2z4wzhL3Zw", "1.x", false));
        }
    }

    public LQFBInstance getSelectedInstance() {
        for (LQFBInstance instance : this) {
            if (instance.isSelected()) {
                return instance;
            }
        }
        return get(0);
    }

    public void setSelectedInstance(int id) {
        for (LQFBInstance instance : this) {
            instance.setSelected(false);
        }
        get(id).setSelected(true);
    }

    public String toXML() {
        String retval = "<?xml version=\"1.0\" encoding=\"utf-8\"?><lqfbinstances>";
        for (LQFBInstance instance : this) {
            retval += instance.toXML();
        }
        return retval + "</lqfbinstances>";
    }

    public void save() {


        try {
            FileOutputStream fos = mainActivity.openFileOutput("instanc.xml", Context.MODE_WORLD_WRITEABLE);
            fos.write(toXML().getBytes());
            fos.close();

        } catch (Exception e) {
            Toast toast = Toast.makeText(mainActivity.getApplicationContext(), "", Toast.LENGTH_LONG);

            toast.setText("Saving not ok :(");
            toast.show();
        }

    }

    public void load() {
        try {
            FileInputStream fis = mainActivity.openFileInput("instanc.xml");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxparser;
            LQFBInstancesFromFileParser parser = new LQFBInstancesFromFileParser(this);
            saxparser = factory.newSAXParser();
            saxparser.parse(fis, parser);
        } catch (Exception e) {
        }


//        Toast toast = Toast.makeText(mainActivity.getApplicationContext(), "", Toast.LENGTH_LONG);
//
//        toast.setText(this.size());
//        toast.show();
        //return 0;
    }
}
