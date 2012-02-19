/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.quadrillenschule.liquidroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 *
 * @author andi
 */
public class UpcomingTabActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the Upcoming events tab");
        setContentView(textview);
    }

}
