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
import android.view.Menu;
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
    AreasTabActivity ata;
    InitiativesTabActivity inis;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ata = new AreasTabActivity();
        ata.onCreate(savedInstanceState);

        inis = new InitiativesTabActivity();
        inis.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        mViewPager = (ViewPager) findViewById(R.id.pager);

        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        mTabsAdapter = new MyAdapter(getSupportFragmentManager(), ata, inis);
        mViewPager.setAdapter(mTabsAdapter);
        ((LiqoidApplication) getApplication()).statusLine = ((TextView) findViewById(R.id.statusline));


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                return inis.onCreateOptionsMenu(menu);
            case 1:
                return ata.onCreateOptionsMenu(menu);
        }
        return false;
    }

    static class MyAdapter extends FragmentPagerAdapter {

        AreasTabActivity ata;
        InitiativesTabActivity inis;

        public MyAdapter(FragmentManager fm, AreasTabActivity ata, InitiativesTabActivity inis) {
            super(fm);
            this.ata = ata;
            this.inis = inis;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return inis;
                case 1:
                    return ata;
            }
            return null;
        }
    }
}
