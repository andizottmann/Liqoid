/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.gui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.quadrillenschule.liquidroid.model.Initiative;
import de.quadrillenschule.liquidroid.model.Initiativen;
import de.quadrillenschule.liquidroid.model.MultiInstanceInitiativen;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author andi
 */
public class IssueItemView extends LinearLayout implements OnClickListener {

    Activity activity;
    public Initiative initiative;
    CheckBox myCheckBox;
    TextView statusLine;

    public IssueItemView(Activity activity, Initiative initiative) {
        super(activity);
        setOrientation(VERTICAL);
        this.activity = activity;
        this.initiative = initiative;
        myCheckBox = new CheckBox(activity, null, android.R.attr.starStyle);
        myCheckBox.setTextColor(Color.BLACK);
        myCheckBox.setBackgroundColor(Color.argb(255, 245, 245, 245));

        myCheckBox.setText(initiative.name);
        myCheckBox.setChecked(initiative.getArea().getInitiativen().isIssueSelected(initiative.issue_id));
        myCheckBox.setOnClickListener(this);
        myCheckBox.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        activity.registerForContextMenu(myCheckBox);

        statusLine = new TextView(activity);
        statusLine.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        statusLine.setText(getStatusText());
        statusLine.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        statusLine.setTextColor(Color.parseColor("#108020"));
        statusLine.setBackgroundColor(Color.argb(255, 245, 245, 245));
        this.addView(statusLine);
        this.addView(myCheckBox);
    }

    protected String getStatusText() {
        DateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm");
        return "  " + initiative.state + "     Created: " + formatter.format(initiative.issue_created) + " " + initiative.getLqfbInstance().getShortName();
    }

    public void onClick(View arg0) {
        int issueid = initiative.issue_id;
        initiative.getArea().getInitiativen().setSelectedIssue(issueid, !initiative.getArea().getInitiativen().isIssueSelected(issueid));

        try {
            Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(30);
        } catch (Exception e) {
            //its not a vibrator :/
        }
    }
}
