/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author andi
 */
public class MultiInstanceInitiativen extends ArrayList<Initiative> {

    public class SortByIssueCreatedComparator implements Comparator<Initiative> {

        public boolean orderNormal = true;

        public int compare(Initiative o1, Initiative o2) {
            try {
                long retval = 0;
                if (orderNormal) {
                    retval = (o1.issue_created.getTime() - o2.issue_created.getTime());
                } else {
                    retval = (o2.issue_created.getTime() - o1.issue_created.getTime());

                }
                if (retval > 0) {
                    return 1;
                }
                if (retval < 0) {
                    return -1;
                }
            } catch (Exception e) {
            }
            return 0;
        }
    }

    public class SortByNextEventComparator implements Comparator<Initiative> {

        public boolean orderNormal = true;

        public int compare(Initiative o1, Initiative o2) {
            try {
                long retval = 0;
                if (orderNormal) {
                    retval = (o1.getDateForNextEvent().getTime() - o2.getDateForNextEvent().getTime());
                } else {
                    retval = (o2.getDateForNextEvent().getTime() - o1.getDateForNextEvent().getTime());

                }
                if (retval > 0) {
                    return 1;
                }
                if (retval < 0) {
                    return -1;
                }
            } catch (Exception e) {
            }
            return 0;
        }
    }


    public class SortByLastEventComparator implements Comparator<Initiative> {

        public boolean orderNormal = true;

        public int compare(Initiative o1, Initiative o2) {
            try {
                long retval = 0;
                Date o1date = o1.dateForLastEvent();
                Date o2date = o2.dateForLastEvent();
                if (orderNormal) {
                    retval = (o2date.getTime() - o1date.getTime());
                } else {
                    retval = (o1date.getTime() - o2date.getTime());

                }
                if (retval > 0) {
                    return 1;
                }
                if (retval < 0) {
                    return -1;
                }
            } catch (Exception e) {
            }
            return 0;
        }
    }

    public void sort(int comparator) {

        Comparator c = new SortByIssueCreatedComparator();
        ((SortByIssueCreatedComparator) c).orderNormal = true;
        if (comparator == Initiative.ISSUE_NEXT_EVENT_COMP) {
            c = new SortByNextEventComparator();
            ((SortByNextEventComparator) c).orderNormal = true;
        }
         if (comparator == Initiative.ISSUE_LAST_EVENT_COMP) {
            c = new SortByLastEventComparator();
            ((SortByLastEventComparator) c).orderNormal = true;
        }
        Collections.sort(this, c);

    }

    public void reverse(int comparator) {
        Comparator c = new SortByIssueCreatedComparator();

        ((SortByIssueCreatedComparator) c).orderNormal = false;
        if (comparator == Initiative.ISSUE_NEXT_EVENT_COMP) {
            c = new SortByNextEventComparator();
            ((SortByNextEventComparator) c).orderNormal = false;
        }
        Collections.sort(this, c);
    }
}
