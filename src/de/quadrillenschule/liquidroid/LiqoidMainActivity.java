package de.quadrillenschule.liquidroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import de.quadrillenschule.liquidroid.model.LQFBInstances;
import de.quadrillenschule.liquidroid.model.MultiInstanceInitiativen;
import de.quadrillenschule.liquidroid.model.RefreshInisListThread;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LiqoidMainActivity extends FragmentActivity implements RefreshInisListThread.RefreshInisListListener {

    ProgressDialog progressDialog;
    public RefreshInisListThread ralt;
    ViewPager mViewPager;
    MyAdapter mTabsAdapter;
    AreasTabActivity ata;
    InitiativesTabActivity inis;
    UpcomingTabActivity upcominginis;
    RecentTabActivity recentinis;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ata = new AreasTabActivity();
        ata.onCreate(savedInstanceState);
        ata.mainActivity = this;

        inis = new InitiativesTabActivity();
        inis.onCreate(savedInstanceState);
        inis.mainActivity = this;

        upcominginis = new UpcomingTabActivity();
        upcominginis.onCreate(savedInstanceState);
        upcominginis.mainActivity = this;

        recentinis = new RecentTabActivity();
        recentinis.onCreate(savedInstanceState);
        recentinis.mainActivity = this;

        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        mViewPager = (ViewPager) findViewById(R.id.pager);

        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        mTabsAdapter = new MyAdapter(getSupportFragmentManager(), ata, inis, upcominginis, recentinis);
        mViewPager.setAdapter(mTabsAdapter);
        ((LiqoidApplication) getApplication()).statusLine = ((TextView) findViewById(R.id.statusline));


    }

    @Override
    public void onResume() {
        super.onResume();

    //    refreshLists(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.prefs:
                startActivity(new Intent(this, GlobalPrefsActivity.class));
                return true;
            case R.id.refresh_areaslist:
                refreshLists(true);
                return true;
            case R.id.unlock_instance:
                ((LiqoidApplication) getApplication()).unlockInstancesDialog(this).show();
                return true;
            case R.id.lock_instance:
                ((LiqoidApplication) getApplication()).lockInstancesDialog(this).show();
                return true;
            case R.id.about:
                ((LiqoidApplication) getApplication()).aboutDialog(this).show();
                return true;
            case R.id.clearcache:
                ((LiqoidApplication) getApplication()).clearCache(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refreshLists(boolean download) {

        LQFBInstances.selectionUpdatesForRefresh = false;
        ralt = new RefreshInisListThread(download, this, handler, (LiqoidApplication) getApplication());
        ralt.start();

    }

    private Activity myself() {
        return this;
    }
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            //Setting the status line text
            long dataage = ralt.overallDataAge;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dataagestr = formatter.format(new Date(dataage));
            String prefix = "";
            if (ralt.currentInstance != null) {
                if (ralt.currentInstance.pauseDownload) {
                    prefix += "Offline - ";
                }
            }
            if (!ralt.dataComplete) {
                prefix += getString(R.string.datanotcomplete) + " - ";
            }
            ((LiqoidApplication) getApplication()).statusLineText(prefix + getApplicationContext().getString(R.string.dataage) + ": " + dataagestr);

            //Updating status of progressdialog
            if ((msg.what == RefreshInisListThread.DOWNLOADING)) {
                progressDialog.setMessage(getApplicationContext().getString(R.string.downloading) + "\n" + ralt.currentlyDownloadedArea + "...");
            }
            if ((msg.what == RefreshInisListThread.DOWNLOADING_INSTANCE)) {
                progressDialog.setMessage(getApplicationContext().getString(R.string.downloading) + "\n" + ralt.currentlyDownloadedInstance + "...");
            }
            if (msg.what == RefreshInisListThread.UPDATING) {

                progressDialog = ProgressDialog.show(myself(), "",
                        getApplicationContext().getString(R.string.updating) + "...", true);
            }
            if (msg.what == RefreshInisListThread.FINISH_OK) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    //Sometimes it is not attached anymore
                    progressDialog = null;
                }
           //     inis.onFinishOk();
             //   ata.onFinishOk();
               // upcominginis.onFinishOk();
               // recentinis.onFinishOk();
            }
            if (ralt.currentInstance != null) {
                if ((progressDialog != null) && (!ralt.currentInstance.pauseDownload)) {
                    if (msg.what == RefreshInisListThread.DOWNLOAD_ERROR) {
                        progressDialog.setMessage(getApplicationContext().getString(R.string.download_error));
                    }
                    if (msg.what == RefreshInisListThread.DOWNLOAD_RETRY) {
                        progressDialog.setMessage(getApplicationContext().getString(R.string.downloading) + "\n" + ralt.currentlyDownloadedArea + " @ " + ralt.currentlyDownloadedInstance);
                    }
                }
            }
        }
    };

    public void finishedRefreshInisList(MultiInstanceInitiativen newInis) {
 
        inis.finishedRefreshInisList(newInis);
        ata.finishedRefreshInisList(newInis);
        upcominginis.finishedRefreshInisList(newInis);
        recentinis.finishedRefreshInisList(newInis);

    }

    static class MyAdapter extends FragmentPagerAdapter {

        AreasTabActivity ata;
        InitiativesTabActivity inis;
        UpcomingTabActivity upcominginis;
        RecentTabActivity recentinis;

        public MyAdapter(FragmentManager fm, AreasTabActivity ata, InitiativesTabActivity inis, UpcomingTabActivity upcominginis, RecentTabActivity recentinis) {
            super(fm);
            this.ata = ata;
            this.inis = inis;
            this.upcominginis = upcominginis;
            this.recentinis = recentinis;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return upcominginis;
                case 1:
                    return recentinis;
                case 2:
                    return inis;
                case 3:
                    return ata;
            }
            return null;
        }
    }
}
