package com.svw.touchtime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.svw.touchtime.R.layout.general_edit_text_view;

public class ReportReviewMenuActivity extends ActionBarActivity {
    private static final int   STARTING_YEAR = 2010;
    private static final int   NUMBER_YEARS = 30;
    private static final int   NUMBER_ITEMS = 8;
    private static final String DEFAULT_CHECKOUT_TIME = "05:00:00 PM";
    ArrayList<DailyActivityList> all_activity_lists;
    private EditText LunchMinuteEdit, SupervisorEdit, CommentsEdit;
    ArrayList<String> list_year;
    ArrayList<String> list_last_name;
    ArrayList<String> list_first_name;
    ArrayList<String> list_group;
    ArrayList<String> list_company;
    ArrayList<String> list_location;
    ArrayList<String> list_job;
    ArrayAdapter<String> adapter_year;
    ArrayAdapter<String> adapter_last_name;
    ArrayAdapter<String> adapter_first_name;
    ArrayAdapter<String> adapter_group;
    ArrayAdapter<String> adapter_company;
    ArrayAdapter<String> adapter_location;
    ArrayAdapter<String> adapter_job;
    ArrayList<String> ObjectKeys;
    ArrayList<String> itemsSelected;
    String  noSelection;
    boolean NoActivity = true;
    boolean [] InitialSelect = new boolean[NUMBER_ITEMS];
    DatePickerDialog dialog;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    Context context;
    Spinner YearSpinner;
    Spinner LastNameSpinner;
    Spinner FirstNameSpinner;
    Spinner GroupSpinner;
    Spinner CompanySpinner;
    Spinner LocationSpinner;
    Spinner JobSpinner;
    private int dateButtonID;
    Button StartDateButton, EndDateButton;

