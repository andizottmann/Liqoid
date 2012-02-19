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
public class AreaFromAPIParser extends DefaultHandler {

    Area currentArea = null;
    public Areas areas, oldAreas;
    StringBuffer charBuff;

    public AreaFromAPIParser(Areas oldAreas) {
        charBuff = new StringBuffer();
        areas = new Areas();
        this.oldAreas=oldAreas;
       // oldAreas = LiquiDroidMainActivity.lqfbInstances.getSelectedInstance().areas;
      
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

    @Override
    public void endDocument() {
        //Reconstruct selected areas
        if (oldAreas != null) {
            for (Area oldArea : oldAreas) {
                if (areas.getById(oldArea.getId()) != null) {
                    areas.getById(oldArea.getId()).setSelected(oldArea.isSelected());
                }
            }
        }
    }
}
