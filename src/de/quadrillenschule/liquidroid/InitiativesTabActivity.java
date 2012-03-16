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
import android.graphics.Color;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import de.quadrillenschule.liquidroid.gui.InitiativenListAdapter;
import de.quadrillenschule.liquidroid.gui.IssueItemView;
import de.quadrillenschule.liquidroid.model.Area;
import de.quadrillenschule.liquidroid.model.Initiative;
import de.quadrillenschule.liquidroid.model.LQFBInstance;
import de.quadrillenschule.liquidroid.model.LQFBInstances;
import de.quadrillenschule.liquidroid.model.MultiInstanceInitiativen;
import de.quadrillenschule.liquidroid.model.RefreshInisListThread;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author andi
 */
public class InitiativesTabActivity extends Activity implements RefreshInisListThread.RefreshInisListListener {

    public InitiativenListAdapter inisListAdapter;
    public MultiInstanceInitiativen allInis;
    ProgressDialog progressDialog;
    //   private boolean pauseDownload = false;
    protected boolean sortNewestFirst = true;
    protected boolean filterOnlySelected = false;
    public RefreshInisListThread ralt;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        allInis = new MultiInstanceInitiativen();
        setContentView(R.layout.initiativentab);
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.allinisgestures);
        gestures.setGestureVisible(false);
        gestures.addOnGesturePerformedListener((LiqoidMainActivity) getParent());
        if ((inisListAdapter == null) && dataIntegrityCheck()) {
            createInisListAdapter();
        } else {
            LQFBInstances.selectionUpdatesForRefresh = true;
        }
        ;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataIntegrityCheck() && (inisListAdapter == null)) {
            createInisListAdapter();

        }
        if (LQFBInstances.selectionUpdatesForRefresh) {
            refreshInisList(false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (dataIntegrityCheck() && (inisListAdapter == null)) {
            createInisListAdapter();

        }/*
        if (LQFBInstances.selectionUpdatesForRefresh) {
        refreshInisList(false);
        }*/
    }

    void createInisListAdapter() {
        allInis = new MultiInstanceInitiativen();
        for (LQFBInstance myInstance : ((LiqoidApplication) getApplication()).lqfbInstances) {
            for (Area a : myInstance.areas.getSelectedAreas()) {
                for (Initiative i : a.getInitiativen()) {
                    allInis.add(i);
                }
            }
        }
        inisListAdapter = getInitiativenListAdapter();
        filterList();
        sortList();
        final ListView listview = (ListView) findViewById(R.id.initiativenList);
        listview.setAdapter(inisListAdapter);
        inisListAdapter.notifyDataSetChanged();


    }

    boolean dataIntegrityCheck() {
        try {
            for (LQFBInstance myInstance : ((LiqoidApplication) getApplication()).lqfbInstances) {
                for (Area a : myInstance.areas) {
                    if (a.getInitiativen().size() > 0) {
                        return true;
                    }
                }
            }
        } catch (NullPointerException npe) {
            return false;
        }
        return false;
    }

    public void refreshInisList(boolean download) {
        LQFBInstances.selectionUpdatesForRefresh = false;
        ralt = new RefreshInisListThread(download, this, handler, (LiqoidApplication) this.getApplication());
        ralt.start();
    }
    public Handler handler = new Handler() {

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
            ((LiqoidApplication) getApplication()).statusLineText(prefix + getApplicationContext().getString(R.string.dataage) + ": " + dataagestr);

            //Updating status of progressdialog
            if ((msg.what == RefreshInisListThread.DOWNLOADING)) {
                progressDialog.setMessage(getApplicationContext().getString(R.string.downloading) + "\n" + ralt.currentlyDownloadedArea + "...");
            }
            if ((msg.what == RefreshInisListThread.DOWNLOADING_INSTANCE)) {
                progressDialog.setMessage(getApplicationContext().getString(R.string.downloading) + "\n" + ralt.currentlyDownloadedInstance + "...");
            }
            if (msg.what == RefreshInisListThread.UPDATING) {

                progressDialog = ProgressDialog.show(InitiativesTabActivity.this, "",
                        getApplicationContext().getString(R.string.updating) + "...", true);
            }
            if (msg.what == RefreshInisListThread.FINISH_OK) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    //Sometimes it is not attached anymore
                    progressDialog = null;
                }
                final ListView listview = (ListView) findViewById(R.id.initiativenList);
                listview.setAdapter(inisListAdapter);
                findViewById(R.id.initiativenList).refreshDrawableState();
                if (allInis.size() == 0) {
                    ((LiqoidApplication) getApplication()).toast(getApplicationContext(), getString(R.string.noareasselected));
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inislist_options, menu);

        return true;
    }

    public void filterList() {
        if (filterOnlySelected) {
            allInis.removeNonSelected();
        }
    }

    public void sortList() {

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
            case R.id.prefs:
                startActivity(new Intent(this, GlobalPrefsActivity.class));
                return true;
            case R.id.refresh_inislist:
                refreshInisList(true);
                return true;
            case R.id.sort_inislist:
                sortNewestFirst = !sortNewestFirst;
                sortList();
                return true;
            case R.id.toggleselectedfilter:

                filterOnlySelected = !filterOnlySelected;
                refreshInisList(false);

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

    public InitiativenListAdapter getInitiativenListAdapter() {
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
                    String issueid = ((IssueItemView) contextMenuView.getParent().getParent()).initiative.issue_id + "";
                    String url = ((IssueItemView) contextMenuView.getParent().getParent()).initiative.getLqfbInstance().getWebUrl() + "issue/show/" + issueid + ".html";
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                    startActivity(myIntent);
                } catch (Exception e) {
                    return false;
                }
                return true;
            case R.id.share_ini:
                try {
                    String issueid = ((IssueItemView) contextMenuView.getParent().getParent()).initiative.issue_id + "";
                    String url = ((IssueItemView) contextMenuView.getParent().getParent()).initiative.getLqfbInstance().getWebUrl() + "issue/show/" + issueid + ".html";
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    intent.putExtra(Intent.EXTRA_TEXT, url);
                    startActivity(Intent.createChooser(intent, getString(R.string.share)));
                } catch (Exception e) {
                    return false;
                }
                return true;
            case R.id.calendar:
                try {
                    Initiative ini = ((IssueItemView) contextMenuView.getParent().getParent()).initiative;
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra("beginTime", relevantCalendarTime(ini));
                    intent.putExtra("endTime", relevantCalendarTime(ini) + 1000 * 60 * 5);
                    intent.putExtra("title", "LQFB " + ini.getLqfbInstance().getShortName() + " " + ini.nextEvent() + " " + ini.name);
                    startActivity(intent);
                } catch (Exception e) {
                    return false;
                }
                return true;
            case R.id.calendar_voting:
                try {
                    Initiative ini = ((IssueItemView) contextMenuView.getParent().getParent()).initiative;
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra("beginTime", ini.getDateForStartVoting().getTime());
                    intent.putExtra("endTime", ini.getDateForStartVoting().getTime() + 1000 * 60 * 5);
                    intent.putExtra("title", "LQFB " + ini.getLqfbInstance().getShortName() + " " + getString(R.string.voting_begin) + " " + ini.name);
                    startActivity(intent);
                } catch (Exception e) {
                    return false;
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public long relevantCalendarTime(Initiative i) {
        return i.getDateForNextEvent().getTime();
    }
    public static final int GREY_COLOR = 0, ORANGE_COLOR = 1, RED_COLOR = 2, YELLOW_COLOR = 3;

    public ImageView getImageViewForcolor(int colorcode) {
        ImageView colorView = new ImageView(this);
        colorView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        colorView.setAdjustViewBounds(true);
        colorView.setBackgroundColor(Color.argb(255, 245, 245, 245));
        colorView.setImageResource(getImageResourceForColor(colorcode));

        colorView.setScaleType(ImageView.ScaleType.FIT_START);
        return colorView;
    }

    public int getImageResourceForColor(int colorcode) {

        switch (colorcode) {
            case ORANGE_COLOR:
                return R.drawable.seek_thumb_pressed;
            case RED_COLOR:
                return R.drawable.seek_thumb_selected;
            case YELLOW_COLOR:
                return R.drawable.seek_thumb_yellow;
            default:
                return R.drawable.seek_thumb_normal;
        }
    }

    public void finishedRefreshInisList(MultiInstanceInitiativen newInis) {
        allInis = newInis;
        inisListAdapter = getInitiativenListAdapter();
        filterList();
        sortList();
        inisListAdapter.notifyDataSetChanged();
    }

    public String getDateFormat() {
        return getString(R.string.dateformat);
    }
}
