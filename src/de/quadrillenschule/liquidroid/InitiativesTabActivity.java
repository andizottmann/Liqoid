/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.gesture.GestureOverlayView;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import de.quadrillenschule.liquidroid.gui.InitiativenListAdapter;
import de.quadrillenschule.liquidroid.gui.IssueItemView;
import de.quadrillenschule.liquidroid.model.Area;
import de.quadrillenschule.liquidroid.model.Initiative;
import de.quadrillenschule.liquidroid.model.Initiativen;
import de.quadrillenschule.liquidroid.model.LQFBInstance;
import de.quadrillenschule.liquidroid.model.MultiInstanceInitiativen;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author andi
 */
public class InitiativesTabActivity extends Activity {

    InitiativenListAdapter inisListAdapter;
    MultiInstanceInitiativen allInis;
    ProgressDialog progressDialog;
    private boolean pauseDownload = false;
    long overallDataAge = 0;
    protected boolean sortNewestFirst = true;
    private String currentlyDownloadedArea = "", currentlyDownloadedInstance = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        allInis = new MultiInstanceInitiativen();//new Initiativen(getSharedPreferences(((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance().getPrefsName(), MODE_PRIVATE));
        setContentView(R.layout.initiativentab);
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.allinisgestures);
        gestures.setGestureVisible(false);
        gestures.addOnGesturePerformedListener((LiqoidMainActivity) getParent());

    }

    @Override
    public void onResume() {
        super.onResume();
        if (inisListAdapter==null){
        refreshInisList(false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //      refreshInisList(false);
    }

    public void refreshInisList(boolean download) {
        RefreshInisListThread ralt = new RefreshInisListThread(download, this);
        ralt.start();
    }

    private class RefreshInisListThread extends Thread {

        boolean download;
        InitiativesTabActivity parent;

        public RefreshInisListThread(boolean download, InitiativesTabActivity parent) {
            this.download = download;
            this.parent = parent;
            if (download) {
                pauseDownload = false;
            }
        }

        void updateAreas() {
            for (LQFBInstance myInstance : ((LiqoidApplication) getApplication()).lqfbInstances) {
                //    LQFBInstance myinstance = ((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance();
                boolean doesDownload = false;
                if (myInstance.willDownloadAreas(((LiqoidApplication) getApplication()).cachedAPI1Queries, download)) {
                    currentlyDownloadedInstance = myInstance.getShortName();
                    handler.sendEmptyMessage(DOWNLOADING_INSTANCE);
                    doesDownload = true;
                }
                int retrycounter = 0;
                int maxretries = 1;

                while ((retrycounter <= maxretries) && (myInstance.downloadAreas(((LiqoidApplication) getApplication()).cachedAPI1Queries, download)) < 0) {

                    if (doesDownload) {
                        handler.sendEmptyMessage(DOWNLOAD_ERROR);
                    }
                    try {
                        this.sleep((2 ^ retrycounter) * 1000);
                        retrycounter++;
                    } catch (InterruptedException ex) {
                    }
                    if (doesDownload) {
                        handler.sendEmptyMessage(DOWNLOAD_RETRY);
                    }
                }
                //  handler.sendEmptyMessage(FINISH_OK);
            }
        }

        @Override
        public void run() {
            handler.sendEmptyMessage(UPDATING);

            updateAreas();
            overallDataAge = System.currentTimeMillis();
            //   inisListAdapter = null;
            allInis = new Initiativen(getSharedPreferences(((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance().getPrefsName(), RESULT_OK));

            for (LQFBInstance myInstance : ((LiqoidApplication) getApplication()).lqfbInstances) {
                currentlyDownloadedInstance = myInstance.getShortName();

                for (Area a : myInstance.areas.getSelectedAreas()) {
                    currentlyDownloadedArea = a.getName();
                    boolean doesDownload = false;
                    if (myInstance.willDownloadInitiativen(a, ((LiqoidApplication) getApplication()).cachedAPI1Queries, download)) {
                        handler.sendEmptyMessage(DOWNLOADING);
                        doesDownload = true;
                    }
                    if (doesDownload) {
                        handler.sendEmptyMessage(DOWNLOAD_RETRY);
                    }
                    int retrycounter = 0;
                    int maxretries = 4;
                    if (pauseDownload) {
                        maxretries = 0;
                    }

                    while ((retrycounter <= maxretries) && (myInstance.downloadInitiativen(a, ((LiqoidApplication) getApplication()).cachedAPI1Queries, download) < 0)) {
                        if (doesDownload) {
                            handler.sendEmptyMessage(DOWNLOAD_ERROR);
                        }
                        try {
                            this.sleep((2 ^ retrycounter) * 1000);
                            retrycounter++;

                        } catch (InterruptedException ex) {
                        }
                        if (doesDownload) {
                            handler.sendEmptyMessage(DOWNLOAD_RETRY);
                        }

                    }
                    if (retrycounter >= maxretries) {
                        pauseDownload = true;
                    }
                    if (overallDataAge > ((LiqoidApplication) getApplication()).cachedAPI1Queries.dataage) {
                        overallDataAge = ((LiqoidApplication) getApplication()).cachedAPI1Queries.dataage;
                    }

                }
                for (Area a : myInstance.areas.getSelectedAreas()) {


                    for (Initiative i : a.getInitiativen()) {
                        allInis.add(i);
                    }
                }
                //     handler.sendEmptyMessage(FINISH_OK);

            }

            inisListAdapter = getInitiativenListAdapter();//new InitiativenListAdapter(parent, allInis, R.id.initiativenList);
            sortList();
            inisListAdapter.notifyDataSetChanged();
            handler.sendEmptyMessage(FINISH_OK);


        }
    }
    private static int FINISH_OK = 0, DOWNLOADING = 1, DOWNLOAD_ERROR = -1, DOWNLOAD_RETRY = 2, UPDATING = 4, DOWNLOADING_INSTANCE = 5;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            //Setting the status line text
            long dataage = overallDataAge;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dataagestr = formatter.format(new Date(dataage));
            String prefix = "";
            if (pauseDownload) {
                prefix = "Offline - ";
            }
            ((LiqoidApplication) getApplication()).statusLineText(prefix + getApplicationContext().getString(R.string.dataage) + ": " + dataagestr);

            //Updating status of progressdialog
            if ((msg.what == DOWNLOADING)) {
                progressDialog.setMessage(getApplicationContext().getString(R.string.downloading) + "\n" + currentlyDownloadedArea + "...");
            }
            if ((msg.what == DOWNLOADING_INSTANCE)) {
                progressDialog.setMessage(getApplicationContext().getString(R.string.downloading) + "\n" + currentlyDownloadedInstance + "...");
            }

            if (msg.what == UPDATING) {

                progressDialog = ProgressDialog.show(InitiativesTabActivity.this, "",
                        getApplicationContext().getString(R.string.updating) + "...", true);
            }
            if (msg.what == FINISH_OK) {

                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    //Sometimes it is not attached anymore
                    progressDialog = null;
                }
                final ListView listview = (ListView) findViewById(R.id.initiativenList);

                listview.setAdapter(inisListAdapter);

                findViewById(R.id.initiativenList).refreshDrawableState();

            }

            if ((progressDialog != null) && (!pauseDownload)) {

                if (msg.what == DOWNLOAD_ERROR) {
                    progressDialog.setMessage(getApplicationContext().getString(R.string.download_error));

                }
                if (msg.what == DOWNLOAD_RETRY) {
                    progressDialog.setMessage(getApplicationContext().getString(R.string.downloading) + "\n" + currentlyDownloadedArea + " @ " + currentlyDownloadedInstance);

                }

            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inislist_options, menu);

        return true;
    }

    protected void sortList() {
        if (sortNewestFirst) {
            allInis.reverse(Initiative.ISSUE_CREATED_COMP);
        } else {
            allInis.sort(Initiative.ISSUE_CREATED_COMP);
        }
        try {
            inisListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh_inislist:
                refreshInisList(true);
                return true;
            case R.id.sort_inislist:
                sortNewestFirst = !sortNewestFirst;
                sortList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected InitiativenListAdapter getInitiativenListAdapter() {
        return new InitiativenListAdapter(this, allInis, R.id.initiativenList);
    }
    private View contextMenuView;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        contextMenuView = v;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.initem_contextmenu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_browser_ini:
                try {
                    String issueid = ((IssueItemView) contextMenuView.getParent()).initiative.issue_id + "";
                    String url = ((IssueItemView) contextMenuView.getParent()).initiative.getLqfbInstance().getWebUrl() + "issue/show/" + issueid + ".html";
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                    startActivity(myIntent);
                } catch (Exception e) {
                    return false;
                }
                return true;
            case R.id.share_ini:
                try {
                    String issueid = ((IssueItemView) contextMenuView.getParent()).initiative.issue_id + "";
                    String url = ((IssueItemView) contextMenuView.getParent()).initiative.getLqfbInstance().getWebUrl() + "issue/show/" + issueid + ".html";
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    intent.putExtra(Intent.EXTRA_TEXT, url);
                    startActivity(Intent.createChooser(intent, getString(R.string.share)));
                } catch (Exception e) {
                    return false;
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
