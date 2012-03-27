package de.quadrillenschule.liquidroid;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;
import android.widget.TextView;
import java.util.ArrayList;

public class LiqoidMainActivity extends TabActivity implements GestureOverlayView.OnGesturePerformedListener, TabHost.OnTabChangeListener {

    TabHost tabHost;
    GestureLibrary gestureLibrary;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLibrary.load()) {
            finish();
        }

        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab

        Intent intent;  // Reusable Intent for each tab
        intent = new Intent().setClass(this, UpcomingTabActivity.class);
        spec = tabHost.newTabSpec("upcoming").setIndicator(res.getString(R.string.tab_upcoming)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, RecentTabActivity.class);
        spec = tabHost.newTabSpec("recent").setIndicator(res.getString(R.string.tab_recent)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, InitiativesTabActivity.class);
        spec = tabHost.newTabSpec("inis").setIndicator(res.getString(R.string.tab_inis)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, AreasTabActivity.class);
        spec = tabHost.newTabSpec("areas").setIndicator(res.getString(R.string.tab_areas)).setContent(intent);
        tabHost.addTab(spec);
        TextView v = new TextView(this);
        int tabheight = (int) (v.getTextSize() * 2.7);

        tabHost.setCurrentTab(3);
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = tabheight;



        }
        lastTab = 3;
        lastTabView = tabHost.getCurrentView();

        tabHost.setOnTabChangedListener(this);

        ((LiqoidApplication) getApplication()).statusLine = ((TextView) findViewById(R.id.statusline));
    }

    @Override
    public void onResume() {
        super.onResume();
        //    ((LiqoidApplication) getApplication()).fireLQFBInstanceChangedEvent();

    }
    private long ANIMATION_DURATION = 600;

    public Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(ANIMATION_DURATION);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public Animation inFromLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(ANIMATION_DURATION);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    public Animation outToRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(ANIMATION_DURATION);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(ANIMATION_DURATION);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }
    private int lastTab = 3;
    private View lastTabView;

    public void onTabChanged(String tabId) {
         if (lastTab == tabHost.getCurrentTab()) {
            return;
        }
        if (((LiqoidApplication) getApplication()).getGlobalPreferences().getBoolean("animations", false)) {
            if (lastTab > tabHost.getCurrentTab()) {
                tabHost.getCurrentView().setAnimation(inFromLeftAnimation());
                lastTabView.setAnimation(outToRightAnimation());

            } else {
                tabHost.getCurrentView().setAnimation(inFromRightAnimation());
                lastTabView.setAnimation(outToLeftAnimation());

            }
        }
        lastTab = tabHost.getCurrentTab();
        lastTabView = tabHost.getCurrentView();
    }

    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);

        // We want at least one prediction
        if (predictions.size() > 0) {
            Prediction prediction = predictions.get(0);
            // We want at least some confidence in the result
            if (prediction.score > 1.0) {
                int base = tabHost.getCurrentTab();

                // Show the spell
                if (prediction.name.equals("right")) {
                    if (base <= 0) {
                        return;
                    }
                    tabHost.setCurrentTab(base - 1);
                }
                if (prediction.name.equals("left")) {
                    if (base > 2) {
                        return;
                    }
                    tabHost.setCurrentTab(base + 1);

                }


            }
        }
    }
}
