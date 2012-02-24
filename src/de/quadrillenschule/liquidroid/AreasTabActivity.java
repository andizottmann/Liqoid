/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;
import de.quadrillenschule.liquidroid.model.AreasListAdapter;

/**
 *
 * @author andi
 */
public class AreasTabActivity extends Activity implements LQFBInstanceChangeListener{

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
       setContentView(R.layout.areastab);

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.setGestureVisible(false);
        gestures.addOnGesturePerformedListener((LiqoidMainActivity) getParent());

        ((LiqoidMainActivity) getParent()).addLQFBInstancesChangeListener(this);
        

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
                    int areaid = ((LiqoidApplication)getApplication()).lqfbInstances.getSelectedInstance().areas.getByName(areaname).getId();
                    String url = ((LiqoidApplication)getApplication()).lqfbInstances.getSelectedInstance().getWebUrl() + "area/show/" + areaid + ".html";
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
            if (force || (((LiqoidApplication)getApplication()).lqfbInstances.getSelectedInstance().areas.size() == 0)) {

                Context context = getApplicationContext();
                /*
                ProgressDialog dialog = ProgressDialog.show(context, "",
                getApplicationContext().getString(R.string.downloading));
                 */

                if (((LiqoidApplication)getApplication()).lqfbInstances.getSelectedInstance().downloadAreas() >= 0) {
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
        areasListAdapter = new AreasListAdapter(this, ((LiqoidApplication)getApplication()).lqfbInstances.getSelectedInstance().areas, R.id.areasList, this);

        final ListView listview = (ListView) findViewById(R.id.areasList);
        listview.setAdapter(areasListAdapter);

    }

    public void lqfbInstanceChanged() {
         areasListAdapter = null;
        refreshAreasList(false);
    }
}
