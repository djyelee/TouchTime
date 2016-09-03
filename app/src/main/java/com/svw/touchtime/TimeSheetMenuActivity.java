package com.svw.touchtime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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
    private static final int NUMBER_ITEMS = 2;
    private static final int NUMBER_COLUMNS = 13;
    private static final int SELECT[] = {R.string.button_detail, R.string.button_summary};
    public ListView time_sheet_list_view;
    Context context;
    Spinner NameSpinner;
    Button WeekButton, ExportButton, PrintButton, SortNameButton, SubtotalButton;
    private TextView header_view;
    private TextView sunday_view, monday_view, tuesday_view, wednesday_view;
    private TextView thursday_view, friday_view, saturday_view;
    private TouchTimeGeneralAdapter adapter_time_sheet;
    ArrayList<HashMap<String, String>> newTimeSheetList;
    ArrayList<HashMap<String, String>> filterTimeSheetList;
    ArrayList<HashMap<String, String>> feedTimeSheetList;
    private DatePickerDialog mDatePicker;
    ArrayList<String> list_name;
    ArrayAdapter<String> adapter_name;
    ArrayList<DailyActivityList> time_sheet_activities;
    String[] activity_item = new String[NUMBER_COLUMNS];
    int[] activity_id = new int[NUMBER_COLUMNS];
    DateFormat df, tf;
    Calendar calendar;
    File NewTimeSheetFile;
    boolean sort_company_ascend = true;
    boolean sort_name_ascend = true;
    boolean sort_supervisor_ascend = true;
    int display_flag = 0;       // set to detail as default
    int sort_select = 0;
    double[] WeekTotal = new double[8];

    String CurrentDate;
    String StartDate, EndDate;
    String noSelection;
    String selectedName;
    int StartYear, Caller;

    ArrayList<String> ObjectKeys;
    ArrayList<String> itemsSelected;

    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private DailyActivityDBWrapper dbActivity;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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
        filterTimeSheetList = new ArrayList<HashMap<String, String>>();
        feedTimeSheetList = new ArrayList<HashMap<String, String>>();
        NameSpinner = (Spinner) findViewById(R.id.time_sheet_name_spinner);
        WeekButton = (Button) findViewById(R.id.time_sheet_week_button);
        ExportButton = (Button) findViewById(R.id.time_sheet_export_button);
        PrintButton = (Button) findViewById(R.id.time_sheet_print_button);
        SortNameButton = (Button) findViewById(R.id.sort_name);
        SubtotalButton = (Button) findViewById(R.id.time_sheet_subtotal);
        header_view = (TextView) findViewById(R.id.time_sheet_view);
        sunday_view = (TextView) findViewById(R.id.textViewSunday);
        monday_view = (TextView) findViewById(R.id.textViewMonday);
        tuesday_view = (TextView) findViewById(R.id.textViewTuesday);
        wednesday_view = (TextView) findViewById(R.id.textViewWednesday);
        thursday_view = (TextView) findViewById(R.id.textViewThursday);
        friday_view = (TextView) findViewById(R.id.textViewFriday);
        saturday_view = (TextView) findViewById(R.id.textViewSaturday);

        context = this;
        df = new SimpleDateFormat(getText(R.string.date_YMD_format).toString());
        tf = new SimpleDateFormat(getText(R.string.date_time_format).toString());
        CurrentDate = df.format(Calendar.getInstance().getTime());

        activity_item[0] = getText(R.string.column_key_name).toString();
        activity_item[1] = getText(R.string.column_key_company).toString();
        activity_item[2] = getText(R.string.column_key_location).toString();
        activity_item[3] = getText(R.string.column_key_job).toString();
        activity_item[4] = getText(R.string.column_key_supervisor).toString();
        activity_item[5] = getText(R.string.column_key_sunday).toString();
        activity_item[6] = getText(R.string.column_key_monday).toString();
        activity_item[7] = getText(R.string.column_key_tuesday).toString();
        activity_item[8] = getText(R.string.column_key_wednesday).toString();
        activity_item[9] = getText(R.string.column_key_thursday).toString();
        activity_item[10] = getText(R.string.column_key_friday).toString();
        activity_item[11] = getText(R.string.column_key_saturday).toString();
        activity_item[12] = getText(R.string.column_key_hours).toString();

        activity_id[0] = R.id.textViewName;
        activity_id[1] = R.id.textViewCompany;
        activity_id[2] = R.id.textViewLocation;
        activity_id[3] = R.id.textViewJob;
        activity_id[4] = R.id.textViewSupervisor;
        activity_id[5] = R.id.textViewSunday;
        activity_id[6] = R.id.textViewMonday;
        activity_id[7] = R.id.textViewTuesday;
        activity_id[8] = R.id.textViewWednesday;
        activity_id[9] = R.id.textViewThursday;
        activity_id[10] = R.id.textViewFriday;
        activity_id[11] = R.id.textViewSaturday;
        activity_id[12] = R.id.textViewHours;

        time_sheet_list_view.setItemsCanFocus(true);
        adapter_time_sheet = new TouchTimeGeneralAdapter(this, feedTimeSheetList, R.layout.time_sheet_view, activity_item, activity_id, 60);
        time_sheet_list_view.setAdapter(adapter_time_sheet);
        adapter_time_sheet.setSelectedItem(-1);         // set to -1 so not highlight will show
        noSelection = getText(R.string.column_key_no_selection).toString();
        list_name = new ArrayList<String>();
        list_name.add(noSelection);
        adapter_name = new ArrayAdapter<String>(context, general_edit_text_view, list_name);
        NameSpinner.setAdapter(adapter_name);
        // fire once with animation set to false, it actually will avoid the first fire
        NameSpinner.setSelection(0, false);
        NameSpinner.setOnItemSelectedListener(OnSpinnerCL);

        calendar = Calendar.getInstance();
        mDatePicker = new DatePickerDialog(new ContextThemeWrapper(this, R.style.TouchTimeCalendar),
                myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        itemsSelected = new ArrayList<String>();
        ObjectKeys = new ArrayList<String>();
        deleteCSVFiles();
        selectedName = noSelection;

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public AdapterView.OnItemSelectedListener OnSpinnerCL = new AdapterView.OnItemSelectedListener() {
        // called only when a different item is selected
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (parent == NameSpinner) {
                if (list_name.get(pos).equals(noSelection)) {
                    selectedName = noSelection;
                    filterTimeSheetActivities(view);
                    sort_name_ascend = true;
                    onSortNameButtonClicked(view);
                } else {
                    selectedName = list_name.get(pos);
                    filterTimeSheetActivities(view);
                    sort_name_ascend = true;
                    onSortNameButtonClicked(view);
                }
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (view.isShown()) {           // somehow onDateSet is called twice in higher version of Android, use this to avoid doing it the second time.
                String keyDate = String.format("%4s/%2s/%2s", year, ++monthOfYear, dayOfMonth).replace(' ', '0');   // monthOfYear starts from 0
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
                StartDate = String.format("%4s/%2s/%2s", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)).replace(' ', '0');   // monthOfYear starts from 0
                StartYear = calendar.get(Calendar.YEAR);
                header_view.setText(getText(R.string.time_sheet_header).toString() + " " + General.convertYMDtoMDY(StartDate));

                keyDate = getText(R.string.time_sheet_sunday).toString() + ' ' + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                sunday_view.setText(keyDate);
                calendar.add(Calendar.DATE, 1);
                keyDate = getText(R.string.time_sheet_monday).toString() + ' ' + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                monday_view.setText(keyDate);
                calendar.add(Calendar.DATE, 1);
                keyDate = getText(R.string.time_sheet_tuesday).toString() + ' ' + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                tuesday_view.setText(keyDate);
                calendar.add(Calendar.DATE, 1);
                keyDate = getText(R.string.time_sheet_wednesday).toString() + ' ' + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                wednesday_view.setText(keyDate);
                calendar.add(Calendar.DATE, 1);
                keyDate = getText(R.string.time_sheet_thursday).toString() + ' ' + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                thursday_view.setText(keyDate);
                calendar.add(Calendar.DATE, 1);
                keyDate = getText(R.string.time_sheet_friday).toString() + ' ' + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                friday_view.setText(keyDate);
                calendar.add(Calendar.DATE, 1);
                keyDate = getText(R.string.time_sheet_saturday).toString() + ' ' + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                saturday_view.setText(keyDate);
                EndDate = String.format("%4s/%2s/%2s", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)).replace(' ', '0');   // monthOfYear starts from 0
                selectTimeSheetActivities(view);  // read new records after filtering, sort by name after
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
            builder.setMessage(getText(R.string.daily_activity_no_message).toString() + " " + String.valueOf(StartDate)).setTitle(R.string.time_sheet_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    newTimeSheetList.clear();
                    filterTimeSheetList.clear();
                    feedTimeSheetList.clear();
                    adapter_time_sheet.notifyDataSetChanged();
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        } else {
            newTimeSheetList.clear();
            filterTimeSheetList.clear();
            feedTimeSheetList.clear();
            itemsSelected.clear();
            ObjectKeys.clear();
            ObjectKeys.add(dbActivity.getDateColumnKey());
            ObjectKeys.add(dbActivity.getDateColumnKey());
            itemsSelected.add(StartDate);
            itemsSelected.add(EndDate);
            readTimeSheetActivities(view);
            sort_name_ascend = true;
            onSortNameButtonClicked(view);
        }
    }

    public void readTimeSheetActivities(View view) {
        String[] Column = new String[NUMBER_ITEMS];
        String[] Compare = new String[NUMBER_ITEMS];
        String[] Values = new String[NUMBER_ITEMS];
        int i = 0, j = 0, k, count = 0;
        int DayOfWeek;
        double ActivityTotal;
        double Hours, Sum;
        HashMap<String, String> map;
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
                    map.put(getText(R.string.column_key_supervisor).toString(), Activity.getSupervisor());
                    Date date = new Date();
                    try {
                        date = df.parse(Activity.getDate());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    calendar.setTime(date);
                    DayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);                             // DayOfWeek 1: Sunday
                    Hours = (double) Activity.getHours() / 60;
                    for (j = 1; j <= 7; j++) {
                        if (j == DayOfWeek) {
                            map.put(activity_item[j + 4], String.format("%1$.2f", Hours)); // activity_items 5: Sunday.  Divided by 60 to get fraction of an hour
                        } else {
                            map.put(activity_item[j + 4], "");                              // blank
                        }
                    }
                    newTimeSheetList.add(map);
                    i++;
                }
                ;
            }
        }
        if (time_sheet_activities.size() > 0) {  // combine records of the same name and job
            for (i = 0; i < newTimeSheetList.size(); i++) {
                for (j = i + 1; j < newTimeSheetList.size(); j++) {
                    if (newTimeSheetList.get(i).get(getText(R.string.column_key_name).toString()).equals(newTimeSheetList.get(j).get(getText(R.string.column_key_name).toString()))
                            && newTimeSheetList.get(i).get(getText(R.string.column_key_company).toString()).equals(newTimeSheetList.get(j).get(getText(R.string.column_key_company).toString()))
                            && newTimeSheetList.get(i).get(getText(R.string.column_key_location).toString()).equals(newTimeSheetList.get(j).get(getText(R.string.column_key_location).toString()))
                            && newTimeSheetList.get(i).get(getText(R.string.column_key_job).toString()).equals(newTimeSheetList.get(j).get(getText(R.string.column_key_job).toString()))) {
                        for (k = 5; k <= 11; k++) {     // K is the index of the activity_item
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
            WeekTotal[7] = 0.0;
            for (k = 0; k < 7; k++) WeekTotal[k] = 0.0;
            for (i = 0; i < newTimeSheetList.size(); i++) {
                ActivityTotal = 0.0;
                for (k = 5, j = 0; k <= 11; k++, j++) {     // K is the index of the activity_item
                    if (!newTimeSheetList.get(i).get(activity_item[k]).isEmpty()) {
                        WeekTotal[j] += Double.parseDouble(newTimeSheetList.get(i).get(activity_item[k]));
                        ActivityTotal += Double.parseDouble(newTimeSheetList.get(i).get(activity_item[k]));
                    }
                }
                newTimeSheetList.get(i).put(getText(R.string.column_key_hours).toString(), String.format("%1$.2f", ActivityTotal));
                WeekTotal[7] += ActivityTotal;
                filterTimeSheetList.add(i, newTimeSheetList.get(i));
                feedTimeSheetList.add(i, newTimeSheetList.get(i));
            }
            General.sortString(list_name);
            list_name = General.removeDuplicates(list_name);
            list_name.add(0, noSelection);
            adapter_name.notifyDataSetChanged();
            NameSpinner.setSelection(0, false);
        }
    }

    public void filterTimeSheetActivities(View view) {
        int i, j, k;
        for (k = 0; k < 8; k++) WeekTotal[k] = 0.0;
        filterTimeSheetList.clear();
        for (i = 0; i < newTimeSheetList.size(); i++) {
            if (selectedName.equals(newTimeSheetList.get(i).get(getText(R.string.column_key_name).toString())) ||
                    selectedName.equals(noSelection)) {
                for (k = 5, j = 0; k <= 12; k++, j++) {     // K is the index of the activity_item, include the activity total
                    if (!newTimeSheetList.get(i).get(activity_item[k]).isEmpty()) {
                        WeekTotal[j] += Double.parseDouble(newTimeSheetList.get(i).get(activity_item[k]));
                    }
                }
                filterTimeSheetList.add(newTimeSheetList.get(i));
            }
        }
    }

    public void subtotalTimeSheetActivities(View view) {
        // take filtered list and add a subtotal line if subtotal_flag is true;
        double[] ESubtotal = new double[8];     // employee subtotal
        double[] ISubtotal = new double[8];     // item subtotal
        int i, j, k, l, m;
        String First, Second;
        HashMap<String, String> map;
        feedTimeSheetList.clear();
        adapter_time_sheet.MyListColors.clear();
        // generate feedTimeSheetList with a subtotal depending on sort_select
        for (i = 0; i < filterTimeSheetList.size(); i++) {      // filtered list is already sorted
            First = filterTimeSheetList.get(i).get(activity_item[sort_select]);
            if (First.isEmpty()) {
                feedTimeSheetList.add(filterTimeSheetList.get(i));
                adapter_time_sheet.MyListColors.add(0);
                continue;  // stop accumulating if empty
            }
            // determine the range for the sorted item
            int begin = i, end = i;
            for (k = 0; k < 8; k++) ISubtotal[k] = 0.0;
            for (j = i; j < filterTimeSheetList.size(); j++) {
                if (First.equals(filterTimeSheetList.get(j).get(activity_item[sort_select]))) {
                    for (k = 0; k < 8; k++) {       // accumulate all with the same sort item
                        ISubtotal[k] += filterTimeSheetList.get(j).get(activity_item[k + 5]).equals("") ?
                                0.0 : Double.parseDouble(filterTimeSheetList.get(j).get(activity_item[k + 5]));
                    }
                    end = j+1;
                    i = j;      // skip to the next one after incremented by 1 at the top
                } else {
                    break;
                }
            }
            for (j = begin; j < end; j++) {
                Second = filterTimeSheetList.get(j).get(getText(R.string.column_key_name).toString());  // Name will not be empty
                if (SELECT[display_flag] == R.string.button_detail) {
                    feedTimeSheetList.add(filterTimeSheetList.get(j));      // show individual name if sort by name and showing detail
                    adapter_time_sheet.MyListColors.add(0);
                } else if (!activity_item[sort_select].equals(getText(R.string.column_key_name).toString()) && SELECT[display_flag] == R.string.button_summary) {
                    for (k = 0; k < 8; k++) ESubtotal[k] = 0.0;
                    for (l = j; l < end; l++) {
                        if (Second.equals(filterTimeSheetList.get(l).get(getText(R.string.column_key_name).toString()))) {
                            for (k = 0; k < 8; k++) {
                                ESubtotal[k] += filterTimeSheetList.get(l).get(activity_item[k + 5]).equals("") ?
                                        0.0 : Double.parseDouble(filterTimeSheetList.get(l).get(activity_item[k + 5]));
                            }
                            j = l;
                        } else {
                            break;
                        }
                    }
                    map = new HashMap<String, String>();
                    map.put(getText(R.string.column_key_name).toString(), Second);
                    map.put(getText(R.string.column_key_company).toString(), (activity_item[sort_select].equals(getText(R.string.column_key_company).toString())) ? First : "");
                    map.put(getText(R.string.column_key_location).toString(), "");
                    map.put(getText(R.string.column_key_job).toString(), "");
                    map.put(getText(R.string.column_key_supervisor).toString(), (activity_item[sort_select].equals(getText(R.string.column_key_supervisor).toString())) ? First : "");
                    for (k = 5, m = 0; k <= 12; k++, m++) {     // K is the index of the activity_item
                                map.put(activity_item[k], String.format("%1$.2f", ESubtotal[m]));
                    }
                    feedTimeSheetList.add(map);
                    adapter_time_sheet.MyListColors.add(1);
                }
            }
            // add extra line at the end for total hours
            map = new HashMap<String, String>();
            map.put(getText(R.string.column_key_name).toString(), (activity_item[sort_select].equals(getText(R.string.column_key_name).toString())) ? First : "");
            map.put(getText(R.string.column_key_company).toString(), (activity_item[sort_select].equals(getText(R.string.column_key_company).toString())) ? First : "");
            map.put(getText(R.string.column_key_location).toString(), "");
            map.put(getText(R.string.column_key_job).toString(), "");
            map.put(getText(R.string.column_key_supervisor).toString(), (activity_item[sort_select].equals(getText(R.string.column_key_supervisor).toString())) ? First : "");
            for (k = 5, l = 0; k <= 12; k++, l++) {     // K is the index of the activity_item
                map.put(activity_item[k], String.format("%1$.2f", ISubtotal[l]));
            }
            feedTimeSheetList.add(map);
            adapter_time_sheet.MyListColors.add(2);
        }
        map = new HashMap<String, String>();
        map.put(getText(R.string.column_key_name).toString(), "");
        map.put(getText(R.string.column_key_company).toString(), "");
        map.put(getText(R.string.column_key_location).toString(), "");
        map.put(getText(R.string.column_key_job).toString(), "");
        map.put(getText(R.string.column_key_supervisor).toString(), getText(R.string.report_total_hours).toString());
        for (k = 5, j = 0; k <= 12; k++, j++) {     // K is the index of the activity_item
            map.put(activity_item[k], String.format("%1$.2f", WeekTotal[j]));
        }
        feedTimeSheetList.add(map);
        adapter_time_sheet.MyListColors.add(3);
    }

    public void onSortNameButtonClicked(View view) {
        if (filterTimeSheetList.size() == 0) return;
        String [] Items = new String [5];
        Items [0] = getText(R.string.column_key_name).toString();
        Items [1] = getText(R.string.column_key_company).toString();
        Items [2] = getText(R.string.column_key_location).toString();
        Items [3] = getText(R.string.column_key_job).toString();
        Items [4] = getText(R.string.column_key_supervisor).toString();
        General.SortStringList(filterTimeSheetList, Items, sort_name_ascend);
        sort_select = 0;   // index for name
        subtotalTimeSheetActivities(view);
        sort_name_ascend = !sort_name_ascend;
        SortNameButton.setText(sort_name_ascend ? getText(R.string.up).toString() : getText(R.string.down).toString());
        adapter_time_sheet.notifyDataSetChanged();
    }

    public void onSortCompanyButtonClicked(View view) {
        if (filterTimeSheetList.size() == 0) return;
        String [] Items = new String [5];
        Items [0] = getText(R.string.column_key_company).toString();
        Items [1] = getText(R.string.column_key_name).toString();
        Items [2] = getText(R.string.column_key_location).toString();
        Items [3] = getText(R.string.column_key_job).toString();
        Items [4] = getText(R.string.column_key_supervisor).toString();
        General.SortStringList(filterTimeSheetList, Items, sort_company_ascend);
        sort_select = 1;        // index for company
        subtotalTimeSheetActivities(view);
        sort_company_ascend = !sort_company_ascend;
        adapter_time_sheet.notifyDataSetChanged();
    }

    public void onSortSupervisorButtonClicked(View view) {
        if (filterTimeSheetList.size() == 0) return;
        String [] Items = new String [5];
        Items [0] = getText(R.string.column_key_supervisor).toString();
        Items [1] = getText(R.string.column_key_name).toString();
        Items [2] = getText(R.string.column_key_company).toString();
        Items [3] = getText(R.string.column_key_location).toString();
        Items [4] = getText(R.string.column_key_job).toString();
        General.SortStringList(filterTimeSheetList, Items, sort_supervisor_ascend);
        sort_select = 4;        // index for supervisor
        subtotalTimeSheetActivities(view);
        sort_supervisor_ascend = !sort_supervisor_ascend;
        adapter_time_sheet.notifyDataSetChanged();
    }

    public void onSubtotalButtonClicked(View view) {
        if (newTimeSheetList.size() <= 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
            builder.setMessage(getText(R.string.no_daily_activity_message).toString());
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    newTimeSheetList.clear();
                    filterTimeSheetList.clear();
                    feedTimeSheetList.clear();
                    adapter_time_sheet.notifyDataSetChanged();
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        } else {
            display_flag = 1 - display_flag;
            SubtotalButton.setText(getText(SELECT[1 - display_flag]).toString());
            subtotalTimeSheetActivities(view);
            adapter_time_sheet.notifyDataSetChanged();
        }
    }

    public void onExportButtonClicked(View view) {
        String to = "svwtouchtime@gmail.com";
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("plain/text");
        try {
            DateFormat dtf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");       // Cannot use "/" in file name, use "-" instead.
            String CurrentTime = dtf.format(Calendar.getInstance().getTime());
            String Subject = "TimeSheet " + CurrentTime;
            deleteCSVFiles();
//          NewTimeSheetFile = new File(context.getExternalCacheDir(), Subject + ".csv");   // app private folder
            NewTimeSheetFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Subject + ".csv");  // download folder
            if (!NewTimeSheetFile.exists()) {
                if (NewTimeSheetFile.createNewFile()) {
                    FileWriter out = (FileWriter) generateCsvFile(NewTimeSheetFile);
                    i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(NewTimeSheetFile));
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                    i.putExtra(Intent.EXTRA_SUBJECT, Subject);
                    i.putExtra(Intent.EXTRA_TEXT, Subject);
                    startActivity(Intent.createChooser(i, "E-mail"));
                    finish();           // force it to quit to remove the file because email intent is asynchronous
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

        Header = getText(R.string.time_sheet_header).toString() + " " + General.convertYMDtoMDY(StartDate) + " " + ((selectedName.equals(noSelection)) ? "" : selectedName);
        Header = "\"" + Header + "\"" + "\n";
        for (i = 0; i < NUMBER_COLUMNS; i++) ColumnNames += activity_item[i] + ',';
        ColumnNames += "\n";
        try {
            writer = new FileWriter(sFileName);
            writer.append(Header);
            writer.append(ColumnNames);
            for (i = 0; i < feedTimeSheetList.size(); i++) {
                Entries = "";
                for (j = 0; j < NUMBER_COLUMNS; j++) {
                    if (j < NUMBER_COLUMNS-1)
                        Entries += "\"" + feedTimeSheetList.get(i).get(activity_item[j]) + "\"" + ",";
                    else
                        Entries += "\"" + feedTimeSheetList.get(i).get(activity_item[j]) + "\"";
                }
                Entries += "\n";
                writer.append(Entries);
            }
            writer.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return writer;
    }

    public void onPrintButtonClicked(View view) {


    }

    public void deleteCSVFiles() {
        File Folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File list[] = Folder.listFiles();
        for (File f : list) {
            String name = f.getName();
            if (name.substring(name.lastIndexOf(".") + 1, name.length()).equals("csv")) {
                f.delete();
            }
        }
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
            deleteCSVFiles();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TimeSheetMenu Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.svw.touchtime/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TimeSheetMenu Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.svw.touchtime/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
