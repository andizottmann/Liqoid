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

public class AllInitiativenListAdapter extends ArrayAdapter<Initiative> {

    private Context context;
    private Initiativen initiativen;
    private int viewId;
    private Activity activity;

    public AllInitiativenListAdapter(Context pcontext, Initiativen initiativen, int viewId, Activity activity) {
        super(pcontext, NO_SELECTION, initiativen);
        this.context = pcontext;
        this.initiativen = initiativen;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        CheckBox retval = new CheckBox(context);

        retval.setTextColor(Color.BLACK);
        retval.setBackgroundColor(Color.argb(255, 245, 245, 245));
        retval.setText(initiativen.get(position).name);
        retval.setChecked(initiativen.get(position).isSelected());

        retval.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
              try {


                initiativen.findByName(((CheckBox) arg0).getText().toString()).get(0).setSelected(!initiativen.findByName(((CheckBox) arg0).getText().toString()).get(0).isSelected());
                } catch (Exception e){
                }
            }
        });
        
        activity.registerForContextMenu(retval);


        return retval;
    }
}
