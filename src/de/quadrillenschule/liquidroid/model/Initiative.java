/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import de.quadrillenschule.liquidroid.R;
/**
 *
 * @author andi
 */
public class Initiative {

    public int id = 0;
    public int area_id = 0;
    public String name = "";
    private String state = "";
    public String current_draft_content="";
    public int issue_id = 0;
    public long issue_discussion_time = 0;
    public long issue_admission_time = 0;
    public long issue_verification_time = 0;
    public long issue_voting_time = 0;
    public int supporter_count = 0;//,issue_voter_count=-1,positive_votes=-1,negative_voters=-1;
    public Date revoked;
    public Date created;
    public Date issue_created;
    public Date issue_accepted;
    public Date issue_half_frozen;
    public Date issue_fully_frozen;
    public Date issue_closed;
    public Date current_draft_created;
    public static final int ISSUE_ID_COMP = 0, ISSUE_CREATED_COMP = 1, ISSUE_NEXT_EVENT_COMP = 2, ISSUE_LAST_EVENT_COMP = 3;
    private LQFBInstance lqfbInstance;
    private Area area;
    private ArrayList<Initiative> concurrentInis=new ArrayList();

    public Initiative(Area area, LQFBInstance lqfbInstance) {
        this.area = area;
        this.lqfbInstance = lqfbInstance;
    }

    public Date getDateForStartVoting() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(issue_created);
        Date retval = new Date((cal.getTimeInMillis()) + issue_discussion_time * 1000 + issue_verification_time * 1000);

        return retval;
    }
    public static final String lastVotingEnded = "Abstimmung beendet", lastVotingStarted = "Abstimmungsbeginn", lastFrozen = "Eingefroren", lastClosed = "Beendet", lastRevoked = "Zurückgezogen", lastNeuerEntwurf = "Neuer Entwurf", lastErzeugt = "Neue Initiative";

    public String lastEvent() {
        if (revoked != null) {
            return lastRevoked;
        }
        if (getState().equals("finished")) {
            return lastVotingEnded;
        }
        if (issue_closed != null) {
            return lastClosed;
        }
        if (issue_fully_frozen != null) {
            return lastVotingStarted;
        }

        if (issue_half_frozen != null) {
            return lastFrozen;
        }
        try {
            if ((Math.abs((created.getTime() - current_draft_created.getTime())) < 2000)) {
                return lastErzeugt;
            } else {
                return lastNeuerEntwurf;
            }
        } catch (NullPointerException e) {
            return lastNeuerEntwurf;
        }
    }

    public Date dateForLastEvent() {
        if (lastEvent().equals(lastRevoked)) {
            return revoked;
        }
        ;
        if (lastEvent().equals(lastFrozen)) {
            return issue_half_frozen;
        }
        ;
        if (lastEvent().equals(lastClosed)) {
            return issue_closed;
        }
        ;
        if (lastEvent().equals(lastVotingEnded)) {
            return issue_closed;
        }
        ;
        if (lastEvent().equals(lastVotingStarted)) {
            return issue_fully_frozen;
        }
        ;
        return current_draft_created;
    }
    public static String nextEingefroren = "->Eingefroren", nextAbstimmung = "->Abstimmung", nextAbstimmungsende = "->Abstimmungsende", nextAkzeptiert = "->Akzeptiert";

    public String nextEvent() {
        if (getState().equals("new")) {
            return nextAkzeptiert;
        }
        if (getState().equals("accepted")) {
            return nextEingefroren;
        }
        if (getState().equals("frozen")) {
            return nextAbstimmung;
        }
        if (getState().equals("voting")) {
            return nextAbstimmungsende;
        }
        return "";
    }

    public Date getDateForNextEvent() {
        Calendar cal = Calendar.getInstance();

        Date retval = null;// = new Date((cal.getTimeInMillis()) + issue_discussion_time * 1000 + issue_verification_time * 1000 + issue_voting_time * 1000);
        if (nextEvent().equals(nextAkzeptiert)) {
            cal.setTime(issue_created);
            retval = new Date((cal.getTimeInMillis()) + issue_admission_time * 1000);
        }
        if (nextEvent().equals(nextAbstimmungsende)) {
            cal.setTime(issue_accepted);
            retval = new Date((cal.getTimeInMillis()) + issue_discussion_time * 1000 + issue_verification_time * 1000 + issue_voting_time * 1000);
        }
        if (nextEvent().equals(nextAbstimmung)) {
            cal.setTime(issue_accepted);
            retval = new Date((cal.getTimeInMillis()) + issue_discussion_time * 1000 + issue_verification_time * 1000);
        }
        if (nextEvent().equals(nextEingefroren)) {
            cal.setTime(issue_accepted);
            retval = new Date((cal.getTimeInMillis()) + issue_discussion_time * 1000);
        }

        return retval;
    }

    /**
     * @return the lqfbInstance
     */
    public LQFBInstance getLqfbInstance() {
        return lqfbInstance;
    }

    /**
     * @return the area
     */
    public Area getArea() {
        return area;
    }

    /**
     * @return the concurrentInis
     */
    public ArrayList<Initiative> getConcurrentInis() {
        return concurrentInis;
    }

    /**
     * @param concurrentInis the concurrentInis to set
     */
    public void setConcurrentInis(ArrayList<Initiative> concurrentInis) {
        this.concurrentInis = concurrentInis;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

      public int getIntlStateResId() {
        if (getState().equals("new")) {
            return R.string.new_;
        }
        if (getState().equals("accepted")) {
              return R.string.accepted;
        }
        if (getState().equals("frozen")) {
            return R.string.frozen;
        }
        if (getState().equals("voting")) {
            return R.string.voting;
        }
        return R.string.unknown;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }
}
