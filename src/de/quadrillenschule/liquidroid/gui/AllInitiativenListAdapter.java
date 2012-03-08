/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.gui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import de.quadrillenschule.liquidroid.InitiativesTabActivity;
import de.quadrillenschule.liquidroid.model.Initiative;
import de.quadrillenschule.liquidroid.model.MultiInstanceInitiativen;

public class AllInitiativenListAdapter extends ArrayAdapter<Initiative> {

    private MultiInstanceInitiativen initiativen;
    private int viewId;
    private InitiativesTabActivity activity;

    public AllInitiativenListAdapter(InitiativesTabActivity activity, MultiInstanceInitiativen initiativen, int viewId) {
        super(activity, NO_SELECTION, initiativen);
        this.initiativen = initiativen;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
         if (initiativen.size() <= position) {
            return null;
        }
        int issueid = initiativen.get(position).issue_id;
        IssueItemView retval = new IssueItemView(activity, initiativen.get(position));
        
        return retval;
    }
}
