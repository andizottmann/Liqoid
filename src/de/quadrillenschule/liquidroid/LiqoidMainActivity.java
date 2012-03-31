package de.quadrillenschule.liquidroid;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import java.util.ArrayList;

public class LiqoidMainActivity extends FragmentActivity {

    GestureLibrary gestureLibrary;
    ViewPager mViewPager;
    MyAdapter mTabsAdapter;

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
        mViewPager = (ViewPager) findViewById(R.id.pager);

        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        mTabsAdapter = new MyAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAdapter);

        // Intent intent;  // Reusable Intent for each tab
        // intent = new Intent().setClass(this, UpcomingTabActivity.class);
        //  spec = tabHost.newTabSpec("upcoming").setIndicator(res.getString(R.string.tab_upcoming));
        // mTabsAdapter.addTab(spec);
        //   mTabsAdapter.addTab(spec,AreasTabActivity.class,null);

//        intent = new Intent().setClass(this, RecentTabActivity.class);
//        spec = tabHost.newTabSpec("recent").setIndicator(res.getString(R.string.tab_recent)).setContent(intent);
//        tabHost.addTab(spec);
//
//        intent = new Intent().setClass(this, InitiativesTabActivity.class);
//        spec = tabHost.newTabSpec("inis").setIndicator(res.getString(R.string.tab_inis)).setContent(intent);
//        tabHost.addTab(spec);
//
//        intent = new Intent().setClass(this, AreasTabActivity.class);
//        spec = tabHost.newTabSpec("areas").setIndicator(res.getString(R.string.tab_areas)).setContent(intent);
//        tabHost.addTab(spec);



        ((LiqoidApplication) getApplication()).statusLine = ((TextView) findViewById(R.id.statusline));

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

    public static class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Fragment getItem(int position) {
            return new AreasTabActivity();
        }
    }
}
