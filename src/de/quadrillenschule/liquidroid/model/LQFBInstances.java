/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import de.quadrillenschule.liquidroid.LiqoidMainActivity;
import java.io.File;
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
 
    public LQFBInstances() {
  
    }

    public void initFromFileOrDefaults(File file){
        this.load(file);
        if (size() <= 0) {
            this.add(new LQFBInstance(
                    "Piraten Bund",
                    "https://lqfb.piratenpartei.de/pp/api/",
                    "https://lqfb.piratenpartei.de/pp/",
                    "6Bw8HGL8Bp2z4wK6L3Zw", "1.x", true));
            this.add(new LQFBInstance(
                    "Piraten Sachsen-Anhalt",
                    "http://lqfb.piraten-lsa.de/lsa/api/",
                    "http://lqfb.piraten-lsa.de/lsa/",
                    "jXKWm5rFLQXQ8f6LMf92", "1.x", true));
            this.add(new LQFBInstance(
                    "Testinstanz (valid URL)",
                    "http://dev.liquidfeedback.org/test/api/",
                    "http://dev.liquidfeedback.org/test/",
                    "GTR8MjH6x98w6mztGB7J", "1.x", false));
            this.add(new LQFBInstance(
                    "Testinstanz (invalid URL)",
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

    public int save(File file) {


        try {
            //    FileOutputStream fos  = getMainActivity().openFileOutput("instanc.xml", Context.MODE_WORLD_WRITEABLE);
         //   File file = new File(MainActivity().getExternalFilesDir(null), "liqoid.xml");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(toXML().getBytes());
            fos.close();

        } catch (Exception e) {
         return -1;
        }
return 0;
    }

    public int load(File file) {
        try {
         //   File file = new File(getMainActivity().getExternalFilesDir(null), "liqoid.xml");

            //      FileInputStream fis = getMainActivity().openFileInput("instanc.xml");
            FileInputStream fis = new FileInputStream(file);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxparser;
            LQFBInstancesFromFileParser parser = new LQFBInstancesFromFileParser(this);
            saxparser = factory.newSAXParser();
            saxparser.parse(fis, parser);
        } catch (Exception e) {
            return -1;
        }


//        Toast toast = Toast.makeText(mainActivity.getApplicationContext(), "", Toast.LENGTH_LONG);
//
//        toast.setText(this.size());
//        toast.show();
        return 0;
    }

  
}
