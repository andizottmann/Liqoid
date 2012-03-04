package de.quadrillenschule.liquidroid;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import de.quadrillenschule.liquidroid.model.Area;
import de.quadrillenschule.liquidroid.model.LQFBInstance;
import de.quadrillenschule.liquidroid.model.LQFBInstances;
import de.quadrillenschule.liquidroid.model.LQFBInstancesListAdapter;
import java.util.ArrayList;

public class LiqoidMainActivity extends TabActivity implements TabHost.OnTabChangeListener, GestureOverlayView.OnGesturePerformedListener, OnItemSelectedListener {

    TabHost tabHost;
    GestureLibrary gestureLibrary;
    ArrayAdapter adapter;

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
        spec = tabHost.newTabSpec("upcoming").setIndicator(res.getString(R.string.tab_upcoming),
                res.getDrawable(R.drawable.ic_tab_upcoming)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, RecentTabActivity.class);
        spec = tabHost.newTabSpec("recent").setIndicator(res.getString(R.string.tab_recent),
                res.getDrawable(R.drawable.ic_tab_recent)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, InitiativesTabActivity.class);
        spec = tabHost.newTabSpec("inis").setIndicator(res.getString(R.string.tab_inis),
                res.getDrawable(R.drawable.ic_tab_inis)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, AreasTabActivity.class);
        spec = tabHost.newTabSpec("areas").setIndicator(res.getString(R.string.tab_areas),
                res.getDrawable(R.drawable.ic_tab_areas)).setContent(intent);
        tabHost.addTab(spec);

        setTabColor(tabHost);
        tabHost.setOnTabChangedListener(this);
        tabHost.setCurrentTab(3);

        final Spinner instanceSpinner = (Spinner) findViewById(R.id.instanceSelector);
        adapter = new LQFBInstancesListAdapter(this, ((LiqoidApplication) getApplication()).lqfbInstances, android.R.layout.simple_spinner_item, this);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instanceSpinner.setAdapter(adapter);
        instanceSpinner.setOnItemSelectedListener(this);
        int i = ((LiqoidApplication) getApplication()).lqfbInstances.indexOf(((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance());
        instanceSpinner.setSelection(i);
        
        ((LiqoidApplication) getApplication()).statusLine=((TextView) findViewById(R.id.statusline));
    }

    @Override
    public void onResume() {
        super.onResume();
        //    ((LiqoidApplication) getApplication()).fireLQFBInstanceChangedEvent();

    }

    public static void setTabColor(TabHost tabhost) {
        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#303030")); //unselected

        }
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(Color.parseColor("#000000")); // selected
    }

    //Instances Spinner item selected
    public void onItemSelected(AdapterView<?> arg0, View arg1, int i, long arg3) {
        LQFBInstances ls = ((LiqoidApplication) getApplication()).lqfbInstances;
        if (ls.indexOf(ls.getSelectedInstance()) != i) {
            ls.setSelectedInstance(i);
            ((LiqoidApplication) getApplication()).fireLQFBInstanceChangedEvent();
        }
    }

    public void onTabChanged(String arg0) {
        setTabColor(tabHost);
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

    public void onNothingSelected(AdapterView<?> arg0) {
        //Do nothing
    }
}
