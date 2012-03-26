/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.os.Bundle;
import android.widget.TextView;
import de.quadrillenschule.liquidroid.gui.InitiativenListAdapter;
import de.quadrillenschule.liquidroid.gui.RecentInitiativenListAdapter;
import de.quadrillenschule.liquidroid.model.Initiative;

/**
 *
 * @author andi
 */
public class RecentTabActivity extends InitiativesTabActivity {
 @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        ((TextView) findViewById(R.id.tabinis_title)).setText(R.string.tab_recent);
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
            allInis.reverse(Initiative.ISSUE_LAST_EVENT_COMP);
        } else {
            allInis.sort(Initiative.ISSUE_LAST_EVENT_COMP);
        }
        try {
            inisListAdapter.notifyDataSetChanged();
        } catch (NullPointerException e) {
        }
    }

    @Override
    public InitiativenListAdapter getInitiativenListAdapter() {
        return new RecentInitiativenListAdapter(this, allInis, R.id.initiativenList);
    }
}
