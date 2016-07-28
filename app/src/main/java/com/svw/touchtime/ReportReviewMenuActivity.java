package com.svw.touchtime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.svw.touchtime.R.layout.general_edit_text_view;

public class ReportReviewMenuActivity extends ActionBarActivity {
    private static final int   NUMBER_ITEMS = 9;
    private static final int   NUMBER_SPINNERS = 9;
    private static final String DEFAULT_CHECKOUT_TIME = "05:00:00 PM";
    public ListView daily_activity_list_view;
    ArrayList<DailyActivityList> all_activity_lists;
    private EditText LunchMinuteEdit, SupervisorEdit, CommentsEdit;
    private TextView report_view, report_hours;
    ArrayList<String> list_name;
    ArrayList<String> list_group;
    ArrayList<String> list_company;
    ArrayList<String> list_location;
    ArrayList<String> list_job;
    ArrayAdapter<String> adapter_name;
    ArrayAdapter<String> adapter_group;
    ArrayAdapter<String> adapter_company;
    ArrayAdapter<String> adapter_location;
    ArrayAdapter<String> adapter_job;
    ArrayList<String> ObjectKeys;
    ArrayList<String> itemsSelected;
    String  noSelection;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();

    Context context;
    Spinner NameSpinner;
    Spinner GroupSpinner;
    Spinner CompanySpinner;
    Spinner LocationSpinner;
    Spinner JobSpinner;
    private int dateButtonID;
    Button StartDateButton, EndDateButton;
    boolean sort_group_ascend = true;
    boolean sort_company_ascend = true;
    boolean sort_name_ascend = true;
    Button NameSort, CompanySort, GroupSort;
    private TouchTimeGeneralAdapter adapter_activity;
    ArrayList<HashMap<String, String>> feedActivityList;
    DailyActivityList Activity;
    HashMap<String, String> map;
    String[] activity_item = new String[11];
    int[] activity_id = new int[11];
    String CurrentDate, CurrentYear;
    private int lastClickId = -1;
    private int rowCount = -1;
    private DailyActivityDBWrapper dbActivity;
    private EmployeeGroupCompanyDBWrapper dbGroup;
    private DatePickerDialog mYearPicker;
    private DatePickerDialog mDatePicker;
    int itemPosition, Caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_review_menu);
        Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));

        daily_activity_list_view = (ListView) findViewById(R.id.daily_activity_list_view);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);

        dbGroup = new EmployeeGroupCompanyDBWrapper(this);      // open database of the year and create if not exist
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        CurrentDate = df.format(Calendar.getInstance().getTime());
        DateFormat yf = new SimpleDateFormat("yyyy");
        CurrentYear = yf.format(Calendar.getInstance().getTime());

        feedActivityList = new ArrayList<HashMap<String, String>>();
        report_view = (TextView) findViewById(R.id.report_view);
        report_hours = (TextView) findViewById(R.id.report_hours);
        LunchMinuteEdit = (EditText) findViewById(R.id.activity_lunch_text);
        SupervisorEdit = (EditText) findViewById(R.id.activity_supervisor_text);
        CommentsEdit = (EditText) findViewById(R.id.activity_comment_text);
        StartDateButton = (Button) findViewById(R.id.report_start_date_button);
        EndDateButton = (Button) findViewById(R.id.report_end_date_button);
        NameSort = (Button) findViewById(R.id.sort_name);
        CompanySort = (Button) findViewById(R.id.sort_company);
        GroupSort = (Button) findViewById(R.id.sort_group);
        context = this;

        activity_item[0] = getText(R.string.column_key_name).toString();
        activity_item[1] = getText(R.string.column_key_timein).toString();
        activity_item[2] = getText(R.string.column_key_timeout).toString();
        activity_item[3] = getText(R.string.column_key_lunch).toString();
        activity_item[4] = getText(R.string.column_key_hours).toString();
        activity_item[5] = getText(R.string.column_key_company).toString();
        activity_item[6] = getText(R.string.column_key_location).toString();
        activity_item[7] = getText(R.string.column_key_job).toString();
        activity_item[8] = getText(R.string.column_key_group_id).toString();
        activity_item[9] = getText(R.string.column_key_supervisor).toString();
        activity_item[10] = getText(R.string.column_key_comments).toString();

        activity_id[0] = R.id.textViewName;
        activity_id[1] = R.id.textViewTimeIn;
        activity_id[2] = R.id.textViewTimeOut;
        activity_id[3] = R.id.textViewLunch;
        activity_id[4] = R.id.textViewHours;
        activity_id[5] = R.id.textViewCompany;
        activity_id[6] = R.id.textViewLocation;
        activity_id[7] = R.id.textViewJob;
        activity_id[8] = R.id.textViewGroup;
        activity_id[9] = R.id.textViewSupervisor;
        activity_id[10] = R.id.textViewComments;
        daily_activity_list_view.setItemsCanFocus(true);
        adapter_activity = new TouchTimeGeneralAdapter(this, feedActivityList, R.layout.report_review_view, activity_item, activity_id, 60);
        daily_activity_list_view.setAdapter(adapter_activity);

        list_name = new ArrayList<String>();
        list_group = new ArrayList<String>();
        list_company = new ArrayList<String>();
        list_location = new ArrayList<String>();
        list_job = new ArrayList<String>();
        itemsSelected = new ArrayList<String>();
        ObjectKeys = new ArrayList<String>();

        noSelection = getText(R.string.column_key_no_selection).toString();

        // Follow the following order to prevent them from firing the first time
        NameSpinner = (Spinner) findViewById(R.id.name_spinner);
        CompanySpinner = (Spinner) findViewById(R.id.company_spinner);
        LocationSpinner = (Spinner) findViewById(R.id.location_spinner);
        JobSpinner = (Spinner) findViewById(R.id.job_spinner);
        GroupSpinner = (Spinner) findViewById(R.id.group_spinner);
        // need to have something so adapters will work
        list_name.add(noSelection);
        list_company.add(noSelection);
        list_location.add(noSelection);
        list_job.add(noSelection);
        list_group.add(noSelection);
        adapter_name = new ArrayAdapter<String>(context, general_edit_text_view, list_name);
        adapter_company = new ArrayAdapter<String>(context, general_edit_text_view, list_company);
        adapter_location = new ArrayAdapter<String>(context, general_edit_text_view, list_location);
        adapter_job = new ArrayAdapter<String>(context, general_edit_text_view, list_job);
        adapter_group = new ArrayAdapter<String>(context, general_edit_text_view, list_group);

        NameSpinner.setAdapter(adapter_name);
        CompanySpinner.setAdapter(adapter_company);
        LocationSpinner.setAdapter(adapter_location);
        JobSpinner.setAdapter(adapter_job);
        GroupSpinner.setAdapter(adapter_group);
        // fire once with animation set to false, it actually will avoid the first fire
        NameSpinner.setSelection(0, false);
        CompanySpinner.setSelection(0, false);
        LocationSpinner.setSelection(0, false);
        JobSpinner.setSelection(0, false);
        GroupSpinner.setSelection(0, false);

        NameSpinner.setOnItemSelectedListener(OnSpinnerCL);
        CompanySpinner.setOnItemSelectedListener(OnSpinnerCL);
        LocationSpinner.setOnItemSelectedListener(OnSpinnerCL);
        JobSpinner.setOnItemSelectedListener(OnSpinnerCL);
        GroupSpinner.setOnItemSelectedListener(OnSpinnerCL);

        selectYear(findViewById(android.R.id.content), Integer.parseInt(CurrentYear));


        LunchMinuteEdit.setOnTouchListener(new TextView.OnTouchListener() {         // set blank whenever touched
            public boolean onTouch(View v, MotionEvent event) {
                LunchMinuteEdit.setText("");
                return false;
            }
        });

        SupervisorEdit.setOnTouchListener(new TextView.OnTouchListener() {         // set blank whenever touched
            public boolean onTouch(View v, MotionEvent event) {
                SupervisorEdit.setText("");
                return false;
            }
        });

        CommentsEdit.setOnTouchListener(new TextView.OnTouchListener() {         // set blank whenever touched
            public boolean onTouch(View v, MotionEvent event) {
                CommentsEdit.setText("");
                return false;
            }
        });

        daily_activity_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, final View view, int position, long arg3) {
                final int item = position;
                view.animate().setDuration(1000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                if (Caller == R.id.caller_administrator) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                                    builder.setMessage(R.string.delete_daily_activity_message).setTitle(R.string.report_review_title);
                                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            int ID = 0;
                                            String G = feedActivityList.get(item).get(getText(R.string.column_key_employee_id).toString());
                                            if (G != null && !G.isEmpty()) ID = Integer.parseInt(G);
                                            if (ID > 0) {
                                                String TI = feedActivityList.get(item).get(getText(R.string.column_key_timein).toString());
                                                dbGroup.updateEmployeeListStatus(ID, 0);        // set it to punch out anyway
                                                dbActivity.deletePunchedInActivityList(ID, TI);
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
                                }
                                view.setAlpha(1);
                            }
                        });
                return false;
            }
        });

        daily_activity_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                itemPosition = position;
                Toast toast = Toast.makeText(getApplicationContext(), String.valueOf(position+1), Toast.LENGTH_SHORT);
                toast.show();
                view.animate().setDuration(30).alpha(0)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        int ID = 0;
                        String G = feedActivityList.get(itemPosition).get(getText(R.string.column_key_employee_id).toString());
                        if (G != null && !G.isEmpty()) ID = Integer.parseInt(G);
                        if (ID > 0) {
                            int oldPos;
                            /*
                            int Top = adapter_activity.getFirstVisiblePosition();
                            if (rowCount < 0) rowCount = adapter_activity.getChildCount() + 1;      // number of rows per screen
                            oldPos = ((Top / rowCount + 1) * rowCount + lastClickId-Top)%rowCount ;
                            int newPos = (itemPosition-adapter_activity.getFirstVisiblePosition());
                            if ((lastClickId != -1) && (oldPos != newPos)) {
                                adapter_activity.getChildAt(oldPos%(adapter_activity.getChildCount())).setBackgroundResource(R.color.svw_cyan);
                                // view.setBackgroundResource(R.color.svw_dark_gray);
                                adapter_activity.getChildAt(newPos).setBackgroundResource(R.color.svw_gray);
                            }
                            if (lastClickId == -1) adapter_activity.getChildAt(itemPosition%(adapter_activity.getChildCount())).setBackgroundResource(R.color.svw_gray);
                            // if (lastClickId == -1) view.setBackgroundResource(R.color.svw_dark_gray);
                            lastClickId = itemPosition;
                            */
                        }
                        HighlightListItem(itemPosition);
                        view.setAlpha(1);
                    }
                });
            }
        });

        Calendar calendar = Calendar.getInstance();
        mDatePicker = new DatePickerDialog(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar),
                myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        mYearPicker = new DatePickerDialog(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar),
                myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        {
            @Override
            protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                int month = getContext().getResources().getIdentifier("android:id/month", null, null);
                int day = getContext().getResources().getIdentifier("android:id/day", null, null);
                if(month != 0){
                    View monthPicker = findViewById(month);
                    if(monthPicker != null) monthPicker.setVisibility(View.GONE);
                }
                if(day != 0){
                    View dayPicker = findViewById(day);
                    if(dayPicker != null) dayPicker.setVisibility(View.GONE);
                }
            }
        };
    }

    public AdapterView.OnItemSelectedListener OnSpinnerCL = new AdapterView.OnItemSelectedListener() {
        // called only when a different item is selected
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (parent == NameSpinner) {
                     if (list_name.get(pos).equals(noSelection)) {
                        itemsSelected.set(ObjectKeys.indexOf(dbActivity.getLastNameColumnKey()), noSelection);
                        itemsSelected.set(ObjectKeys.indexOf(dbActivity.getFirstNameColumnKey()), noSelection);
                        itemsSelected.set(ObjectKeys.indexOf(dbActivity.getIDColumnKey()), noSelection);
                    } else {
                        String[] Name = list_name.get(pos).split(", ");
                        itemsSelected.set(ObjectKeys.indexOf(dbActivity.getLastNameColumnKey()), Name[0]);
                        itemsSelected.set(ObjectKeys.indexOf(dbActivity.getFirstNameColumnKey()), Name[1]);
                        itemsSelected.set(ObjectKeys.indexOf(dbActivity.getIDColumnKey()), Name[2]);
                    }
                    updateSpinnerListView(view);
            } else if (parent == GroupSpinner) {
                     itemsSelected.set(ObjectKeys.indexOf(dbActivity.getWorkGroupColumnKey()), list_group.get(pos));
                    updateSpinnerListView(view);
            } else if (parent == CompanySpinner) {
                    itemsSelected.set(ObjectKeys.indexOf(dbActivity.getCompanyColumnKey()), list_company.get(pos));
                    updateSpinnerListView(view);
            } else if (parent == LocationSpinner) {
                    itemsSelected.set(ObjectKeys.indexOf(dbActivity.getLocationColumnKey()), list_location.get(pos));
                    updateSpinnerListView(view);
            } else if (parent == JobSpinner) {
                    itemsSelected.set(ObjectKeys.indexOf(dbActivity.getJobColumnKey()), list_job.get(pos));
                    updateSpinnerListView(view);
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
                switch (dateButtonID) {
                    case R.id.report_start_date_button:
                        if (feedActivityList.size() > 0) {
                            itemsSelected.set(ObjectKeys.indexOf(dbActivity.getDateColumnKey()), keyDate);
                            StartDateButton.setText(keyDate);
                        }
                        break;
                    case R.id.report_end_date_button:
                        if (feedActivityList.size() > 0) {
                            itemsSelected.set(ObjectKeys.indexOf(dbActivity.getDateColumnKey())+1, keyDate);         // add 1 for the end date
                            EndDateButton.setText(keyDate);
                        }
                        break;
                    case R.id.report_review_select_year:
                        selectYear(view, year);
                        break;
                }
                if (dateButtonID == R.id.report_start_date_button || dateButtonID == R.id.report_end_date_button) {
                    String StartDate = itemsSelected.get(ObjectKeys.indexOf(dbActivity.getDateColumnKey()));
                    String EndDate = itemsSelected.get(ObjectKeys.indexOf(dbActivity.getDateColumnKey())+1);
                    if (!StartDate.equals(noSelection) && !EndDate.equals(noSelection) && StartDate.compareTo(EndDate) > 0) {     // swap if start date is larger than the end date
                        itemsSelected.set(ObjectKeys.indexOf(dbActivity.getDateColumnKey()), EndDate);
                        itemsSelected.set(ObjectKeys.indexOf(dbActivity.getDateColumnKey())+1, StartDate);
                        StartDateButton.setText(EndDate);
                        EndDateButton.setText(StartDate);
                    }
                    updateSpinnerListView(view);
                }
            }
        }
    };

    public void onSelectDateButtonClicked(View view) {
        dateButtonID = view.getId();
        if (dateButtonID == R.id.report_review_select_year) {
            if (Build.VERSION.SDK_INT >= 11) {
                mYearPicker.getDatePicker().setCalendarViewShown(false);
            }
            mYearPicker.show();
        } else {
            mDatePicker.show();
        }
    }

    public DailyActivityList getUniqueActivity(int item) {
        ArrayList<DailyActivityList> ActivityList;
        String [] Column = new String[3];
        String [] Compare = new String[3];
        String [] Values = new String[3];
        Column[0] = dbActivity.getIDColumnKey();
        Column[1] = dbActivity.getTimeInColumnKey();
        Column[2] = dbActivity.getTimeOutColumnKey();
        String[] Name = feedActivityList.get(item).get(getText(R.string.column_key_name).toString()).split(", ");       // name is stored as last, first, ID
        Values[0] = String.valueOf(Name[2]);            // getting the third component, which is the ID
        Values[1] = feedActivityList.get(item).get(getText(R.string.column_key_timein).toString());
        Values[2] = feedActivityList.get(item).get(getText(R.string.column_key_timeout).toString());
        Compare[0] = Compare[1] = Compare[2] = "=";
        ActivityList = dbActivity.getActivityLists(Column, Compare, Values);
        return (ActivityList.size() == 0) ? null : ActivityList.get(0);              // should only match and return one, so take the first one
    }

    public void setUniqueActivity(DailyActivityList uniqueActivity, int item) {
        String [] Column = new String[3];
        String [] Values = new String[3];
        Column[0] = dbActivity.getIDColumnKey();
        Column[1] = dbActivity.getTimeInColumnKey();
        Column[2] = dbActivity.getTimeOutColumnKey();
        String[] Name = feedActivityList.get(item).get(getText(R.string.column_key_name).toString()).split(", ");       // name is stored as last, first, ID
        Values[0] = String.valueOf(Name[2]);            // getting the third component, which is the ID
        Values[1] = feedActivityList.get(item).get(getText(R.string.column_key_timein).toString());
        Values[2] = feedActivityList.get(item).get(getText(R.string.column_key_timeout).toString());
        dbActivity.updateActivityList(uniqueActivity, Column, Values);
    }

    public void onColumnClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        itemPosition = daily_activity_list_view.getPositionForView((View) view.getParent());
        // is employee punched in?
        Activity = getUniqueActivity(itemPosition);
        if (Activity == null) return;
        boolean update = false;
        switch (view.getId()) {
            case R.id.textViewSupervisor:
                feedActivityList.get(itemPosition).put(getText(R.string.column_key_supervisor).toString(), SupervisorEdit.getText().toString());
                Activity.setSupervisor(SupervisorEdit.getText().toString());
                break;
            case R.id.textViewComments:
                feedActivityList.get(itemPosition).put(getText(R.string.column_key_comments).toString(), CommentsEdit.getText().toString());
                Activity.setComments(CommentsEdit.getText().toString());
                break;
            case R.id.textViewLunch:
                String Lunch = LunchMinuteEdit.getText().toString();
                if (!Lunch.isEmpty() && Long.parseLong(Lunch) >= 0) {
                    Activity.setLunch(Long.parseLong(Lunch));
                    feedActivityList.get(itemPosition).put(getText(R.string.column_key_lunch).toString(),
                            String.format("%2s:%2s", String.valueOf(Activity.Lunch / 60),
                                    String.valueOf(Activity.Lunch % 60)).replace(' ', '0'));
                    update = true;
                }
                break;
        }
        if (update) {
            if (feedActivityList.get(itemPosition).get(getText(R.string.column_key_timeout).toString()).isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                builder.setMessage(R.string.employee_already_punched_in_message).setTitle(R.string.report_review_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                General.TouchTimeDialog(dialog, view);
            } else {
                long diff = General.MinuteDifference(Activity.getTimeIn(), Activity.getTimeOut());
                diff = (diff > 0 && diff >= Activity.getLunch()) ? diff - Activity.getLunch() : 0;
                Activity.setHours(diff);
                feedActivityList.get(itemPosition).put(getText(R.string.column_key_hours).toString(),
                            String.format("%2s:%2s", String.valueOf(Activity.Hours / 60),
                                    String.valueOf(Activity.Hours % 60)).replace(' ', '0'));
                setUniqueActivity(Activity, itemPosition);          // must update activity first before changing feedActivityList
             }
        } else {
            setUniqueActivity(Activity, itemPosition);          // still allow to change supervisor and comment
        }
        adapter_activity.setSelectedItem(itemPosition);
        adapter_activity.notifyDataSetChanged();
    }

    private void HighlightListItem(int position) {
        adapter_activity.setSelectedItem(position);
        adapter_activity.notifyDataSetChanged();
    }

    public void updateSpinnerListView(View view) {
        boolean readAll = false;
        feedActivityList.clear();
        String [] Column = new String[NUMBER_ITEMS];
        String [] Compare = new String[NUMBER_ITEMS];
        String [] Values = new String[NUMBER_ITEMS];
        int i = 0, count = 0;
        long total_hours = 0;
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
            all_activity_lists = dbActivity.getActivityLists(Column, Compare, Values);
            // if (all_activity_lists.size() <= 0) return;
        } else {
            all_activity_lists = dbActivity.getAllActivityLists();
            readAll = true;
            // actually don't have to clear, but just in case something in the future
            list_name.clear();
            list_company.clear();
            list_location.clear();
            list_job.clear();
            list_group.clear();
        }
        HighlightListItem(0);
        i = 0;
        while (i < all_activity_lists.size()) {
            map = new HashMap<String, String>();
            DailyActivityList Activity;
            Activity = all_activity_lists.get(i);
            // converting 12 hour to 24 hour time
/*            try {
                DateFormat tf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
                DateFormat ttf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d1 = tf.parse(Activity.getTimeIn());
                Date d2 = tf.parse(Activity.getTimeOut());
                dbActivity.deletePunchedInActivityList(Activity.getEmployeeID(), Activity.getTimeIn());
                Activity.setTimeIn(ttf.format(d1));
                Activity.setTimeOut(ttf.format(d2));
                dbActivity.createActivityList(Activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
*/
            String Name;
            Name = Activity.getLastName() + ", " + Activity.getFirstName() + ", " + String.valueOf(Activity.getEmployeeID());
            map.put(getText(R.string.column_key_name).toString(), Name);
            if (readAll) list_name.add(Name);
            map.put(getText(R.string.column_key_date).toString(), all_activity_lists.get(i).getDate());
            map.put(getText(R.string.column_key_timein).toString(), Activity.getTimeIn());
            map.put(getText(R.string.column_key_timeout).toString(), Activity.getTimeOut());
            // convert from number of minutes to hh:mm
            map.put(getText(R.string.column_key_lunch).toString(), String.format("%2s:%2s", String.valueOf(Activity.getLunch() / 60),
                    String.valueOf(Activity.getLunch() % 60)).replace(' ', '0'));
            // convert from number of minutes to hh:mm
            map.put(getText(R.string.column_key_hours).toString(), String.format("%2s:%2s", String.valueOf(Activity.getHours() / 60),
                    String.valueOf(Activity.getHours() % 60)).replace(' ', '0'));
            total_hours = total_hours + Activity.getHours();
            map.put(getText(R.string.column_key_company).toString(), Activity.getCompany());
            if (readAll) list_company.add(Activity.getCompany());
            map.put(getText(R.string.column_key_location).toString(), Activity.getLocation());
            if (readAll) list_location.add(Activity.getLocation());
            map.put(getText(R.string.column_key_job).toString(), Activity.getJob());
            if (readAll) list_job.add(Activity.getJob());
            map.put(getText(R.string.column_key_group_id).toString(), Activity.getWorkGroup());
            if (readAll && !Activity.getWorkGroup().isEmpty()) list_group.add(Activity.getWorkGroup());
            // Read the whole list the first time, check if they are all punched out
            if (readAll && Activity.getTimeOut().isEmpty() && CurrentDate.compareTo(Activity.getDate()) > 0) {
                // reading the entire years record the first time, time out is empty and current date > punch in date, probably forgot to punch out
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                builder.setMessage(getText(R.string.employee_must_punch_out_message).toString() + " Employee: " + Name).setTitle(R.string.report_review_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                General.TouchTimeDialog(dialog, view);
            }
            map.put(getText(R.string.column_key_supervisor).toString(), Activity.getSupervisor());
            map.put(getText(R.string.column_key_comments).toString(), Activity.getComments());
            feedActivityList.add(map);
            i++;
        };
        String H = getText(R.string.report_total_hours).toString() + " " + String.format("%s:%2s", String.valueOf(total_hours / 60), String.valueOf(total_hours % 60)).replace(' ', '0');
        report_hours.setText(H);

        if (readAll) {
            General.sortString(list_name);
            General.sortString(list_company);
            General.sortString(list_location);
            General.sortString(list_job);
            General.sortString(list_group);
            list_name = General.removeDuplicates(list_name);
            list_company = General.removeDuplicates(list_company);
            list_location = General.removeDuplicates(list_location);
            list_job = General.removeDuplicates(list_job);
            list_group = General.removeDuplicates(list_group);
            list_name.add(0, noSelection);
            list_company.add(0, noSelection);
            list_location.add(0, noSelection);
            list_job.add(0, noSelection);
            list_group.add(0, noSelection);
            adapter_name.notifyDataSetChanged();
            adapter_group.notifyDataSetChanged();
            adapter_company.notifyDataSetChanged();
            adapter_location.notifyDataSetChanged();
            adapter_job.notifyDataSetChanged();
        }
    }

    public void selectYear(View view, int year) {
        dbActivity = new DailyActivityDBWrapper(context, year);
        if (dbActivity.getActivityListCount() == 0) {
            context.deleteDatabase(dbActivity.getDatabaseName());       // it is empty, might as well delete it
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
            builder.setMessage(getText(R.string.daily_activity_no_message).toString() + " " + String.valueOf(year)).setTitle(R.string.report_review_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    feedActivityList.clear();
                    adapter_activity.notifyDataSetChanged();
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        } else {
            ObjectKeys.clear();
            itemsSelected.clear();
            ObjectKeys.add(dbActivity.getIDColumnKey());
            ObjectKeys.add(dbActivity.getLastNameColumnKey());
            ObjectKeys.add(dbActivity.getFirstNameColumnKey());
            ObjectKeys.add(dbActivity.getDateColumnKey());
            ObjectKeys.add(dbActivity.getDateColumnKey());
            ObjectKeys.add(dbActivity.getCompanyColumnKey());
            ObjectKeys.add(dbActivity.getLocationColumnKey());
            ObjectKeys.add(dbActivity.getJobColumnKey());
            ObjectKeys.add(dbActivity.getWorkGroupColumnKey());
            for (int i=0; i<ObjectKeys.size(); i++) itemsSelected.add(noSelection);
            updateSpinnerListView(view);
            report_view.setText(getText(R.string.report_review_message) + " " + year);
        }
    }

    public void onSortNameButtonClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        String [] Items = new String [5];
        Items [0] = getText(R.string.column_key_name).toString();
        Items [1] = getText(R.string.column_key_group_id).toString();
        Items [2] = getText(R.string.column_key_company).toString();
        Items [3] = getText(R.string.column_key_date).toString();
        Items [4] = getText(R.string.column_key_timein).toString();
        General.SortStringList(feedActivityList, Items, sort_name_ascend);
        sort_group_ascend = sort_company_ascend = false;
        sort_name_ascend = !sort_name_ascend;
        NameSort.setText(sort_name_ascend ? getText(R.string.up).toString() : getText(R.string.down).toString());
        daily_activity_list_view.setAdapter(adapter_activity);
    }

    public void onSortGroupButtonClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        String [] Items = new String [5];
        Items [0] = getText(R.string.column_key_group_id).toString();
        Items [1] = getText(R.string.column_key_name).toString();
        Items [2] = getText(R.string.column_key_company).toString();
        Items [3] = getText(R.string.column_key_date).toString();
        Items [4] = getText(R.string.column_key_timein).toString();
        General.SortStringList(feedActivityList, Items, sort_group_ascend);
        sort_name_ascend = sort_company_ascend = false;
        sort_group_ascend = !sort_group_ascend;
        GroupSort.setText(sort_group_ascend ? getText(R.string.up).toString() : getText(R.string.down).toString());
        daily_activity_list_view.setAdapter(adapter_activity);
    }

    public void onSortCompanyButtonClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        String [] Items = new String [5];
        Items [0] = getText(R.string.column_key_company).toString();
        Items [1] = getText(R.string.column_key_name).toString();
        Items [2] = getText(R.string.column_key_date).toString();
        Items [3] = getText(R.string.column_key_timein).toString();
        Items [4] = getText(R.string.column_key_group_id).toString();
        General.SortStringList(feedActivityList, Items, sort_company_ascend);
        sort_name_ascend = sort_group_ascend = false;
        sort_company_ascend = !sort_company_ascend;
        CompanySort.setText(sort_company_ascend ? getText(R.string.up).toString() : getText(R.string.down).toString());
        daily_activity_list_view.setAdapter(adapter_activity);
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
