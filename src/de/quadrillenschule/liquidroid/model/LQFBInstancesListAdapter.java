/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

/**
 *
 * @author andi
 */
public class LQFBInstancesListAdapter extends ArrayAdapter<LQFBInstance> {
  NoFilter noFilter;
    public LQFBInstancesListAdapter(Context pcontext, LQFBInstances values, int viewId, Activity activity) {
        super(pcontext,viewId, values);
      
    }

    
    @Override
    public Filter getFilter() {
        if (noFilter == null) {
            noFilter = new NoFilter();
        }
        return noFilter;
    }

    private class NoFilter extends Filter {

        protected FilterResults performFiltering(CharSequence prefix) {
            return new FilterResults();
        }

        protected void publishResults(CharSequence constraint,
                FilterResults results) {
            // Do nothing
        }
    }
}
