package com.svw.touchtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class AdministratorMenuActivity extends ActionBarActivity {
    private int Caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_menu);

        TextView main_menu;
        main_menu = (TextView) findViewById(R.id.main_menu);
        Caller = getIntent().getIntExtra("Caller", -1);
        setTitle(getText(R.string.title_back).toString().concat(" " + getText(R.string.title_activity_main).toString()));
        if (Caller == R.id.caller_administrator)
            main_menu.setText(getText(R.string.title_activity_administrator_menu).toString());
        else
            main_menu.setText(getText(R.string.title_activity_supervisor_menu).toString());
    }

    public void ReportReviewMenu(View view) {
        Intent intent = new Intent(this, ReportReviewMenuActivity.class);
        intent.putExtra("Caller", Caller);
        startActivity(intent);
    }

    public void EmployeeProfileMenu(View view) {
        Intent intent = new Intent(this, EmployeeProfileMenuActivity.class);
        intent.putExtra("Caller", Caller);
        startActivity(intent);
    }

    public void EmployeePunchMenu(View view) {
        Intent intent = new Intent(this, EmployeePunchMenuActivity.class);
        intent.putExtra("Caller", Caller);
        startActivity(intent);
    }

    public void WorkGroupPunchMenu(View view) {
        Intent intent = new Intent(this, WorkGroupPunchMenuActivity.class);
        intent.putExtra("Caller", Caller);
        startActivity(intent);
    }

    public void WorkGroupMenu(View view) {
        Intent intent = new Intent(this, WorkGroupMenuActivity.class);
        intent.putExtra("Caller", Caller);
        startActivity(intent);
    }

    public void DailyActivityMenu(View view) {
        Intent intent = new Intent(this, DailyActivityMenuActivity.class);
        intent.putExtra("Caller", Caller);
        startActivity(intent);
    }

    public void ReportExportMenu(View view) {
        Intent intent = new Intent(this, ReportExportMenuActivity.class);
        intent.putExtra("Caller", Caller);
        startActivity(intent);
    }

    public void CompanyJobLocationMenu(View view) {
        Intent intent = new Intent(this, CompanyJobLocationMenuActivity.class);
        intent.putExtra("Caller", Caller);
        startActivity(intent);
    }

    public void CompanyProfileMenu(View view) {
        Intent intent = new Intent(this, CompanyProfileMenuActivity.class);
        intent.putExtra("Caller", Caller);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_administrator_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
