package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SettingActivity extends ActionBarActivity {
    private EditText OldPasswordEdit, NewPasswordEdit1, NewPasswordEdit2, EmailAddressdEdit;
    private EditText ShortestActivityEdit, LunchTimeEdit, WorkHoursEdit, BackupDaysEdit;
    ArrayList<String> Settings = new ArrayList<String>();
    boolean GoodPassword = false;
    String  CurrentDate;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    String  FileName = "TouchTimeSettings";
    public  String SettingOldPassword;
    public  String SettingEmail;
    public  int SettingShortestActivity;
    public  int SettingLunchTime;
    public  double SettingWorkHours;
    public  int SettingBackupDays;
    public  String SettingLastBackup;

    String  MasterPassword = "8015927860";
    static final int SHORTEST_ACT = 10;
    static final int LUNCH_TIME = 30;
    static final double WORK_HOURS = 5.0;
    static final int BACKUP_DAYS = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_main).toString()));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
        getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        OldPasswordEdit = (EditText) findViewById(R.id.old_password_text);
        NewPasswordEdit1 = (EditText) findViewById(R.id.new_password_text1);
        NewPasswordEdit2 = (EditText) findViewById(R.id.new_password_text2);
        EmailAddressdEdit = (EditText) findViewById(R.id.default_email_address);
        ShortestActivityEdit = (EditText) findViewById(R.id.shortest_activities);
        LunchTimeEdit = (EditText) findViewById(R.id.default_lunch_time);
        WorkHoursEdit = (EditText) findViewById(R.id.minimum_lunch_hours);
        BackupDaysEdit = (EditText) findViewById(R.id.minimum_back_up_days);

        DateFormat tf = new SimpleDateFormat(getText(R.string.date_YMD_format).toString(), Locale.US);
        CurrentDate = tf.format(new Date());

        if (!ReadSettings()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setMessage(R.string.settings_not_available).setTitle(R.string.settings_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, findViewById(android.R.id.content));
        }
        EmailAddressdEdit.setText(SettingEmail);
        ShortestActivityEdit.setText(String.valueOf(SettingShortestActivity));
        LunchTimeEdit.setText(String.valueOf(SettingLunchTime));
        WorkHoursEdit.setText(String.valueOf(SettingWorkHours));
        BackupDaysEdit.setText(String.valueOf(SettingBackupDays));
    }

    public void onChangePasswordClicked( View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (OldPasswordEdit.getText().toString().equals(SettingOldPassword) || OldPasswordEdit.getText().toString().equals(MasterPassword)) {
            if (!NewPasswordEdit1.getText().toString().isEmpty() && !NewPasswordEdit2.getText().toString().isEmpty() &&
                    NewPasswordEdit1.getText().toString().equals(NewPasswordEdit2.getText().toString())) {
                SettingOldPassword = NewPasswordEdit1.getText().toString();
                GoodPassword = true;
                WriteSettings();
            } else {
                builder.setMessage(R.string.settings_password_not_match).setTitle(R.string.settings_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GoodPassword = false;
                    }
                });
                AlertDialog dialog = builder.create();
                General.TouchTimeDialog(dialog, findViewById(android.R.id.content));
            }
        } else {
            builder.setMessage(R.string.settings_wrong_password).setTitle(R.string.settings_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    GoodPassword = false;
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, findViewById(android.R.id.content));
        }
    }

    public void onUpdateSettingsClicked( View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (OldPasswordEdit.getText().toString().equals(SettingOldPassword) || OldPasswordEdit.getText().toString().equals(MasterPassword)) {
            GoodPassword = true;
            WriteSettings();
        } else {
            builder.setMessage(R.string.settings_wrong_password).setTitle(R.string.settings_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, findViewById(android.R.id.content));
        }
    }

    public boolean ReadSettings() {
        ArrayList<String> Output = new ArrayList<String>();
        if (readSettingsFile(FileName, Output)) {
            if (Output.size() > 0) SettingOldPassword = Output.get(0);
            if (Output.size() > 1) SettingEmail = Output.get(1);
            if (Output.size() > 2) SettingShortestActivity = Integer.parseInt(Output.get(2));
            if (Output.size() > 3) SettingLunchTime = Integer.parseInt(Output.get(3));
            if (Output.size() > 4) SettingWorkHours = Double.parseDouble(Output.get(4));
            if (Output.size() > 5) SettingBackupDays = Integer.parseInt(Output.get(5));
            if (Output.size() > 6) SettingLastBackup = Output.get(6);
            return true;
        } else {
            DateFormat tf = new SimpleDateFormat(getText(R.string.date_YMD_format).toString(), Locale.US);
            CurrentDate = tf.format(new Date());
            SettingOldPassword = "password";
            SettingEmail = "";
            SettingShortestActivity = SHORTEST_ACT;
            SettingLunchTime = LUNCH_TIME;
            SettingWorkHours = WORK_HOURS;
            SettingBackupDays = BACKUP_DAYS;
            SettingLastBackup = CurrentDate;
            return false;
        }
    }

    public void WriteSettings() {
        Settings.add(SettingOldPassword);
        Settings.add(EmailAddressdEdit.getText().toString());
        Settings.add(ShortestActivityEdit.getText().toString());
        Settings.add(LunchTimeEdit.getText().toString());
        Settings.add(WorkHoursEdit.getText().toString());
        Settings.add(BackupDaysEdit.getText().toString());
        if (SettingLastBackup.isEmpty()) {
            Settings.add(CurrentDate);
        } else {
            Settings.add(SettingLastBackup);
        }
        if (GoodPassword) writeSettingsFile(FileName, Settings);
    }

    public boolean readSettingsFile(String FILENAME, ArrayList<String> lines) {
        FileInputStream read;
        lines.clear();
        try {
            read = openFileInput(FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(read);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            inputStreamReader.close();
            read.close();
            return true;
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return false;
        } // openFileOutput underlined red
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public boolean writeSettingsFile(String FILENAME, ArrayList<String> lines) {
        FileOutputStream write;
        try {
            write = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            for (String line : lines) {
                write.write((line + "\n").getBytes());  // add terminal character so that it doesn't get written as one line
            }
            write.close();
            return true;
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return false;
        } // openFileOutput underlined red
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        WriteSettings();
        super.onBackPressed();
    }
}
