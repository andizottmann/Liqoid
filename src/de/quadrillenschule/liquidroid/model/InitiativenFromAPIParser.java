/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author andi
 */
public class InitiativenFromAPIParser extends DefaultHandler {

    public Issues inis;
    Initiative currentInitiative;
    StringBuffer charBuff;
    Area area;
    LQFBInstance lqfbInstance;

    public InitiativenFromAPIParser(Area area, LQFBInstance lqfbInstance) {
        super();
        this.area = area;
        charBuff = new StringBuffer();
        inis = area.getInitiativen();
        this.lqfbInstance = lqfbInstance;
    }

    @Override
    public void characters(char ch[], int start, int length) {
        charBuff.append(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //super.startElement(uri, localName, qName, attributes);

        if (qName.equals("initiative")) {

            currentInitiative = new Initiative(area, lqfbInstance);


        }

        charBuff = new StringBuffer();


    }

    static long myTimeParser(String str) {

        if (str.contains("days")) {
            String numberstr = str.substring(0, str.indexOf("days") - 1);
            return (long) 24 * (long) 60 * (long) 60 * Long.parseLong(numberstr);
        }
        if (str.contains(":")) {

            String[] splitted = str.split(":");
            return (Integer.parseInt(splitted[0]) * 60 * 60) + (Integer.parseInt(splitted[1]) * 60) + (Integer.parseInt(splitted[2]));
        }
        return 0;
    }

    static Date myDateParser(String str) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return (Date) formatter.parse(str);
        } catch (ParseException ex) {
            return null;
        }

    }

    static String dateToStringFormatter(Date date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        return formatter.format(date);

    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("id")) {
            currentInitiative.id = Integer.parseInt(charBuff.toString());
            lqfbInstance.setMaxIni(currentInitiative.id);
        }
        if (qName.equals("initiative")) {
            if (currentInitiative != null) {
                inis.add(currentInitiative);
            }
        }
        if (qName.equals("name")) {
            currentInitiative.name = charBuff.toString();
        }
        if (qName.equals("current_draft_content")) {
            currentInitiative.current_draft_content = charBuff.toString();
        }

        if (qName.equals("issue_state")) {
            currentInitiative.setState(charBuff.toString());
        }
        if (qName.equals("issue_id")) {
            currentInitiative.issue_id = Integer.parseInt(charBuff.toString());
        }
        if (qName.equals("area_id")) {
            currentInitiative.area_id = Integer.parseInt(charBuff.toString());
        }

        if (qName.equals("issue_admission_time")) {
            currentInitiative.issue_admission_time = myTimeParser(charBuff.toString());
            //   System.out.println(currentInitiative.issue_admission_time);

        }
        if (qName.equals("issue_discussion_time")) {
            currentInitiative.issue_discussion_time = myTimeParser(charBuff.toString());
        }
        if (qName.equals("current_draft_created")) {
            currentInitiative.current_draft_created = myDateParser(charBuff.toString());
        }

        if (qName.equals("supporter_count")) {
            try {
            currentInitiative.supporter_count = Integer.parseInt(charBuff.toString());
            } catch (NumberFormatException e){
             currentInitiative.supporter_count =0;
            }
        }

        if (qName.equals("issue_verification_time")) {
            currentInitiative.issue_verification_time = myTimeParser(charBuff.toString());
            //   System.out.println(currentInitiative.issue_verification_time);

        }
        if (qName.equals("issue_voting_time")) {
            currentInitiative.issue_voting_time = myTimeParser(charBuff.toString());
            //    System.out.println(currentInitiative.issue_voting_time);

        }
        if (qName.equals("issue_created")) {
            currentInitiative.issue_created = myDateParser(charBuff.toString());


        }
        if (qName.equals("issue_accepted")) {
            currentInitiative.issue_accepted = myDateParser(charBuff.toString());
        }
        if (qName.equals("issue_half_frozen")) {
            currentInitiative.issue_half_frozen = myDateParser(charBuff.toString());
        }
        if (qName.equals("issue_fully_frozen")) {
            currentInitiative.issue_fully_frozen = myDateParser(charBuff.toString());
        }
        if (qName.equals("issue_closed")) {
            currentInitiative.issue_closed = myDateParser(charBuff.toString());
        }
        if (qName.equals("created")) {
            currentInitiative.created = myDateParser(charBuff.toString());
        }
        if (qName.equals("revoked")) {
            currentInitiative.revoked = myDateParser(charBuff.toString());
        }

    }
}
