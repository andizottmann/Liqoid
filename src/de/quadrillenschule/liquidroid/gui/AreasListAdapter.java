/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.gui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import de.quadrillenschule.liquidroid.AreasTabActivity;
import de.quadrillenschule.liquidroid.LiqoidApplication;
import de.quadrillenschule.liquidroid.model.Area;
import de.quadrillenschule.liquidroid.model.Areas;
import de.quadrillenschule.liquidroid.R;

public class AreasListAdapter extends ArrayAdapter<Area> {

    private Areas areas;
    private int viewId;
    private Activity activity;

    public AreasListAdapter(Activity activity, Areas values, int viewId) {
        super(activity, NO_SELECTION, values);
        //  this.context = pcontext;
        this.areas = values;
        this.activity = activity;

    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        CheckBox retval = new CheckBox(activity);

        retval.setTextColor(Color.BLACK);
        retval.setBackgroundColor(Color.argb(255, 245, 245, 245));
        String inisspecial = " Inis:" + areas.get(position).getInitiativen().size();
        if (!areas.getSelectedAreas().contains(areas.get(position))) {
            inisspecial = " Inis:?";
        }
        retval.setText(Html.fromHtml(areas.get(position).getName() + "<br><small> " + activity.getString(R.string.members) + ":" + areas.get(position).getMember_weight() + " &nbsp;&nbsp; " + inisspecial + " &nbsp;&nbsp; Favs <font color=\"#eecc00\">\u2605</font>" + areas.get(position).getInitiativen().getSelectedIssues().size() + "</small>"));
        retval.setChecked(areas.isSelected(position));
        retval.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Area myarea = areas.getByName(((CheckBox) arg0).getText().toString());
                boolean value = ((CheckBox) arg0).isChecked();
                areas.setSelectedArea(myarea, value);

                try {
                    if (((LiqoidApplication) activity.getApplication()).getGlobalPreferences().getBoolean("vibrate", true)) {
                        Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(30);
                    }
                } catch (Exception e) {
                    //its not a vibrator :/
                }

            }
        });

        activity.registerForContextMenu(retval);


        return retval;
    }
}
