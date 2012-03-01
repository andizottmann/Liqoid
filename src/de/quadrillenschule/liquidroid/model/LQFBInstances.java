/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import de.quadrillenschule.liquidroid.LiqoidApplication;
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
    LiqoidApplication liqoidApplication;

    public LQFBInstances(LiqoidApplication liqoidApplication) {
        super();
        this.liqoidApplication = liqoidApplication;
        //  initFromFileOrDefaults();
    }

    public void initFromFileOrDefaults() {

        if (isEmpty()) {
            if (load() < 0) {
                //this.clear();
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
    }

    public LQFBInstance getSelectedInstance() {

        for (LQFBInstance instance : this) {
            if (instance.isSelected()) {
                return instance;
            }
        }
        return null;
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
    boolean saving = false;

    public int save() {
        if (!saving) {
            saving = true;

            try {
                FileOutputStream fos = new FileOutputStream(liqoidApplication.getApplicationFile());
                fos.write(toXML().getBytes());
                fos.close();
                saving = false;
            } catch (Exception e) {
                saving = false;
                return -1;
            }
        } else {
            return -1;
        }
        return 0;
    }

    public int load() {
        int retval = 0;
        int selectedInstance = indexOf(getSelectedInstance());
        ArrayList<LQFBInstance> keepInstances = new ArrayList();
        keepInstances.addAll(this);
        if (!liqoidApplication.getApplicationFile().canRead()) {
            return -1;
        }
        try {
            ArrayList<LQFBInstance> newInstances = new ArrayList();
            FileInputStream fis = new FileInputStream(liqoidApplication.getApplicationFile());
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxparser;
            LQFBInstancesFromFileParser parser = new LQFBInstancesFromFileParser(newInstances);
            saxparser = factory.newSAXParser();
            saxparser.parse(fis, parser);
            this.clear();
            for (LQFBInstance i : newInstances) {
                add(i);
            }

        } catch (Exception e) {
            this.clear();
            for (LQFBInstance i : keepInstances) {
                add(i);
            }
            retval = -1;
        }
        if (selectedInstance >= 0) {
            setSelectedInstance(selectedInstance);
        }
        return retval;
    }
}
