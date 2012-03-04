/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.ListView;
import de.quadrillenschule.liquidroid.model.AreasListAdapter;
import de.quadrillenschule.liquidroid.model.LQFBInstance;

/**
 *
 * @author andi
 */
public class AreasTabActivity extends Activity implements LQFBInstanceChangeListener {

    private AreasListAdapter areasListAdapter;
    private ProgressDialog progressDialog;
    private View contextMenuView;
    private boolean pauseDownload = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.areastab);

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.setGestureVisible(false);
        gestures.addOnGesturePerformedListener((LiqoidMainActivity) getParent());

        ((LiqoidApplication) getApplication()).addLQFBInstancesChangeListener(this);


    }

    @Override
    public void onResume() {
        super.onResume();
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
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refreshAreasList(boolean force) {
      if (force) {
            pauseDownload = false;
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
            LQFBInstance myinstance = ((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance();
            if (myinstance.willDownloadAreas(((LiqoidApplication) getApplication()).cachedAPI1Queries, download)) {
                handler.sendEmptyMessage(1);
            }
            int retrycounter = 0;
            int maxretries = 4;
            if (pauseDownload) {
                maxretries = 0;
            }
            while ((retrycounter <= maxretries) && (myinstance.downloadAreas(((LiqoidApplication) getApplication()).cachedAPI1Queries, download)) < 0) {

                handler.sendEmptyMessage(-1);
                try {
                    this.sleep((2 ^ retrycounter) * 1000);
                    retrycounter++;
                } catch (InterruptedException ex) {
                }
                handler.sendEmptyMessage(-2);
            }

            if (retrycounter >= maxretries) {
                pauseDownload = true;
            }
            areasListAdapter = new AreasListAdapter(parent, myinstance.areas, R.id.areasList);
            handler.sendEmptyMessage(0);
        }
    }
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if ((msg.what == 1)&&(!pauseDownload)) {
                progressDialog = ProgressDialog.show(AreasTabActivity.this, "",
                        getApplicationContext().getString(R.string.downloading) + "\n" + ((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance().getName() + "...", true);

            }
            if (msg.what == 0) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    //Sometimes it is not attached anymore
                    progressDialog = null;
                }
                final ListView listview = (ListView) findViewById(R.id.areasList);
                listview.setAdapter(areasListAdapter);

            }
            if (progressDialog != null) {
                if (msg.what == -1) {
                    progressDialog.setMessage(getApplicationContext().getString(R.string.download_error));

                }
                if (msg.what == -2) {
                    progressDialog.setMessage(getApplicationContext().getString(R.string.downloading));

                }
            }

        }
    };

    public void lqfbInstanceChanged() {
        refreshAreasList(false);
    }
}
