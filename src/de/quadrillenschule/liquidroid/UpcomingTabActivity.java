/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.os.Bundle;
import android.widget.TextView;
import de.quadrillenschule.liquidroid.gui.InitiativenListAdapter;
import de.quadrillenschule.liquidroid.gui.UpcomingInitiativenListAdapter;
import de.quadrillenschule.liquidroid.model.Initiative;

/**
 *
 * @author andi
 */
public class UpcomingTabActivity extends InitiativesTabActivity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        ((TextView) findViewById(R.id.tabinis_title)).setText(R.string.tab_upcoming);
    }

    @Override
    public void filterList() {
        if (filterOnlySelected) {
            allInis.removeNonSelected();
        }
    }

    @Override
    public void sortList() {
        if (!sortNewestFirst) {
            allInis.reverse(Initiative.ISSUE_NEXT_EVENT_COMP);
        } else {
            allInis.sort(Initiative.ISSUE_NEXT_EVENT_COMP);
        }
        try {
            inisListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    @Override
    public InitiativenListAdapter getInitiativenListAdapter() {
        return new UpcomingInitiativenListAdapter(this, allInis, R.id.initiativenList);
    }
}
