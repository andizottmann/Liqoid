/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author andi
 */
public class AreasFromAPI2Parser {

    public Areas areas;
    SharedPreferences instancePrefs;

    public AreasFromAPI2Parser(SharedPreferences instancePrefs) {
        areas = new Areas(instancePrefs);
        this.instancePrefs = instancePrefs;
    }
//{"id":2,"unit_id":2,"active":true,"name":"Statutes of the Earth Moon Federation","description":"","direct_member_count":8,"member_weight":9}

    public void parse(String jsonstring) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonstring);
        JSONArray result = jsonObject.getJSONArray("result");
    
        for (int i = 0; i < result.length(); i++) {
            JSONObject a = result.getJSONObject(i);

            Area area = new Area(instancePrefs);
            area.setId(a.getInt("id"));
            area.setActive(a.getBoolean("active"));
            area.setName(a.getString("name"));
            area.setDescription(a.getString("description"));
            area.setDirect_member_count(a.getInt("direct_member_count"));
            area.setMember_weight(a.getInt("member_weight"));
            area.setUnit_id(a.getInt("unit_id"));
            areas.add(area);
        }
    }
}
