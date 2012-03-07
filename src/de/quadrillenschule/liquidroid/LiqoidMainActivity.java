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
import android.widget.TabHost;
import android.widget.TextView;
import java.util.ArrayList;

public class LiqoidMainActivity extends TabActivity implements GestureOverlayView.OnGesturePerformedListener{

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

        tabHost.setCurrentTab(3);

      
        ((LiqoidApplication) getApplication()).statusLine = ((TextView) findViewById(R.id.statusline));
    }

    @Override
    public void onResume() {
        super.onResume();
        //    ((LiqoidApplication) getApplication()).fireLQFBInstanceChangedEvent();

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
