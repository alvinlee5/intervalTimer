package com.example.myfirstapp;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.List;

/**
 * Created by Alvin on 2017-01-07.
 */
public class MyPreferenceActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MyPreferenceFragment())
                .commit();
    }

/*    @Override
    public void onBuildHeaders(List<Header> target){
        loadHeadersFromResource(R.xml.headers_preference, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName){
        return MyPreferenceFragment.class.getName().equals(fragmentName);
    }*/

}
