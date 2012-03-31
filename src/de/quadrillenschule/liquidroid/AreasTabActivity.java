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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.areastab, container, false);
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
        //   setContentView(R.layout.areastab);



        //   TextView tv=new TextView(instanceSpinner.getContext());

       

    }

    @Override
    public void onResume() {
        super.onResume();
        if ((((LiqoidApplication) getActivity().getApplication()).dataIntegrityCheck()) && (areasListAdapter == null)) {
            onlyUpdateAreasListFromMemory();
        }
        if (!(((LiqoidApplication) getActivity().getApplication()).dataIntegrityCheck()) && (areasListAdapter == null)) {
            refreshAreasList(false);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if ((((LiqoidApplication)  getActivity().getApplication()).dataIntegrityCheck()) && (areasListAdapter == null)) {
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
        MenuInflater inflater =  getActivity().getMenuInflater();
        inflater.inflate(R.menu.areaslist_contextmenu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_browser:

                String areaname = ((CheckBox) contextMenuView).getText().toString();
                int areaid = ((LiqoidApplication)  getActivity().getApplication()).lqfbInstances.getSelectedInstance().areas.getByName(areaname).getId();
                String url = ((LiqoidApplication)  getActivity().getApplication()).lqfbInstances.getSelectedInstance().getWebUrl() + "area/show/" + areaid + ".html";
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                startActivity(myIntent);

                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

  
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  getActivity().getMenuInflater();
        inflater.inflate(R.menu.areaslist_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.prefs:
                startActivity(new Intent( getActivity(), GlobalPrefsActivity.class));
                return true;
            case R.id.refresh_areaslist:
                refreshAreasList(true);
                return true;
            case R.id.unlock_instance:
                ((LiqoidApplication)  getActivity().getApplication()).unlockInstancesDialog( getActivity()).show();
                return true;
            case R.id.lock_instance:
                ((LiqoidApplication)  getActivity().getApplication()).lockInstancesDialog( getActivity()).show();
                return true;
            case R.id.about:
                ((LiqoidApplication)  getActivity().getApplication()).aboutDialog( getActivity()).show();
                return true;
            case R.id.clearcache:
                ((LiqoidApplication)  getActivity().getApplication()).clearCache( getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refreshAreasList(boolean force) {
        if (force) {
            for (LQFBInstance li : ((LiqoidApplication)  getActivity().getApplication()).lqfbInstances) {
                li.pauseDownload = false;
            }
        }
        RefreshInisListThread rilt = new RefreshInisListThread(force, this, handler, ((LiqoidApplication)  getActivity().getApplication()));
        rilt.start();
        //   RefreshAreasListThread ralt = new RefreshAreasListThread(force, this);
        //  ralt.start();
    }

    public void finishedRefreshInisList(MultiInstanceInitiativen newInis) {
        areasListAdapter = new AreasListAdapter(this, ((LiqoidApplication)  getActivity().getApplication()).lqfbInstances.getSelectedInstance().areas, R.id.areasList);



    }
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            //Update status line
            long dataage = ((LiqoidApplication) getActivity(). getApplication()).cachedAPI1Queries.dataage;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dataagestr = formatter.format(new Date(dataage));
            String prefix = "";
            if (currentDownloadInstance != null) {
                if (currentDownloadInstance.pauseDownload) {
                    prefix = "Offline - ";
                }
            }

            ((LiqoidApplication)  getActivity().getApplication()).statusLineText(prefix +  getActivity().getApplicationContext().getString(R.string.dataage) + ": " + dataagestr);
            if ((msg.what == RefreshInisListThread.UPDATING)) {
                progressDialog = ProgressDialog.show( getActivity(), "",
                         getActivity().getApplicationContext().getString(R.string.updating) + "...", true);

            }
            if (currentDownloadInstance != null) {
                //update progressdialog
                if ((msg.what == RefreshInisListThread.DOWNLOADING) && (!currentDownloadInstance.pauseDownload)) {
                    progressDialog.setMessage(
                             getActivity().getApplicationContext().getString(R.string.downloading) + "\n" + ((LiqoidApplication)  getActivity().getApplication()).lqfbInstances.getSelectedInstance().getName() + "...");

                }
            }
            if (msg.what == RefreshInisListThread.FINISH_OK) {
                try {
                    progressDialog.cancel();
                } catch (Exception e) {
                    //Sometimes it is not attached anymore
                    progressDialog = null;
                }
                final ListView listview = (ListView)  getActivity().findViewById(R.id.areasList);
                listview.setAdapter(areasListAdapter);
            }
            if (progressDialog != null) {
                if (msg.what == RefreshInisListThread.DOWNLOAD_ERROR) {
                    progressDialog.setMessage( getActivity().getApplicationContext().getString(R.string.download_error));

                }
                if (msg.what == RefreshInisListThread.DOWNLOAD_RETRY) {
                    progressDialog.setMessage( getActivity().getApplicationContext().getString(R.string.downloading));

                }
            }

        }
    };

    public void onlyUpdateAreasListFromMemory() {
        //  refreshAreasList(false);
        areasListAdapter = new AreasListAdapter(this, ((LiqoidApplication)  getActivity().getApplication()).lqfbInstances.getSelectedInstance().areas, R.id.areasList);
        final ListView listview = (ListView)  getActivity().findViewById(R.id.areasList);
        listview.setAdapter(areasListAdapter);

    }

    //Instances Spinner item selected
    public void onItemSelected(AdapterView<?> arg0, View arg1, int i, long arg3) {
        LQFBInstances ls = ((LiqoidApplication)  getActivity().getApplication()).lqfbInstances;
        ls.setSelectedInstance(((LiqoidApplication)  getActivity().getApplication()).lqfbInstances.get(i).getShortName());
        ((LiqoidApplication)  getActivity().getApplication()).fireLQFBInstanceChangedEvent();
    }
}
