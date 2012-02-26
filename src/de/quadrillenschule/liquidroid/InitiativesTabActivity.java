/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.app.Activity;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import de.quadrillenschule.liquidroid.model.AllInitiativenListAdapter;
import de.quadrillenschule.liquidroid.model.Area;
import de.quadrillenschule.liquidroid.model.Initiative;
import de.quadrillenschule.liquidroid.model.Initiativen;

/**
 *
 * @author andi
 */
public class InitiativesTabActivity extends Activity implements LQFBInstanceChangeListener {

    AllInitiativenListAdapter inisListAdapter;
    Initiativen allInis;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        allInis = new Initiativen();

        setContentView(R.layout.initiativentab);

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.allinisgestures);
        gestures.setGestureVisible(false);
        gestures.addOnGesturePerformedListener((LiqoidMainActivity) getParent());

        ((LiqoidApplication)getApplication()).addLQFBInstancesChangeListener(this);
        lqfbInstanceChanged();

    }

    public void refreshInisList(boolean force) {

        if (force || inisListAdapter == null) {

            allInis.clear();
            Context context = getApplicationContext();
            for (Area a : ((LiqoidApplication)getApplication()).lqfbInstances.getSelectedInstance().areas) {
                if (a.isSelected()) {

                    if (force) {
                        if (((LiqoidApplication)getApplication()).lqfbInstances.getSelectedInstance().downloadInitiativen(a) >= 0) {
                            Toast toast = Toast.makeText(context, R.string.download_ok, Toast.LENGTH_SHORT);
                            toast.show();

                        } else {
                            Toast toast = Toast.makeText(context, R.string.download_error, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                    for (Initiative i : a.getInitiativen()) {
                        allInis.add(i);
                    }
                }
            }
        }

        allInis.sortById();
        inisListAdapter = new AllInitiativenListAdapter(this, allInis, R.id.initiativenList, this);
        final ListView listview = (ListView) findViewById(R.id.initiativenList);
        listview.setAdapter(inisListAdapter);

    }

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
        inisListAdapter = null;
        refreshInisList(false);
    }
}
