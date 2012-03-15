/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import android.app.Application;
import android.content.SharedPreferences;
import de.quadrillenschule.liquidroid.LiqoidApplication;
import java.io.FileNotFoundException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 *
 * @author andi
 */
public class LQFBInstance {

    private String name = "";
    private String prefsName = "";
    private String apiUrl = "";
    private String webUrl = "";
    private String developerkey = "";
    public Areas areas;
    private String apiversion = "";
    private String shortName = "";
    private AreasFromAPIParser areaParser;
    private InitiativenFromAPIParser iniParser;
    public static final String AREA_API = "area";
    public boolean pauseDownload = false;
    SharedPreferences instancePrefs;

    public LQFBInstance(LiqoidApplication la, String shortName, String prefsName, String name, String apiUrl, String webUrl, String developerkey, String apiversion) {
        this.prefsName = prefsName;
        this.shortName = shortName;
        this.name = name;
        this.apiUrl = apiUrl;
        this.webUrl = webUrl;
        this.developerkey = developerkey;
        this.instancePrefs = la.getSharedPreferences(prefsName, Application.MODE_PRIVATE);
        areas = new Areas(instancePrefs);// areaParser.areas;
        this.apiversion = apiversion;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean willDownloadAreas(CachedAPI1Queries cachedAPI1Queries, boolean forceNetwork) {
        try {
            return cachedAPI1Queries.willDownload(cachedAPI1Queries.getApiURL("area", "", apiUrl, developerkey), forceNetwork);
        } catch (FileNotFoundException e) {
            return true;
        }
    }

    public int downloadAreas(CachedAPI1Queries cachedAPI1Queries, boolean forceNetwork, boolean noDownload) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxparser;

        areaParser = new AreasFromAPIParser(instancePrefs);
        try {
            saxparser = factory.newSAXParser();
            saxparser.parse(cachedAPI1Queries.queryInputStream(this,"area", "", apiUrl, developerkey, forceNetwork, noDownload), areaParser);
            //   cachedAPI1Queries.storeInCache(areaParser.docBuff.toString());
            areas = areaParser.areas;

        } catch (Exception e) {
            return -1;
        }

        return 0;
    }

    public boolean willDownloadInitiativen(Area area, CachedAPI1Queries cachedAPI1Queries, boolean forceNetwork) {
        String[] states = {"new", "accepted", "frozen", "voting"};
        boolean retval = false;
        for (String state : states) {

            try {
                if (cachedAPI1Queries.willDownload(cachedAPI1Queries.getApiURL("initiative", "&area_id=" + area.getId() + "&state=" + state, apiUrl, developerkey), forceNetwork)) {
                    retval = true;
                }
            } catch (FileNotFoundException e) {
                return true;
            }
        }
        return retval;
    }

    public int downloadInitiativen(Area area, CachedAPI1Queries cachedAPI1Queries, boolean forceNetwork, boolean noDownload) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxparser;

        area.getInitiativen().clear();
        iniParser = new InitiativenFromAPIParser(area, this);
        String[] states = {"new", "accepted", "frozen", "voting"};
        for (String state : states) {
            try {
                saxparser = factory.newSAXParser();
                saxparser.parse(cachedAPI1Queries.queryInputStream(this,"initiative", "&area_id=" + area.getId() + "&state=" + state, apiUrl, developerkey, forceNetwork, noDownload), iniParser);
            } catch (Exception e) {
                return -1;
            }

        }
        return 0;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the apiUrl
     */
    public String getApiUrl() {
        return apiUrl;
    }

    /**
     * @param apiUrl the apiUrl to set
     */
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /**
     * @return the webUrl
     */
    public String getWebUrl() {
        return webUrl;
    }

    /**
     * @param webUrl the webUrl to set
     */
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    /**
     * @return the developerkey
     */
    public String getDeveloperkey() {
        return developerkey;
    }

    /**
     * @param developerkey the developerkey to set
     */
    public void setDeveloperkey(String developerkey) {
        this.developerkey = developerkey;
    }

    /**
     * @return the apiversion
     */
    public String getApiversion() {
        return apiversion;
    }

    /**
     * @param apiversion the apiversion to set
     */
    public void setApiversion(String apiversion) {
        this.apiversion = apiversion;
    }

    /**
     * @return the prefsName
     */
    public String getPrefsName() {
        return prefsName;
    }

    /**
     * @param prefsName the prefsName to set
     */
    public void setPrefsName(String prefsName) {
        this.prefsName = prefsName;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @return the maxIni
     */
    public int getMaxIni() {
       return instancePrefs.getInt("max_ini", 0);
       
    }

    /**
     * @param maxIni the maxIni to set
     */
    public void setMaxIni(int maxIni) {
       instancePrefs.edit().putInt("max_ini", maxIni);
    }
}
