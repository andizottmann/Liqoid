/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import java.io.InputStream;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author andi
 */
public class InitiativenFromAPI2Parser {

    public Issues inis;
    Initiative currentInitiative;
    Area area;
    LQFBInstance lqfbInstance;

    public InitiativenFromAPI2Parser(Area area, LQFBInstance lqfbInstance) {

        this.area = area;
        inis = area.getInitiativen();
        this.lqfbInstance = lqfbInstance;
    }

    public void parse(String jsonstring) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonstring);

    }
}
