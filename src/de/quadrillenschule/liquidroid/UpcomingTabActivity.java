/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import de.quadrillenschule.liquidroid.model.Initiative;

/**
 *
 * @author andi
 */
public class UpcomingTabActivity extends InitiativesTabActivity {

    @Override
    protected void sortList() {
        if (sortNewestFirst) {
            allInis.reverse(Initiative.ISSUE_NEXT_EVENT_COMP);
        } else {
            allInis.sort(Initiative.ISSUE_NEXT_EVENT_COMP);
        }
        inisListAdapter.notifyDataSetChanged();
    }
}
