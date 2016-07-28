package com.svw.touchtime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintManager;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.svw.touchtime.R.layout.general_edit_text_view;

public class TimeSheetMenuActivity extends ActionBarActivity {
    private static final int   NUMBER_ITEMS = 2;
    private static final int   NUMBER_COLUMNS = 12;
    public ListView time_sheet_list_view;
    Context context;
    Spinner NameSpinner;
    Button WeekButton, ExportButton, PrintButton;
    private TextView name_view, company_view, location_view, job_view;
    private TextView sunday_view, monday_view, tuesday_view, wednesday_view;
    private TextView thursday_view, friday_view, saturday_view, hours_view;
    private TouchTimeGeneralAdapter adapter_time_sheet;
    ArrayList<HashMap<String, String>> newTimeSheetList;
    ArrayList<HashMap<String, String>> feedTimeSheetList;
    private DatePickerDialog mDatePicker;
    ArrayList<String> list_name;
    ArrayAdapter<String> adapter_name;
    ArrayList<DailyActivityList> time_sheet_activities;
    String[] activity_item = new String[NUMBER_COLUMNS];
    int[] activity_id = new int[NUMBER_COLUMNS];
    DateFormat df, tf;
    Calendar calendar;
    File NewTimeSheetFile, OldTimeSheetFile;

    String CurrentDate;
    String StartDate, EndDate;
    String noSelection;
    String selectedName;
    int StartYear, Caller;

