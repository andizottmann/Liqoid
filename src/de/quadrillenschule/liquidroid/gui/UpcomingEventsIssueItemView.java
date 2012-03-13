/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.gui;

import android.content.SharedPreferences;
import de.quadrillenschule.liquidroid.InitiativesTabActivity;
import de.quadrillenschule.liquidroid.LiqoidApplication;
import de.quadrillenschule.liquidroid.model.Initiative;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author andi
 */
public class UpcomingEventsIssueItemView extends IssueItemView {

    public UpcomingEventsIssueItemView(InitiativesTabActivity activity, Initiative initiative) {
        super(activity, initiative);
    }

    @Override
    protected String getStatusText() {
        DateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm");
        return "  " + initiative.nextEvent() + " " + formatter.format(initiative.getDateForNextEvent()) + " " + initiative.getLqfbInstance().getShortName();
    }

    @Override
    protected int itemSpecificColorcode() {
        long delta = initiative.getDateForNextEvent().getTime() - System.currentTimeMillis();
        long oneday = 1000 * 60 * 60 * 24;
        SharedPreferences gp = ((LiqoidApplication) activity.getApplication()).getGlobalPreferences();
        if (delta < Long.parseLong(gp.getString(LiqoidApplication.REDLIMIT_PREF, oneday + ""))) {
            return (activity.RED_COLOR);
        }
        if (delta < Long.parseLong(gp.getString(LiqoidApplication.ORANGELIMIT_PREF, oneday * 3 + ""))) {
            return (activity.ORANGE_COLOR);
        }
        if (delta < Long.parseLong(gp.getString(LiqoidApplication.YELLOWLIMIT_PREF, oneday * 5 + ""))) {
            return (activity.YELLOW_COLOR);
        }
        return activity.GREY_COLOR;
    }
}
