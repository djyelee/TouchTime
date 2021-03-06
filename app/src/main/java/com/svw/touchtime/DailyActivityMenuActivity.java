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
import java.util.Locale;

public class DailyActivityMenuActivity extends ActionBarActivity {
    ArrayList<DailyActivityList> all_activity_lists;
    public ListView daily_activity_list_view;
    public EditText LunchMinuteEdit, SupervisorEdit, CommentsEdit;
    public TextView DailyActivityView;
    Button SetTimeIn, SetTimeOut, CompanySort, DateSort, SummaryButton;
    public TouchTimeGeneralAdapter adapter_activity;
    ArrayList<HashMap<String, String>> feedActivityList;
    HashMap<String, String> map;
    DailyActivityList Activity;
    boolean sort_id_ascend = false;
    boolean sort_last_name_ascend = false;
    boolean sort_group_ascend = false;
    boolean sort_company_ascend = false;
    boolean sort_date_ascend = false;
    boolean select_date = false;
    int itemPosition, Caller;
    int timeClickedID;
    Context context;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private DailyActivityDBWrapper dbActivity;
    private EmployeeGroupCompanyDBWrapper dbGroup;

    private TimePickerDialog mTimePicker;
    private DatePickerDialog mDatePicker;
    DateFormat dtFormat, dateFormat;
    String ActivityDateString;
    String TimeInString = "";
    String TimeOutString = "";
    int display_flag = 0;               // set to detail as default
    private static final int SELECT[] = {R.string.button_detail, R.string.button_summary};
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
        SummaryButton = (Button) findViewById(R.id.daily_activity_summary);
        DailyActivityView = (TextView) findViewById(R.id.daily_activity_view);
        feedActivityList = new ArrayList<HashMap<String, String>>();
        dbGroup = new EmployeeGroupCompanyDBWrapper(this);      // open database of the year and create if not exist
        Activity = new DailyActivityList();
        dateFormat = new SimpleDateFormat(getText(R.string.date_YMD_format).toString(), Locale.US);
        dtFormat = new SimpleDateFormat(getText(R.string.date_time_format).toString(), Locale.US);
        ActivityDateString = dateFormat.format(Calendar.getInstance().getTime());

