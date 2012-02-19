/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

public class AreasListAdapter extends ArrayAdapter<Area> {

    private Context context;
    private Areas areas;
    private int viewId;
    private Activity activity;

    public AreasListAdapter(Context pcontext, Areas values, int viewId, Activity activity) {
        super(pcontext, NO_SELECTION, values);
        this.context = pcontext;
        this.areas = values;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        CheckBox retval = new CheckBox(context);

        retval.setTextColor(Color.BLACK);
        retval.setBackgroundColor(Color.argb(255, 245, 245, 245));
        retval.setText(areas.get(position).getName());
        retval.setChecked(areas.get(position).isSelected());

        retval.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
              
                areas.getByName(((CheckBox) arg0).getText().toString()).setSelected(!areas.getByName(((CheckBox) arg0).getText().toString()).isSelected());

            }
        });
        
        activity.registerForContextMenu(retval);


        return retval;
    }
}
