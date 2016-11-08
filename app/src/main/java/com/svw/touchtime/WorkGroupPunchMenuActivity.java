package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;


public class WorkGroupPunchMenuActivity extends ActionBarActivity {
    public TextView Current_date;
    public TextView Current_time;
    private ListView universal_list_view;
    WorkGroupList Group;
    WorkGroupList GroupSelected;
    private TouchTimeGeneralAdapter adapter_group;
    ArrayList<HashMap<String, String>> feedGroupList;
    private ArrayList<Integer> unique_groupID;
    private ArrayList<Integer> valid_groupID;
    private ArrayList<Integer> unique_itemNumber;
    HashMap<String, String> map;
    DailyActivityList Activity;

    String[] group_item = new String[7];
    int[] group_id = new int[7];
    private int itemGroup = -1;
    boolean sort_group_id_ascend = true;
    boolean sort_status_ascend = true;
    boolean sort_group_name_ascend = true;
    boolean sort_company_ascend = true;
    boolean select_all = false;
    Button btn_select_all;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private EmployeeGroupCompanyDBWrapper dbGroup;
    private DailyActivityDBWrapper dbActivity;
    Context context;
    static final int PICK_JOB_REQUEST = 123;
    static final int MOVE_JOB_REQUEST = 456;
    static final int MIN_HOURS = 10;
    static final int LUNCH_TIME = 30;
    static final int WORK_HOURS = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_group_punch_menu);
        int Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);

        btn_select_all = (Button) findViewById(R.id.group_select_all);
        universal_list_view = (ListView) findViewById(R.id.universal_list_view);
        feedGroupList = new ArrayList<HashMap<String, String>>();
        unique_groupID = new ArrayList<Integer>();
        valid_groupID = new ArrayList<Integer>();
        unique_itemNumber = new ArrayList<Integer>();
        ArrayList<WorkGroupList> all_group_lists;
        context = this;

        btn_select_all.setText(getText(R.string.button_select_all));
        DateFormat yf = new SimpleDateFormat("yyyy", Locale.US);
        int year = Integer.parseInt(yf.format(Calendar.getInstance().getTime()));
        dbActivity = new DailyActivityDBWrapper(this, year);
        // database and other data
        dbGroup = new EmployeeGroupCompanyDBWrapper(this);
        // retrieve work group lists
        Group = new WorkGroupList();
        GroupSelected = new WorkGroupList();
        Activity = new DailyActivityList();
        all_group_lists = dbGroup.getAllWorkGroupLists();
        group_item[0] = getText(R.string.column_key_group_id).toString();
        group_item[1] = getText(R.string.column_key_status).toString();
        group_item[2] = getText(R.string.column_key_group_name).toString();
        group_item[3] = getText(R.string.column_key_supervisor).toString();
        group_item[4] = getText(R.string.column_key_company).toString();
        group_item[5] = getText(R.string.column_key_location).toString();
        group_item[6] = getText(R.string.column_key_job).toString();
        group_id[0] = R.id.textViewGroupID;
        group_id[1] = R.id.textViewStatus;
        group_id[2] = R.id.textViewGroupName;
        group_id[3] = R.id.textViewSupervisor;
        group_id[4] = R.id.textViewCompany;
        group_id[5] = R.id.textViewLocation;
        group_id[6] = R.id.textViewJob;
        int i = 0;
        if (all_group_lists.size() > 0) {
            do {
                map = new HashMap<String, String>();
                map.put(getText(R.string.column_key_group_id).toString(), String.valueOf(all_group_lists.get(i).getGroupID()));
                map.put(getText(R.string.column_key_status).toString(), all_group_lists.get(i).getStatus() == 0 ? getText(R.string.out).toString() : getText(R.string.in).toString());
                map.put(getText(R.string.column_key_group_name).toString(), all_group_lists.get(i).getGroupName().isEmpty() ? "" : String.valueOf(all_group_lists.get(i).getGroupName()));
                map.put(getText(R.string.column_key_company).toString(), all_group_lists.get(i).getCompany());
                map.put(getText(R.string.column_key_location).toString(), all_group_lists.get(i).getLocation());
                map.put(getText(R.string.column_key_job).toString(), all_group_lists.get(i).getJob());
                feedGroupList.add(map);
                if (itemGroup < 0 && map.get(getText(R.string.column_key_status).toString()).equals(getText(R.string.out).toString())) itemGroup = i;
            } while (++i < all_group_lists.size());
            if (itemGroup < 0) itemGroup = 0;        // all work groups are punched in, then display the first one
            Group = dbGroup.getWorkGroupList(all_group_lists.get(itemGroup).getGroupID());
            universal_list_view.setItemsCanFocus(true);
            adapter_group = new TouchTimeGeneralAdapter(this, feedGroupList, R.layout.work_group_punch_view, group_item, group_id, 60);
            universal_list_view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            universal_list_view.setAdapter(adapter_group);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setMessage(R.string.no_work_group_message).setTitle(R.string.group_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dbGroup.closeDB();
                    dbActivity.closeDB();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, findViewById(android.R.id.content));
        }

        Current_date = (TextView) findViewById(R.id.current_date);
        Current_time = (TextView) findViewById(R.id.current_time);
        DateFormat df = new SimpleDateFormat(getText(R.string.date_MDY_format).toString(), Locale.US);
        // Current_date.setText(df.getDateInstance().format(new Date()));
        Current_date.setText(df.format(Calendar.getInstance().getTime()));
        CountDownTimer uy = new CountDownTimer(2000000000, 1000) {
            public void onFinish() {
                Current_time.setText("Finish");
            }
            @Override
            public void onTick(long l) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Current_time.setText(currentDateTimeString);
            }
        }.start();

        universal_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
               itemGroup = position;
               String G = feedGroupList.get(itemGroup).get(getText(R.string.column_key_group_id).toString());
                if (!G.isEmpty()) {     // selected something
                    if (universal_list_view.isItemChecked(itemGroup)) {      // item is checked
                        Group = dbGroup.getWorkGroupList(Integer.parseInt(G));
                        unique_groupID.add(Integer.parseInt(G));
                        unique_itemNumber.add(itemGroup);
                    } else {                                                    // item is unchecked
                        unique_groupID.remove(unique_groupID.indexOf(Integer.parseInt(G)));
                        unique_itemNumber.remove(unique_itemNumber.indexOf(itemGroup));
                    }
                }
                HighlightListItem(itemGroup);
            }
        });
    }

    public void onPunchInButtonClicked(final View view) {
        if (itemGroup >= 0 && unique_groupID.size() > 0) {
            DateFormat df = new SimpleDateFormat(getText(R.string.date_YMD_format).toString(), Locale.US);
            LinkedList<Boolean> validGroup = new LinkedList<Boolean>();
            LinkedList<Integer> validGroupID = new LinkedList<Integer>();
            final LinkedList<Integer> listIndex = new LinkedList<Integer>();
            String GroupName;
            for (int i = 0; i < unique_groupID.size(); i++) {      // check if any group is already punched in
                GroupSelected = dbGroup.getWorkGroupList(unique_groupID.get(i));
                GroupName = GroupSelected.getGroupName() + ", " + String.valueOf(GroupSelected.getGroupID());
                if (dbGroup.checkWorkGroupID(GroupSelected.getGroupID())) {    // This always returns true. It is needed to include others in the scope.
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                    if (GroupSelected.getStatus() == 1) {
                        validGroup.add(false);
                        validGroupID.add(unique_groupID.get(i));
                        builder.setMessage(getText(R.string.group_already_punched_in_message).toString() + " - " + GroupName).setTitle(R.string.group_punch_title);
                    } else if (GroupSelected.getCompany().isEmpty() || GroupSelected.getLocation().isEmpty() || GroupSelected.getJob().isEmpty()) {
                        validGroup.add(false);
                        validGroupID.add(unique_groupID.get(i));
                        builder.setMessage(getText(R.string.no_company_location_job_message).toString() + " - " + GroupName).setTitle(R.string.group_punch_title);
                    } else {
                        validGroup.add(true);
                        validGroupID.add(unique_groupID.get(i));
                        dbGroup.updateWorkGroupStatus(unique_groupID.get(i), 1);
                    }
                    if (!validGroup.getLast()) {
                        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        General.TouchTimeDialog(dialog, view);
                    }
                }
                if (validGroup.getLast() && !dbGroup.getWorkGroupList((int)validGroupID.getLast()).getEmployees().isEmpty()) {
                    feedGroupList.get(unique_itemNumber.get(i)).put(getText(R.string.column_key_status).toString(), getText(R.string.in).toString());
                    adapter_group.notifyDataSetChanged();
                    LinkedList<Boolean> validEmployee = new LinkedList<Boolean>();
                    EmployeeProfileList Employee = new EmployeeProfileList();
                    String[] array = dbGroup.getWorkGroupList((int)validGroupID.getLast()).Employees.split(",");
                    String EmployeeName;
                    for (String s : array) {
                        String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                        if (!ss.isEmpty() && dbGroup.checkEmployeeID(Integer.parseInt(ss))) {    // make sure employee is still available
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                            Employee = dbGroup.getEmployeeList(Integer.parseInt(ss));
                            EmployeeName = Employee.getLastName() + ", " + Employee.getFirstName() + ", " + String.valueOf(Employee.getEmployeeID());
                            if (Employee.getActive() == 0) {
                                validEmployee.add(false);   // inactive will be left alone
                                builder.setMessage(getText(R.string.employee_not_active).toString() + " - " + EmployeeName).setTitle(R.string.group_punch_title);
                            } else if (Employee.getCurrent() == 0 || Employee.getDocExp().compareTo(df.format(Calendar.getInstance().getTime())) < 0) {
                                validEmployee.add(false);   // expired employees will be left alone
                                builder.setMessage(getText(R.string.employee_doc_expire).toString() + " - " + EmployeeName).setTitle(R.string.group_punch_title);
                            } else if (Employee.getStatus() == 1) {
                                validEmployee.add(false);   // already in will be left alone
                                builder.setMessage(getText(R.string.employee_already_punched_in_message).toString() + " - " + EmployeeName).setTitle(R.string.group_punch_title);
                            } else if (Employee.getCompany().isEmpty() || Employee.getLocation().isEmpty() || Employee.getJob().isEmpty()) {
                                validEmployee.add(false);   // no company, location, or job will be left out
                                builder.setMessage(getText(R.string.no_company_location_job_message).toString() + " - " + EmployeeName).setTitle(R.string.group_punch_title);
                            } else {
                                validEmployee.add(true);
                            }
                            if (!validEmployee.getLast()) {
                                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                General.TouchTimeDialog(dialog, view);
                            }
                        }
                        if (validEmployee.size() > 0 && validEmployee.getLast()) {
                            //// only employees that are out, active, not expired and have a job will be punched in here
                            //AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                            if (Employee.getGroup() > 0) {      // of course, the employee should belong to a group
                                EmployeeName = Employee.getLastName() + ", " + Employee.getFirstName() + ", " + String.valueOf(Employee.getEmployeeID());
                                if (!Employee.getCompany().equals(GroupSelected.getCompany())
                                        || !Employee.getLocation().equals(GroupSelected.getLocation())
                                        || !Employee.getJob().equals(GroupSelected.getJob())
                                        || Employee.Group != GroupSelected.GroupID) {
                                    // employee that is out and has a different job from the company
                                    //builder.setMessage(getText(R.string.employee_punch_in_different_job_message).toString() + " - " + EmployeeName).setTitle(R.string.group_punch_title);
                                    listIndex.add(Employee.getEmployeeID());                  // store employee ID
                                    //builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                     //   public void onClick(DialogInterface dialog, int id) {
                                            // force employees to the same job and the same GroupID in case it is different
                                            dbGroup.updateEmployeeListCompanyLocationJob((int) listIndex.getLast(), GroupSelected.Company, GroupSelected.Location, GroupSelected.Job);
                                            dbGroup.updateEmployeeGroup((int) listIndex.getLast(), GroupSelected.GroupID);
                                            punchInDailyActivity((int) listIndex.removeLast());
                                    //    }
                                    //});
                                    //builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    //    public void onClick(DialogInterface dialog, int id) {
                                    //        listIndex.removeLast();         // still need to be removed even it is not used
                                    //    }
                                    //});
                                    //AlertDialog dialog = builder.create();
                                    //General.TouchTimeDialog(dialog, view);
                                } else {
                                    listIndex.add(Employee.getEmployeeID());                    // store employee ID
                                    punchInDailyActivity((int) listIndex.removeLast());
                                }
                            } else {        // in case the employee has been removed from the group
                                listIndex.add(Employee.getEmployeeID());                        // store employee ID
                                punchInDailyActivity((int) listIndex.removeLast());
                            }
                        }
                    }
                }
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setMessage(R.string.no_work_group_message).setTitle(R.string.group_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        }
        markSelectedItems();
    }

    public void onPunchOutButtonClicked(final View view) {
        if (itemGroup >= 0 && unique_groupID.size() > 0) {
            LinkedList<Boolean> validGroup = new LinkedList<Boolean>();
            LinkedList<Integer> validGroupID = new LinkedList<Integer>();
            final LinkedList<Integer> listIndex = new LinkedList<Integer>();
            String GroupName;
            for (int i = 0; i < unique_groupID.size(); i++) {      // check if anyone is already punched in
                GroupSelected = dbGroup.getWorkGroupList(unique_groupID.get(i));
                GroupName = GroupSelected.getGroupName() + ", " + String.valueOf(GroupSelected.getGroupID());
                if (dbGroup.checkWorkGroupID(GroupSelected.getGroupID())) {      // This always returns true. It is needed to include others in the scope.
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                    if (GroupSelected.getStatus() == 0) {    // group is already out will be left alone
                        validGroup.add(false);
                        validGroupID.add(unique_groupID.get(i));
                        builder.setMessage(getText(R.string.group_already_punched_out_message).toString() + " - " + GroupName).setTitle(R.string.group_punch_title);
                    } else {        // group is in will be punched out
                        validGroup.add(true);
                        validGroupID.add(unique_groupID.get(i));
                        dbGroup.updateWorkGroupStatus(unique_groupID.get(i), 0);
                    }
                    if (!validGroup.getLast()) {
                        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        General.TouchTimeDialog(dialog, view);
                    }
                }
                // group is valid (in) and has employees in the group
                if (validGroup.getLast() && !dbGroup.getWorkGroupList((int)validGroupID.getLast()).getEmployees().isEmpty()) {
                    feedGroupList.get(unique_itemNumber.get(i)).put(getText(R.string.column_key_status).toString(), getText(R.string.out).toString());
                    adapter_group.notifyDataSetChanged();
                    LinkedList<Boolean> validEmployee = new LinkedList<Boolean>();
                    EmployeeProfileList Employee = new EmployeeProfileList();
                    String[] array = dbGroup.getWorkGroupList((int)validGroupID.getLast()).Employees.split(",");
                    String EmployeeName;
                    for (String s : array) {
                        String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                        if (!ss.isEmpty() && dbGroup.checkEmployeeID(Integer.parseInt(ss))) {    // make sure employee is still available
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                            Employee = dbGroup.getEmployeeList(Integer.parseInt(ss));
                            EmployeeName = Employee.getLastName() + ", " + Employee.getFirstName() + ", " + String.valueOf(Employee.getEmployeeID());
                            if (Employee.getStatus() == 0) {
                                validEmployee.add(false);  // already out will be left out
                                builder.setMessage(getText(R.string.employee_already_punched_out_message).toString() + " - " + EmployeeName).setTitle(R.string.group_punch_title);
                            } else if (!Employee.getCompany().equals(GroupSelected.getCompany())
                                    || !Employee.getLocation().equals(GroupSelected.getLocation())
                                    || !Employee.getJob().equals(GroupSelected.getJob())) {
                                builder.setMessage(getText(R.string.employee_already_punched_in_message).toString() + " - " + EmployeeName).setTitle(R.string.group_punch_title);
                                validEmployee.add(false);  // employee is punched in to a different job will be left alone
                            }else {
                                validEmployee.add(true);
                            }
                            if (!validEmployee.getLast()) {
                                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                General.TouchTimeDialog(dialog, view);
                            }
                        }
                        if (validEmployee.size() > 0 && validEmployee.getLast()) {
                            //// only employees that are in and have the same job will be punched out here
                            listIndex.add(Employee.getEmployeeID());
                            punchOutDailyActivity((int) listIndex.removeLast());
                        }
                    }
                }
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setMessage(R.string.no_work_group_message).setTitle(R.string.group_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
                General.TouchTimeDialog(dialog, view);
        }
        markSelectedItems();
    }

    public void updateUniqueActivity(DailyActivityList uniqueActivity) {
        String [] Column = new String[6];
        String [] Values = new String[6];
        Column[0] = dbActivity.getIDColumnKey();
        Column[1] = dbActivity.getTimeInColumnKey();
        Column[2] = dbActivity.getTimeOutColumnKey();
        Values[0] = String.valueOf(uniqueActivity.EmployeeID);
        Values[1] = uniqueActivity.TimeIn;
        Values[2] = uniqueActivity.TimeOut;
        dbActivity.updateActivityList(uniqueActivity, Column, Values);
    }

    public void punchInDailyActivity(int EmployeeID) {
        DateFormat df = new SimpleDateFormat(getText(R.string.date_YMD_format).toString(), Locale.US);
        DateFormat tf = new SimpleDateFormat(getText(R.string.date_time_format).toString(), Locale.US);
        String currentDateString = df.format(new Date());
        String currentDateTimeString = tf.format(new Date());
        EmployeeProfileList EmployeeNew = new EmployeeProfileList();
        EmployeeNew = dbGroup.getEmployeeList(EmployeeID);

        dbGroup.updateEmployeeStatus(EmployeeID, 1);
        Activity = new DailyActivityList();
        Activity.setEmployeeID(EmployeeNew.getEmployeeID());
        Activity.setLastName(EmployeeNew.getLastName());
        Activity.setFirstName(EmployeeNew.getFirstName());
        if (EmployeeNew.getGroup() > 0) {
            Activity.setWorkGroup(String.valueOf(dbGroup.getWorkGroupList(EmployeeNew.getGroup()).getGroupID()));
            Activity.setSupervisor(String.valueOf(dbGroup.getWorkGroupList(EmployeeNew.getGroup()).getSupervisor()));
        }
        if (!EmployeeNew.getCompany().isEmpty())
            Activity.setCompany(EmployeeNew.getCompany());
        if (!EmployeeNew.getLocation().isEmpty())
            Activity.setLocation(EmployeeNew.getLocation());
        if (!EmployeeNew.getJob().isEmpty()) Activity.setJob(EmployeeNew.getJob());
        Activity.setDate(currentDateString);
        Activity.setTimeIn(currentDateTimeString);
        dbActivity.createActivityList(Activity);
        adapter_group.notifyDataSetChanged();
     }

    public void punchOutDailyActivity(int EmployeeID) {
        DateFormat tf = new SimpleDateFormat(getText(R.string.date_time_format).toString(), Locale.US);
        String currentDateTimeString = tf.format(new Date());
        Activity = dbActivity.getPunchedInActivityList(EmployeeID);     // should only have one record
        if (Activity == null) {
            dbGroup.updateEmployeeStatus(EmployeeID, 0);      // something went wrong, the activity record disappeared, set status to out
            return;
        } else if (Activity.getEmployeeID() > 0) {            // calculate the time (in minutes) for the current work period and update record
            long diff = General.MinuteDifference(tf, Activity.getTimeIn(), currentDateTimeString);  // function always returns positive
            if (diff < MIN_HOURS) {     // work time too short, remove automatically
                dbActivity.deletePunchedInActivityList(EmployeeID, Activity.getTimeIn());
                dbGroup.updateEmployeeStatus(EmployeeID, 0);
                return;
            } else {
                Activity.setLunch(0);       // set to 0 and check later
                Activity.setHours(diff);    // set to work horus and check later
                Activity.setTimeOut(currentDateTimeString);
                dbActivity.updatePunchedInActivityList(Activity);  // update and check and change later
            }
            // retrieve all activity records from the same date that belong to the employee
            ArrayList<DailyActivityList> activity_lists;
            ArrayList<HashMap<String, String>> activityList = new ArrayList<HashMap<String, String>>();
            String[] Column = {dbActivity.getIDColumnKey(), dbActivity.getDateColumnKey()};
            String[] Compare = {"=", "="};
            String[] Values = {String.valueOf(EmployeeID), Activity.Date};
            activity_lists = dbActivity.getActivityLists(Column, Compare, Values);
            // if employee has multiple work periods in one day
            if (activity_lists.size() == 1) {
                if (diff >= WORK_HOURS) {       // original work hours already recorded, check and change here
                    activity_lists.get(0).setLunch(LUNCH_TIME);
                    activity_lists.get(0).setHours(diff - LUNCH_TIME);
                    updateUniqueActivity(activity_lists.get(0));        // update
                }
            } else if (activity_lists.size() > 1) {
                // get total work hours (including the current period) and total lunch hours
                long total_work = 0, total_lunch = 0;
                for (int i = 0; i < activity_lists.size(); i++) {
                    total_work += activity_lists.get(i).Hours;
                    total_lunch += activity_lists.get(i).Lunch;
                    map = new HashMap<String, String>();
                    map.put(getText(R.string.column_key_employee_id).toString(), String.valueOf(i)); // borrow ID column key for index
                    map.put(getText(R.string.column_key_lunch).toString(), String.valueOf(activity_lists.get(i).Lunch));
                    map.put(getText(R.string.column_key_hours).toString(), String.valueOf(activity_lists.get(i).Hours));
                    activityList.add(map);
                }
                total_work += total_lunch;   // lunch time already deducted must be counted toward the total hours.
                // sort in descending order so the longest work period gets deducted the lunch horus first
                General.SortIntegerList(activityList, getText(R.string.column_key_hours).toString(), false);
                // assign lunch hours
                if (total_lunch < LUNCH_TIME && total_work >= WORK_HOURS) { // lunch hours are not all deducted yet and work hours has reach the limit to take a lunch break
                    long hours_needed;
                    // distribute lunch hours over as many records as needed
                    for (int i = 0; i < activityList.size(); i++) {
                        if (total_lunch < LUNCH_TIME) {
                            DailyActivityList temp = activity_lists.get(Integer.parseInt(activityList.get(i).get(getText(R.string.column_key_employee_id).toString())));
                            hours_needed = LUNCH_TIME - total_lunch;
                            // change work hour last, it is needed for lunch time calculation
                            temp.setLunch(temp.Lunch + (temp.Hours >= hours_needed ? hours_needed : temp.Hours));
                            total_lunch += temp.Hours >= hours_needed ? hours_needed : temp.Hours;
                            temp.setHours(temp.Hours >= hours_needed ? temp.Hours - hours_needed : 0);
                            updateUniqueActivity(temp);        // update
                        } else break;
                    }
                }
            }
            dbGroup.updateEmployeeStatus(EmployeeID, 0);
        }
    }

    public void onSelectAllButtonClicked(final View view) {
        if (feedGroupList.size() == 0) return;
        if (select_all) {
            btn_select_all.setText(getText(R.string.button_select_all));
            unique_groupID.clear();
        } else {
            String ID;
            unique_groupID.clear();
            btn_select_all.setText(getText(R.string.button_deselect_all));
            for (int i=0; i<feedGroupList.size(); i++) {
                ID = feedGroupList.get(i).get(getText(R.string.column_key_group_id).toString());
                if (!ID.isEmpty()) unique_groupID.add(Integer.parseInt(ID));
            }
        }
        markSelectedItems();     // after clearing unique_employee all will be unchecked
        select_all = !select_all;
    }

    public void onSelectJobButtonClicked(final View view) {
        if (feedGroupList.size() == 0) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (itemGroup >= 0 && unique_groupID.size() > 0 ) {
             valid_groupID.clear();
             for (int i = 0; i < unique_groupID.size(); i++) {      // check if anyone is already punched in
                 if (feedGroupList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_status).toString()).equals(getText(R.string.out))) {
                     valid_groupID.add(unique_groupID.get(i));
                 }
             }
             if (valid_groupID.size() > 0) {       // some groups are punched out.  select new job for them.
                 Intent intent = new Intent(this, CompanyJobLocationSelectionActivity.class);
                 ArrayList<String> CompanyLocationJob = new ArrayList<>();
                 CompanyLocationJob.add(getText(R.string.title_activity_work_group_punch_menu).toString());        // caller
                 // use the last selected one as default
                 CompanyLocationJob.add(Group.getCompany());              // company
                 CompanyLocationJob.add(Group.getLocation());             // location
                 CompanyLocationJob.add(Group.getJob());                  // job
                 intent.putStringArrayListExtra("CompanyLocationJob", CompanyLocationJob);
                 startActivityForResult(intent, PICK_JOB_REQUEST);
             } else {
                 builder.setMessage(getText(R.string.group_already_punched_in_message).toString()).setTitle(R.string.group_punch_title);
                 builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                     }
                 });
                 AlertDialog dialog = builder.create();
                 General.TouchTimeDialog(dialog, view);
             }
       } else {
            // put the dialog inside so it will not dim the screen when returns.
            builder.setMessage(R.string.no_work_group_message).setTitle(R.string.group_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        }
    }

    public void onMoveJobButtonClicked(final View view) {
        if (feedGroupList.size() == 0) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (itemGroup >= 0 && unique_groupID.size() > 0 ) {
            valid_groupID.clear();
            for (int i = 0; i < unique_groupID.size(); i++) {      // check if anyone is already punched in
                if (feedGroupList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_status).toString()).equals(getText(R.string.out))) {
                    valid_groupID.add(unique_groupID.get(i));
                }
            }
            Intent intent = new Intent(this, CompanyJobLocationSelectionActivity.class);
            ArrayList<String> CompanyLocationJob = new ArrayList<>();
            CompanyLocationJob.add(getText(R.string.title_activity_work_group_punch_menu).toString());        // caller
            // use the last selected one as default
            CompanyLocationJob.add(Group.getCompany());              // company
            CompanyLocationJob.add(Group.getLocation());             // location
            CompanyLocationJob.add(Group.getJob());                  // job
            intent.putStringArrayListExtra("CompanyLocationJob", CompanyLocationJob);
            startActivityForResult(intent, MOVE_JOB_REQUEST);
         } else {
            // put the dialog inside so it will not dim the screen when returns.
            builder.setMessage(R.string.no_work_group_message).setTitle(R.string.group_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {  // Make sure the request was successful
            ArrayList<String> CompanyLocationJob = new ArrayList<String>();
            CompanyLocationJob = data.getStringArrayListExtra("CompanyLocationJob");
            WorkGroupList GroupClicked = new WorkGroupList();
            if (requestCode == PICK_JOB_REQUEST) {           // Check which request we're responding to
                // update all that are valid (not punch in)
                for (int i=0; i<valid_groupID.size(); i++) {
                    for (int j = 0; j < feedGroupList.size(); j++) {
                        if (feedGroupList.get(j).get(getText(R.string.column_key_group_id).toString()).equals(String.valueOf(valid_groupID.get(i))) &&
                                feedGroupList.get(j).get(getText(R.string.column_key_status).toString()).equals(getText(R.string.out))) {
                            feedGroupList.get(j).put(getText(R.string.column_key_company).toString(), CompanyLocationJob.get(1));
                            feedGroupList.get(j).put(getText(R.string.column_key_location).toString(), CompanyLocationJob.get(2));
                            feedGroupList.get(j).put(getText(R.string.column_key_job).toString(), CompanyLocationJob.get(3));
                            universal_list_view.setItemChecked(j, false);
                            unique_groupID.remove(unique_groupID.indexOf(valid_groupID.get(i)));
                            unique_itemNumber.remove(unique_itemNumber.indexOf(j));
                            break;
                        }
                    }
                    dbGroup.updateWorkGroupListCompanyLocationJob(valid_groupID.get(i), CompanyLocationJob.get(1),
                            CompanyLocationJob.get(2), CompanyLocationJob.get(3));
                    GroupClicked = dbGroup.getWorkGroupList(valid_groupID.get(i));
                    // update employees in the group
                    if (!GroupClicked.getEmployees().isEmpty()) {
                        String[] array = GroupClicked.Employees.split(",");
                        for (String s : array) {
                            String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                            if (!ss.isEmpty()) {
                                if (dbGroup.checkEmployeeID(Integer.parseInt(ss))) {                 // make sure employee is still available
                                    if (dbGroup.getEmployeeListStatus(Integer.parseInt(ss)) == 0) {
                                        dbGroup.updateEmployeeListCompanyLocationJob(Integer.parseInt(ss), CompanyLocationJob.get(1),
                                                CompanyLocationJob.get(2), CompanyLocationJob.get(3));
                                        dbGroup.updateEmployeeGroup(Integer.parseInt(ss), GroupClicked.GroupID);  // force the same ID just in case
                                    }
                                }
                            }
                        }
                    }
                    adapter_group.notifyDataSetChanged();
                }
            } else if (requestCode == MOVE_JOB_REQUEST) {           // Check which request we're responding to
                // update all that are selected
                for (int i=0; i<unique_groupID.size(); i++) {           // all selected groups will change job
                    // get the old workgroup before update
                    GroupSelected = dbGroup.getWorkGroupList(unique_groupID.get(i));
                    // update company and job
                    dbGroup.updateWorkGroupListCompanyLocationJob(unique_groupID.get(i), CompanyLocationJob.get(1),
                            CompanyLocationJob.get(2), CompanyLocationJob.get(3));
                    for (int j = 0; j < feedGroupList.size(); j++) {
                        if (feedGroupList.get(j).get(getText(R.string.column_key_group_id).toString()).equals(String.valueOf(unique_groupID.get(i)))) {
                            feedGroupList.get(j).put(getText(R.string.column_key_company).toString(), CompanyLocationJob.get(1));
                            feedGroupList.get(j).put(getText(R.string.column_key_location).toString(), CompanyLocationJob.get(2));
                            feedGroupList.get(j).put(getText(R.string.column_key_job).toString(), CompanyLocationJob.get(3));
                            feedGroupList.get(j).put(getText(R.string.column_key_status).toString(), getText(R.string.in).toString());
                            universal_list_view.setItemChecked(j, false);
                            break;
                        }
                    }
                    dbGroup.updateWorkGroupStatus(unique_groupID.get(i), 1);
                    // update employees in the group
                    if (!GroupSelected.getEmployees().isEmpty()) {      // there are employees in the group
                        String[] array = GroupSelected.Employees.split(",");
                        EmployeeProfileList Employee = new EmployeeProfileList();
                        final LinkedList<Integer> employeeID = new LinkedList<Integer>();
                        for (String s : array) {
                            String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                            if (!ss.isEmpty()) {
                                if (dbGroup.checkEmployeeID(Integer.parseInt(ss))) {                 // make sure employee is still available
                                    if (dbGroup.getEmployeeListStatus(Integer.parseInt(ss)) == 1) {  // employee already punched in
                                        // Need to confirm if switch job if punched in different job
                                        // check if employee job is the same as the group job.  if the same, good.  If not, confirm
                                        Employee = dbGroup.getEmployeeList(Integer.parseInt(ss));
                                        // String EmployeeName = Employee.getLastName() + ", " + Employee.getFirstName() + ", " + String.valueOf(Employee.getEmployeeID());
                                        if (Employee.getCompany().equals(GroupSelected.getCompany())
                                                && Employee.getLocation().equals(GroupSelected.getLocation())
                                                && Employee.getJob().equals(GroupSelected.getJob())) {  // already punched in to the same job as the group
                                            employeeID.add(Employee.EmployeeID);
                                            //AlertDialog.Builder builder1 = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                                            //builder1.setMessage(getText(R.string.employee_punch_out_different_job_message).toString() + " - " + EmployeeName).setTitle(R.string.group_punch_title);
                                            //builder1.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            //    public void onClick(DialogInterface dialog, int id) {
                                                    // already punched in the same job as the group, punch it out
                                                    punchOutDailyActivity((int) employeeID.getLast());
                                                    // update to the job before punch back in
                                                    dbGroup.updateEmployeeListCompanyLocationJob((int) employeeID.getLast(), CompanyLocationJob.get(1),
                                                        CompanyLocationJob.get(2), CompanyLocationJob.get(3));
                                                    // punch back in
                                                    punchInDailyActivity((int) employeeID.getLast());
                                                    employeeID.removeLast();
                                            //    }
                                            //});
                                            //builder1.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            //    public void onClick(DialogInterface dialog, int id) {
                                            //    }
                                            //});
                                            //AlertDialog dialog = builder1.create();
                                            //General.TouchTimeDialog(dialog, findViewById(android.R.id.content));
                                        }
                                    } else {            // if not already punched in, just update with the group but don't punch in
                                        dbGroup.updateEmployeeListCompanyLocationJob(Integer.parseInt(ss), CompanyLocationJob.get(1),
                                                CompanyLocationJob.get(2), CompanyLocationJob.get(3));
                                        // punchInDailyActivity(Integer.parseInt(ss));
                                    }
                                }
                            }
                        }
                    }
                }
                adapter_group.notifyDataSetChanged();
                unique_groupID.clear();
                unique_itemNumber.clear();
            }
            if (!Group.getCompany().equals(CompanyLocationJob.get(1)) ||
                    !Group.getLocation().equals(CompanyLocationJob.get(2)) ||
                    !Group.getJob().equals(CompanyLocationJob.get(3))) {        // different job is selected
                Group.setCompany(CompanyLocationJob.get(1));
                Group.setLocation(CompanyLocationJob.get(2));
                Group.setJob(CompanyLocationJob.get(3));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                builder.setMessage(getText(R.string.group_same_job).toString()).setTitle(R.string.employee_menu_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                General.TouchTimeDialog(dialog, findViewById(android.R.id.content));
            }
        }
    }

    private void HighlightListItem(int position) {
        adapter_group.setSelectedItem(position);
        adapter_group.notifyDataSetChanged();
    }

    public void onSortIDButtonClicked(View view) {
        if (feedGroupList.size() == 0) return;
        String Items;
        Items = getText(R.string.column_key_group_id).toString();
        int lastSelectedID = 0;

        if (itemGroup >= 0) lastSelectedID = Integer.parseInt(feedGroupList.get(itemGroup).get(getText(R.string.column_key_group_id).toString()));
        General.SortIntegerList(feedGroupList, Items, sort_group_id_ascend);
        if (itemGroup >= 0) {
            itemGroup = General.GetIntegerIndex(feedGroupList, getText(R.string.column_key_group_id).toString(), lastSelectedID);
            adapter_group.setSelectedItem(itemGroup);
        }
        markSelectedItems();
        sort_group_id_ascend = !sort_group_id_ascend;
    }

    public void onSortStatusButtonClicked(View view) {
        if (feedGroupList.size() == 0) return;
        String [] Items = new String [5];
        Items[0] = getText(R.string.column_key_status).toString();
        Items[1] = getText(R.string.column_key_company).toString();
        Items[2] = getText(R.string.column_key_location).toString();
        Items[3] = getText(R.string.column_key_job).toString();
        Items[4] = getText(R.string.column_key_group_name).toString();
        int lastSelectedID = 0;

        if (itemGroup >= 0) lastSelectedID = Integer.parseInt(feedGroupList.get(itemGroup).get(getText(R.string.column_key_group_id).toString()));
        General.SortStringList(feedGroupList, Items, sort_status_ascend);
        if (itemGroup >= 0) {
            itemGroup = General.GetIntegerIndex(feedGroupList, getText(R.string.column_key_group_id).toString(), lastSelectedID);
            adapter_group.setSelectedItem(itemGroup);
        }
        markSelectedItems();
        sort_status_ascend = !sort_status_ascend;
    }

    public void onSortCompanyButtonClicked(View view) {
        if (feedGroupList.size() == 0) return;
        String [] Items = new String [4];
        Items[0] = getText(R.string.column_key_company).toString();
        Items[1] = getText(R.string.column_key_location).toString();
        Items[2] = getText(R.string.column_key_job).toString();
        Items[3] = getText(R.string.column_key_group_name).toString();
        int lastSelectedID = 0;

        if (itemGroup >= 0) lastSelectedID = Integer.parseInt(feedGroupList.get(itemGroup).get(getText(R.string.column_key_group_id).toString()));
        General.SortStringList(feedGroupList, Items, sort_company_ascend);
        if (itemGroup >= 0) {
            itemGroup = General.GetIntegerIndex(feedGroupList, getText(R.string.column_key_group_id).toString(), lastSelectedID);
            adapter_group.setSelectedItem(itemGroup);
        }
        markSelectedItems();
        sort_company_ascend = !sort_company_ascend;
    }

    public void onSortGroupNameButtonClicked(View view) {
        if (feedGroupList.size() == 0) return;
        String [] Items = new String[4];
        String [] GroupName = new String[1];
        Items[0] = getText(R.string.column_key_group_name).toString();
        Items[1] = getText(R.string.column_key_company).toString();
        Items[2] = getText(R.string.column_key_location).toString();
        Items[3] = getText(R.string.column_key_job).toString();

        if (itemGroup >= 0) GroupName [0] = feedGroupList.get(itemGroup).get(getText(R.string.column_key_group_name).toString());
        General.SortStringList(feedGroupList, Items, sort_group_name_ascend);
        if (itemGroup >= 0) {
            itemGroup = General.GetStringIndex(feedGroupList, Items, GroupName);
            adapter_group.setSelectedItem(itemGroup);
        }
        markSelectedItems();
        sort_group_name_ascend = !sort_group_name_ascend;
    }

    public void markSelectedItems () {
        unique_itemNumber.clear();    // clear first
        for (int i=0; i<unique_groupID.size(); i++) unique_itemNumber.add(i);    // add dummy indexes as place holders
        for (int j = 0; j < feedGroupList.size(); j++) {
            universal_list_view.setItemChecked(j, false);   // set to unchecked by default
            for (int i=0; i<unique_groupID.size(); i++) {
                if (feedGroupList.get(j).get(getText(R.string.column_key_group_id).toString()).equals(String.valueOf(unique_groupID.get(i)))) {
                    unique_itemNumber.set(i, j);             // don't clear.  instead, set new values so they sync with unique_employeeID
                    universal_list_view.setItemChecked(j, true);
                    break;   // quit after checked
                }
            }
        }
        adapter_group.notifyDataSetChanged();
        universal_list_view.smoothScrollToPosition(itemGroup);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_work_group_punch_menu, menu);
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
            dbGroup.closeDB();
            dbActivity.closeDB();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
