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
    private String apiUrl = "";
    private String webUrl = "";
    private String developerkey = "";
    private boolean selected = false;
    public Areas areas;
    private String apiversion = "";
    private AreaFromAPIParser areaParser;
    private InitiativenFromAPIParser iniParser;
    public static final String AREA_API = "area";

    public LQFBInstance(String name, String apiUrl, String webUrl, String developerkey, String apiversion, boolean selected) {


        this.name = name;
        this.apiUrl = apiUrl;
        this.webUrl = webUrl;
        this.developerkey = developerkey;
        areaParser = new AreaFromAPIParser(null);
        areas = areaParser.areas;
        this.selected = selected;
        this.apiversion = apiversion;
    }

    public LQFBInstance() {
        areaParser = new AreaFromAPIParser(null);

        areas = areaParser.areas;
    }

    @Override
    public String toString() {
        return name;
    }

    public String toXML() {
        String retval = "<lqfbinstance>";
        retval += "<iname>" + name + "</iname>";
        retval += "<apiUrl>" + apiUrl + "</apiUrl>";
        retval += "<webUrl>" + webUrl + "</webUrl>";
        retval += "<developerkey>" + developerkey + "</developerkey>";
        retval += "<iselected>" + selected + "</iselected>";
        retval += "<apiversion>" + apiversion + "</apiversion>";
        retval += areas.toXML();
        retval += "</lqfbinstance>";
        return retval;

    }

    public int downloadAreas() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxparser;
        areaParser = new AreaFromAPIParser(areas);
        try {
            saxparser = factory.newSAXParser();
            saxparser.parse(API1Queries.queryOutputStream("area", "", apiUrl, developerkey), areaParser);
        } catch (Exception e) {
            //  temp=null;
            areas = areaParser.areas;
            return -1;
        }
        areas = areaParser.areas;
        // areaParser=temp;
        return 0;
    }

    public int downloadInitiativen(Area area) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxparser;
        iniParser = new InitiativenFromAPIParser(area,area.getInitiativen());
        area.setInitiativen(new Initiativen());
        String[] states = {"new", "accepted", "frozen", "voting"};
        for (String state : states) {
            try {
                saxparser = factory.newSAXParser();
                saxparser.parse(API1Queries.queryOutputStream("initiative", "&area_id=" + area.getId() + "&state=" + state, apiUrl, developerkey), iniParser);
            } catch (Exception e) {
            }
            for (Initiative i : iniParser.inis) {
                area.getInitiativen().add(i);
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
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
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
}
