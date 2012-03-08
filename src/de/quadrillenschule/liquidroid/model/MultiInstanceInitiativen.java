/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.quadrillenschule.liquidroid.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author andi
 */
public class MultiInstanceInitiativen  extends ArrayList<Initiative> {

    
    public class SortByIssueCreatedComparator implements Comparator<Initiative> {
        public boolean orderNormal = true;
        public int compare(Initiative o1, Initiative o2) {
            if ((o1 == null) || (o2 == null)) {
                return 0;
            }
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

    public void sort(int comparator) {
        SortByIssueCreatedComparator c = new SortByIssueCreatedComparator();
        c.orderNormal = true;

        Collections.sort(this, c);

    }

    public void reverse(int comparator) {
        SortByIssueCreatedComparator c = new SortByIssueCreatedComparator();
        c.orderNormal = false;

        Collections.sort(this, c);

    }

}
