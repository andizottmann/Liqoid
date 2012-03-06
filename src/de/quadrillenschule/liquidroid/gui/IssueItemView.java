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
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author andi
 */
public class IssueItemView extends LinearLayout implements OnClickListener {

    Activity activity;
    private Initiativen initiativen;
    private int issueid;
    CheckBox myCheckBox;
    TextView statusLine;

    public IssueItemView(Activity activity, Initiativen initiativen, int issueid) {
        super(activity);
        setOrientation(VERTICAL);
        this.activity = activity;
        this.initiativen = initiativen;
        this.issueid = issueid;
        Initiative myini = initiativen.findByIssueID(issueid).get(0);
        myCheckBox = new CheckBox(activity, null, android.R.attr.starStyle);
        myCheckBox.setTextColor(Color.BLACK);
        myCheckBox.setBackgroundColor(Color.argb(255, 245, 245, 245));

        myCheckBox.setText(initiativen.findByIssueID(issueid).get(0).name);
        myCheckBox.setChecked(initiativen.isIssueSelected(issueid));
        myCheckBox.setOnClickListener(this);
        myCheckBox.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        statusLine = new TextView(activity);
        statusLine.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        DateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm");

        statusLine.setText("  " + myini.state + "     Created: " + formatter.format(myini.issue_created));
        statusLine.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        statusLine.setTextColor(Color.parseColor("#108020"));
        statusLine.setBackgroundColor(Color.argb(255, 245, 245, 245));

        this.addView(statusLine);
        this.addView(myCheckBox);
    }

    /**
     * @return the initiativen
     */
    public Initiativen getInitiativen() {
        return initiativen;
    }

    /**
     * @param initiativen the initiativen to set
     */
    public void setInitiativen(Initiativen initiativen) {
        this.initiativen = initiativen;
    }

    /**
     * @return the issueid
     */
    public int getIssueid() {
        return issueid;
    }

    /**
     * @param issueid the issueid to set
     */
    public void setIssueid(int issueid) {
        this.issueid = issueid;
    }

    public void onClick(View arg0) {
        initiativen.setSelectedIssue(issueid, !initiativen.isIssueSelected(issueid));

        try {
            Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(30);
        } catch (Exception e) {
            //its not a vibrator :/
        }
    }
}
