/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import de.quadrillenschule.liquidroid.gui.AreasListAdapter;
import de.quadrillenschule.liquidroid.gui.LQFBInstancesListAdapter;
import de.quadrillenschule.liquidroid.model.LQFBInstance;
import de.quadrillenschule.liquidroid.model.LQFBInstances;
import de.quadrillenschule.liquidroid.model.MultiInstanceInitiativen;
import de.quadrillenschule.liquidroid.model.RefreshInisListThread;

/**
 *
 * @author andi
 */
public class AreasTabActivity extends Fragment implements LQFBInstanceChangeListener, AdapterView.OnItemSelectedListener, RefreshInisListThread.RefreshInisListListener {

    private AreasListAdapter areasListAdapter;
    private ProgressDialog progressDialog;
    private View contextMenuView;
    private LQFBInstance currentDownloadInstance = null;
    //  private boolean pauseDownload = false;
    ArrayAdapter adapter;
    Activity mainActivity;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.areastab, container, false);

        final Spinner instanceSpinner = (Spinner) v.findViewById(R.id.instanceSelector);
        adapter = new LQFBInstancesListAdapter(v.getContext(), ((LiqoidApplication) this.getActivity().getApplication()).lqfbInstances, android.R.layout.simple_spinner_item, getActivity());
        //  LQFBInstancesListAdapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ((LiqoidApplication) getActivity().getApplication()).addLQFBInstancesChangeListener(this);

        ((Button) v.findViewById(R.id.addinstance)).setText(Html.fromHtml("<u>" + getActivity().getApplication().getString(R.string.addinstance) + "...</u>"));
        ((Button) v.findViewById(R.id.addinstance)).setBackgroundColor(Color.argb(255, 245, 245, 245));
        ((Button) v.findViewById(R.id.addinstance)).setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getActivity().getApplication().getString(R.string.addinstanceurl)));
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                startActivity(myIntent);
            }
        });
        instanceSpinner.setAdapter(adapter);
        instanceSpinner.setOnItemSelectedListener(this);
        int i = ((LiqoidApplication) getActivity().getApplication()).lqfbInstances.indexOf(((LiqoidApplication) getActivity().getApplication()).lqfbInstances.getSelectedInstance());
        instanceSpinner.setSelection(i);
        return v;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if ((((LiqoidApplication) getActivity().getApplication()).dataIntegrityCheck()) && (areasListAdapter == null)) {
            onlyUpdateAreasListFromMemory();
        }

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        //do nothing
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        contextMenuView = v;
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.areaslist_contextmenu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_browser:

                String areaname = ((CheckBox) contextMenuView).getText().toString();
                int areaid = ((LiqoidApplication) getActivity().getApplication()).lqfbInstances.getSelectedInstance().areas.getByName(areaname).getId();
                String url = ((LiqoidApplication) getActivity().getApplication()).lqfbInstances.getSelectedInstance().getWebUrl() + "area/show/" + areaid + ".html";
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                startActivity(myIntent);

                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void finishedRefreshInisList(MultiInstanceInitiativen newInis) {
        areasListAdapter = new AreasListAdapter(mainActivity, ((LiqoidApplication) mainActivity.getApplication()).lqfbInstances.getSelectedInstance().areas, R.id.areasList);
    }

    public void onFinishOk() {
        final ListView listview = (ListView) v.findViewById(R.id.areasList);
        listview.setAdapter(areasListAdapter);
    }

    public void onlyUpdateAreasListFromMemory() {
        //  refreshAreasList(false);
        areasListAdapter = new AreasListAdapter(mainActivity, ((LiqoidApplication) mainActivity.getApplication()).lqfbInstances.getSelectedInstance().areas, R.id.areasList);
        final ListView listview = (ListView) v.findViewById(R.id.areasList);
        listview.setAdapter(areasListAdapter);

    }

    //Instances Spinner item selected
    public void onItemSelected(AdapterView<?> arg0, View arg1, int i, long arg3) {
        if (mainActivity == null) {
            mainActivity = getActivity();
        }
        LQFBInstances ls = ((LiqoidApplication) mainActivity.getApplication()).lqfbInstances;
        ls.setSelectedInstance(((LiqoidApplication) mainActivity.getApplication()).lqfbInstances.get(i).getShortName());
        ((LiqoidApplication) mainActivity.getApplication()).fireLQFBInstanceChangedEvent();
    }
}
