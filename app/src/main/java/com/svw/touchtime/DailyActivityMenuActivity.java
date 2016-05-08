package com.svw.touchtime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DailyActivityMenuActivity extends ActionBarActivity {
    public ListView daily_activity_list_view;
    public EditText LunchMinuteEdit, SupervisorEdit, CommentsEdit;
    public TextView DailyActivityView;
    Button SetTimeIn, SetTimeOut, CompanySort, DateSort;
    public TouchTimeGeneralAdapter adapter_activity;
    ArrayList<HashMap<String, String>> feedActivityList;
    HashMap<String, String> map;
    DailyActivityList Activity;
    boolean sort_id_ascend = false;
    boolean sort_last_name_ascend = false;
    boolean sort_group_ascend = false;
    boolean sort_company_ascend = false;
    boolean sort_date_ascend = false;
    int itemPosition, Caller;
    int timeClickedID;
    Context context;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private DailyActivityDBWrapper dbActivity;
    private EmployeeGroupCompanyDBWrapper dbGroup;

    private TimePickerDialog mTimePicker;
    private DatePickerDialog mDatePicker;
    DateFormat dateFormat;
    String ActivityDateString;
    String TimeInString;
    String TimeOutString;
    static final int PICK_JOB_REQUEST = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_activity_menu);
        Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);

        daily_activity_list_view = (ListView) findViewById(R.id.daily_activity_list_view);
        LunchMinuteEdit = (EditText) findViewById(R.id.activity_lunch_text);
        SupervisorEdit = (EditText) findViewById(R.id.activity_supervisor_text);
        CommentsEdit = (EditText) findViewById(R.id.activity_comment_text);
        SetTimeIn = (Button) findViewById(R.id.set_time_in);
        SetTimeOut = (Button) findViewById(R.id.set_time_out);
        CompanySort = (Button) findViewById(R.id.sort_company);
        DateSort = (Button) findViewById(R.id.sort_date);
        DailyActivityView = (TextView) findViewById(R.id.daily_activity_view);
        feedActivityList = new ArrayList<HashMap<String, String>>();
        dbGroup = new EmployeeGroupCompanyDBWrapper(this);      // open database of the year and create if not exist
        Activity = new DailyActivityList();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ActivityDateString = dateFormat.format(Calendar.getInstance().getTime());

        retrieveActivityRecord();

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

        context = this;
        daily_activity_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, final View view, int position, long arg3) {
                final int item = position;
                view.animate().setDuration(500).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                if (Caller == R.id.caller_administrator) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                                    builder.setMessage(R.string.delete_daily_activity_message).setTitle(R.string.daily_activity_title);
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
                Toast toast = Toast.makeText(getApplicationContext(), String.valueOf(position + 1), Toast.LENGTH_SHORT);
                toast.show();
                view.animate().setDuration(30).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                HighlightListItem(itemPosition);
                                view.setAlpha(1);
                            }
                        });
            }
        });

        Calendar calendar = Calendar.getInstance();
        mTimePicker = new TimePickerDialog(new ContextThemeWrapper(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar),
                myTimeListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        mDatePicker = new DatePickerDialog(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar),
                myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (view.isShown()) {           // somehow onDateSet is called twice in higher version of Android, use this to avoid doing it the second time.
                if (timeClickedID == R.id.set_time_in) {
                    TimeInString = String.format("%4s-%2s-%2s", year, ++monthOfYear, dayOfMonth).replace(' ', '0');    // monthOfYear starts from 0
                } else if (timeClickedID == R.id.set_time_out) {
                    TimeOutString = String.format("%4s-%2s-%2s", year, ++monthOfYear, dayOfMonth).replace(' ', '0');    // monthOfYear starts from 0
                } else if (timeClickedID == R.id.daily_activity_select_date) {
                    ActivityDateString = String.format("%4s-%2s-%2s", year, ++monthOfYear, dayOfMonth).replace(' ', '0');    // monthOfYear starts from 0
                    retrieveActivityRecord();
                }
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            Calendar calendar = Calendar.getInstance();     // current date and time
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);
            DateFormat dtf = new SimpleDateFormat("HH:mm:ss");
            String TimeString = dtf.format(calendar.getTime());
            if (timeClickedID == R.id.set_time_in) {
                TimeInString = TimeInString + " " + TimeString;
                SetTimeIn.setText(TimeString);   // show time
            } else if (timeClickedID == R.id.set_time_out) {
                TimeOutString = TimeOutString + " " + TimeString;
                SetTimeOut.setText(TimeString);  // show time
            }
        }
    };

    public DailyActivityList getUniqueActivity(int item) {
        ArrayList<DailyActivityList> ActivityList;
        String [] Column = new String[3];
        String [] Compare = new String[3];
        String [] Values = new String[3];
        Column[0] = dbActivity.getIDColumnKey();
        Column[1] = dbActivity.getTimeInColumnKey();
        Column[2] = dbActivity.getTimeOutColumnKey();
        Values[0] = String.valueOf(feedActivityList.get(item).get(getText(R.string.column_key_employee_id).toString()));
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
        Values[0] = String.valueOf(feedActivityList.get(item).get(getText(R.string.column_key_employee_id).toString()));
        Values[1] = feedActivityList.get(item).get(getText(R.string.column_key_timein).toString());
        Values[2] = feedActivityList.get(item).get(getText(R.string.column_key_timeout).toString());
        dbActivity.updateActivityList(uniqueActivity, Column, Values);
     }

    public void onSelectDateButtonClicked(View view) {
        timeClickedID = view.getId();
        mDatePicker.show();
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
                                String.format("%2s:%2s", String.valueOf(Activity.Lunch / 60 % 24),
                                        String.valueOf(Activity.Lunch % 60)).replace(' ', '0'));
                        update = true;
                    }
                    break;
                case R.id.textViewTimeIn:
                    if (!SetTimeIn.getText().toString().equals(getText(R.string.column_view_timein)) && !TimeInString.isEmpty()) {
                        Activity.setTimeIn(TimeInString);
                        update = true;
                    }
                    break;
                case R.id.textViewTimeOut:
                    if (!SetTimeOut.getText().toString().equals(getText(R.string.column_view_timeout)) && !TimeOutString.isEmpty()) {
                        Activity.setTimeOut(TimeOutString);
                        update = true;
                    }
                    break;
            }
            if (update) {
                if (dbGroup.getEmployeeListStatus(Integer.parseInt(feedActivityList.get(itemPosition).get(getText(R.string.column_key_employee_id).toString()))) == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                    builder.setMessage(R.string.employee_already_punched_in_message).setTitle(R.string.daily_activity_title);
                    builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    General.TouchTimeDialog(dialog, view);
                } else {
                    if (Activity.getTimeIn().isEmpty() || Activity.getTimeOut().isEmpty()) {   // should always be already punched in, but could be changed by the supervisor
                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                        if (Activity.getTimeIn().isEmpty()) {                                  //
                            builder.setMessage(R.string.daily_activity_no_timein_message).setTitle(R.string.daily_activity_title);
                            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                        } else if (Activity.getTimeOut().isEmpty()) {
                            builder.setMessage(R.string.daily_activity_no_timeout_message).setTitle(R.string.daily_activity_title);
                            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                        }
                        AlertDialog dialog = builder.create();
                        General.TouchTimeDialog(dialog, view);
                    } else {
                        long diff = General.MinuteDifference(Activity.getTimeIn(), Activity.getTimeOut());
                        diff = (diff > 0 && diff >= Activity.getLunch()) ? diff - Activity.getLunch() : 0;
                        Activity.setHours(diff);
                        feedActivityList.get(itemPosition).put(getText(R.string.column_key_hours).toString(),
                                String.format("%2s:%2s", String.valueOf(Activity.Hours / 60),
                                        String.valueOf(Activity.Hours % 60)).replace(' ', '0'));
                    }
                    setUniqueActivity(Activity, itemPosition);          // must update activity first before changing feedActivityList
                    if (timeClickedID == R.id.set_time_in) {            // these two items are used for indexing purpose. They cannot be updated before activity is updated
                        feedActivityList.get(itemPosition).put(getText(R.string.column_key_timein).toString(), TimeInString);
                    } else if (timeClickedID == R.id.set_time_out) {
                        feedActivityList.get(itemPosition).put(getText(R.string.column_key_timeout).toString(), TimeOutString);
                    }
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

    public void onClearTimeClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        timeClickedID = view.getId();
        if (timeClickedID == R.id.clear_time_in) {
            TimeInString = "";
            SetTimeIn.setText(getText(R.string.column_view_timein).toString());
        } else if (timeClickedID == R.id.clear_time_out) {
            TimeOutString = "";
            SetTimeOut.setText(getText(R.string.column_view_timeout).toString());
        }
        if (dbGroup.getEmployeeListStatus(Integer.parseInt(feedActivityList.get(itemPosition).get(getText(R.string.column_key_employee_id).toString()))) == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
            builder.setMessage(R.string.employee_already_punched_in_message).setTitle(R.string.daily_activity_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        } else {
            Activity = getUniqueActivity(itemPosition);
            if (Activity == null) return;
            if (timeClickedID == R.id.clear_time_in) {
                Activity.setTimeIn(TimeInString);
            } else if (timeClickedID == R.id.clear_time_out) {
                Activity.setTimeOut(TimeOutString);
            }
            Activity.setHours(0);
            feedActivityList.get(itemPosition).put(getText(R.string.column_key_hours).toString(), "00:00");
            setUniqueActivity(Activity, itemPosition);              // update activity first
            if (timeClickedID == R.id.clear_time_in) {            // these two items are used for indexing purpose. They cannot be updated before activity is updated
                feedActivityList.get(itemPosition).put(getText(R.string.column_key_timein).toString(), TimeInString);
            } else if (timeClickedID == R.id.clear_time_out) {
                feedActivityList.get(itemPosition).put(getText(R.string.column_key_timeout).toString(), TimeOutString);
            }
            adapter_activity.setSelectedItem(itemPosition);
            adapter_activity.notifyDataSetChanged();
        }
    }

    public void onSetTimeClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        timeClickedID = view.getId();
        mTimePicker.show();         // show first but picked last
        mDatePicker.show();
    }

    public void onSelectCompanyClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        Intent intent = new Intent(this, CompanyJobLocationSelectionActivity.class);
        ArrayList<String> CompanyLocationJob = new ArrayList<>();
        CompanyLocationJob.add(getText(R.string.title_activity_daily_activity_menu).toString());        // caller
        // use the last selected one as default
        CompanyLocationJob.add(feedActivityList.get(itemPosition).get(getText(R.string.column_key_company).toString()));              // company
        CompanyLocationJob.add(feedActivityList.get(itemPosition).get(getText(R.string.column_key_location).toString()));             // location
        CompanyLocationJob.add(feedActivityList.get(itemPosition).get(getText(R.string.column_key_job).toString()));                  // job
        intent.putStringArrayListExtra("CompanyLocationJob", CompanyLocationJob);
        startActivityForResult(intent, PICK_JOB_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == PICK_JOB_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                ArrayList<String> CompanyLocationJob = new ArrayList<String>();
                CompanyLocationJob = data.getStringArrayListExtra("CompanyLocationJob");
                Activity = getUniqueActivity(itemPosition);
                if (Activity == null) return;
                feedActivityList.get(itemPosition).put(getText(R.string.column_key_company).toString(), CompanyLocationJob.get(1));              // company
                feedActivityList.get(itemPosition).put(getText(R.string.column_key_location).toString(), CompanyLocationJob.get(2));             // location
                feedActivityList.get(itemPosition).put(getText(R.string.column_key_job).toString(), CompanyLocationJob.get(3));                  // job
                Activity.setCompany(CompanyLocationJob.get(1));              // company
                Activity.setLocation(CompanyLocationJob.get(2));             // location
                Activity.setJob(CompanyLocationJob.get(3));                  // job
                setUniqueActivity(Activity, itemPosition);
                adapter_activity.setSelectedItem(itemPosition);
                adapter_activity.notifyDataSetChanged();
            }
        }
    }

    public void onSortIDButtonClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        String [] Items = new String [7];
        Items [0] = getText(R.string.column_key_employee_id).toString();
        Items [1] = getText(R.string.column_key_last_name).toString();
        Items [2] = getText(R.string.column_key_first_name).toString();
        Items [3] = getText(R.string.column_key_group_id).toString();
        Items [4] = getText(R.string.column_key_company).toString();
        Items [5] = getText(R.string.column_key_date).toString();
        Items [6] = getText(R.string.column_key_timein).toString();
        General.SortIntegerStringList(feedActivityList, Items, sort_id_ascend);
        sort_last_name_ascend = sort_group_ascend = sort_company_ascend = sort_date_ascend = false;
        sort_id_ascend = !sort_id_ascend;
        daily_activity_list_view.setAdapter(adapter_activity);
    }

    public void onSortLastNameButtonClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        String [] Items = new String [6];
        Items [0] = getText(R.string.column_key_last_name).toString();
        Items [1] = getText(R.string.column_key_first_name).toString();
        Items [2] = getText(R.string.column_key_group_id).toString();
        Items [3] = getText(R.string.column_key_company).toString();
        Items [4] = getText(R.string.column_key_date).toString();
        Items [5] = getText(R.string.column_key_timein).toString();
        General.SortStringList(feedActivityList, Items, sort_last_name_ascend);
        sort_id_ascend = sort_group_ascend = sort_company_ascend = sort_date_ascend = false;
        sort_last_name_ascend = !sort_last_name_ascend;
        daily_activity_list_view.setAdapter(adapter_activity);
    }

    public void onSortGroupButtonClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        String [] Items = new String [6];
        Items [0] = getText(R.string.column_key_group_id).toString();
        Items [1] = getText(R.string.column_key_last_name).toString();
        Items [2] = getText(R.string.column_key_first_name).toString();
        Items [3] = getText(R.string.column_key_company).toString();
        Items [4] = getText(R.string.column_key_date).toString();
        Items [5] = getText(R.string.column_key_timein).toString();
        General.SortStringList(feedActivityList, Items, sort_group_ascend);
        sort_id_ascend = sort_last_name_ascend = sort_company_ascend = sort_date_ascend = false;
        sort_group_ascend = !sort_group_ascend;
        daily_activity_list_view.setAdapter(adapter_activity);
    }

    public void onSortCompanyButtonClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        String [] Items = new String [6];
        Items [0] = getText(R.string.column_key_company).toString();
        Items [1] = getText(R.string.column_key_group_id).toString();
        Items [2] = getText(R.string.column_key_last_name).toString();
        Items [3] = getText(R.string.column_key_first_name).toString();
        Items [4] = getText(R.string.column_key_date).toString();
        Items [5] = getText(R.string.column_key_timein).toString();
        General.SortStringList(feedActivityList, Items, sort_company_ascend);
        sort_id_ascend = sort_last_name_ascend = sort_group_ascend = sort_date_ascend = false;
        sort_company_ascend = !sort_company_ascend;
        CompanySort.setText(sort_company_ascend ? getText(R.string.up).toString() : getText(R.string.down).toString());
        daily_activity_list_view.setAdapter(adapter_activity);
    }

    public void onSortDateButtonClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        String [] Items = new String [6];
        Items [0] = getText(R.string.column_key_date).toString();
        Items [1] = getText(R.string.column_key_timein).toString();
        Items [2] = getText(R.string.column_key_last_name).toString();
        Items [3] = getText(R.string.column_key_first_name).toString();
        Items [4] = getText(R.string.column_key_group_id).toString();
        Items [5] = getText(R.string.column_key_company).toString();
        General.SortStringList(feedActivityList, Items, sort_date_ascend);
        sort_id_ascend = sort_last_name_ascend = sort_group_ascend = sort_company_ascend = false;
        sort_date_ascend = !sort_date_ascend;
        DateSort.setText(sort_date_ascend ? getText(R.string.up).toString() : getText(R.string.down).toString());
        daily_activity_list_view.setAdapter(adapter_activity);
    }

    public void retrieveActivityRecord() {
        String[] employee_item = new String[20];
        int[] employee_id = new int[20];
        ArrayList<DailyActivityList> all_activity_lists;
        int year = Integer.parseInt(ActivityDateString.substring(0, 4));
        dbActivity = new DailyActivityDBWrapper(this, year);      // open database of the year and create if not exist
        // retrieve activity record only on the current date
        String[] Column = new String[5];
        String[] Compare = new String[5];
        String[] Values = new String[5];
        Column[0] = dbActivity.getDateColumnKey();
        Compare[0] = "=";
        Values[0] = ActivityDateString;
        all_activity_lists = dbActivity.getActivityLists(Column, Compare, Values);
        feedActivityList.clear();           // clear so the old record won't be kept

        employee_item[0] = getText(R.string.column_key_last_name).toString();
        employee_item[1] = getText(R.string.column_key_first_name).toString();
        employee_item[2] = getText(R.string.column_key_timein).toString();
        employee_item[3] = getText(R.string.column_key_timeout).toString();
        employee_item[4] = getText(R.string.column_key_hours).toString();
        employee_item[5] = getText(R.string.column_key_lunch).toString();
        employee_item[6] = getText(R.string.column_key_company).toString();
        employee_item[7] = getText(R.string.column_key_location).toString();
        employee_item[8] = getText(R.string.column_key_job).toString();
        employee_item[9] = getText(R.string.column_key_group_id).toString();
        employee_item[10] = getText(R.string.column_key_supervisor).toString();
        employee_item[11] = getText(R.string.column_key_comments).toString();
        employee_id[0] = R.id.textViewLastName;
        employee_id[1] = R.id.textViewFirstName;
        employee_id[2] = R.id.textViewTimeIn;
        employee_id[3] = R.id.textViewTimeOut;
        employee_id[4] = R.id.textViewHours;
        employee_id[5] = R.id.textViewLunch;
        employee_id[6] = R.id.textViewCompany;
        employee_id[7] = R.id.textViewLocation;
        employee_id[8] = R.id.textViewJob;
        employee_id[9] = R.id.textViewGroup;
        employee_id[10] = R.id.textViewSupervisor;
        employee_id[11] = R.id.textViewComments;

        int i = 0;
        if (all_activity_lists.size() > 0) {
            do {
                map = new HashMap<String, String>();
                map.put(getText(R.string.column_key_employee_id).toString(), String.valueOf(all_activity_lists.get(i).getEmployeeID()));  // need it for indexing but not display
                map.put(getText(R.string.column_key_last_name).toString(), all_activity_lists.get(i).getLastName());
                map.put(getText(R.string.column_key_first_name).toString(), all_activity_lists.get(i).getFirstName());
                map.put(getText(R.string.column_key_date).toString(), all_activity_lists.get(i).getDate());
                map.put(getText(R.string.column_key_timein).toString(), all_activity_lists.get(i).getTimeIn());
                map.put(getText(R.string.column_key_timeout).toString(), all_activity_lists.get(i).getTimeOut());
                // convert from number of minutes to hh:mm
                map.put(getText(R.string.column_key_hours).toString(), String.format("%2s:%2s", String.valueOf(all_activity_lists.get(i).getHours() / 60),
                        String.valueOf(all_activity_lists.get(i).getHours() % 60)).replace(' ', '0'));
                // convert from number of minutes to hh:mm
                map.put(getText(R.string.column_key_lunch).toString(), String.format("%2s:%2s", String.valueOf(all_activity_lists.get(i).getLunch() / 60),
                        String.valueOf(all_activity_lists.get(i).getLunch() % 60)).replace(' ', '0'));
                map.put(getText(R.string.column_key_company).toString(), all_activity_lists.get(i).getCompany());
                map.put(getText(R.string.column_key_location).toString(), all_activity_lists.get(i).getLocation());
                map.put(getText(R.string.column_key_job).toString(), all_activity_lists.get(i).getJob());
                map.put(getText(R.string.column_key_group_id).toString(), all_activity_lists.get(i).getWorkGroup());
                map.put(getText(R.string.column_key_supervisor).toString(), all_activity_lists.get(i).getSupervisor());
                map.put(getText(R.string.column_key_comments).toString(), all_activity_lists.get(i).getComments());
                feedActivityList.add(map);
            } while (++i < all_activity_lists.size());
            DailyActivityView.setText(getText(R.string.daily_activity_view_message) + " " + Values[0]);
        } else {
            DailyActivityView.setText(getText(R.string.daily_activity_no_message) + " " + Values[0]);
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setMessage(getText(R.string.no_daily_activity_message) + " for " + Values[0] + " !").setTitle(R.string.daily_activity_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // finish();
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, this.findViewById(android.R.id.content));
        }
        daily_activity_list_view.setItemsCanFocus(true);
        adapter_activity = new TouchTimeGeneralAdapter(this, feedActivityList, R.layout.daily_activity_view, employee_item, employee_id, 60);
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
            dbActivity.closeDB();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