        retrieveActivityRecord(findViewById(android.R.id.content));

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
                                if (Caller == R.id.caller_administrator && SELECT[display_flag] == R.string.button_detail) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                                    builder.setMessage(R.string.delete_daily_activity_message).setTitle(R.string.daily_activity_title);
                                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            int ID = 0;
                                            String G = feedActivityList.get(item).get(getText(R.string.column_key_employee_id).toString());
                                            if (G != null && !G.isEmpty()) ID = Integer.parseInt(G);
                                            if (ID > 0) {
                                                String TI = feedActivityList.get(item).get(getText(R.string.column_key_timein).toString());
                                                dbGroup.updateEmployeeStatus(ID, 0);        // set it to punch out anyway
                                                dbActivity.deletePunchedInActivityList(ID, General.convertMDYTtoYMDT(TI));
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
        mTimePicker = new TimePickerDialog(new ContextThemeWrapper(context, R.style.TouchTimeCalendar),
                myTimeListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        mDatePicker = new DatePickerDialog(new ContextThemeWrapper(this, R.style.TouchTimeCalendar),
                myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (view.isShown()) {           // somehow onDateSet is called twice in higher version of Android, use this to avoid doing it the second time.
                String newDate = String.format("%4s/%2s/%2s", year, ++monthOfYear, dayOfMonth).replace(' ', '0');    // monthOfYear starts from 0
                ActivityDateString = newDate;
                if (select_date) {
                    retrieveActivityRecord(view);
                    select_date = false;
                } else if (timeClickedID == R.id.set_time_in) {
                    TimeInString = newDate;    // monthOfYear starts from 0
                } else if (timeClickedID == R.id.set_time_out) {
                    TimeOutString = newDate;    // monthOfYear starts from 0
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
            DateFormat dtf = new SimpleDateFormat(getText(R.string.time_format).toString());
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
        String [] Column = new String[6];
        String [] Compare = new String[6];
        String [] Values = new String[6];
        Column[0] = dbActivity.getIDColumnKey();
        Column[1] = dbActivity.getTimeInColumnKey();
        Column[2] = dbActivity.getTimeOutColumnKey();
        Column[3] = dbActivity.getCompanyColumnKey();
        Column[4] = dbActivity.getLocationColumnKey();
        Column[5] = dbActivity.getJobColumnKey();
        Values[0] = String.valueOf(feedActivityList.get(item).get(getText(R.string.column_key_employee_id).toString()));
        Values[1] = General.convertMDYTtoYMDT(feedActivityList.get(item).get(getText(R.string.column_key_timein).toString()));
        Values[2] = General.convertMDYTtoYMDT(feedActivityList.get(item).get(getText(R.string.column_key_timeout).toString()));
        Values[3] = feedActivityList.get(item).get(getText(R.string.column_key_company).toString());
        Values[4] = feedActivityList.get(item).get(getText(R.string.column_key_location).toString());
        Values[5] = feedActivityList.get(item).get(getText(R.string.column_key_job).toString());
        Compare[0] = Compare[1] = Compare[2] = Compare[3] = Compare[4] = Compare[5] = "=";
        ActivityList = dbActivity.getActivityLists(Column, Compare, Values);
        return (ActivityList.size() == 0) ? null : ActivityList.get(0);              // should only match and return one, so take the first one
    }

    public void setUniqueActivity(DailyActivityList uniqueActivity, int item) {
        String [] Column = new String[6];
        String [] Values = new String[6];
        Column[0] = dbActivity.getIDColumnKey();
        Column[1] = dbActivity.getTimeInColumnKey();
        Column[2] = dbActivity.getTimeOutColumnKey();
        Column[3] = dbActivity.getCompanyColumnKey();
        Column[4] = dbActivity.getLocationColumnKey();
        Column[5] = dbActivity.getJobColumnKey();
        Values[0] = String.valueOf(feedActivityList.get(item).get(getText(R.string.column_key_employee_id).toString()));
        Values[1] = General.convertMDYTtoYMDT(feedActivityList.get(item).get(getText(R.string.column_key_timein).toString()));
        Values[2] = General.convertMDYTtoYMDT(feedActivityList.get(item).get(getText(R.string.column_key_timeout).toString()));
        Values[3] = feedActivityList.get(item).get(getText(R.string.column_key_company).toString());
        Values[4] = feedActivityList.get(item).get(getText(R.string.column_key_location).toString());
        Values[5] = feedActivityList.get(item).get(getText(R.string.column_key_job).toString());
        dbActivity.updateActivityList(uniqueActivity, Column, Values);
     }

    public void updateUniqueActivity(DailyActivityList uniqueActivity, int item) {
        String [] Column = new String[6];
        String [] Values = new String[6];
        Column[0] = dbActivity.getIDColumnKey();
        Column[1] = dbActivity.getTimeInColumnKey();
        Column[2] = dbActivity.getTimeOutColumnKey();
        Column[3] = dbActivity.getCompanyColumnKey();
        Column[4] = dbActivity.getLocationColumnKey();
        Column[5] = dbActivity.getJobColumnKey();
        Values[0] = String.valueOf(feedActivityList.get(item).get(getText(R.string.column_key_employee_id).toString()));
        Values[1] = General.convertMDYTtoYMDT(feedActivityList.get(item).get(getText(R.string.column_key_timein).toString()));
        Values[2] = General.convertMDYTtoYMDT(feedActivityList.get(item).get(getText(R.string.column_key_timeout).toString()));
        Values[3] = feedActivityList.get(item).get(getText(R.string.column_key_company).toString());
        Values[4] = feedActivityList.get(item).get(getText(R.string.column_key_location).toString());
        Values[5] = feedActivityList.get(item).get(getText(R.string.column_key_job).toString());
        dbActivity.updateActivityList(uniqueActivity, Column, Values);
    }

    public void onSelectDateButtonClicked(View view) {
        select_date = true;
        mDatePicker.show();
    }

    public void onSelectSummaryButtonClicked(View view) {
        retrieveActivityRecord(view);           // feedActivityList is already sorted by last name.
        if (feedActivityList.size() <= 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
            builder.setMessage(getText(R.string.no_daily_activity_message).toString());
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    feedActivityList.clear();
                    adapter_activity.notifyDataSetChanged();
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        } else {
            display_flag = 1 - display_flag;
            SummaryButton.setText(getText(SELECT[1 - display_flag]).toString());
            if (SELECT[display_flag] == R.string.button_summary) {
                sort_id_ascend = true;
                onSortIDButtonClicked(view);    // sort by ID in ascending order
                ArrayList<HashMap<String, String>> newActivityList = new ArrayList<HashMap<String, String>>();
                int i, j, begin, end, totallunch, totalhours;
                String ID, In, Out;
                for (i = 0; i < feedActivityList.size(); i++) {      // list is already sorted by ID
                    ID = feedActivityList.get(i).get(getText(R.string.column_key_employee_id).toString());
                    In = feedActivityList.get(i).get(getText(R.string.column_key_timein).toString());
                    Out = feedActivityList.get(i).get(getText(R.string.column_key_timeout).toString());
                    begin = end = i;
                    totallunch = totalhours = 0;
                    for (j=i; j<feedActivityList.size(); j++) {
                        if (ID.equals(feedActivityList.get(j).get(getText(R.string.column_key_employee_id).toString()))) {
                            In = In.compareTo(feedActivityList.get(j).get(getText(R.string.column_key_timein).toString())) > 0
                                    ? feedActivityList.get(j).get(getText(R.string.column_key_timein).toString()) : In;
                            if (feedActivityList.get(j).get(getText(R.string.column_key_timeout).toString()).isEmpty() || Out.isEmpty()) {
                                Out = "";
                            } else {
                                Out = Out.compareTo(feedActivityList.get(j).get(getText(R.string.column_key_timeout).toString())) < 0
                                        ? feedActivityList.get(j).get(getText(R.string.column_key_timeout).toString()) : Out;
                            }
                            String ItemHours[] = feedActivityList.get(j).get(getText(R.string.column_key_hours).toString()).split(":");
                            totalhours += Integer.parseInt(ItemHours[0]) * 60 +  Integer.parseInt(ItemHours[1]);
                            String ItemLunch[] = feedActivityList.get(j).get(getText(R.string.column_key_lunch).toString()).split(":");
                            totallunch += Integer.parseInt(ItemLunch[0]) * 60 +  Integer.parseInt(ItemLunch[1]);
                            end = j;
                        } else {       // no more activity with the same iD
                            break;
                        }
                    }
                    feedActivityList.get(begin).put(getText(R.string.column_key_timein).toString(), In);
                    feedActivityList.get(begin).put(getText(R.string.column_key_timeout).toString(), Out);
                    feedActivityList.get(begin).put(getText(R.string.column_key_hours).toString(), String.format("%2s:%2s", String.valueOf(totalhours / 60),
                            String.valueOf(totalhours % 60)).replace(' ', '0'));
                    feedActivityList.get(begin).put(getText(R.string.column_key_lunch).toString(), String.format("%2s:%2s", String.valueOf(totallunch / 60),
                            String.valueOf(totallunch % 60)).replace(' ', '0'));
                    newActivityList.add(feedActivityList.get(begin));
                    i = end;
                }
                feedActivityList.clear();
                for (i = 0; i < newActivityList.size(); i++) feedActivityList.add(newActivityList.get(i));  // copy new list back to feed list
                sort_last_name_ascend = true;
                onSortLastNameButtonClicked(view);
             }
            adapter_activity.notifyDataSetChanged();
        }
    }

    public void onColumnClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0 || SELECT[display_flag] == R.string.button_summary) return;
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
                        Activity.setDate(ActivityDateString);                     // store time in date for indexing purpose
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
                if (dateFormat.format(Calendar.getInstance().getTime()).equals(feedActivityList.get(itemPosition).get(getText(R.string.column_key_date).toString())) &&
                        dbGroup.getEmployeeListStatus(Integer.parseInt(feedActivityList.get(itemPosition).get(getText(R.string.column_key_employee_id).toString()))) == 1) {
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
                        long diff = General.MinuteDifference(dtFormat, Activity.getTimeIn(), Activity.getTimeOut());
                        diff = (diff >= Activity.getLunch()) ? diff - Activity.getLunch() : 0;
                        Activity.setHours(diff);
                        feedActivityList.get(itemPosition).put(getText(R.string.column_key_hours).toString(),
                                String.format("%2s:%2s", String.valueOf(Activity.Hours / 60),
                                        String.valueOf(Activity.Hours % 60)).replace(' ', '0'));
                    }
                    setUniqueActivity(Activity, itemPosition);          // must update activity first before changing feedActivityList
                    if (view.getId() == R.id.textViewTimeIn && !TimeInString.isEmpty()) {            // these two items are used for indexing purpose. They cannot be updated before activity is updated
                        feedActivityList.get(itemPosition).put(getText(R.string.column_key_timein).toString(), General.convertYMDTtoMDYT(TimeInString));
                    }
                    if (view.getId() == R.id.textViewTimeOut && !TimeOutString.isEmpty()) {
                        feedActivityList.get(itemPosition).put(getText(R.string.column_key_timeout).toString(), General.convertYMDTtoMDYT(TimeOutString));
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
        if (feedActivityList.size() == 0 || itemPosition < 0 || SELECT[display_flag] == R.string.button_summary) return;
        int timeClearID = view.getId();
        if (timeClearID == R.id.clear_time_in) {
            TimeInString = "";
            SetTimeIn.setText(getText(R.string.column_view_timein).toString());
        } else if (timeClearID == R.id.clear_time_out) {
            TimeOutString = "";
            SetTimeOut.setText(getText(R.string.column_view_timeout).toString());
        }
        if (dateFormat.format(Calendar.getInstance().getTime()).equals(feedActivityList.get(itemPosition).get(getText(R.string.column_key_date).toString())) &&
                dbGroup.getEmployeeListStatus(Integer.parseInt(feedActivityList.get(itemPosition).get(getText(R.string.column_key_employee_id).toString()))) == 1) {
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
            if (timeClearID == R.id.clear_time_in) {
                Activity.setTimeIn(TimeInString);
            } else if (timeClearID == R.id.clear_time_out) {
                Activity.setTimeOut(TimeOutString);
            }
            Activity.setHours(0);
            feedActivityList.get(itemPosition).put(getText(R.string.column_key_hours).toString(), "00:00");
            setUniqueActivity(Activity, itemPosition);              // update activity first
            if (timeClearID == R.id.clear_time_in) {            // these two items are used for indexing purpose. They cannot be updated before activity is updated
                feedActivityList.get(itemPosition).put(getText(R.string.column_key_timein).toString(), General.convertYMDTtoMDYT(TimeInString));
            } else if (timeClearID == R.id.clear_time_out) {
                feedActivityList.get(itemPosition).put(getText(R.string.column_key_timeout).toString(), General.convertYMDTtoMDYT(TimeOutString));
            }
            adapter_activity.setSelectedItem(itemPosition);
            adapter_activity.notifyDataSetChanged();
        }
    }

    public void onSetTimeClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0 || SELECT[display_flag] == R.string.button_summary) return;
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
                Activity.setCompany(CompanyLocationJob.get(1));              // company
                Activity.setLocation(CompanyLocationJob.get(2));             // location
                Activity.setJob(CompanyLocationJob.get(3));                  // job
                setUniqueActivity(Activity, itemPosition);
                feedActivityList.get(itemPosition).put(getText(R.string.column_key_company).toString(), CompanyLocationJob.get(1));              // company
                feedActivityList.get(itemPosition).put(getText(R.string.column_key_location).toString(), CompanyLocationJob.get(2));             // location
                feedActivityList.get(itemPosition).put(getText(R.string.column_key_job).toString(), CompanyLocationJob.get(3));                  // job
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
        Items [3] = getText(R.string.column_key_date).toString();
        Items [4] = getText(R.string.column_key_timein).toString();
        Items [5] = getText(R.string.column_key_group_id).toString();
        Items [6] = getText(R.string.column_key_company).toString();
        General.SortStringList(feedActivityList, Items, sort_id_ascend);
        sort_last_name_ascend = sort_group_ascend = sort_company_ascend = sort_date_ascend = false;
        sort_id_ascend = !sort_id_ascend;
        daily_activity_list_view.setAdapter(adapter_activity);
        daily_activity_list_view.smoothScrollToPosition(itemPosition);
    }

    public void onSortLastNameButtonClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        String [] Items = new String [6];
        Items [0] = getText(R.string.column_key_last_name).toString();
        Items [1] = getText(R.string.column_key_first_name).toString();
        Items [2] = getText(R.string.column_key_date).toString();
        Items [3] = getText(R.string.column_key_timein).toString();
        Items [4] = getText(R.string.column_key_group_id).toString();
        Items [5] = getText(R.string.column_key_company).toString();
        General.SortStringList(feedActivityList, Items, sort_last_name_ascend);
        sort_id_ascend = sort_group_ascend = sort_company_ascend = sort_date_ascend = false;
        sort_last_name_ascend = !sort_last_name_ascend;
        daily_activity_list_view.setAdapter(adapter_activity);
        daily_activity_list_view.smoothScrollToPosition(itemPosition);
    }

