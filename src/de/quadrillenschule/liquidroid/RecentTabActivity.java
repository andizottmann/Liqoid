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
public class RecentTabActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        TextView textview = new TextView(this);
        textview.setText("This is the Recent events tab");
        setContentView(textview);
    }

}
