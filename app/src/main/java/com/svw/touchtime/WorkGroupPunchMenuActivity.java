package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class WorkGroupPunchMenuActivity extends ActionBarActivity {
    private ListView work_group_list_view;
    private ListView universal_list_view;
    public TextView Current_date;
    public TextView Current_time;
    private RadioGroup radioGroup;
    private RadioButton employeeButton, companyButton, jobButton, locationButton;
    private ArrayList<WorkGroupList> all_work_group_lists;
    private ArrayList<CompanyJobLocationList> all_company_lists;
    private ArrayList<String> unique_group;
    private ArrayList<String> unique_employee;
    private ArrayList<String> unique_com;
    private ArrayList<String> unique_loc;
    private ArrayList<String> unique_job;
    private SimpleAdapter adapter_group;
    private SimpleAdapter adapter_employee;
    private SimpleAdapter adapter_com;
    private SimpleAdapter adapter_job;
    private SimpleAdapter adapter_loc;
    ArrayList<HashMap<String, String>> feedGroupList;
    ArrayList<HashMap<String, String>> feedEmployeeList;
    ArrayList<HashMap<String, String>> feedCompanyList;
    ArrayList<HashMap<String, String>> feedJobList;
    ArrayList<HashMap<String, String>> feedLocationList;
    HashMap<String, String> map;
    WorkGroupList WorkGroup;
    CompanyJobLocationList Company;
    EmployeeProfileList Employee;
    DailyActivityList Activity;

    String[] company_item = new String[5];      // The followings must be different for each adapter
    int[] company_id = new int[5];
    String[] employee_item = new String[5];
    int[] employee_id = new int[5];
    String[] group_item = new String[5];
    int[] group_id = new int[5];
    String[] job_item = new String[5];
    int[] job_id = new int[5];
    String[] location_item = new String[5];
    int[] location_id = new int[5];
    private int itemWorkGroup = 0;
    private int itemCompany = 0;
    private int itemLocation = 0;
    private int itemJob = 0;

    private EmployeeWorkGroupDBWrapper dbGroup;
    private CompanyJobLocationDBWrapper dbCompany;
    private DailyActivityDBWrapper dbActivity;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_supervisor)
            setTitle(getText(R.string.title_back).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));
        else
            setTitle(getText(R.string.title_back).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        setContentView(R.layout.activity_work_group_punch_menu);

        work_group_list_view = (ListView) findViewById(R.id.work_group_list_view);
        universal_list_view = (ListView) findViewById(R.id.universal_list_view);
        radioGroup = (RadioGroup) findViewById(R.id.selection);
        employeeButton = (RadioButton) findViewById(R.id.radio_employee);
        companyButton = (RadioButton) findViewById(R.id.radio_company);
        jobButton = (RadioButton) findViewById(R.id.radio_job);
        locationButton = (RadioButton) findViewById(R.id.radio_loc);
        feedGroupList = new ArrayList<HashMap<String, String>>();
        feedEmployeeList = new ArrayList<HashMap<String, String>>();
        feedCompanyList = new ArrayList<HashMap<String, String>>();
        feedJobList = new ArrayList<HashMap<String, String>>();
        feedLocationList = new ArrayList<HashMap<String, String>>();

        DateFormat yf = new SimpleDateFormat("yyyy");
        int year = Integer.parseInt(yf.format(Calendar.getInstance().getTime()));
        dbActivity = new DailyActivityDBWrapper(this, year);
        // database and other data
        dbGroup = new EmployeeWorkGroupDBWrapper(this);
        // retrieve work group lists
        all_work_group_lists = dbGroup.getAllWorkGroupLists();
        unique_group = new ArrayList<String>();
        unique_employee = new ArrayList<String>();
        WorkGroup = new WorkGroupList();
        if (all_work_group_lists.size() > 0) {
            int i = 0;
            do {
                unique_group.add(String.valueOf(all_work_group_lists.get(i).getGroupID()));
                map = new HashMap<String, String>();
                if (all_work_group_lists.get(i).getStatus() == 0)
                    map.put(getText(R.string.group_selection_item_name).toString(), unique_group.get(i));
                else
                    map.put(getText(R.string.group_selection_item_name).toString(), unique_group.get(i) + "*");
                feedGroupList.add(map);
            } while (++i < all_work_group_lists.size());
            WorkGroup = dbGroup.getWorkGroupList(all_work_group_lists.get(0).getGroupID());
            itemWorkGroup = 1;
            group_item[0] = getText(R.string.group_selection_item_name).toString();
            group_id[0] = R.id.groupDisplayID;
            work_group_list_view.setItemsCanFocus(true);
            work_group_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.group_display_header, null, false), null, false);
            adapter_group = new SimpleAdapter(this, feedGroupList, R.layout.group_display_view, group_item, group_id);
            work_group_list_view.setAdapter(adapter_group);
            displayWorkGroup();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.no_work_group_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dbGroup.closeDB();
                    dbActivity.closeDB();
                    dbCompany.closeDB();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        // retrieve employee lists
        unique_employee = new ArrayList<String>();
        // display selected employees
        employee_item[0] = getText(R.string.employee_selection_item_id).toString();
        employee_item[1] = getText(R.string.employee_selection_item_last_name).toString();
        employee_item[2] = getText(R.string.employee_selection_item_first_name).toString();
        employee_id[0] = R.id.textDisplayID;
        employee_id[1] = R.id.textDisplayLastName;
        employee_id[2] = R.id.textDisplayFirstName;
        universal_list_view.setItemsCanFocus(true);
        universal_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.employee_display_header, null, false), null, false);
        adapter_employee = new SimpleAdapter(this, feedEmployeeList, R.layout.employee_display_view, employee_item, employee_id);
        getEmployeeList();
        universal_list_view.setAdapter(adapter_employee);

        // retrieve company lists
        dbCompany = new CompanyJobLocationDBWrapper(this);
        all_company_lists = dbCompany.getAllCompanyLists();
        Company = new CompanyJobLocationList();
        unique_com = new ArrayList<String>();
        unique_job = new ArrayList<String>();
        unique_loc = new ArrayList<String>();
        if (all_company_lists.size() > 0) {
            int i = 0;
            do {
                unique_com.add(all_company_lists.get(i++).getName());
            } while (i < all_company_lists.size());
            feedCompanyList.clear();
            i = 0;
            while (i < unique_com.size()) {
                map = new HashMap<String, String>();
                map.put(getText(R.string.group_selection_item_name).toString(), unique_com.get(i++));
                feedCompanyList.add(map);
            };
            if (!WorkGroup.getCompany().isEmpty() && unique_com.indexOf(WorkGroup.getCompany()) >= 0) {
                getCompanyJobLocation();
            } else {
                itemCompany = 0;
                itemLocation = 0;
                itemJob = 0;
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
            builder.setMessage(R.string.no_company_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dbGroup.closeDB();
                    dbActivity.closeDB();
                    dbCompany.closeDB();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        company_item[0] = getText(R.string.group_selection_item_name).toString();
        company_id[0] = R.id.groupDisplayID;
//      universal_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.group_display_header, null, false), null, false);
        adapter_com = new SimpleAdapter(this, feedCompanyList, R.layout.group_display_view, company_item, company_id);

        // set up location & job
        job_item[0] = getText(R.string.group_selection_item_name).toString();
        job_id[0] = R.id.groupDisplayID;
        adapter_job = new SimpleAdapter(this, feedJobList, R.layout.group_display_view, job_item, job_id);

        location_item[0] = getText(R.string.group_selection_item_name).toString();
        location_id[0] = R.id.groupDisplayID;
        adapter_loc = new SimpleAdapter(this, feedLocationList, R.layout.group_display_view, location_item, location_id);

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
                // DateFormat tf = new SimpleDateFormat("HH:mm:ss");
                // Current_time.setText(tf.format(Calendar.getInstance().getTime()));
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Current_time.setText(currentDateTimeString);
            }
        }.start();

        universal_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                if (radioGroup.getCheckedRadioButtonId() == companyButton.getId()) {
                    itemCompany = position;
                    if (unique_com.size() >= itemCompany && itemCompany > 0) WorkGroup.setCompany(unique_com.get(itemCompany-1));
                    getCompanyJobLocation();
                } else if (radioGroup.getCheckedRadioButtonId() == jobButton.getId()) {
                    itemJob = position;
                    if (unique_job.size() >= itemJob && itemJob > 0) WorkGroup.setJob(unique_job.get(itemJob-1));
                } else if (radioGroup.getCheckedRadioButtonId() == locationButton.getId()) {
                    itemLocation = position;
                    if (unique_loc.size() >= itemLocation && itemLocation > 0) WorkGroup.setLocation(unique_loc.get(itemLocation-1));
                }
                // store current location because switching adapter will lose track of the location
                int index = universal_list_view.getFirstVisiblePosition();
                View v = universal_list_view.getChildAt(0);
                int top = (v == null) ? 0 : v.getTop();
                onRadioButtonClicked(view);
                // restore current location
                universal_list_view.setSelectionFromTop(index, top);
            }
        });

        work_group_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                itemWorkGroup = position;
                view.animate().setDuration(30).alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        WorkGroup = dbGroup.getWorkGroupList(Integer.parseInt(unique_group.get(itemWorkGroup - 1)));
                        displayWorkGroup();
                        if (!WorkGroup.getCompany().isEmpty() && unique_com.indexOf(WorkGroup.getCompany()) >= 0) {
                            getCompanyJobLocation();
                            getEmployeeList();
                        } else {
                            itemCompany = 0;
                            itemLocation = 0;
                            itemJob = 0;
                        }
                        // store current location
                        int index = work_group_list_view.getFirstVisiblePosition();
                        View v = work_group_list_view.getChildAt(0);
                        int top = (v == null) ? 0 : v.getTop();
                        onRadioButtonClicked(view);
                        // restore current location
                        work_group_list_view.setSelectionFromTop(index, top);
                        view.setAlpha(1);
                    }
                });
            }
        });
    }

    public void displayWorkGroup() {
        if (!feedGroupList.isEmpty()) adapter_group.notifyDataSetChanged();
        if (itemWorkGroup > 0) work_group_list_view.setItemChecked(itemWorkGroup, true);
    }

    public void getEmployeeList() {
        feedEmployeeList.clear();     // clear the old list
        unique_employee.clear();      // c lear the old list
        if (!WorkGroup.getEmployees().isEmpty()) {
            String[] array = WorkGroup.getEmployees().split(",");
            for (String s : array) {
                String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                if (!ss.isEmpty()) {
                    if (dbGroup.checkEmployeeID(Integer.parseInt(ss))) {         // make sure employee is still available
                        unique_employee.add(ss);
                    }
                }
            }
            for (String s : unique_employee) {
                map = new HashMap<String, String>();
                map.put(getText(R.string.employee_selection_item_id).toString(), String.valueOf(dbGroup.getEmployeeList(Integer.parseInt(s)).getEmployeeID()));
                map.put(getText(R.string.employee_selection_item_last_name).toString(), dbGroup.getEmployeeList(Integer.parseInt(s)).getLastName());
                map.put(getText(R.string.employee_selection_item_first_name).toString(), dbGroup.getEmployeeList(Integer.parseInt(s)).getFirstName());
                feedEmployeeList.add(map);
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
            builder.setMessage(R.string.empty_employee_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dbGroup.closeDB();
                    dbActivity.closeDB();
                    dbCompany.closeDB();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    public void getCompanyJobLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        int i;
        if (WorkGroup.getCompany().isEmpty()) {
            builder.setMessage(R.string.select_company_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    return;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            itemCompany = unique_com.indexOf(WorkGroup.getCompany()) + 1;           // company availability is already checked
            Company = dbCompany.getCompanyList(unique_com.get(itemCompany - 1));
            unique_loc.clear();
            feedLocationList.clear();
            if (!Company.getLocation().equalsIgnoreCase("[]")) {
                for (String s : Company.Location.split(",")) {
                    String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                    if (!ss.isEmpty()) unique_loc.add(ss);
                }
                i = 0;
                while (i < unique_loc.size()) {
                    map = new HashMap<String, String>();
                    map.put(getText(R.string.group_selection_item_name).toString(), unique_loc.get(i++));
                    feedLocationList.add(map);
                }
                ;
                itemLocation = unique_loc.indexOf(WorkGroup.getLocation());
                itemLocation = itemLocation < 0 ? 0 : itemLocation + 1;
            } else {
                builder.setMessage(R.string.no_location_message).setTitle(R.string.empty_entry_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbGroup.closeDB();
                        dbActivity.closeDB();
                        dbCompany.closeDB();
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            unique_job.clear();
            feedJobList.clear();
            if (!Company.getJob().equalsIgnoreCase("[]")) {
                for (String s : Company.Job.split(",")) {
                    String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                    if (!ss.isEmpty()) unique_job.add(ss);
                }
                i = 0;
                while (i < unique_job.size()) {
                    map = new HashMap<String, String>();
                    map.put(getText(R.string.group_selection_item_name).toString(), unique_job.get(i++));
                    feedJobList.add(map);
                }
                ;
                itemJob = unique_job.indexOf(WorkGroup.getJob());
                itemJob = itemJob < 0 ? 0 : itemJob + 1;
            } else {
                builder.setMessage(R.string.no_job_message).setTitle(R.string.empty_entry_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbGroup.closeDB();
                        dbActivity.closeDB();
                        dbCompany.closeDB();
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    public void onRadioButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        if (radioGroup.getCheckedRadioButtonId() == employeeButton.getId()) {
            getEmployeeList();
            universal_list_view.setAdapter(adapter_employee);
        } else if (radioGroup.getCheckedRadioButtonId() == companyButton.getId()) {
            if(!feedCompanyList.isEmpty()) universal_list_view.setAdapter(adapter_com);
            if (itemCompany > 0) universal_list_view.setItemChecked(itemCompany, true);
        } else if (radioGroup.getCheckedRadioButtonId() == locationButton.getId()) {
            if (itemCompany <= 0) {
                builder.setMessage(R.string.no_company_location_job_message).setTitle(R.string.empty_entry_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                universal_list_view.setAdapter(adapter_loc);
                if (itemLocation > 0) universal_list_view.setItemChecked(itemLocation, true);
            }
        } else if (radioGroup.getCheckedRadioButtonId() == jobButton.getId()) {
            if (itemCompany <= 0) {
                builder.setMessage(R.string.no_company_location_job_message).setTitle(R.string.empty_entry_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                universal_list_view.setAdapter(adapter_job);
                if (itemJob > 0) universal_list_view.setItemChecked(itemJob, true);
            }
        }
    }

    public void onAssignButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        if (itemCompany <= 0 || itemLocation <= 0 || itemJob <= 0) {
            builder.setMessage(R.string.no_company_location_job_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        } else {
            builder.setMessage(R.string.assign_work_group_message).setTitle(R.string.work_group_assign_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    groupAssign();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onPunchInButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        if (WorkGroup.getStatus() == 1) {
            builder.setMessage(R.string.group_already_punched_in_message).setTitle(R.string.work_group_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        } else if (itemCompany <= 0 || itemLocation <= 0 || itemJob <= 0) {
            builder.setMessage(R.string.no_company_location_job_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        } else {
            builder.setMessage(R.string.group_punch_in_message).setTitle(R.string.work_group_punch_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    groupPunchIn();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onPunchOutButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        if (WorkGroup.getStatus() == 0) {
            builder.setMessage(R.string.group_not_punched_in_message).setTitle(R.string.work_group_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        } else {
            builder.setMessage(R.string.group_punch_out_message).setTitle(R.string.work_group_punch_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    groupPunchOut();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onMoveJobButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        if (itemCompany <= 0 || itemLocation <= 0 || itemJob <= 0) {
            builder.setMessage(R.string.no_company_location_job_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        } else if (WorkGroup.getStatus() == 0) {
            builder.setMessage(R.string.group_move_anyway_message).setTitle(R.string.work_group_punch_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    groupPunchIn();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        } else {
            builder.setMessage(R.string.group_move_message).setTitle(R.string.work_group_punch_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    groupPunchOut();
                    groupPunchIn();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void groupAssign() {
        for (String s : unique_employee) {          // create one record for each employee
            Employee = dbGroup.getEmployeeList(Integer.parseInt(s));
            if (!WorkGroup.getCompany().isEmpty()) Employee.setCompany(WorkGroup.getCompany());
            if (!WorkGroup.getLocation().isEmpty()) Employee.setLocation(WorkGroup.getLocation());
            if (!WorkGroup.getJob().isEmpty()) Employee.setJob(WorkGroup.getJob());
            dbGroup.updateEmployeeList(Employee);
        }
        dbGroup.updateWorkGroupList(WorkGroup);
    }

    public void groupPunchIn() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        WorkGroup.setStatus(1);             // set punch in status
        feedGroupList.remove(itemWorkGroup - 1);
        map = new HashMap<String, String>();
        map.put(getText(R.string.group_selection_item_name).toString(), unique_group.get(itemWorkGroup-1) + "*");
        feedGroupList.add(itemWorkGroup-1, map);
        displayWorkGroup();

        dbGroup.updateWorkGroupList(WorkGroup);
        Activity = new DailyActivityList();
        if (!WorkGroup.getGroupName().isEmpty()) Activity.setWorkGroup(WorkGroup.getGroupName());
        if (!WorkGroup.getCompany().isEmpty()) Activity.setCompany(WorkGroup.getCompany());
        if (!WorkGroup.getLocation().isEmpty()) Activity.setLocation(WorkGroup.getLocation());
        if (!WorkGroup.getJob().isEmpty()) Activity.setJob(WorkGroup.getJob());
        if (!WorkGroup.getSupervisor().isEmpty()) Activity.setSupervisor(WorkGroup.getSupervisor());
        Activity.setDate(df.format(Calendar.getInstance().getTime()));
        Activity.setTimeIn(currentDateTimeString);
        for (String s : unique_employee) {          // create one record for each employee
            Employee = dbGroup.getEmployeeList(Integer.parseInt(s));
            if (Employee.getStatus() == 0) {
                Employee.setStatus(1);
                if (!WorkGroup.getCompany().isEmpty()) Employee.setCompany(WorkGroup.getCompany());
                if (!WorkGroup.getLocation().isEmpty()) Employee.setLocation(WorkGroup.getLocation());
                if (!WorkGroup.getJob().isEmpty()) Employee.setJob(WorkGroup.getJob());
                dbGroup.updateEmployeeList(Employee);
                Activity.setEmployeeID(dbGroup.getEmployeeList(Integer.parseInt(s)).getEmployeeID());
                Activity.setLastName(dbGroup.getEmployeeList(Integer.parseInt(s)).getLastName());
                Activity.setFirstName(dbGroup.getEmployeeList(Integer.parseInt(s)).getFirstName());
                dbActivity.createActivityList(Activity);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
                builder.setMessage("ID#" + String.valueOf(Employee.getEmployeeID()) + " "+ getText(R.string.employee_already_punched_in_message)).setTitle(R.string.work_group_punch_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    public void groupPunchOut() {
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
        WorkGroup.setStatus(0);                 // set punch in status
        feedGroupList.remove(itemWorkGroup - 1);
        map = new HashMap<String, String>();
        map.put(getText(R.string.group_selection_item_name).toString(), unique_group.get(itemWorkGroup-1));
        feedGroupList.add(itemWorkGroup - 1, map);
        displayWorkGroup();

        dbGroup.updateWorkGroupList(WorkGroup);
        Activity = new DailyActivityList();
        for (String s : unique_employee) {          // create one record for each employee
            Employee = dbGroup.getEmployeeList(Integer.parseInt(s));
            if (Employee.getStatus() == 1) {
                Employee.setStatus(0);
                dbGroup.updateEmployeeList(Employee);
                Activity = dbActivity.getPunchedInActivityList(Employee.getEmployeeID());
                if (Activity != null && Activity.getEmployeeID() > 0) {
                    long diff = General.MinuteDifference(Activity.getTimeIn(), currentDateTimeString);
                    diff = diff > 0 && diff > Activity.Lunch ? diff-Activity.Lunch : 0;
                    Activity.setHours(diff);
                    Activity.setTimeOut(currentDateTimeString);
                    dbActivity.updatePunchedInActivityList(Activity);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
                builder.setMessage("ID#" + String.valueOf(Employee.getEmployeeID()) + " "+ getText(R.string.employee_already_punched_out_message)).setTitle(R.string.work_group_punch_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
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
            dbCompany.closeDB();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
