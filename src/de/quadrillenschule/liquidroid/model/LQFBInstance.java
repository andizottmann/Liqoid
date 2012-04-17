/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import android.app.Application;
import android.content.SharedPreferences;
import de.quadrillenschule.liquidroid.LiqoidApplication;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.json.JSONException;
import org.xml.sax.SAXException;

/**
 *
 * @author andi
 */
public class LQFBInstance {

    private String name = "";
    private String prefsName = "";
    private String apiUrl = "";
    private String webUrl = "";
    public Areas areas;
    private String apiversion = "";
    private String shortName = "";
    private AreasFromAPI1Parser areaParser;
    private InitiativenFromAPI1Parser iniParser;
    public static final String AREA_API = "area";
    public boolean pauseDownload = false;
    SharedPreferences instancePrefs;
    public static final String API1 = "1.x", API2 = "2.x";

    public LQFBInstance(LiqoidApplication la, String shortName, String prefsName, String name, String apiUrl, String webUrl, String developerkey, String apiversion) {
        this.prefsName = prefsName;
        this.shortName = shortName;
        this.name = name;
        this.apiUrl = apiUrl;
        this.webUrl = webUrl;
        this.instancePrefs = la.getSharedPreferences(prefsName, Application.MODE_PRIVATE);
        if (developerkey.equals("")) {
            developerkey = getDeveloperkey();
        }
        setDeveloperkey(developerkey);

        areas = new Areas(instancePrefs);// areaParser.areas;
        this.apiversion = apiversion;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean willDownloadAreas(CachedAPIQueries cachedAPI1Queries, boolean forceNetwork,String apiversion) {
        return !cachedAPI1Queries.cacheExists(cachedAPI1Queries.getApiURL("area", "", apiUrl, getDeveloperkey(),apiversion));

    }

    public int downloadAreas(CachedAPIQueries cachedAPI1Queries, boolean forceNetwork, boolean noDownload) {

        if (apiversion.equals(API1)) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxparser;

            areaParser = new AreasFromAPI1Parser(instancePrefs);
            try {
                saxparser = factory.newSAXParser();
                saxparser.parse(cachedAPI1Queries.queryInputStream(this, null, "area", "", apiUrl, getDeveloperkey(), "", forceNetwork, noDownload), areaParser);
            } catch (SAXException ex) {
                return -1;
            } catch (IOException ex) {
                return -1;
            } catch (ParserConfigurationException ex) {
                return -1;
            } catch (IllegalArgumentException ex) {
                return -1;
            }
            areas = areaParser.areas;
        }
        if (apiversion.equals(API2)) {
            try {
                AreasFromAPI2Parser parser = new AreasFromAPI2Parser(instancePrefs);

                parser.parse(cachedAPI1Queries.convertStreamToString(cachedAPI1Queries.queryInputStream(this, null, "area", "", apiUrl, getDeveloperkey(), "", forceNetwork, noDownload)));
                areas = parser.areas;
            } catch (JSONException ex) {
                return -1;

            } catch (IOException ex) {
                return -1;
            }

        }
        return 0;
    }

    public boolean willDownloadInitiativen(Area area, CachedAPIQueries cachedAPI1Queries, boolean forceNetwork,String apiversion) {
        String[] states = {"new", "accepted", "frozen", "voting"};


        boolean retval = false;


        for (String state : states) {

            if (!cachedAPI1Queries.cacheExists(cachedAPI1Queries.getApiURL("initiative", "&area_id=" + area.getId() + "&state=" + state, apiUrl, getDeveloperkey(),apiversion))) {
                retval = true;


            }

        }
        return retval;


    }

    public int downloadInitiativen(Area area, CachedAPIQueries cachedAPI1Queries, boolean forceNetwork, boolean noDownload) {
        int retval = 0;
        if (apiversion.equals(API1)) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxparser;
            iniParser = new InitiativenFromAPI1Parser(area, this);
            String[] states = {"new", "accepted", "frozen", "voting"};
            for (String state : states) {
                try {
                    saxparser = factory.newSAXParser();
                    saxparser.parse(cachedAPI1Queries.queryInputStream(this, area, "initiative", "&area_id=" + area.getId() + "&state=" + state, apiUrl, getDeveloperkey(), state, forceNetwork, noDownload), iniParser);
                } catch (ParserConfigurationException ex) {
                    retval = -1;
                } catch (SAXException ex) {
                    retval = -1;
                } catch (IOException ex) {
                    retval = -1;
                } catch (IllegalArgumentException ex) {
                    retval = -1;
                }
            }
        }
        return retval;
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
        return instancePrefs.getString("developerkey", "");


    }

    /**
     * @param developerkey the developerkey to set
     */
    public void setDeveloperkey(String developerkey) {
        instancePrefs.edit().putString("developerkey", developerkey).commit();



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
        if (maxIni > getMaxIni()) {
            instancePrefs.edit().putInt("max_ini", maxIni).commit();

        }
    }

    public boolean hasSelectedInititiativen(int areaid) {
        return instancePrefs.getBoolean("hasselected" + areaid, false);
    }

    public void setHasSelectedInitiativen(boolean value, int areaid) {
        instancePrefs.edit().putBoolean("hasselected" + areaid, value).commit();
    }
}
