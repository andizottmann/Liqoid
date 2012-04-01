/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import de.quadrillenschule.liquidroid.gui.InitiativenListAdapter;
import de.quadrillenschule.liquidroid.gui.IssueItemView;
import de.quadrillenschule.liquidroid.model.Area;
import de.quadrillenschule.liquidroid.model.Initiative;
import de.quadrillenschule.liquidroid.model.LQFBInstance;
import de.quadrillenschule.liquidroid.model.MultiInstanceInitiativen;
import de.quadrillenschule.liquidroid.model.RefreshInisListThread;

/**
 *
 * @author andi
 */
public class InitiativesTabActivity extends Fragment implements RefreshInisListThread.RefreshInisListListener {

    public InitiativenListAdapter inisListAdapter;
    public MultiInstanceInitiativen allInis;
    ProgressDialog progressDialog;
    protected boolean sortNewestFirst = true;
    protected boolean filterOnlySelected = false;
    public RefreshInisListThread ralt;
    public Activity mainActivity;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.initiativentab, container, false);

        return v;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

    }

    @Override
    public void onStart() {
        super.onStart();
        allInis = new MultiInstanceInitiativen();
    }

    @Override
    public void onResume() {
        super.onResume();
        final ListView listview = getListView();
        if (inisListAdapter == null) {
            inisListAdapter = getInitiativenListAdapter();

        }
        listview.setAdapter(inisListAdapter);
    }

    /*   @Override
    public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (((LiqoidApplication) getActivity().getApplication()).dataIntegrityCheck() && (inisListAdapter == null)) {
    createInisListAdapter();

    }
    }*/
    void createInisListAdapter() {
        allInis = new MultiInstanceInitiativen();
        for (LQFBInstance myInstance : ((LiqoidApplication) getActivity().getApplication()).lqfbInstances) {
            for (Area a : myInstance.areas.getSelectedAreas()) {
                for (Initiative i : a.getInitiativen()) {
                    allInis.add(i);
                }
            }
        }
        inisListAdapter = getInitiativenListAdapter();
        filterList();
        sortList();
        final ListView listView = getListView();
        listView.setAdapter(inisListAdapter);
        inisListAdapter.notifyDataSetChanged();
    }

    //  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getActivity().getMenuInflater();
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
    }

    public InitiativenListAdapter getInitiativenListAdapter() {
        if (mainActivity == null) {
            mainActivity = getActivity();
        }
        return new InitiativenListAdapter(mainActivity, allInis);
    }
    private View contextMenuView;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        contextMenuView = v;
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.initem_contextmenu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Initiative ini = ((IssueItemView) contextMenuView.getParent().getParent().getParent()).initiative;

        switch (item.getItemId()) {

            case R.id.open_browser_ini:
                try {
                    String issueid = ini.issue_id + "";
                    String url = ini.getLqfbInstance().getWebUrl() + "issue/show/" + issueid + ".html";
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    startActivity(myIntent);
                } catch (Exception e) {
                    return false;
                }
                return true;
            case R.id.share_ini:
                try {
                    String issueid = ini.issue_id + "";
                    String url = ini.getLqfbInstance().getWebUrl() + "issue/show/" + issueid + ".html";
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
                    //  Initiative ini = ((IssueItemView) contextMenuView.getParent().getParent()).initiative;
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
                    //    Initiative ini = ((IssueItemView) contextMenuView.getParent().getParent()).initiative;
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

    public void finishedRefreshInisList(MultiInstanceInitiativen newInis) {

        allInis = newInis;
        inisListAdapter = getInitiativenListAdapter();
        filterList();
        sortList();
        inisListAdapter.notifyDataSetChanged();
    }

    public ListView getListView() {

        return (ListView) v.findViewById(R.id.initiativenList);
    }

    public void onFinishOk() {
        if (inisListAdapter == null) {
            inisListAdapter = getInitiativenListAdapter();
        }
        /*   filterList();
        sortList();*/
        try {
            final ListView listview = getListView();

            listview.setAdapter(inisListAdapter);
        } catch (NullPointerException npe) {
        }
    }

    public String getDateFormat() {
        return getString(R.string.dateformat);
    }
}
