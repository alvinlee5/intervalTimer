package com.example.myfirstapp;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Alvin on 2017-01-07.
 */

public class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Load the round_preferences from an XML resource
        addPreferencesFromResource(R.xml.round_preferences);
    }
}


// http://stackoverflow.com/questions/23523806/how-do-you-create-preference-activity-and-preference-fragment-on-android