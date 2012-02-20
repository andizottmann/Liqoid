/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import de.quadrillenschule.liquidroid.model.AreasListAdapter;
import de.quadrillenschule.liquidroid.model.LQFBInstance;
import de.quadrillenschule.liquidroid.model.LQFBInstancesListAdapter;

/**
 *
 * @author andi
 */
public class AreasTabActivity extends Activity implements OnItemSelectedListener {

    ArrayAdapter adapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.areastab);
        final Spinner instanceSpinner = (Spinner) findViewById(R.id.instanceSelector);

       adapter= new LQFBInstancesListAdapter(this, LiqoidMainActivity.lqfbInstances,android.R.layout.simple_spinner_item, this);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        instanceSpinner.setAdapter(adapter);
        instanceSpinner.setOnItemSelectedListener(this);

    }

   

    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        LiqoidMainActivity.lqfbInstances.setSelectedInstance((int) arg3);
        areasListAdapter = null;
        refreshAreasList(false);
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        //do nothing
    }
    private View contextMenuView;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
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
                    int areaid = LiqoidMainActivity.lqfbInstances.getSelectedInstance().areas.getByName(areaname).getId();
                    String url = LiqoidMainActivity.lqfbInstances.getSelectedInstance().getWebUrl() + "area/show/" + areaid + ".html";
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
    AreasListAdapter areasListAdapter;

    public void refreshAreasList(boolean force) {

        if (force || areasListAdapter == null) {
            if (force || (LiqoidMainActivity.lqfbInstances.getSelectedInstance().areas.size() == 0)) {

                Context context = getApplicationContext();
                /*
                ProgressDialog dialog = ProgressDialog.show(context, "",
                getApplicationContext().getString(R.string.downloading));
                 */

                if (LiqoidMainActivity.lqfbInstances.getSelectedInstance().downloadAreas() >= 0) {
                    //                  dialog.cancel();

                    Toast toast = Toast.makeText(context, R.string.download_ok, Toast.LENGTH_SHORT);
                    toast.show();

                } else {
                    //                dialog.cancel();
                    Toast toast = Toast.makeText(context, R.string.download_error, Toast.LENGTH_LONG);
                    toast.show();
                }

            }

        }
        areasListAdapter = new AreasListAdapter(this, LiqoidMainActivity.lqfbInstances.getSelectedInstance().areas, R.id.areasList, this);

        final ListView listview = (ListView) findViewById(R.id.areasList);
        listview.setAdapter(areasListAdapter);

    }
}
