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
import de.quadrillenschule.liquidroid.InitiativesTabActivity;
import de.quadrillenschule.liquidroid.LiqoidApplication;

public class AllInitiativenListAdapter extends ArrayAdapter<Initiative> {

    private Initiativen initiativen;
    private int viewId;
    private InitiativesTabActivity activity;

    public AllInitiativenListAdapter(InitiativesTabActivity activity, Initiativen initiativen, int viewId) {
        super(activity, NO_SELECTION, initiativen);
        this.initiativen = initiativen;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        CheckBox retval = new CheckBox(activity);

        retval.setTextColor(Color.BLACK);
        retval.setBackgroundColor(Color.argb(255, 245, 245, 245));
        retval.setText(initiativen.get(position).name);
        retval.setChecked(initiativen.get(position).isSelected());

        retval.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                try {


                    initiativen.findByName(((CheckBox) arg0).getText().toString()).get(0).setSelected(!initiativen.findByName(((CheckBox) arg0).getText().toString()).get(0).isSelected());
                    ((LiqoidApplication) activity.getApplication()).saveSelectedIssuesToPrefs();
                } catch (Exception e) {
                }
            }
        });

        activity.registerForContextMenu(retval);


        return retval;
    }
}
