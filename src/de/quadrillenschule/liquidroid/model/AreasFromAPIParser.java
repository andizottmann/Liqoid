/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import android.content.SharedPreferences;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author andi
 */
public class AreasFromAPIParser extends DefaultHandler {

    Area currentArea = null;
    public Areas areas;
    StringBuffer charBuff;

    public AreasFromAPIParser(SharedPreferences instancePrefs) {
        charBuff = new StringBuffer();
        areas = new Areas(instancePrefs);
  
    }

    @Override
    public void startDocument() {

    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {


        if (qName.equals("area")) {


            currentArea = new Area();

        }
        charBuff = new StringBuffer();

    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("id")) {
            currentArea.setId(Integer.parseInt(charBuff.toString()));
        }
        if (qName.equals("name")) {
            currentArea.setName(charBuff.toString());
        }
        if (qName.equals("area")) {

            areas.add(currentArea);

        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        charBuff.append(ch, start, length);
    }
}
