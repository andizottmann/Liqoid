/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author andi
 */
public class LQFBInstancesFromFileParser extends DefaultHandler {

    StringBuffer charBuff;
    LQFBInstances lqfbInstances;
    LQFBInstance currentInstance;
    Area currentArea;

    public LQFBInstancesFromFileParser(LQFBInstances lqfbInstances) {
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

    }

    @Override
    public void characters(char ch[], int start, int length) {
        charBuff.append(ch, start, length);
    }
}
