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


public class WorkGroupPunchMenuActivity extends ActionBarActivity {
    public TextView Current_date;
    public TextView Current_time;
    private ListView universal_list_view;
    WorkGroupList Group;
    private TouchTimeGeneralAdapter adapter_group;
    ArrayList<HashMap<String, String>> feedGroupList;
    private ArrayList<String> unique_groupID;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_group_punch_menu);
        int Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));

        btn_select_all = (Button) findViewById(R.id.group_select_all);
        universal_list_view = (ListView) findViewById(R.id.universal_list_view);
        feedGroupList = new ArrayList<HashMap<String, String>>();
        unique_groupID = new ArrayList<String>();
        unique_itemNumber = new ArrayList<Integer>();
        ArrayList<WorkGroupList> all_group_lists;
        context = this;

        btn_select_all.setText(getText(R.string.button_select_all));
        DateFormat yf = new SimpleDateFormat("yyyy");
        int year = Integer.parseInt(yf.format(Calendar.getInstance().getTime()));
        dbActivity = new DailyActivityDBWrapper(this, year);
        // database and other data
        dbGroup = new EmployeeGroupCompanyDBWrapper(this);
        // retrieve work group lists
        Group = new WorkGroupList();
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
            adapter_group = new TouchTimeGeneralAdapter(this, feedGroupList, R.layout.work_group_punch_view, group_item, group_id);
            universal_list_view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            universal_list_view.setAdapter(adapter_group);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
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
                        unique_groupID.add(G);
                        unique_itemNumber.add(itemGroup);
                    } else {                                                    // item is unchecked
                        unique_groupID.remove(unique_groupID.indexOf(G));
                        unique_itemNumber.remove(unique_itemNumber.indexOf(itemGroup));
                    }
                }
                HighlightListItem(itemGroup);
            }
        });
    }

    public void onPunchInButtonClicked(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (itemGroup >= 0 && unique_groupID.size() > 0) {
            boolean already_punched_in = false, already_has_job = true;
            boolean employee_active = true, employee_current = true;
            for (int i = 0; i < unique_groupID.size(); i++) {      // check if anyone is already punched in
                if (feedGroupList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_status)).equals(getText(R.string.in))) {
                    already_punched_in = true;
                    break;
                }
            }
            for (int i = 0; i < unique_groupID.size(); i++) {      // check if anyone is already punched in
                if (feedGroupList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_company)).isEmpty() ||
                        feedGroupList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_location)).isEmpty() ||
                        feedGroupList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_job)).isEmpty()) {
                    already_has_job = false;
                    break;
                }
            }
            if (already_punched_in) {   // since they are all punched in, they should all have a job
                builder.setMessage(getText(R.string.group_already_punched_in_message).toString()).setTitle(R.string.group_punch_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else if (!already_has_job) {
                builder.setMessage(getText(R.string.no_company_location_job_message).toString()).setTitle(R.string.group_punch_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else if (!employee_active) {
                builder.setMessage(getText(R.string.employee_not_active).toString()).setTitle(R.string.employee_menu_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else if (!employee_current) {
                builder.setMessage(getText(R.string.employee_doc_expire).toString()).setTitle(R.string.employee_menu_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else {
                builder.setMessage(getText(R.string.group_punch_in_message).toString()).setTitle(R.string.group_punch_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        groupPunchIn(view);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            }
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        } else {
            builder.setMessage(R.string.no_work_group_message).setTitle(R.string.group_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        }
    }

    public void onPunchOutButtonClicked(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (itemGroup >= 0 && unique_groupID.size() > 0) {
            boolean already_punched_out = false;
            for (int i = 0; i < unique_groupID.size(); i++) {      // check if anyone is lready punched in
                if (feedGroupList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_status)).equals(getText(R.string.out))) {
                    already_punched_out = true;
                    break;
                }
            }
            if (already_punched_out) {
                builder.setMessage(getText(R.string.group_already_punched_out_message).toString()).setTitle(R.string.group_punch_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else {
                builder.setMessage(getText(R.string.group_punch_out_message).toString()).setTitle(R.string.group_punch_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        groupPunchOut(view);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            }
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        } else {
            builder.setMessage(R.string.no_work_group_message).setTitle(R.string.group_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        }
    }

    public void groupPunchIn(View view) {
        for (int i=0; i<unique_groupID.size(); i++) {      // check if anyone is lready punched in
            feedGroupList.get(unique_itemNumber.get(i)).put(getText(R.string.column_key_status).toString(), getText(R.string.in).toString());
            dbGroup.updateWorkGroupListStatus(Integer.parseInt(unique_groupID.get(i)), 1);
            String  EmployeeList = dbGroup.getWorkGroupListEmployees(Integer.parseInt(unique_groupID.get(i)));
            if (!EmployeeList.isEmpty()) {
                String[] array = EmployeeList.split(",");
                for (String s : array) {
                    String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                    if (!ss.isEmpty()) {
                        if (dbGroup.checkEmployeeID(Integer.parseInt(ss))) {                 // make sure employee is still available
                            int Status = dbGroup.getEmployeeListStatus(Integer.parseInt(ss));
                            if (Status == 1) {          // employee already punched in

                                //  if the job is the same as the group?
                                //     Leave alone and send a message  -- Employee is already punched in to the same job !
                                //  else
                                //     Leave alone and send a message  -- Employee is already punched in to a different job!

                            } else {     // punch in with the group
                                dbGroup.updateEmployeeListStatus(Integer.parseInt(ss), 1);       // set to punch in

                                // if the employee jos is different from the group
                                //      -- Employee job is different, reassigned job!

                            }
                        }
                    }
                }
            }
        }
        adapter_group.notifyDataSetChanged();

/*
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        Activity = new DailyActivityList();
        Group = dbGroup.getWorkGroupList(Integer.parseInt(feedGroupList.get(itemGroup).get(getText(R.string.column_key_group_id).toString())));
        Group.setStatus(1);
        dbGroup.updateWorkGroupList(Group);

        feedGroupList.remove(itemGroup);
        map = new HashMap<String, String>();
        map.put(getText(R.string.column_key_group_id).toString(), String.valueOf(Group.getGroupID()));
        map.put(getText(R.string.column_key_status).toString(), Group.getStatus() == 0 ? getText(R.string.out).toString() : getText(R.string.in).toString());
        map.put(getText(R.string.column_key_group_name).toString(), Group.getGroupName().isEmpty() ? "" : String.valueOf(Group.getGroupName()));
        map.put(getText(R.string.column_key_company).toString(), Group.getCompany());
        map.put(getText(R.string.column_key_location).toString(), Group.getLocation());
        map.put(getText(R.string.column_key_job).toString(), Group.getJob());
        feedGroupList.add(itemGroup, map);

        Activity.setEmployeeID(Group.getGroupID());
        Activity.setLastName(Group.getLastName());
        Activity.setFirstName(Group.getFirstName());
        if (Employee.getGroup() > 0) Activity.setWorkGroup(dbGroup.getWorkGroupList(Employee.getGroup()).getGroupName());
        if (!Employee.getCompany().isEmpty()) Activity.setCompany(Employee.getCompany());
        if (!Employee.getLocation().isEmpty()) Activity.setLocation(Employee.getLocation());
        if (!Employee.getJob().isEmpty()) Activity.setJob(Employee.getJob());
        Activity.setDate(df.format(Calendar.getInstance().getTime()));
        Activity.setTimeIn(currentDateTimeString);
        dbActivity.createActivityList(Activity);
      */
    }

    public void groupPunchOut(View view) {
        for (int i=0; i<unique_groupID.size(); i++) {      // check if anyone is lready punched in
            feedGroupList.get(unique_itemNumber.get(i)).put(getText(R.string.column_key_status).toString(), getText(R.string.out).toString());
            dbGroup.updateWorkGroupListStatus(Integer.parseInt(unique_groupID.get(i)), 0);
            String  Employees = dbGroup.getWorkGroupListEmployees(Integer.parseInt(unique_groupID.get(i)));
            if (!Employees.isEmpty()) {
                String[] array = Employees.split(",");
                for (String s : array) {
                    String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                    if (!ss.isEmpty()) {
                        if (dbGroup.checkEmployeeID(Integer.parseInt(ss))) {                 // make sure employee is still available
                            int Status = dbGroup.getEmployeeListStatus(Integer.parseInt(ss));
                            if (Status == 1) {          // employee is not punched out

                                // If the employee job is the same as the group?
                                //     Punch out
                                // else
                                //     -- Employee is punched in to a different job! Punch out?

                                dbGroup.updateEmployeeListStatus(Integer.parseInt(ss), 0);       // set to punched out
                            }   // employee is already punched out, do nothing and continue
                        }
                    }
                }
            }
        }
        adapter_group.notifyDataSetChanged();
      /*
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        Activity = new DailyActivityList();
        Employee = dbGroup.getEmployeeList(Integer.parseInt(feedGroupList.get(itemGroup).get(getText(R.string.column_key_employee_id).toString())));
        Employee.setStatus(0);
        dbGroup.updateEmployeeList(Employee);

        feedGroupList.remove(itemGroup);
        map = new HashMap<String, String>();
        map.put(getText(R.string.column_key_employee_id).toString(), String.valueOf(Employee.getEmployeeID()));
        map.put(getText(R.string.column_key_last_name).toString(), Employee.getLastName());
        map.put(getText(R.string.column_key_first_name).toString(), Employee.getFirstName());
        map.put(getText(R.string.column_key_group_id).toString(), Employee.getGroup() <= 0 ? "" : String.valueOf(Employee.getGroup()));
        map.put(getText(R.string.column_key_status).toString(), Employee.getStatus() == 0 ? getText(R.string.out).toString() : getText(R.string.in).toString());
        feedGroupList.add(itemGroup, map);

        Activity = dbActivity.getPunchedInActivityList(Employee.getEmployeeID());
        if (Activity != null && Activity.getEmployeeID() > 0) {
            long diff = General.MinuteDifference(Activity.getTimeIn(), currentDateTimeString);
            diff = diff > 0 && diff > Activity.Lunch ? diff-Activity.Lunch : 0;
            Activity.setHours(diff);
            Activity.setTimeOut(currentDateTimeString);
            dbActivity.updatePunchedInActivityList(Activity);
        }
        */
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
                if (!ID.isEmpty()) unique_groupID.add(ID);
            }
        }
        markSelectedItems();     // after clearing unique_employee all will be unchecked
        select_all = !select_all;
    }

    public void onSelectJobButtonClicked(final View view) {
        if (feedGroupList.size() == 0) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (itemGroup >= 0 && unique_groupID.size() > 0 ) {
             boolean already_punched_in = false;
             for (int i = 0; i < unique_groupID.size(); i++) {      // check if anyone is lready punched in
                 if (feedGroupList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_status)).equals(getText(R.string.in))) {
                     already_punched_in = true;
                     break;
                 }
             }
             if (already_punched_in) {
                 builder.setMessage(getText(R.string.group_already_punched_in_message).toString()).setTitle(R.string.group_punch_title);
                 builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                     }
                 });
                 AlertDialog dialog = builder.create();
                 General.TouchTimeDialog(dialog, view);
             } else {
                 Intent intent = new Intent(this, CompanyJobLocationSelectionActivity.class);
                 ArrayList<String> CompanyLocationJob = new ArrayList<>();
                 CompanyLocationJob.add(getText(R.string.title_activity_work_group_punch_menu).toString());        // caller
                 // use the last selected one as default
                 CompanyLocationJob.add(Group.getCompany());              // company
                 CompanyLocationJob.add(Group.getLocation());             // location
                 CompanyLocationJob.add(Group.getJob());                  // job
                 intent.putStringArrayListExtra("CompanyLocationJob", CompanyLocationJob);
                 startActivityForResult(intent, PICK_JOB_REQUEST);
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

    public void onRemoveJobButtonClicked(final View view) {
        if (feedGroupList.size() == 0) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (itemGroup >= 0 && unique_groupID.size() > 0) {
            boolean already_punched_in = false;
            for (int i = 0; i < unique_groupID.size(); i++) {      // check if anyone is lready punched in
                if (feedGroupList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_status)).equals(getText(R.string.in))) {
                    already_punched_in = true;
                    break;
                }
            }
            if (already_punched_in) {
                builder.setMessage(getText(R.string.group_already_punched_in_message).toString()).setTitle(R.string.group_punch_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                General.TouchTimeDialog(dialog, view);
            } else {
                Group.setCompany("");
                Group.setLocation("");
                Group.setJob("");
                for (int i = 0; i < unique_groupID.size(); i++) {
                    for (int j = 0; j < feedGroupList.size(); j++) {
                        if (feedGroupList.get(j).get(getText(R.string.column_key_group_id).toString()).equals(unique_groupID.get(i))) {
                            // feedGroupList.set(j, map);
                            feedGroupList.get(j).put(getText(R.string.column_key_company).toString(), "");
                            feedGroupList.get(j).put(getText(R.string.column_key_location).toString(), "");
                            feedGroupList.get(j).put(getText(R.string.column_key_job).toString(), "");
                            universal_list_view.setItemChecked(j, false);
                        }
                    }
                    dbGroup.clearWorkGroupListCompanyLocationJob(Integer.parseInt(unique_groupID.get(i)));

                    // update employees in the group
                    if (!Group.getEmployees().isEmpty()) {
                        String[] array = Group.Employees.split(",");
                        for (String s : array) {
                            String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                            if (!ss.isEmpty()) {
                                if (dbGroup.checkEmployeeID(Integer.parseInt(ss))) {                 // make sure employee is still available
                                    dbGroup.clearEmployeeListCompanyLocationJob(Integer.parseInt(ss));
                                    // clear company, location, and job for employees in this group
                                }
                            }
                        }
                    }
                }
                unique_groupID.clear();
                itemGroup = -1;                                 // set to -1 to display without highlight
                adapter_group.setSelectedItem(itemGroup);       // set to -1 to display without highlight
                adapter_group.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == PICK_JOB_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                ArrayList<String> CompanyLocationJob = new ArrayList<String>();
                CompanyLocationJob = data.getStringArrayListExtra("CompanyLocationJob");
                // update the selected employee
                Group.setCompany(CompanyLocationJob.get(1));
                Group.setLocation(CompanyLocationJob.get(2));
                Group.setJob(CompanyLocationJob.get(3));
                // update all that are selected
                for (int i=0; i<unique_groupID.size(); i++) {
                    for (int j = 0; j < feedGroupList.size(); j++) {
                        if (feedGroupList.get(j).get(getText(R.string.column_key_group_id).toString()).equals(unique_groupID.get(i))) {
                            // feedGroupList.set(j, map);
                            feedGroupList.get(j).put(getText(R.string.column_key_company).toString(), CompanyLocationJob.get(1));
                            feedGroupList.get(j).put(getText(R.string.column_key_location).toString(), CompanyLocationJob.get(2));
                            feedGroupList.get(j).put(getText(R.string.column_key_job).toString(), CompanyLocationJob.get(3));
                            universal_list_view.setItemChecked(j, true);
                            break;
                        }
                    }
                    dbGroup.updateWorkGroupListCompanyLocationJob(Integer.parseInt(unique_groupID.get(i)), CompanyLocationJob.get(1),
                            CompanyLocationJob.get(2), CompanyLocationJob.get(3));

                    // update employees in the group
                    if (!Group.getEmployees().isEmpty()) {
                        String[] array = Group.Employees.split(",");
                        for (String s : array) {
                            String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                            if (!ss.isEmpty()) {
                                if (dbGroup.checkEmployeeID(Integer.parseInt(ss))) {                 // make sure employee is still available
                                    dbGroup.updateEmployeeListCompanyLocationJob(Integer.parseInt(ss), CompanyLocationJob.get(1),
                                            CompanyLocationJob.get(2), CompanyLocationJob.get(3));
                                    // update company, location, and job for employees in this group
                                }
                            }
                        }
                    }
                }
                adapter_group.setSelectedItem(itemGroup);
                adapter_group.notifyDataSetChanged();
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
        adapter_group.notifyDataSetChanged();
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
        adapter_group.notifyDataSetChanged();
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
        adapter_group.notifyDataSetChanged();
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
        adapter_group.notifyDataSetChanged();
    }

    public void markSelectedItems () {
        unique_itemNumber.clear();
        for (int j = 0; j < feedGroupList.size(); j++) {
            universal_list_view.setItemChecked(j, false);   // set to unchecked by default
            for (int i=0; i<unique_groupID.size(); i++) {
                if (feedGroupList.get(j).get(getText(R.string.column_key_group_id).toString()).equals(unique_groupID.get(i))) {
                    unique_itemNumber.add(j);
                    universal_list_view.setItemChecked(j, true);
                    break;   // quit after checked
                }
            }
        }
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
