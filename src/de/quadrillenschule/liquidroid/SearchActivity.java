/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import de.quadrillenschule.liquidroid.gui.InitiativenListAdapter;
import de.quadrillenschule.liquidroid.gui.RecentInitiativenListAdapter;
import de.quadrillenschule.liquidroid.model.Area;
import de.quadrillenschule.liquidroid.model.Initiative;
import de.quadrillenschule.liquidroid.model.LQFBInstance;
import de.quadrillenschule.liquidroid.model.MultiInstanceInitiativen;

/**
 *
 * @author andi
 */
public class SearchActivity extends InitiativesTabActivity {

    @Override
    public void onCreate(Bundle icicle) {
        listViewId = R.id.searchList;
        super.onCreate(icicle);
        postOnCreate();
    }

    @Override
    public void postOnCreate() {
        listViewId = R.id.searchList;
        allInis = new MultiInstanceInitiativen();
        setContentView(R.layout.search);

        EditText et = (EditText) findViewById(R.id.searchText);
        et.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                createInisListAdapter();
                return false;
            }
        });
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                createInisListAdapter();
                return true;
            }
        });
        CheckBox fulltextcb = (CheckBox) findViewById(R.id.searchFull);
        fulltextcb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                createInisListAdapter();
            }
        });
        CheckBox casetextcb = (CheckBox) findViewById(R.id.searchCase);
        casetextcb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                createInisListAdapter();
            }
        });
    }

    @Override
    public void filterList() {
        if (filterOnlySelected) {
            allInis.removeNonSelected();
        }
    }

    @Override
    public void sortList() {
        if (!sortNewestFirst) {
            allInis.reverse(Initiative.ISSUE_LAST_EVENT_COMP);
        } else {
            allInis.sort(Initiative.ISSUE_LAST_EVENT_COMP);
        }
        try {
            inisListAdapter.notifyDataSetChanged();
        } catch (NullPointerException e) {
        }
    }

    @Override
    public InitiativenListAdapter getInitiativenListAdapter() {
        EditText et = (EditText) findViewById(R.id.searchText);
        CheckBox fulltextcb = (CheckBox) findViewById(R.id.searchFull);
        CheckBox casetextcb = (CheckBox) findViewById(R.id.searchCase);
        return new InitiativenListAdapter(this, allInis, R.id.searchList);
    }

    @Override
    void createInisListAdapter() {
        allInis = new MultiInstanceInitiativen();
        for (LQFBInstance myInstance : ((LiqoidApplication) getApplication()).lqfbInstances) {
            for (Area a : myInstance.areas.getSelectedAreas()) {
                for (Initiative i : a.getInitiativen()) {
                    allInis.add(i);
                }
            }
        }
        EditText et = (EditText) findViewById(R.id.searchText);
        CheckBox fulltextcb = (CheckBox) findViewById(R.id.searchFull);
        CheckBox casetextcb = (CheckBox) findViewById(R.id.searchCase);
        allInis = allInis.searchResult(et.getText().toString(), fulltextcb.isChecked(), casetextcb.isChecked());
        inisListAdapter = getInitiativenListAdapter();
        filterList();
        sortList();
        final ListView listView = (ListView) findViewById(listViewId);
        listView.setAdapter(inisListAdapter);
        inisListAdapter.notifyDataSetChanged();
    }
}
