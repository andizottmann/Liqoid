/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import de.quadrillenschule.liquidroid.model.AllInitiativenListAdapter;
import de.quadrillenschule.liquidroid.model.Area;
import de.quadrillenschule.liquidroid.model.Initiative;
import de.quadrillenschule.liquidroid.model.Initiativen;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andi
 */
public class InitiativesTabActivity extends Activity implements LQFBInstanceChangeListener {

    AllInitiativenListAdapter inisListAdapter;
    Initiativen allInis;
    ProgressDialog progressDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        allInis = new Initiativen();

        setContentView(R.layout.initiativentab);

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.allinisgestures);
        gestures.setGestureVisible(false);
        gestures.addOnGesturePerformedListener((LiqoidMainActivity) getParent());

        ((LiqoidApplication) getApplication()).addLQFBInstancesChangeListener(this);
        //   lqfbInstanceChanged();
        refreshInisList(false);

    }

    public void refreshInisList(boolean force) {

        progressDialog = ProgressDialog.show(InitiativesTabActivity.this, "",
                getApplicationContext().getString(R.string.downloading) + "\n" + ((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance().getName() + "...", true);
        RefreshInisListThread ralt = new RefreshInisListThread(force, this);
        ralt.start();

    }
    public String currentlyDownloadedArea = "";

    private class RefreshInisListThread extends Thread {

        boolean force;
        InitiativesTabActivity parent;

        public RefreshInisListThread(boolean force, InitiativesTabActivity parent) {
            this.force = force;
            this.parent = parent;
        }

        @Override
        public void run() {

            if (force || ((LiqoidApplication) getApplication()).lqfbInstances.load() < 0) {
                for (Area a : ((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance().areas) {
                    if (a.isSelected()) {

                        if (force) {
                            currentlyDownloadedArea = a.getName();
                            handler.sendEmptyMessage(2);

                            if (((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance().downloadInitiativen(a) >= 0) {
                            } else {
                                handler.sendEmptyMessage(-1);
                                try {
                                    this.sleep(2000);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(AreasTabActivity.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
            }
            allInis.clear();
            for (Area a : ((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance().areas) {
                if (a.isSelected()) {

                    for (Initiative i : a.getInitiativen()) {
                        allInis.add(i);
                    }
                }
            }
            allInis.sortById();
            inisListAdapter = new AllInitiativenListAdapter(parent, allInis, R.id.initiativenList);
            handler.sendEmptyMessage(0);

        }
    }
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0) {

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                final ListView listview = (ListView) findViewById(R.id.initiativenList);
                listview.setAdapter(inisListAdapter);

            }
            if (msg.what == -1) {
                progressDialog.setMessage(getApplicationContext().getString(R.string.download_error));

            }
            if (msg.what == 2) {
                progressDialog.setMessage(getApplicationContext().getString(R.string.downloading) + "\n" + ((LiqoidApplication) getApplication()).lqfbInstances.getSelectedInstance().getName() + "\n" + currentlyDownloadedArea + "...");

            }


        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inislist_options, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh_inislist:
                refreshInisList(true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void lqfbInstanceChanged() {
        refreshInisList(false);
        final ListView listview = (ListView) findViewById(R.id.initiativenList);
        listview.setAdapter(inisListAdapter);

    }
}
