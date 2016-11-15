package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class AdministratorMenuActivity extends SettingActivity {
    private int Caller;
    String Password;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_menu);

        TextView main_menu;
        main_menu = (TextView) findViewById(R.id.main_menu);
        Caller = getIntent().getIntExtra("Caller", -1);
        setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_main).toString()));
        if (Caller == R.id.caller_administrator)
            main_menu.setText(getText(R.string.title_activity_administrator_menu).toString());
        else
            main_menu.setText(getText(R.string.title_activity_supervisor_menu).toString());

        context = this;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);

        if (Caller == R.id.caller_administrator) {
            ReadSettings();

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            input.setTextColor(getResources().getColor(R.color.svw_cyan));
            input.setTextSize(getResources().getInteger(R.integer.password_size));
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setView(input);    //edit text added to alert
            builder.setMessage(R.string.settings_enter_password).setTitle(R.string.settings_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (!input.getText().toString().equals(SettingOldPassword)) {
                        AlertDialog.Builder Pbuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                        Pbuilder.setMessage(R.string.settings_wrong_password).setTitle(R.string.settings_title);
                        Pbuilder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                onBackPressed();
                            }
                        });
                        AlertDialog Pdialog = Pbuilder.create();
                        General.TouchTimeDialog(Pdialog, findViewById(android.R.id.content));
                    }
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, findViewById(android.R.id.content));
        }
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

    public void TimeSheetMenu(View view) {
        Intent intent = new Intent(this, TimeSheetMenuActivity.class);
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
            if (Caller == R.id.caller_administrator) {
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
