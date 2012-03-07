/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author andi
 */
public class Initiative implements Comparable<Initiative> {

    public int id = 0;
    public int area_id = 0;
    public String name = "";
    public String state = "";
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
    public static final int ISSUE_ID_COMP = 0, ISSUE_CREATED_COMP = 1;
    private int comparator = 1;

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
        if (state.equals("finished")) {
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
        if ((Math.abs((created.getTime() - current_draft_created.getTime())) < 2000)) {
            return lastErzeugt;
        } else {
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
        if (state.equals("new")) {
            return nextAkzeptiert;
        }
        if (state.equals("accepted")) {
            return nextEingefroren;
        }
        if (state.equals("frozen")) {
            return nextAbstimmung;
        }
        if (state.equals("voting")) {
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

    public int compareTo(Initiative arg0) {
        switch (comparator) {
            case ISSUE_ID_COMP:
                return ((Integer) this.issue_id).compareTo((Integer) arg0.issue_id);
            case ISSUE_CREATED_COMP:
                return (this.issue_created).compareTo(arg0.issue_created);
            default:
                return ((Integer) this.issue_id).compareTo((Integer) arg0.issue_id);
        }
    }

    /**
     * @return the comparator
     */
    public int getComparator() {
        return comparator;
    }

    /**
     * @param comparator the comparator to set
     */
    public void setComparator(int comparator) {
        this.comparator = comparator;
    }
}
