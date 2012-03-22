/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.gui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.quadrillenschule.liquidroid.InitiativesTabActivity;
import de.quadrillenschule.liquidroid.LiqoidApplication;
import de.quadrillenschule.liquidroid.model.Initiative;
import de.quadrillenschule.liquidroid.tools.Utils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author andi
 */
public class IssueItemView extends LinearLayout implements OnClickListener {

    InitiativesTabActivity activity;
    public Initiative initiative;
    CheckBox myCheckBox;
    TextView statusLine;
    ImageView colorView;
    LinearLayout contentContainer;
    Button expandButton;

    public IssueItemView(InitiativesTabActivity activity, Initiative initiative) {
        super(activity);
        setOrientation(HORIZONTAL);
        this.activity = activity;
        this.initiative = initiative;

        myCheckBox = new CheckBox(activity, null, android.R.attr.starStyle);
        myCheckBox.setTextColor(Color.BLACK);
        myCheckBox.setBackgroundColor(Color.argb(255, 245, 245, 245));
        myCheckBox.setText(initiative.name + " (" + ((int) initiative.getConcurrentInis().size() + 1) + " Alt.)");
        myCheckBox.setChecked(initiative.getArea().getInitiativen().isIssueSelected(initiative.issue_id));
        myCheckBox.setOnClickListener(this);
        myCheckBox.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        activity.registerForContextMenu(myCheckBox);

        statusLine = new TextView(activity);
        statusLine.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        statusLine.setText(Html.fromHtml(getStatusText()));
        statusLine.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        statusLine.setTextColor(Color.parseColor("#108020"));
        statusLine.setBackgroundColor(Color.argb(255, 245, 245, 245));

        expandButton = new Button(activity);
        expandButton.setBackgroundColor(itemSpecificColorcode());
        expandButton.setTypeface(Typeface.MONOSPACE);
        expandButtonSetText();
        expandButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        expandButton.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                expand();
            }
        });
        contentContainer = new LinearLayout(activity);
        contentContainer.setOrientation(VERTICAL);
        contentContainer.setBackgroundColor(Color.argb(255, 245, 245, 245));
        contentContainer.addView(statusLine);
        contentContainer.addView(myCheckBox);
        LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.MATCH_PARENT);
        contentContainer.setLayoutParams(lp);
        contentContainer.setPadding(8, 1, 1, 1);

        this.addView(expandButton);
        this.addView(contentContainer);
    }

    protected long itemSpecificDelta() {
        return System.currentTimeMillis() - initiative.issue_created.getTime();
    }

    protected int itemSpecificColorcode() {
        long delta = System.currentTimeMillis() - initiative.issue_created.getTime();
        long oneday = 1000 * 60 * 60 * 24;
        SharedPreferences gp = ((LiqoidApplication) activity.getApplication()).getGlobalPreferences();
        if (delta < Long.parseLong(gp.getString(LiqoidApplication.REDLIMIT_PREF, oneday + ""))) {
            return Color.argb(255, 255, 100, 100);
        }
        if (delta < Long.parseLong(gp.getString(LiqoidApplication.ORANGELIMIT_PREF, oneday * 3 + ""))) {
            return Color.argb(255, 255, 140, 100);
        }
        if (delta < Long.parseLong(gp.getString(LiqoidApplication.YELLOWLIMIT_PREF, oneday * 5 + ""))) {
            return Color.argb(255, 255, 255, 160);
        }
        return Color.LTGRAY;
    }

    protected String getStatusText() {
        DateFormat formatter = new SimpleDateFormat(activity.getDateFormat());
        return " <b><font color=black> " + initiative.state + "</font></b> <font color=blue>" + formatter.format(initiative.issue_created) + "</font> <b><font color=black>" + initiative.getLqfbInstance().getShortName() + "</font></b>";
    }

    public void onClick(View arg0) {
        int issueid = initiative.issue_id;
        initiative.getArea().getInitiativen().setSelectedIssue(issueid, !initiative.getArea().getInitiativen().isIssueSelected(issueid));

        try {
            if (((LiqoidApplication) activity.getApplication()).getGlobalPreferences().getBoolean("vibrate", true)) {
                Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(30);
            }
        } catch (Exception e) {
            //its not a vibrator :/
        }
    }
    private boolean expandview = false;

    public void expandButtonSetText() {
        expandButton.setText(Utils.lessThanDays(itemSpecificDelta()));

    }

    public void expand() {
        if (expandview) {
            statusLine.setText(Html.fromHtml(getStatusText()));
            expandButtonSetText();
            expandview = false;
        } else {
            String string = "";
            string += "Area: <font color=black><b>" + initiative.getArea().getName() + "</b><br></font>";
            string += "Members: <font color=black><b>" + initiative.getArea().getMember_weight() + "</b><br><br></font>";

            string += "Supporter: <font color=black><b>" + initiative.supporter_count + "</b> &nbsp; " + initiative.name + "</font>";
            for (Initiative i : initiative.getConcurrentInis()) {
                string += "<br><br>Supporter: <font color=black><b>" + i.supporter_count + "</b> &nbsp; " + i.name + "</font>";
            }
            statusLine.setText(Html.fromHtml(getStatusText() + "<br>" + string + "<br>"));

            expandButtonSetText();
            expandview = true;
        }
    }
}
