/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import de.quadrillenschule.liquidroid.gui.InitiativenListAdapter;
import de.quadrillenschule.liquidroid.gui.RecentInitiativenListAdapter;
import de.quadrillenschule.liquidroid.model.Initiative;
import de.quadrillenschule.liquidroid.model.MultiInstanceInitiativen;

/**
 *
 * @author andi
 */
public class RecentTabActivity extends InitiativesTabActivity {

  

    @Override
    public void finishedRefreshInisList(MultiInstanceInitiativen newInis) {

        allInis = newInis;
        inisListAdapter = getInitiativenListAdapter();
        filterList();
        sortList();
        inisListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //  ((TextView) getActivity().findViewById(R.id.tabinis_title)).setText(R.string.tab_recent);
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
        if (mainActivity == null) {
            mainActivity = getActivity();

        }
        return new RecentInitiativenListAdapter(mainActivity, allInis);
    }
}
