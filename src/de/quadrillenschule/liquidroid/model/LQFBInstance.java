/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

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
    private AreasFromAPIParser areaParser;
    private InitiativenFromAPIParser iniParser;
    public static final String AREA_API = "area";

    public LQFBInstance(String prefsName, String name, String apiUrl, String webUrl, String developerkey, String apiversion) {

        this.prefsName = prefsName;
        this.name = name;
        this.apiUrl = apiUrl;
        this.webUrl = webUrl;
        this.developerkey = developerkey;
        // areaParser = new AreaFromAPIParser(null);
        areas = new Areas();// areaParser.areas;
         this.apiversion = apiversion;
    }

    public LQFBInstance() {
        //  areaParser = new AreaFromAPIParser(null);

        areas = new Areas();
    }

    @Override
    public String toString() {
        return name;
    }

    public int downloadAreas(CachedAPI1Queries cachedAPI1Queries, boolean forceNetwork) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxparser;
        Areas selectedAreas = new Areas();
        for (Area a : areas) {
            if (a.isSelected()) {
                selectedAreas.add(a);
            }
        }
        areaParser = new AreasFromAPIParser();
        try {
            saxparser = factory.newSAXParser();
            saxparser.parse(cachedAPI1Queries.queryInputStream("area", "", apiUrl, developerkey, forceNetwork), areaParser);
         //   cachedAPI1Queries.storeInCache(areaParser.docBuff.toString());
            areas = areaParser.areas;

        } catch (Exception e) {
            return -1;
        }
        for (Area a : areas) {
            if (selectedAreas.getById(a.getId()) != null) {
                a.setSelected(true);
            }
        }
        return 0;
    }

    public int downloadInitiativen(Area area,CachedAPI1Queries cachedAPI1Queries, boolean forceNetwork) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxparser;

        area.getInitiativen().clear();
        iniParser = new InitiativenFromAPIParser(area);
        String[] states = {"new", "accepted", "frozen", "voting"};
        for (String state : states) {
            try {
                saxparser = factory.newSAXParser();
                saxparser.parse(cachedAPI1Queries.queryInputStream("initiative", "&area_id=" + area.getId() + "&state=" + state, apiUrl, developerkey,forceNetwork), iniParser);
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
}
