/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
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
//{"issue_id":1,"id":1,"name":"My first initiative","discussion_url":null,
//"created":"2011-09-10T23:45:49.641Z","revoked":null,"revoked_by_member_id":null,
//"suggested_initiative_id":null,"admitted":true,"supporter_count":3,"informed_supporter_count":1,
//"satisfied_supporter_count":3,"satisfied_informed_supporter_count":1,"positive_votes":0,
//"negative_votes":0,"rank":1,"direct_majority":false,"indirect_majority":true,"schulze_rank":1,
//"better_than_status_quo":false,"worse_than_status_quo":false,"reverse_beat_path":true,
//"multistage_majority":true,"eligible":false,"winner":false}

//{"id":15,"area_id":1,"policy_id":1,"state":"verification","created":"2012-03-25T16:06:55.480Z",
// "accepted":"2012-03-25T21:15:31.437Z","half_frozen":"2012-04-10T14:30:45.637Z","fully_frozen":null,
//"closed":null,"ranks_available":false,"cleaned":null,"admission_time":{"days":8},
//"discussion_time":{"days":15},"verification_time":{"days":8},"voting_time":{"days":15},
//"snapshot":"2012-04-17T18:25:23.093Z","latest_snapshot_event":"periodic","population":19,
//"voter_count":null,"status_quo_schulze_rank":null},
    public void parse(String jsonstringini, String jsonstringissue) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonstringissue);
        JSONArray issuesresult = jsonObject.getJSONArray("result");
        Issues helperIssues = new Issues(lqfbInstance.instancePrefs);
        for (int i = 0; i < issuesresult.length(); i++) {
            Initiative ini = new Initiative(area, lqfbInstance);
            JSONObject a = issuesresult.getJSONObject(i);
            ini.issue_id = a.getInt("id");
            ini.setState(a.getString("state"));
            ini.issue_discussion_time = myTimeParser(a.getJSONObject("discussion_time"));
            ini.issue_verification_time = myTimeParser(a.getJSONObject("verification_time"));
            ini.issue_voting_time = myTimeParser(a.getJSONObject("voting_time"));
            ini.issue_created = myDateParser(a.getString("created"));
            ini.issue_accepted = myDateParser(a.getString("accepted"));
            ini.issue_half_frozen = myDateParser(a.getString("half_frozen"));
            ini.issue_fully_frozen = myDateParser(a.getString("fully_frozen"));
            ini.issue_closed = myDateParser(a.getString("closed"));

            helperIssues.add(ini);
        }

        jsonObject = new JSONObject(jsonstringini);
        JSONArray result = jsonObject.getJSONArray("result");

        for (int i = 0; i < result.length(); i++) {
            JSONObject a = result.getJSONObject(i);
            Initiative ini = new Initiative(area, lqfbInstance);
            ini.issue_id = a.getInt("issue_id");
            ini.id = a.getInt("id");
            ini.name = a.getString("name");
            ini.created = myDateParser(a.getString("created"));
            ini.current_draft_created = ini.created;
            ini.supporter_count = a.getInt("supporter_count");
            Initiative helperIni = helperIssues.findByIssueID(ini.issue_id).get(0);
            ini.issue_discussion_time = helperIni.issue_discussion_time;
            ini.issue_verification_time = helperIni.issue_verification_time;
            ini.issue_voting_time = helperIni.issue_voting_time;
            ini.issue_created = helperIni.issue_created;
            ini.issue_accepted = helperIni.issue_accepted;
            ini.issue_half_frozen = helperIni.issue_half_frozen;
            ini.issue_fully_frozen = helperIni.issue_fully_frozen;
            ini.issue_closed = helperIni.issue_closed;
            inis.add(ini);
        }
    }

    static Date myDateParser(String str) {
        //"2011-09-10T23:45:49.641Z"
        String format = "yyyy-MM-dd HH:mm:ss";
        DateFormat formatter = new SimpleDateFormat(format);
        try {
            str = str.replace("T", " ").substring(0, format.length());

            return formatter.parse(str);
        } catch (ParseException ex) {
            return null;
        } catch (StringIndexOutOfBoundsException ex) {
            return null;
        }

    }
    //{"months":1,"days":15}

    static long myTimeParser(JSONObject a) {
        long retval = 0;
        try {
            retval += (long) 24 * (long) 60 * (long) 60 * a.getInt("days");
        } catch (JSONException ex) {
        }
        try {
            retval += (long) 24 * (long) 60 * (long) 60 * 7 * a.getInt("weeks");
        } catch (JSONException ex) {
        }
        try {
            retval += (long) 24 * (long) 60 * (long) 60 * 30 * a.getInt("months");
        } catch (JSONException ex) {
        }
        return retval;
    }
}