    public void onSortGroupButtonClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        String [] Items = new String [6];
        Items [0] = getText(R.string.column_key_group_id).toString();
        Items [1] = getText(R.string.column_key_last_name).toString();
        Items [2] = getText(R.string.column_key_first_name).toString();
        Items [3] = getText(R.string.column_key_date).toString();
        Items [4] = getText(R.string.column_key_timein).toString();
        Items [5] = getText(R.string.column_key_company).toString();
        General.SortStringList(feedActivityList, Items, sort_group_ascend);
        sort_id_ascend = sort_last_name_ascend = sort_company_ascend = sort_date_ascend = false;
        sort_group_ascend = !sort_group_ascend;
        daily_activity_list_view.setAdapter(adapter_activity);
        daily_activity_list_view.smoothScrollToPosition(itemPosition);
    }

    public void onSortCompanyButtonClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        String [] Items = new String [6];
        Items [0] = getText(R.string.column_key_company).toString();
        Items [1] = getText(R.string.column_key_last_name).toString();
        Items [2] = getText(R.string.column_key_first_name).toString();
        Items [3] = getText(R.string.column_key_date).toString();
        Items [4] = getText(R.string.column_key_timein).toString();
        Items [5] = getText(R.string.column_key_group_id).toString();
        General.SortStringList(feedActivityList, Items, sort_company_ascend);
        sort_id_ascend = sort_last_name_ascend = sort_group_ascend = sort_date_ascend = false;
        sort_company_ascend = !sort_company_ascend;
        CompanySort.setText(sort_company_ascend ? getText(R.string.up).toString() : getText(R.string.down).toString());
        daily_activity_list_view.setAdapter(adapter_activity);
        daily_activity_list_view.smoothScrollToPosition(itemPosition);
    }

    public void onSortDateButtonClicked(View view) {
        if (feedActivityList.size() == 0 || itemPosition < 0) return;
        String [] Items = new String [6];
        Items [0] = getText(R.string.column_key_date).toString();
        Items [1] = getText(R.string.column_key_timein).toString();
        Items [2] = getText(R.string.column_key_last_name).toString();
        Items [3] = getText(R.string.column_key_first_name).toString();
        Items [4] = getText(R.string.column_key_company).toString();
        Items [5] = getText(R.string.column_key_group_id).toString();
        General.SortStringList(feedActivityList, Items, sort_date_ascend);
        sort_id_ascend = sort_last_name_ascend = sort_group_ascend = sort_company_ascend = false;
        sort_date_ascend = !sort_date_ascend;
        DateSort.setText(sort_date_ascend ? getText(R.string.up).toString() : getText(R.string.down).toString());
        daily_activity_list_view.setAdapter(adapter_activity);
        daily_activity_list_view.smoothScrollToPosition(itemPosition);
    }

    public void retrieveActivityRecord(View view) {
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

        String employee_item[] = {
                getText(R.string.column_key_last_name).toString(), getText(R.string.column_key_first_name).toString(),
                getText(R.string.column_key_timein).toString(), getText(R.string.column_key_timeout).toString(),
                getText(R.string.column_key_hours).toString(), getText(R.string.column_key_lunch).toString(),
                getText(R.string.column_key_company).toString(), getText(R.string.column_key_location).toString(),
                getText(R.string.column_key_job).toString(), getText(R.string.column_key_group_id).toString(),
                getText(R.string.column_key_supervisor).toString(), getText(R.string.column_key_comments).toString()};
        int employee_id[] = {
                R.id.textViewLastName, R.id.textViewFirstName,
                R.id.textViewTimeIn, R.id.textViewTimeOut,
                R.id.textViewHours, R.id.textViewLunch,
                R.id.textViewCompany, R.id.textViewLocation,
                R.id.textViewJob, R.id.textViewGroup,
                R.id.textViewSupervisor, R.id.textViewComments};

        int i = 0;
        if (all_activity_lists.size() > 0) {
            do {
                map = new HashMap<String, String>();
                map.put(getText(R.string.column_key_employee_id).toString(), String.valueOf(all_activity_lists.get(i).getEmployeeID()));  // need it for indexing but not display
                map.put(getText(R.string.column_key_last_name).toString(), all_activity_lists.get(i).getLastName());
                map.put(getText(R.string.column_key_first_name).toString(), all_activity_lists.get(i).getFirstName());
                map.put(getText(R.string.column_key_date).toString(), General.convertYMDtoMDY(all_activity_lists.get(i).getDate()));
                map.put(getText(R.string.column_key_timein).toString(), General.convertYMDTtoMDYT(all_activity_lists.get(i).getTimeIn()));
                map.put(getText(R.string.column_key_timeout).toString(), General.convertYMDTtoMDYT(all_activity_lists.get(i).getTimeOut()));
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
            DailyActivityView.setText(getText(R.string.daily_activity_view_message) + " " + General.convertYMDtoMDY(Values[0]));
            sort_last_name_ascend = true;
            onSortLastNameButtonClicked(view);
        } else {
            DailyActivityView.setText(getText(R.string.daily_activity_no_message) + " " + General.convertYMDtoMDY(Values[0]));
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setMessage(getText(R.string.no_daily_activity_message) + " for " + General.convertYMDtoMDY(Values[0]) + " !").setTitle(R.string.daily_activity_title);
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
