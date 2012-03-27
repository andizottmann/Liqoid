/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Vibrator;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.quadrillenschule.liquidroid.InitiativesTabActivity;
import de.quadrillenschule.liquidroid.LiqoidApplication;
import de.quadrillenschule.liquidroid.model.Initiative;
import de.quadrillenschule.liquidroid.tools.Utils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import de.quadrillenschule.liquidroid.R;

/**
 *
 * @author andi
 */
public class IssueItemView extends LinearLayout implements OnClickListener {

    InitiativesTabActivity activity;
    public Initiative initiative;
    CheckBox myCheckBox;
    TextView statusLine, instanceView, areaView;
    LinearLayout expandView;
    ImageView colorView;
    //LinearLayout contentContainer, statusContainer;
    // RelativeLayout contentContainer;
    Button expandButton;
    private boolean expandview = false;

    public IssueItemView(InitiativesTabActivity activity, Initiative initiative) {
        super(activity);
        this.activity = activity;
        this.initiative = initiative;


        RelativeLayout.LayoutParams rlp;
        RelativeLayout rl = new RelativeLayout(activity);
        rl.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        rl.setPadding(4, 2, 2, 2);
        expandButton = new Button(activity);
        expandButton.setBackgroundColor(itemSpecificColorcode());
        expandButton.setTypeface(Typeface.MONOSPACE);
        expandButtonSetText();
        expandButton.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                expand();
            }
        });
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        this.addView(expandButton, lp);


        statusLine = new TextView(activity);
        rlp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        statusLine.setText(Html.fromHtml(getStatusText()));
        statusLine.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        statusLine.setTextColor(Color.parseColor("#108020"));
        statusLine.setSingleLine();
        statusLine.setId(2);
        rl.addView(statusLine, rlp);

        instanceView = new TextView(activity);
        rlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rlp.addRule(RelativeLayout.ALIGN_RIGHT);
        rlp.addRule(RelativeLayout.ALIGN_TOP);
        rlp.setMargins(0, 0, 5, 0);
        instanceView.setText(Html.fromHtml("<b><font color=black>" + initiative.getLqfbInstance().getShortName() + "</font></b>"));
        instanceView.setTextColor(Color.parseColor("#108020"));
        instanceView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        instanceView.setSingleLine();
        instanceView.setId(3);
        rl.addView(instanceView, rlp);

        areaView = new TextView(activity);
        rlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rlp.addRule(RelativeLayout.ALIGN_RIGHT);
        rlp.addRule(RelativeLayout.ALIGN_TOP);
        rlp.addRule(RelativeLayout.BELOW, statusLine.getId());
        rlp.addRule(RelativeLayout.BELOW, instanceView.getId());
        rlp.setMargins(0, 0, 5, 0);
        areaView.setText(Html.fromHtml("<b><font color=black>" + initiative.getArea().getName() + "</font></b>"));
        areaView.setTextColor(Color.parseColor("#108020"));
        areaView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        areaView.setSingleLine();
        areaView.setId(31);
        rl.addView(areaView, rlp);

        myCheckBox = new CheckBox(activity, null, android.R.attr.starStyle);
        myCheckBox.setTextColor(Color.BLACK);
        myCheckBox.setText(Html.fromHtml(initiative.name + "<font color=\"#009010\"> / Alt: <b>" + ((int) initiative.getConcurrentInis().size()) + "</b></font>"));
        myCheckBox.setChecked(initiative.getArea().getInitiativen().isIssueSelected(initiative.issue_id));
        myCheckBox.setOnClickListener(this);
        rlp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.BELOW, areaView.getId());
        rlp.addRule(RelativeLayout.ALIGN_TOP);
        myCheckBox.setId(4);
        activity.registerForContextMenu(myCheckBox);
        rl.addView(myCheckBox, rlp);

        expandView = new LinearLayout(activity);
        expandView.setOrientation(VERTICAL);
        rlp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.BELOW, myCheckBox.getId());
        rlp.addRule(RelativeLayout.ALIGN_TOP);
        //  expandView.setText("");
        // expandView.setTextColor(Color.parseColor("#108020"));
        //  expandView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        expandView.setId(5);
        rl.addView(expandView, rlp);

        this.addView(rl);
        this.forceLayout();
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
        return " <b><font color=black> " + activity.getString(initiative.getIntlStateResId()) + "</font> - <font color=blue>" + formatter.format(initiative.issue_created) + "</font></b>";
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

    public void expandButtonSetText() {
        String text = Utils.lessThanDays(itemSpecificDelta());
        if (expandview) {
            text += "\n\n\u2191";
        } else {
            text += "\n\n\u2193";
        }
        expandButton.setText(text);

    }

    public void expand() {
        if (expandview) {
            statusLine.setText(Html.fromHtml(getStatusText()));

            expandView.removeAllViews();
            expandview = false;
            expandButtonSetText();
        } else {
            expandView.addView(generateIniButton(initiative));
            for (Initiative i : initiative.getConcurrentInis()) {
                expandView.addView(generateIniButton(i));

            }
            //statusLine.setText(Html.fromHtml(getStatusText() + "<br>" + string + "<br>"));

            expandview = true;
            expandButtonSetText();
        }
    }

    public Button generateIniButton(final Initiative i) {
        Button retval = new Button(activity);
        retval.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Status: "+activity.getString(i.getIntlStateResId())+"\n\n"+i.current_draft_content).setNeutralButton(R.string.open_browser, new android.content.DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(i.getLqfbInstance().getWebUrl() + "initiative/show/" + i.id + ".html"));
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        activity.startActivity(myIntent);
                    }
                }).setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                     Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    intent.putExtra(Intent.EXTRA_TEXT, i.getLqfbInstance().getWebUrl() + "initiative/show/" + i.id + ".html");
                    activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share)));
                    }
                }).setTitle(i.name);

                AlertDialog ad = builder.create();
                ad.show();

            }
        });
        retval.setText(Html.fromHtml(i.name + "<font color=\"#009010\"><br>"+activity.getString(R.string.supporter)+":<b>" + i.supporter_count + "</b></b>"));
        retval.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        retval.setGravity(Gravity.LEFT);
        return retval;
    }
}
