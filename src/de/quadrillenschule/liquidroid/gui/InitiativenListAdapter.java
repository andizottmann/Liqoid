/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.gui;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import de.quadrillenschule.liquidroid.InitiativesTabActivity;
import de.quadrillenschule.liquidroid.model.Initiative;
import de.quadrillenschule.liquidroid.model.MultiInstanceInitiativen;

public class InitiativenListAdapter extends ArrayAdapter<Initiative> {

    protected MultiInstanceInitiativen initiativen;
     protected Activity activity;


    public InitiativenListAdapter( Activity activity, MultiInstanceInitiativen initiativen) {

        super(activity, NO_SELECTION, initiativen);
        this.initiativen = initiativen;

        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        if (initiativen.size() <= position) {
            return null;
        }
        View retval = getInternalView(position);
        return retval;
    }

    protected View getInternalView(int position) {
        IssueItemView retval = new IssueItemView(activity, initiativen.get(position));

        return retval;
    }
}