    private SimpleAdapter adapter_activity;
    ArrayList<HashMap<String, String>> feedActivityList;
    DailyActivityList Activity;
    HashMap<String, String> map;
    String[] employee_item = new String[20];
    int[] employee_id = new int[20];
    int CurrentYear = 0;
    String CurrentDate;
    private int lastClickId = -1;
    private int rowCount = -1;
    private DailyActivityDBWrapper dbActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_review_menu);
        int Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));

        ListView daily_activity_list_view;
        daily_activity_list_view = (ListView) findViewById(R.id.daily_activity_list_view);
        feedActivityList = new ArrayList<HashMap<String, String>>();
        LunchMinuteEdit = (EditText) findViewById(R.id.activity_lunch_text);
        SupervisorEdit = (EditText) findViewById(R.id.activity_supervisor_text);
        CommentsEdit = (EditText) findViewById(R.id.activity_comment_text);
        StartDateButton = (Button) findViewById(R.id.report_start_date_button);
        EndDateButton = (Button) findViewById(R.id.report_end_date_button);
        context = this;

        employee_item[0] = getText(R.string.column_key_employee_id).toString();
        employee_item[1] = getText(R.string.column_key_last_name).toString();
        employee_item[2] = getText(R.string.column_key_first_name).toString();
        employee_item[3] = getText(R.string.column_key_group_id).toString();
        employee_item[4] = getText(R.string.column_key_company).toString();
        employee_item[5] = getText(R.string.column_key_location).toString();
        employee_item[6] = getText(R.string.column_key_job).toString();
        employee_item[7] = getText(R.string.column_key_date).toString();
        employee_item[8] = getText(R.string.column_key_timein).toString();
        employee_item[9] = getText(R.string.column_key_timeout).toString();
        employee_item[10] = getText(R.string.column_key_lunch).toString();
        employee_item[11] = getText(R.string.column_key_hours).toString();
        employee_item[12] = getText(R.string.column_key_supervisor).toString();
        employee_item[13] = getText(R.string.column_key_comments).toString();
        employee_id[0] = R.id.textViewID;
        employee_id[1] = R.id.textViewLastName;
        employee_id[2] = R.id.textViewFirstName;
        employee_id[3] = R.id.textViewGroup;
        employee_id[4] = R.id.textViewCompany;
        employee_id[5] = R.id.textViewLocation;
        employee_id[6] = R.id.textViewJob;
        employee_id[7] = R.id.textViewDate;
        employee_id[8] = R.id.textViewTimeIn;
        employee_id[9] = R.id.textViewTimeOut;
        employee_id[10] = R.id.textViewLunch;
        employee_id[11] = R.id.textViewHours;
        employee_id[12] = R.id.textViewSupervisor;
        employee_id[13] = R.id.textViewComments;

        daily_activity_list_view.setItemsCanFocus(true);
        adapter_activity = new SimpleAdapter(this, feedActivityList, R.layout.daily_activity_view, employee_item, employee_id);
        daily_activity_list_view.setAdapter(adapter_activity);

        Calendar calendar;
        calendar = Calendar.getInstance();
        CurrentYear = calendar.get(Calendar.YEAR);
        CurrentDate = String.format("%4s-%2s-%2s", String.valueOf(calendar.get(Calendar.YEAR)), String.valueOf(calendar.get(Calendar.MONTH)+1),
                String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))).replace(' ', '0');
        list_year = new ArrayList<String>();
        list_last_name = new ArrayList<String>();
        list_first_name = new ArrayList<String>();
        list_group = new ArrayList<String>();
        list_company = new ArrayList<String>();
        list_location = new ArrayList<String>();
        list_job = new ArrayList<String>();
        itemsSelected = new ArrayList<String>();
        ObjectKeys = new ArrayList<String>();
        noSelection = getText(R.string.column_key_no_selection).toString();
        int i;
        for (i = 0; i < NUMBER_YEARS && i + STARTING_YEAR <= CurrentYear; i++) list_year.add(String.valueOf(i + STARTING_YEAR));
        for (i = 0; i < NUMBER_ITEMS; i++) InitialSelect[i] = true;

        YearSpinner = (Spinner) findViewById(R.id.year_spinner);
        LastNameSpinner = (Spinner) findViewById(R.id.last_name_spinner);
        FirstNameSpinner = (Spinner) findViewById(R.id.first_name_spinner);
        GroupSpinner = (Spinner) findViewById(R.id.group_spinner);
        CompanySpinner = (Spinner) findViewById(R.id.company_spinner);
        LocationSpinner = (Spinner) findViewById(R.id.location_spinner);
        JobSpinner = (Spinner) findViewById(R.id.job_spinner);

        YearSpinner.setOnItemSelectedListener(OnYearSpinnerCL);
        LastNameSpinner.setOnItemSelectedListener(OnYearSpinnerCL);
        FirstNameSpinner.setOnItemSelectedListener(OnYearSpinnerCL);
        GroupSpinner.setOnItemSelectedListener(OnYearSpinnerCL);
        CompanySpinner.setOnItemSelectedListener(OnYearSpinnerCL);
        LocationSpinner.setOnItemSelectedListener(OnYearSpinnerCL);
        JobSpinner.setOnItemSelectedListener(OnYearSpinnerCL);

        adapter_last_name = new ArrayAdapter<String>(context, general_edit_text_view, list_last_name);
        adapter_first_name = new ArrayAdapter<String>(context, general_edit_text_view, list_first_name);
        adapter_group = new ArrayAdapter<String>(context, general_edit_text_view, list_group);
        adapter_company = new ArrayAdapter<String>(context, general_edit_text_view, list_company);
        adapter_location = new ArrayAdapter<String>(context, general_edit_text_view, list_location);
        adapter_job = new ArrayAdapter<String>(context, general_edit_text_view, list_job);

        LastNameSpinner.setAdapter(adapter_last_name);
        FirstNameSpinner.setAdapter(adapter_first_name);
        GroupSpinner.setAdapter(adapter_group);
        CompanySpinner.setAdapter(adapter_company);
        LocationSpinner.setAdapter(adapter_location);
        JobSpinner.setAdapter(adapter_job);

        adapter_year = new ArrayAdapter<String>(this, general_edit_text_view, list_year);
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        YearSpinner.setAdapter(adapter_year);
        YearSpinner.setSelection(list_year.indexOf(String.valueOf(CurrentYear)));

        dialog = new DatePickerDialog(this, myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        {
            @Override
            protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                int year = getContext().getResources().getIdentifier("android:id/year", null, null);
                if(year != 0){
                    View yearPicker = findViewById(year);
                    if(yearPicker != null){
                        yearPicker.setVisibility(View.GONE);
                    }
                }
            }
        };

        daily_activity_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, final View view, int position, long arg3) {
                final int item = position;
                view.animate().setDuration(1000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                                builder.setMessage(R.string.delete_daily_activity_message).setTitle(R.string.report_review_title);
                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        int ID = 0;
                                        String G = feedActivityList.get(item).get(getText(R.string.column_key_employee_id).toString());
                                        if (G != null && !G.isEmpty()) ID = Integer.parseInt(G);
                                        if (ID > 0) {
                                            String TI = feedActivityList.get(item).get(getText(R.string.column_key_timein).toString());
                                            String DT = feedActivityList.get(item).get(getText(R.string.column_key_date).toString());
                                            dbActivity.deletePunchedInActivityList(ID, DT, TI);
                                            feedActivityList.remove(item);
                                            adapter_activity.notifyDataSetChanged();
                                        }
                                    }
                                });
                                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                General.TouchTimeDialog(dialog, view);
                                view.setAlpha(1);
                            }
                        });
                return false;
            }
        });

        daily_activity_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final int pos = position;
                view.animate().setDuration(30).alpha(0) .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        int ID = 0;
                        String Lunch = LunchMinuteEdit.getText().toString();
                        String Supervisor = SupervisorEdit.getText().toString();
                        String Comments = CommentsEdit.getText().toString();
                        String G = feedActivityList.get(pos).get(getText(R.string.column_key_employee_id).toString());
                        boolean updated = false;
                        if (G != null && !G.isEmpty()) ID = Integer.parseInt(G);
                        if (ID > 0) {
                            ArrayList<DailyActivityList> ActivityList;
                            Activity = new DailyActivityList();
                            String[] Column = new String[5];
                            String[] Compare = new String[5];
                            String[] Values = new String[5];
                            Column[0] = dbActivity.getIDColumnKey();
                            Column[1] = dbActivity.getDateColumnKey();
                            Column[2] = dbActivity.getTimeInColumnKey();
                            Values[0] = String.valueOf(ID);
                            Values[1] = feedActivityList.get(pos).get(getText(R.string.column_key_date).toString());
                            Values[2] = feedActivityList.get(pos).get(getText(R.string.column_key_timein).toString());
                            Compare[0] = Compare[1] = Compare[2] = "=";
                            ActivityList = dbActivity.getActivityLists(Column, Compare, Values);
                            Activity = ActivityList.get(0);   // should only return one, take the first one
                            if (!Lunch.isEmpty() && Long.parseLong(Lunch) >= 0) {
                                if (!Activity.TimeIn.isEmpty()) {
                                    long diff = General.MinuteDifference(Activity.getTimeIn(), Activity.getTimeOut());
                                    diff = (diff > 0 && diff >= Long.parseLong(Lunch)) ? diff - Long.parseLong(Lunch) : 0;
                                    Activity.setHours(diff);
                                    feedActivityList.get(pos).put(getText(R.string.column_key_hours).toString(),
                                            String.format("%2s:%2s", String.valueOf(Activity.Hours / 60 % 24),
                                                    String.valueOf(Activity.Hours % 60)).replace(' ', '0'));
                                }
                                Activity.setLunch(Long.parseLong(Lunch));
                                feedActivityList.get(pos).put(getText(R.string.column_key_lunch).toString(),
                                        String.format("%2s:%2s", String.valueOf(Activity.Lunch / 60 % 24),
                                                String.valueOf(Activity.Lunch % 60)).replace(' ', '0'));
                                // LunchMinuteEdit.setText("");
                                updated = true;
                            }
                            if (!Supervisor.isEmpty()) {
                                feedActivityList.get(pos).put(getText(R.string.column_key_supervisor).toString(), Supervisor);
                                // SupervisorEdit.setText("");
                                Activity.setSupervisor(Supervisor);
                                updated = true;
                            }
                            if (!Comments.isEmpty()) {
                                feedActivityList.get(pos).put(getText(R.string.column_key_comments).toString(), Comments);
                                // CommentsEdit.setText("");
                                Activity.setComments(Comments);
                                updated = true;
                            }
                            if (updated) {
                                dbActivity.updateActivityList(Activity, Column, Values);
                                adapter_activity.notifyDataSetChanged();
                            }
/*                            int oldPos;
                            int Top = adapter_parent.getFirstVisiblePosition();
                            if (rowCount < 0) rowCount = adapter_parent.getChildCount() + 1;      // number of rows per screen
                            oldPos = ((Top / rowCount + 1) * rowCount + lastClickId-Top)%rowCount ;
                            int newPos = (pos-adapter_parent.getFirstVisiblePosition());
                            if ((lastClickId != -1) && (oldPos != newPos)) {
                                adapter_parent.getChildAt(oldPos%(adapter_parent.getChildCount())).setBackgroundResource(R.color.svw_cyan);
                                // view.setBackgroundResource(R.color.svw_dark_gray);
                                adapter_parent.getChildAt(newPos).setBackgroundResource(R.color.svw_gray);
                            }
                            if (lastClickId == -1) adapter_parent.getChildAt(pos%(adapter_parent.getChildCount())).setBackgroundResource(R.color.svw_gray);
                            // if (lastClickId == -1) view.setBackgroundResource(R.color.svw_dark_gray);
                            lastClickId = pos;
                            */
                        }
                        view.setAlpha(1);
                    }
                });
            }
        });
    }

    public AdapterView.OnItemSelectedListener OnYearSpinnerCL = new AdapterView.OnItemSelectedListener() {
        // called only when a different item is selected
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (parent == YearSpinner) {
                dbActivity = new DailyActivityDBWrapper(context, Integer.parseInt(list_year.get(pos)));
                if (dbActivity.getActivityListCount() == 0) {
                    NoActivity = true;
                    context.deleteDatabase(dbActivity.getDatabaseName());      // it is empty, might as well delete
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                    builder.setMessage(R.string.no_daily_activity_message).setTitle(R.string.report_review_title);
                    builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            feedActivityList.clear();
                            adapter_activity.notifyDataSetChanged();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    General.TouchTimeDialog(dialog, view);
                } else {
                    NoActivity = false;
                    ObjectKeys.clear();
                    ObjectKeys.add(dbActivity.getLastNameColumnKey());
                    ObjectKeys.add(dbActivity.getFirstNameColumnKey());
                    ObjectKeys.add(dbActivity.getWorkGroupColumnKey());
                    ObjectKeys.add(dbActivity.getCompanyColumnKey());
                    ObjectKeys.add(dbActivity.getLocationColumnKey());
                    ObjectKeys.add(dbActivity.getJobColumnKey());
                    ObjectKeys.add(dbActivity.getDateColumnKey());      // same key, one for start date one for end date
                    ObjectKeys.add(dbActivity.getDateColumnKey());      // same key, one for start date one for end date
                    for (int i=0; i<ObjectKeys.size(); i++) itemsSelected.add(i, noSelection);
                    updateSpinnerListView(view);
                }
            } else if (parent == LastNameSpinner) {
                if (InitialSelect[ObjectKeys.indexOf(dbActivity.getLastNameColumnKey())]) {
                    InitialSelect[ObjectKeys.indexOf(dbActivity.getLastNameColumnKey())] = false;
                } else if (!itemsSelected.get(ObjectKeys.indexOf(dbActivity.getLastNameColumnKey())).equals(list_last_name.get(pos))) {
                    itemsSelected.set(ObjectKeys.indexOf(dbActivity.getLastNameColumnKey()), list_last_name.get(pos));
                    updateSpinnerListView(view);
                }
            } else if (parent == FirstNameSpinner) {
                if (InitialSelect[ObjectKeys.indexOf(dbActivity.getFirstNameColumnKey())]) {
                    InitialSelect[ObjectKeys.indexOf(dbActivity.getFirstNameColumnKey())] = false;
                } else if (!itemsSelected.get(ObjectKeys.indexOf(dbActivity.getFirstNameColumnKey())).equals(list_first_name.get(pos))) {
                    itemsSelected.set(ObjectKeys.indexOf(dbActivity.getFirstNameColumnKey()), list_first_name.get(pos));
                    updateSpinnerListView(view);
                }
            } else if (parent == GroupSpinner) {
                if (InitialSelect[ObjectKeys.indexOf(dbActivity.getWorkGroupColumnKey())]) {
                    InitialSelect[ObjectKeys.indexOf(dbActivity.getWorkGroupColumnKey())] = false;
                } else if (!itemsSelected.get(ObjectKeys.indexOf(dbActivity.getWorkGroupColumnKey())).equals(list_group.get(pos))) {
                    itemsSelected.set(ObjectKeys.indexOf(dbActivity.getWorkGroupColumnKey()), list_group.get(pos));
                    updateSpinnerListView(view);
                }
            } else if (parent == CompanySpinner) {
                if (InitialSelect[ObjectKeys.indexOf(dbActivity.getCompanyColumnKey())]) {
                    InitialSelect[ObjectKeys.indexOf(dbActivity.getCompanyColumnKey())] = false;
                } else if (!itemsSelected.get(ObjectKeys.indexOf(dbActivity.getCompanyColumnKey())).equals(list_company.get(pos))) {
                    itemsSelected.set(ObjectKeys.indexOf(dbActivity.getCompanyColumnKey()), list_company.get(pos));
                    updateSpinnerListView(view);
                }
            } else if (parent == LocationSpinner) {
                if (InitialSelect[ObjectKeys.indexOf(dbActivity.getLocationColumnKey())]) {
                    InitialSelect[ObjectKeys.indexOf(dbActivity.getLocationColumnKey())] = false;
                } else if (!itemsSelected.get(ObjectKeys.indexOf(dbActivity.getLocationColumnKey())).equals(list_location.get(pos))) {
                    itemsSelected.set(ObjectKeys.indexOf(dbActivity.getLocationColumnKey()), list_location.get(pos));
                    updateSpinnerListView(view);
                }
            } else if (parent == JobSpinner) {
                if (InitialSelect[ObjectKeys.indexOf(dbActivity.getJobColumnKey())]) {
                    InitialSelect[ObjectKeys.indexOf(dbActivity.getJobColumnKey())] = false;
                } else if (!itemsSelected.get(ObjectKeys.indexOf(dbActivity.getJobColumnKey())).equals(list_job.get(pos))) {
                    itemsSelected.set(ObjectKeys.indexOf(dbActivity.getJobColumnKey()), list_job.get(pos));
                    updateSpinnerListView(view);
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
                String displayDate = String.format("%2s/%2s", ++monthOfYear, dayOfMonth).replace(' ', '0');   // monthOfYear starts from 0
                String keyDate = String.format("%4s-%2s-%2s", CurrentYear, monthOfYear, dayOfMonth).replace(' ', '0');
                switch (dateButtonID) {
                    case R.id.report_start_date_button:
                        itemsSelected.set(ObjectKeys.indexOf(dbActivity.getDateColumnKey()), keyDate);
                        StartDateButton.setText(displayDate);
                        break;
                    case R.id.report_end_date_button:
                        itemsSelected.set(ObjectKeys.indexOf(dbActivity.getDateColumnKey()) + 1, keyDate);         // add 1 for the end date
                        EndDateButton.setText(displayDate);
                        break;
                }
                String StartDate = itemsSelected.get(ObjectKeys.indexOf(dbActivity.getDateColumnKey()));
                String EndDate = itemsSelected.get(ObjectKeys.indexOf(dbActivity.getDateColumnKey()) + 1);
                if (StartDate.compareTo(EndDate) > 0) {     // swap if start date is larger than the end date
                    itemsSelected.set(ObjectKeys.indexOf(dbActivity.getDateColumnKey()), EndDate);
                    itemsSelected.set(ObjectKeys.indexOf(dbActivity.getDateColumnKey()) + 1, StartDate);
                }
                updateSpinnerListView(view);
            }
        }
    };

    public void onSelectDateButtonClicked(View view) {
        dateButtonID = view.getId();
        General.TouchTimeDialog(dialog, view);
    }

    public void updateSpinnerListView(View view) {
        boolean readAll = false;
        feedActivityList.clear();
        list_last_name.clear();
        list_first_name.clear();
        list_group.clear();
        list_company.clear();
        list_location.clear();
        list_job.clear();
        String [] Column = new String[NUMBER_ITEMS];
        String [] Compare = new String[NUMBER_ITEMS];
        String [] Values = new String[NUMBER_ITEMS];
        int i = 0, count = 0, total_hours = 0;
        boolean first = true;
        for (String s : itemsSelected) {
            if (!s.equals(noSelection)) {
                Column[count] = ObjectKeys.get(i);
                if (ObjectKeys.get(i).equals(dbActivity.getDateColumnKey())) {
                    if (first) {
                        Compare[count] = ">=";   // start date
                        first = false;
                    } else {
                        Compare[count] = "<=";   // end date
                    }
                } else {
                    Compare[count] = "=";
                }
                Values[count] = s;
                count++;
            }
            i++;
        }
        if (count > 0) {        // selected at least 1
            all_activity_lists = dbActivity.getActivityLists(Column, Compare, Values);
            // if (all_activity_lists.size() <= 0) return;
        } else {
            all_activity_lists = dbActivity.getAllActivityLists();
            readAll = true;
        }
        i = 0;
        while (i < all_activity_lists.size()) {
            map = new HashMap<String, String>();
            DailyActivityList Activity;
            Activity = all_activity_lists.get(i);
            map.put(getText(R.string.column_key_employee_id).toString(), String.valueOf(Activity.getEmployeeID()));
            map.put(getText(R.string.column_key_last_name).toString(), Activity.getLastName());
            list_last_name.add(Activity.getLastName());
            map.put(getText(R.string.column_key_first_name).toString(), Activity.getFirstName());
            list_first_name.add(Activity.getFirstName());
            map.put(getText(R.string.column_key_group_id).toString(), Activity.getWorkGroup());
            if (!Activity.getWorkGroup().isEmpty()) list_group.add(Activity.getWorkGroup());
            map.put(getText(R.string.column_key_company).toString(), Activity.getCompany());
            list_company.add(Activity.getCompany());
            map.put(getText(R.string.column_key_location).toString(), Activity.getLocation());
            list_location.add(Activity.getLocation());
            map.put(getText(R.string.column_key_job).toString(), Activity.getJob());
            list_job.add(Activity.getJob());
            map.put(getText(R.string.column_key_date).toString(), Activity.getDate());
            map.put(getText(R.string.column_key_timein).toString(), Activity.getTimeIn());
            if (readAll && Activity.getTimeOut().isEmpty() && CurrentDate.compareTo(Activity.getDate()) > 0) {
                // reading the entire years record the first time, time out is empty and current date > then punch in date
                Activity.setTimeOut(DEFAULT_CHECKOUT_TIME);
                long diff = General.MinuteDifference(Activity.getTimeIn(), Activity.getTimeOut());
                diff = diff > 0 && diff > Activity.Lunch ? diff-Activity.Lunch : 0;
                Activity.setHours(diff);
                dbActivity.updatePunchedInActivityList(Activity);
            }
            map.put(getText(R.string.column_key_timeout).toString(), Activity.getTimeOut());
            // convert from number of minutes to hh:mm
            map.put(getText(R.string.column_key_lunch).toString(), String.format("%2s:%2s", String.valueOf(Activity.getLunch() / 60 % 24),
                    String.valueOf(Activity.getLunch() % 60)).replace(' ', '0'));
            // convert from number of minutes to hh:mm
            map.put(getText(R.string.column_key_hours).toString(), String.format("%2s:%2s", String.valueOf(Activity.getHours() / 60 % 24),
                    String.valueOf(Activity.getHours() % 60)).replace(' ', '0'));
            total_hours += Activity.getHours();
            map.put(getText(R.string.column_key_supervisor).toString(), Activity.getSupervisor());
            map.put(getText(R.string.column_key_comments).toString(), Activity.getComments());
            feedActivityList.add(map);
            i++;
        };
        adapter_activity.notifyDataSetChanged();
        General.sortString(list_last_name);
        General.sortString(list_first_name);
        General.sortString(list_group);
        General.sortString(list_company);
        General.sortString(list_location);
        General.sortString(list_job);
        list_last_name = General.removeDuplicates(list_last_name);
        list_first_name = General.removeDuplicates(list_first_name);
        list_group = General.removeDuplicates(list_group);
        list_company = General.removeDuplicates(list_company);
        list_location = General.removeDuplicates(list_location);
        list_job = General.removeDuplicates(list_job);
        list_last_name.add(0, noSelection);
        list_first_name.add(0, noSelection);
        list_group.add(0, noSelection);
        list_company.add(0, noSelection);
        list_location.add(0, noSelection);
        list_job.add(0, noSelection);
        adapter_last_name.clear();              // these lines must stay here
        adapter_first_name.clear();
        adapter_group.clear();
        adapter_company.clear();
        adapter_location.clear();
        adapter_job.clear();
        for (i=0; i<list_last_name.size(); i++) adapter_last_name.add(list_last_name.get(i));
        for (i=0; i<list_first_name.size(); i++) adapter_first_name.add(list_first_name.get(i));
        for (i=0; i<list_group.size(); i++) adapter_group.add(list_group.get(i));
        for (i=0; i<list_company.size(); i++) adapter_company.add(list_company.get(i));
        for (i=0; i<list_location.size(); i++) adapter_location.add(list_location.get(i));
        for (i=0; i<list_job.size(); i++) adapter_job.add(list_job.get(i));
        adapter_last_name.notifyDataSetChanged();
        adapter_first_name.notifyDataSetChanged();
        adapter_group.notifyDataSetChanged();
        adapter_company.notifyDataSetChanged();
        adapter_location.notifyDataSetChanged();
        adapter_job.notifyDataSetChanged();

        TextView TotalHours = (TextView) findViewById(R.id.total_hours_view);
        TotalHours.setText(String.format("%s:%2s", String.valueOf(total_hours/60), String.valueOf(total_hours%60)).replace(' ', '0'));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_activity_menu, menu);
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
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
