/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author andi
 */
public class LQFBInstancesFromFileParser extends DefaultHandler {

    StringBuffer charBuff;
    ArrayList<LQFBInstance> lqfbInstances;
    LQFBInstance currentInstance;
    Area currentArea;
    Initiative currentInitiative;

    public LQFBInstancesFromFileParser(ArrayList<LQFBInstance> lqfbInstances) {
        charBuff = new StringBuffer();
        this.lqfbInstances = lqfbInstances;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {


        if (qName.equals("lqfbinstances")) {
            lqfbInstances.clear();
        }
        if (qName.equals("lqfbinstance")) {
            currentInstance = new LQFBInstance();
        }

        if (qName.equals("area")) {
            currentArea = new Area();
        }

        if (qName.equals("initiative")) {
            currentInitiative = new Initiative();
        }

        charBuff = new StringBuffer();

    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("lqfbinstance")) {
            lqfbInstances.add(currentInstance);
        }

        //Instances
        if (qName.equals("iname")) {
            currentInstance.setName(charBuff.toString());
        }
        if (qName.equals("apiUrl")) {
            currentInstance.setApiUrl(charBuff.toString());
        }
        if (qName.equals("webUrl")) {
            currentInstance.setWebUrl(charBuff.toString());
        }
        if (qName.equals("developerkey")) {
            currentInstance.setDeveloperkey(charBuff.toString());
        }
        if (qName.equals("iselected")) {
            currentInstance.setSelected(Boolean.parseBoolean(charBuff.toString()));
        }
        if (qName.equals("apiversion")) {
            currentInstance.setApiversion(charBuff.toString());
        }
//Areas
        if (qName.equals("area")) {
            currentInstance.areas.add(currentArea);
        }
        if (qName.equals("area_id")) {
            currentArea.setId(Integer.parseInt(charBuff.toString()));
        }
        if (qName.equals("area_name")) {
            currentArea.setName(charBuff.toString());
        }
        if (qName.equals("area_selected")) {
            currentArea.setSelected(Boolean.parseBoolean(charBuff.toString()));
        }
        if (qName.equals("area_description")) {
            currentArea.setDescription(charBuff.toString());
        }
        if (qName.equals("area_direct_member_count")) {
            currentArea.setDirect_member_count(Integer.parseInt(charBuff.toString()));
        }
        if (qName.equals("area_member_weight")) {
            currentArea.setMember_weight(Integer.parseInt(charBuff.toString()));
        }
        if (qName.equals("area_autoreject_weight")) {
            currentArea.setAutoreject_weight(Integer.parseInt(charBuff.toString()));
        }
        if (qName.equals("area_active")) {
            currentArea.setActive(Boolean.parseBoolean(charBuff.toString()));
        }
//Initiatives
        if (qName.equals("initiative")) {
            currentArea.getInitiativen().add(currentInitiative);
        }
        if (qName.equals("ini_id")) {
            currentInitiative.id = (Integer.parseInt(charBuff.toString()));
        }
        if (qName.equals("ini_selected")) {
            currentInitiative.setSelected(Boolean.parseBoolean(charBuff.toString()));
        }
        if (qName.equals("ini_name")) {
            currentInitiative.name = (charBuff.toString());
        }
        if (qName.equals("ini_state")) {
            currentInitiative.state = (charBuff.toString());
        }
        if (qName.equals("ini_created")) {
            currentInitiative.created = InitiativenFromAPIParser.myDateParser(charBuff.toString());
        }
        if (qName.equals("ini_issue_created")) {
            currentInitiative.issue_created = InitiativenFromAPIParser.myDateParser(charBuff.toString());
        }
        if (qName.equals("ini_issue_id")) {
            currentInitiative.issue_id = (Integer.parseInt(charBuff.toString()));
        }
        if (qName.equals("ini_issue_discussion_time")) {
            currentInitiative.issue_discussion_time = (Long.parseLong(charBuff.toString()));
        }
        if (qName.equals("ini_issue_admission_time")) {
            currentInitiative.issue_admission_time = (Long.parseLong(charBuff.toString()));
        }
        if (qName.equals("ini_issue_verification_time")) {
            currentInitiative.issue_verification_time = (Long.parseLong(charBuff.toString()));
        }
         if (qName.equals("ini_issue_voting_time")) {
            currentInitiative.issue_voting_time = (Long.parseLong(charBuff.toString()));
        }
        if (qName.equals("ini_supporter_count")) {
            currentInitiative.supporter_count = (Integer.parseInt(charBuff.toString()));
        }
          if (qName.equals("ini_issue_accepted")) {
            currentInitiative.issue_accepted = InitiativenFromAPIParser.myDateParser(charBuff.toString());
        }
          if (qName.equals("ini_issue_half_frozen")) {
            currentInitiative.issue_half_frozen = InitiativenFromAPIParser.myDateParser(charBuff.toString());
        }
          if (qName.equals("ini_issue_fully_frozen")) {
            currentInitiative.issue_fully_frozen = InitiativenFromAPIParser.myDateParser(charBuff.toString());
        }
          if (qName.equals("ini_issue_closed")) {
            currentInitiative.issue_closed = InitiativenFromAPIParser.myDateParser(charBuff.toString());
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        charBuff.append(ch, start, length);
    }
}
