package com.svw.touchtime;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.WindowManager;

public class SettingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_main).toString()));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);

        getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);






    }

}