    ArrayList<String> ObjectKeys;
    ArrayList<String> itemsSelected;

    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    HashMap<String, String> map;
    private DailyActivityDBWrapper dbActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet_menu);
        Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
        time_sheet_list_view = (ListView) findViewById(R.id.time_sheet_list_view);
        newTimeSheetList = new ArrayList<HashMap<String, String>>();
        feedTimeSheetList = new ArrayList<HashMap<String, String>>();
        NameSpinner = (Spinner) findViewById(R.id.time_sheet_name_spinner);
        WeekButton = (Button) findViewById(R.id.time_sheet_week_button);
        ExportButton = (Button) findViewById(R.id.time_sheet_export_button);
        PrintButton = (Button) findViewById(R.id.time_sheet_print_button);
        name_view = (TextView) findViewById(R.id.textViewName);
        company_view = (TextView) findViewById(R.id.textViewCompany);
        location_view = (TextView) findViewById(R.id.textViewLocation);
        job_view = (TextView) findViewById(R.id.textViewJob);
        sunday_view = (TextView) findViewById(R.id.textViewSunday);
        monday_view = (TextView) findViewById(R.id.textViewMonday);
        tuesday_view = (TextView) findViewById(R.id.textViewTuesday);
        wednesday_view = (TextView) findViewById(R.id.textViewWednesday);
        thursday_view = (TextView) findViewById(R.id.textViewThursday);
        friday_view = (TextView) findViewById(R.id.textViewFriday);
        saturday_view = (TextView) findViewById(R.id.textViewSaturday);
        hours_view = (TextView) findViewById(R.id.textViewHours);

        context = this;
        df = new SimpleDateFormat("yyyy-MM-dd");
        tf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        CurrentDate = df.format(Calendar.getInstance().getTime());

        activity_item[0] = getText(R.string.column_key_name).toString();
        activity_item[1] = getText(R.string.column_key_company).toString();
        activity_item[2] = getText(R.string.column_key_location).toString();
        activity_item[3] = getText(R.string.column_key_job).toString();
        activity_item[4] = getText(R.string.column_key_sunday).toString();
        activity_item[5] = getText(R.string.column_key_monday).toString();
        activity_item[6] = getText(R.string.column_key_tuesday).toString();
        activity_item[7] = getText(R.string.column_key_wednesday).toString();
        activity_item[8] = getText(R.string.column_key_thursday).toString();
        activity_item[9] = getText(R.string.column_key_friday).toString();
        activity_item[10] = getText(R.string.column_key_saturday).toString();
        activity_item[11] = getText(R.string.column_key_hours).toString();

        activity_id[0] = R.id.textViewName;
        activity_id[1] = R.id.textViewCompany;
        activity_id[2] = R.id.textViewLocation;
        activity_id[3] = R.id.textViewJob;
        activity_id[4] = R.id.textViewSunday;
        activity_id[5] = R.id.textViewMonday;
        activity_id[6] = R.id.textViewTuesday;
        activity_id[7] = R.id.textViewWednesday;
        activity_id[8] = R.id.textViewThursday;
        activity_id[9] = R.id.textViewFriday;
        activity_id[10] = R.id.textViewSaturday;
        activity_id[11] = R.id.textViewHours;

        time_sheet_list_view.setItemsCanFocus(true);
        adapter_time_sheet = new TouchTimeGeneralAdapter(this, feedTimeSheetList, R.layout.time_sheet_view, activity_item, activity_id, 60);
        time_sheet_list_view.setAdapter(adapter_time_sheet);
        noSelection = getText(R.string.column_key_no_selection).toString();
        list_name = new ArrayList<String>();
        list_name.add(noSelection);
        adapter_name = new ArrayAdapter<String>(context, general_edit_text_view, list_name);
        NameSpinner.setAdapter(adapter_name);
        // fire once with animation set to false, it actually will avoid the first fire
        NameSpinner.setSelection(0, false);
        NameSpinner.setOnItemSelectedListener(OnSpinnerCL);

        calendar = Calendar.getInstance();
        mDatePicker = new DatePickerDialog(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar),
                myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        itemsSelected = new ArrayList<String>();
        ObjectKeys = new ArrayList<String>();
        OldTimeSheetFile = new File(context.getExternalCacheDir(), "TimeSheet" + ".csv");
        selectedName =  noSelection;
    }

    public AdapterView.OnItemSelectedListener OnSpinnerCL = new AdapterView.OnItemSelectedListener() {
        // called only when a different item is selected
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (parent == NameSpinner) {
                if (list_name.get(pos).equals(noSelection)) {
                    selectedName = noSelection;
                } else {
                    selectedName = list_name.get(pos);
                }
                filterTimeSheetActivities(view);
            }
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (view.isShown()) {           // somehow onDateSet is called twice in higher version of Android, use this to avoid doing it the second time.
                String keyDate = String.format("%4s-%2s-%2s", year, ++monthOfYear, dayOfMonth).replace(' ', '0');   // monthOfYear starts from 0
                // WeekButton.setText(keyDate);
                Date date = new Date();
                try {
                    date = df.parse(keyDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                calendar.setTime(date);
                while (calendar.get(Calendar.DAY_OF_WEEK) > calendar.getFirstDayOfWeek()) {
                    calendar.add(Calendar.DATE, -1);                   // Substract 1 day until first day of week (1: Sunday).
                }
                StartDate = String.format("%4s-%2s-%2s", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH)).replace(' ', '0');   // monthOfYear starts from 0
                StartYear = calendar.get(Calendar.YEAR);
                WeekButton.setText(StartDate);
                keyDate = getText(R.string.time_sheet_sunday).toString()+' '+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                sunday_view.setText(keyDate);
                calendar.add(Calendar.DATE, 1);
                keyDate = getText(R.string.time_sheet_monday).toString()+' '+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                monday_view.setText(keyDate);
                calendar.add(Calendar.DATE, 1);
                keyDate = getText(R.string.time_sheet_tuesday).toString()+' '+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                tuesday_view.setText(keyDate);
                calendar.add(Calendar.DATE, 1);
                keyDate = getText(R.string.time_sheet_wednesday).toString()+' '+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                wednesday_view.setText(keyDate);
                calendar.add(Calendar.DATE, 1);
                keyDate = getText(R.string.time_sheet_thursday).toString()+' '+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                thursday_view.setText(keyDate);
                calendar.add(Calendar.DATE, 1);
                keyDate = getText(R.string.time_sheet_friday).toString()+' '+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                friday_view.setText(keyDate);
                calendar.add(Calendar.DATE, 1);
                keyDate = getText(R.string.time_sheet_saturday).toString()+' '+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                saturday_view.setText(keyDate);
                EndDate = String.format("%4s-%2s-%2s", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH)).replace(' ', '0');   // monthOfYear starts from 0
                selectTimeSheetActivities(view);
            }
        }
    };

    public void onSelectWeekButtonClicked(View view) {
        mDatePicker.show();
    }

    public void selectTimeSheetActivities(View view) {
        dbActivity = new DailyActivityDBWrapper(context, StartYear);
        if (dbActivity.getActivityListCount() == 0) {
            context.deleteDatabase(dbActivity.getDatabaseName());       // it is empty, might as well delete it
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
            builder.setMessage(getText(R.string.daily_activity_no_message).toString() + " " + String.valueOf(StartYear)).setTitle(R.string.time_sheet_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    newTimeSheetList.clear();
                    feedTimeSheetList.clear();
                    adapter_time_sheet.notifyDataSetChanged();
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        } else {
            newTimeSheetList.clear();
            feedTimeSheetList.clear();
            itemsSelected.clear();
            ObjectKeys.clear();
            ObjectKeys.add(dbActivity.getDateColumnKey());
            ObjectKeys.add(dbActivity.getDateColumnKey());
            itemsSelected.add(StartDate);
            itemsSelected.add(EndDate);
            readTimeSheetActivities(view);
        }
    }

    public void readTimeSheetActivities(View view) {
        String [] Column = new String[NUMBER_ITEMS];
        String [] Compare = new String[NUMBER_ITEMS];
        String [] Values = new String[NUMBER_ITEMS];
        int i = 0, j = 0, k, count = 0;
        int DayOfWeek;
        double ActivityTotal, TimeSheetTotal;
        double Hours, Sum;
        double[] DayTotal = new double[7];
        for (String s : itemsSelected) {
            if (!s.equals(noSelection)) {
                Column[count] = ObjectKeys.get(i);
                Compare[count] = "=";           // default =
                if (ObjectKeys.get(i).equals(dbActivity.getDateColumnKey())) {
                    if (i == ObjectKeys.indexOf(dbActivity.getDateColumnKey()))
                        Compare[count] = ">=";      // start date
                    if (i == ObjectKeys.indexOf(dbActivity.getDateColumnKey()) + 1)
                        Compare[count] = "<=";    // end date
                }
                Values[count] = s;
                count++;
            }
            i++;
        }
        if (count > 0) {        // selected at least 1
            time_sheet_activities = dbActivity.getActivityLists(Column, Compare, Values);
            if (time_sheet_activities.size() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                builder.setMessage(getText(R.string.time_sheet_no_message).toString()).setTitle(R.string.time_sheet_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                General.TouchTimeDialog(dialog, view);
            } else {
                list_name.clear();
                i = 0;
                DailyActivityList Activity;
                while (i < time_sheet_activities.size()) {
                    map = new HashMap<String, String>();
                    Activity = time_sheet_activities.get(i);
                    String Name;
                    Name = Activity.getLastName() + ", " + Activity.getFirstName() + " : " + String.valueOf(Activity.getEmployeeID());

                    map.put(getText(R.string.column_key_name).toString(), Name);
                    list_name.add(Name);
                    map.put(getText(R.string.column_key_company).toString(), Activity.getCompany());
                    map.put(getText(R.string.column_key_location).toString(), Activity.getLocation());
                    map.put(getText(R.string.column_key_job).toString(), Activity.getJob());
                    Date date = new Date();
                    try {
                        date = df.parse(Activity.getDate());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    calendar.setTime(date);
                    DayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);                             // DayOfWeek 1: Sunday
                    Hours = (double)Activity.getHours() / 60;
                    for (j=1; j<=7; j++) {
                        if (j==DayOfWeek) {
                            map.put(activity_item[j + 3], String.format("%1$.2f", Hours)); // activity_items 4: Sunday.  Divided by 60 to get fraction of an hour
                        } else {
                            map.put(activity_item[j + 3], "");                              // blank
                        }
                    }
                    newTimeSheetList.add(map);
                    i++;
                };
            }
        }
        if (time_sheet_activities.size() > 0) {
            for (i = 0; i < newTimeSheetList.size(); i++) {
                for (j = i + 1; j < newTimeSheetList.size(); j++) {
                    if (newTimeSheetList.get(i).get(getText(R.string.column_key_name).toString()).equals(newTimeSheetList.get(j).get(getText(R.string.column_key_name).toString()))
                            && newTimeSheetList.get(i).get(getText(R.string.column_key_company).toString()).equals(newTimeSheetList.get(j).get(getText(R.string.column_key_company).toString()))
                            && newTimeSheetList.get(i).get(getText(R.string.column_key_location).toString()).equals(newTimeSheetList.get(j).get(getText(R.string.column_key_location).toString()))
                            && newTimeSheetList.get(i).get(getText(R.string.column_key_job).toString()).equals(newTimeSheetList.get(j).get(getText(R.string.column_key_job).toString()))) {
                        for (k = 4; k <= 10; k++) {     // K is the index of the activity_item
                            if (!newTimeSheetList.get(j).get(activity_item[k]).isEmpty()) {
                                if (!newTimeSheetList.get(i).get(activity_item[k]).isEmpty()) {
                                    Sum = Double.parseDouble(newTimeSheetList.get(i).get(activity_item[k])) + Double.parseDouble(newTimeSheetList.get(j).get(activity_item[k]));
                                } else {
                                    Sum = Double.parseDouble(newTimeSheetList.get(j).get(activity_item[k]));
                                }
                                newTimeSheetList.get(i).put(activity_item[k], String.format("%1$.2f", Sum));
                                newTimeSheetList.remove(j);
                                j--;        // adjust index because record is removed.
                                break;
                            }
                        }
                    }
                }
            }
            TimeSheetTotal = 0.0;
            for (k = 0; k < 7; k++) DayTotal[k] = 0.0;
            for (i = 0; i < newTimeSheetList.size(); i++) {
                ActivityTotal = 0.0;
                for (k = 4, j = 0; k <= 10; k++, j++) {     // K is the index of the activity_item
                    if (!newTimeSheetList.get(i).get(activity_item[k]).isEmpty()) {
                        DayTotal[j] += Double.parseDouble(newTimeSheetList.get(i).get(activity_item[k]));
                        ActivityTotal += Double.parseDouble(newTimeSheetList.get(i).get(activity_item[k]));
                    }
                }
                newTimeSheetList.get(i).put(getText(R.string.column_key_hours).toString(), String.format("%1$.2f", ActivityTotal));
                TimeSheetTotal += ActivityTotal;
                feedTimeSheetList.add(i, newTimeSheetList.get(i));
            }
            map = new HashMap<String, String>();
            map.put(getText(R.string.column_key_name).toString(), "");
            map.put(getText(R.string.column_key_company).toString(), "");
            map.put(getText(R.string.column_key_location).toString(), "");
            map.put(getText(R.string.column_key_job).toString(), "");
            for (k = 4, j = 0; k <= 10; k++, j++) {     // K is the index of the activity_item
                map.put(activity_item[k], String.format("%1$.2f", DayTotal[j]));
            }
            map.put(getText(R.string.column_key_hours).toString(), String.format("%1$.2f", TimeSheetTotal));
            feedTimeSheetList.add(map);

            adapter_time_sheet.notifyDataSetChanged();
            General.sortString(list_name);
            list_name = General.removeDuplicates(list_name);
            list_name.add(0, noSelection);
            adapter_name.notifyDataSetChanged();
            NameSpinner.setSelection(0, false);
        }
    }

    public void filterTimeSheetActivities(View view) {
        int i, j, k;
        double[] DayTotal = new double[8];

        for (k = 0; k < 8; k++) DayTotal[k] = 0.0;
        feedTimeSheetList.clear();
        for (i = 0; i < newTimeSheetList.size(); i++) {
            if (selectedName.equals(newTimeSheetList.get(i).get(getText(R.string.column_key_name).toString())) ||
                    selectedName.equals(noSelection)) {
                for (k = 4, j = 0; k <= 11; k++, j++) {     // K is the index of the activity_item, include the activity total
                    if (!newTimeSheetList.get(i).get(activity_item[k]).isEmpty()) {
                        DayTotal[j] += Double.parseDouble(newTimeSheetList.get(i).get(activity_item[k]));
                    }
                }
                feedTimeSheetList.add(newTimeSheetList.get(i));
            }
        }
        map = new HashMap<String, String>();
        map.put(getText(R.string.column_key_name).toString(), "");
        map.put(getText(R.string.column_key_company).toString(), "");
        map.put(getText(R.string.column_key_location).toString(), "");
        map.put(getText(R.string.column_key_job).toString(), "");
        for (k = 4, j = 0; k <= 11; k++, j++) {     // K is the index of the activity_item
            map.put(activity_item[k], String.format("%1$.2f", DayTotal[j]));
        }
        feedTimeSheetList.add(map);
        adapter_time_sheet.notifyDataSetChanged();
    }

    public void onExportButtonClicked(View view) {
        String to = "djlee@smartvisionworks.com";
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("plain/text");
        try {
            String CurrentTime = tf.format(Calendar.getInstance().getTime());
            String Subject = "Time Sheet " + CurrentTime;
            if (OldTimeSheetFile.exists()) OldTimeSheetFile.delete();
            NewTimeSheetFile = new File(context.getExternalCacheDir(), Subject + ".csv");
            if (!NewTimeSheetFile.exists()) {
                if (NewTimeSheetFile.createNewFile()) {
                    FileWriter out = (FileWriter) generateCsvFile(NewTimeSheetFile);
                    i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(NewTimeSheetFile));
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                    i.putExtra(Intent.EXTRA_SUBJECT, Subject);
                    i.putExtra(Intent.EXTRA_TEXT, Subject);
                    startActivity(Intent.createChooser(i, "E-mail"));
                    OldTimeSheetFile = NewTimeSheetFile;        // because email intent is asynchronous, keep track of the file opened and delete it
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileWriter generateCsvFile(File sFileName) {
            int i, j;
            FileWriter writer = null;
            String Header = "";
            String ColumnNames = "";
            String Entries;

            Header = getText(R.string.time_sheet_header).toString() + " " + StartDate + " " + ((selectedName.equals(noSelection)) ? "" : selectedName);
            Header = "\"" + Header + "\""  + "\n";
            for (i=0; i<NUMBER_COLUMNS; i++) ColumnNames += activity_item[i] + ',';
            ColumnNames += "\n";
            try {
                writer = new FileWriter(sFileName);
                writer.append(Header);
                writer.append(ColumnNames);
                for (i=0; i<feedTimeSheetList.size(); i++) {
                    Entries = "";
                    for (j=0; j<NUMBER_COLUMNS; j++) {
                        Entries += "\"" + feedTimeSheetList.get(i).get(activity_item[j]) + "\"" + ",";
                    }
                    Entries += "\n";
                    writer.append(Entries);
                }
                writer.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally
            {
                try {
                    writer.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return writer;
    }

    public void setPrintButtonClicked(View view) {
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
 //       printManager.print("My document", new CustomPrintDocumentAdapter(this), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_sheet_menu, menu);
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
        } else if (id == android.R.id.home) {
            if (OldTimeSheetFile.exists()) OldTimeSheetFile.delete();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
