/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.quadrillenschule.liquidroid;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 *
 * @author andi
 */
public class GlobalPrefsActivity extends PreferenceActivity{
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.globalprefsscreen);
}
}
