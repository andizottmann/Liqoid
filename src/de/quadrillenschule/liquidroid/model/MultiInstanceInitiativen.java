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

            return 0;
        }
    }

    public class SortByNextEventComparator implements Comparator<Initiative> {

        public boolean orderNormal = true;

        public int compare(Initiative o1, Initiative o2) {

            long retval = 0;
            try {
                if (orderNormal) {
                    retval = (o1.getDateForNextEvent().getTime() - o2.getDateForNextEvent().getTime());
                } else {
                    retval = (o2.getDateForNextEvent().getTime() - o1.getDateForNextEvent().getTime());

                }
            } catch (NullPointerException npe) {
                retval = -1;
            }
            if (retval > 0) {
                return 1;
            }
            if (retval < 0) {
                return -1;
            }

            return 0;
        }
    }

    public class SortByLastEventComparator implements Comparator<Initiative> {

        public boolean orderNormal = true;

        public int compare(Initiative o1, Initiative o2) {
            long retval = 0;
            Date o1date = o1.dateForLastEvent();
            Date o2date = o2.dateForLastEvent();
            try {
                if (orderNormal) {
                    retval = (o2date.getTime() - o1date.getTime());
                } else {
                    retval = (o1date.getTime() - o2date.getTime());

                }
            } catch (NullPointerException npe) {
                retval = -1;
            }
            if (retval > 0) {
                return 1;
            }
            if (retval < 0) {
                return -1;
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
        if (comparator == Initiative.ISSUE_LAST_EVENT_COMP) {
            c = new SortByLastEventComparator();
            ((SortByLastEventComparator) c).orderNormal = false;
        }
        Collections.sort(this, c);
    }

    public void removeNonSelected() {
        ArrayList<Initiative> removeList = new ArrayList<Initiative>();
        for (Initiative i : this) {
            if (!i.getArea().getInitiativen().getSelectedIssues().isIssueSelected(i.issue_id)) {
                removeList.add(i);
            }
        }
        removeAll(removeList);
    }

    public MultiInstanceInitiativen searchResult(String searchtext, boolean searchcontent, boolean searchcase) {
        MultiInstanceInitiativen retval = new MultiInstanceInitiativen();
        for (Initiative i : this) {
            if (i.name.contains(searchtext)) {
                if (!retval.contains(i)) {
                    retval.add(i);
                }
            }

            if (i.current_draft_content.contains(searchtext) && searchcontent) {
                if (!retval.contains(i)) {
                    retval.add(i);
                }
            }

            if (!searchcase) {
                if (i.name.toUpperCase().contains(searchtext.toUpperCase())) {
                    if (!retval.contains(i)) {
                        retval.add(i);
                    }
                }
                if (i.current_draft_content.toUpperCase().contains(searchtext.toUpperCase()) && searchcontent) {
                    if (!retval.contains(i)) {
                        retval.add(i);
                    }
                }
            }
            for (Initiative j : i.getConcurrentInis()) {
                if (j.name.contains(searchtext)) {
                    if (!retval.contains(i)) {
                        retval.add(i);
                    }
                }
                if (i.current_draft_content.contains(searchtext) && searchcontent) {
                    if (!retval.contains(i)) {
                        retval.add(i);
                    }
                }
                if (!searchcase) {
                    if (j.name.toUpperCase().contains(searchtext.toUpperCase())) {
                        if (!retval.contains(i)) {
                            retval.add(i);
                        }
                    }
                    if (j.current_draft_content.toUpperCase().contains(searchtext.toUpperCase()) && searchcontent) {
                        if (!retval.contains(i)) {
                            retval.add(i);
                        }
                    }
                }

            }
        }
        return retval;
    }
}
