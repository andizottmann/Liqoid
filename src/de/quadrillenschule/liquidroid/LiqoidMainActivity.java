package de.quadrillenschule.liquidroid;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TabHost;
import de.quadrillenschule.liquidroid.model.LQFBInstances;

public class LiqoidMainActivity extends TabActivity implements TabHost.OnTabChangeListener {

public static LQFBInstances lqfbInstances;
TabHost tabHost;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (lqfbInstances==null){
        lqfbInstances=new LQFBInstances(this);
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

    }

    @Override
    public void onPause(){
        lqfbInstances.save();
        super.onPause();
    }

    public static void setTabColor(TabHost tabhost) {
        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#303030")); //unselected
       
        }
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(Color.parseColor("#000000")); // selected
    }

    public void onTabChanged(String arg0) {
       setTabColor(tabHost);
    }
}
