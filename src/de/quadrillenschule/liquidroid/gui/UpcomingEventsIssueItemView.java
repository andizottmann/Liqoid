/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.gui;

import android.app.Activity;
import de.quadrillenschule.liquidroid.model.Initiative;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author andi
 */
public class UpcomingEventsIssueItemView extends IssueItemView {

    public UpcomingEventsIssueItemView(Activity activity, Initiative initiative) {
        super(activity, initiative);
    }

    @Override
    protected String getStatusText() {
        DateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm");
        return "  " + initiative.nextEvent() + " " + formatter.format(initiative.getDateForNextEvent()) + " " + initiative.getLqfbInstance().getShortName();
    }
}
