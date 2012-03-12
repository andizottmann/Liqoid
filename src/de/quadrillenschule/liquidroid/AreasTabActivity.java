/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import de.quadrillenschule.liquidroid.gui.AreasListAdapter;
import de.quadrillenschule.liquidroid.gui.LQFBInstancesListAdapter;
import de.quadrillenschule.liquidroid.model.LQFBInstance;
import de.quadrillenschule.liquidroid.model.LQFBInstances;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author andi
 */
public class AreasTabActivity extends Activity implements LQFBInstanceChangeListener, AdapterView.OnItemSelectedListener {

    private AreasListAdapter areasListAdapter;
    private ProgressDialog progressDialog;
    private View contextMenuView;
    private LQFBInstance currentDownloadInstance = null;
    //  private boolean pauseDownload = false;
    ArrayAdapter adapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.areastab);

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.setGestureVisible(false);
        gestures.addOnGesturePerformedListener((LiqoidMainActivity) getParent());

        ((LiqoidApplication) getApplication()).addLQFBInstancesChangeListener(this);

        final Spinner instanceSpinner = (Spinner) findViewById(R.id.instanceSelector);
        adapter = new LQFBInstancesListAdapter(this, ((LiqoidApplication) getApplication()).lqfbInstances, android.R.layout.simple_spinner_item, this);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instanceSpinner.setAdapter(adapter);
        instanceSpinner.setOnItemSelectedListener(this);
        int i = ((LiqoidApplication) getApplication()).lqfbInstances.indexOf(((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance());
        instanceSpinner.setSelection(i);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (areasListAdapter == null) {
            refreshAreasList(false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        refreshAreasList(false);

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        //do nothing
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        contextMenuView = v;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.areaslist_contextmenu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_browser:
                try {
                    String areaname = ((CheckBox) contextMenuView).getText().toString();
                    int areaid = ((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance().areas.getByName(areaname).getId();
                    String url = ((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance().getWebUrl() + "area/show/" + areaid + ".html";
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                    startActivity(myIntent);
                } catch (Exception e) {
                    return false;
                }
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.areaslist_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh_areaslist:
                refreshAreasList(true);
                return true;
            case R.id.about:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.fullcredits)).setCancelable(false).setNegativeButton(":)", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refreshAreasList(boolean force) {
        if (force) {
            for (LQFBInstance li : ((LiqoidApplication) getApplication()).lqfbInstances) {
                li.pauseDownload = false;
            }
        }
        RefreshAreasListThread ralt = new RefreshAreasListThread(force, this);
        ralt.start();
    }

    private class RefreshAreasListThread extends Thread {

        boolean download;
        AreasTabActivity parent;

        public RefreshAreasListThread(boolean download, AreasTabActivity parent) {
            this.download = download;
            this.parent = parent;

        }

        @Override
        public void run() {
            handler.sendEmptyMessage(START_DOWNLOAD);

            for (LQFBInstance myinstance : ((LiqoidApplication) getApplication()).lqfbInstances) {
                currentDownloadInstance = myinstance;
                if (myinstance.willDownloadAreas(((LiqoidApplication) getApplication()).cachedAPI1Queries, download)) {
                    handler.sendEmptyMessage(DOWNLOADING);
                }
                int retrycounter = 0;
                int maxretries = 4;
                boolean instancedownload = download;
                if (myinstance.pauseDownload) {
                    maxretries = 0;
                    instancedownload = false;
                }
                while ((retrycounter <= maxretries) && (myinstance.downloadAreas(((LiqoidApplication) getApplication()).cachedAPI1Queries, instancedownload, myinstance.pauseDownload)) < 0) {

                    handler.sendEmptyMessage(DOWNLOAD_ERROR);
                    try {
                        this.sleep((2 ^ retrycounter) * 1000);
                        retrycounter++;
                    } catch (InterruptedException ex) {
                    }
                    handler.sendEmptyMessage(DOWNLOAD_RETRY);
                }

                if (retrycounter >= maxretries) {
                    myinstance.pauseDownload = true;
                }
            }
            areasListAdapter = new AreasListAdapter(parent, ((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance().areas, R.id.areasList);

            findViewById(R.id.areasList).refreshDrawableState();
            handler.sendEmptyMessage(0);
        }
    }
    private static int FINISH_OK = 0, DOWNLOADING = 1, DOWNLOAD_ERROR = -1, DOWNLOAD_RETRY = 2, START_DOWNLOAD = 3;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            //Update status line
            long dataage = ((LiqoidApplication) getApplication()).cachedAPI1Queries.dataage;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dataagestr = formatter.format(new Date(dataage));
            String prefix = "";
            if (currentDownloadInstance != null) {
                if (currentDownloadInstance.pauseDownload) {
                    prefix = "Offline - ";
                }
            }

            ((LiqoidApplication) getApplication()).statusLineText(prefix + getApplicationContext().getString(R.string.dataage) + ": " + dataagestr);
            if ((msg.what == START_DOWNLOAD)) {
                progressDialog = ProgressDialog.show(AreasTabActivity.this, "",
                        getApplicationContext().getString(R.string.updating) + "...", true);

            }
            if (currentDownloadInstance != null) {
                //update progressdialog
                if ((msg.what == DOWNLOADING) && (!currentDownloadInstance.pauseDownload)) {
                    progressDialog.setMessage(
                            getApplicationContext().getString(R.string.downloading) + "\n" + ((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance().getName() + "...");

                }
            }
            if (msg.what == FINISH_OK) {
                try {
                    progressDialog.cancel();
                } catch (Exception e) {
                    //Sometimes it is not attached anymore
                    progressDialog = null;
                }
                final ListView listview = (ListView) findViewById(R.id.areasList);
                listview.setAdapter(areasListAdapter);
            }
            if (progressDialog != null) {
                if (msg.what == DOWNLOAD_ERROR) {
                    progressDialog.setMessage(getApplicationContext().getString(R.string.download_error));

                }
                if (msg.what == DOWNLOAD_RETRY) {
                    progressDialog.setMessage(getApplicationContext().getString(R.string.downloading));

                }
            }

        }
    };

    public void lqfbInstanceChanged() {
        refreshAreasList(false);
    }

    //Instances Spinner item selected
    public void onItemSelected(AdapterView<?> arg0, View arg1, int i, long arg3) {
        LQFBInstances ls = ((LiqoidApplication) getApplication()).lqfbInstances;
        if (ls.indexOf(ls.getSelectedInstance()) != i) {
            ls.setSelectedInstance(i);
            ((LiqoidApplication) getApplication()).fireLQFBInstanceChangedEvent();
        }
    }
}
