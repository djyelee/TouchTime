package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
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


public class EmployeePunchMenuActivity extends ActionBarActivity {
    public TextView Current_date;
    public TextView Current_time;
    private ListView universal_list_view;
    private RadioGroup radioGroup;
    private RadioButton employeeButton, companyButton, jobButton, locationButton;
    EmployeeProfileList Employee;
    private ArrayList<String> unique_com;
    private ArrayList<String> unique_loc;
    private ArrayList<String> unique_job;
    private SimpleAdapter adapter_employee;
    private SimpleAdapter adapter_com;
    private SimpleAdapter adapter_job;
    private SimpleAdapter adapter_loc;
    ArrayList<HashMap<String, String>> feedEmployeeList;
    ArrayList<HashMap<String, String>> feedCompanyList;
    ArrayList<HashMap<String, String>> feedJobList;
    ArrayList<HashMap<String, String>> feedLocationList;
    HashMap<String, String> map;
    CompanyJobLocationList Company;
    DailyActivityList Activity;

    String[] employee_item = new String[5];
    int[] employee_id = new int[5];
    String[] company_item = new String[5];      // The followings must be different for each adapter
    int[] company_id = new int[5];
    String[] job_item = new String[5];
    int[] job_id = new int[5];
    String[] location_item = new String[5];
    int[] location_id = new int[5];
    private int itemEmployee = -1;
    private int itemCompany = -1;
    private int itemLocation = -1;
    private int itemJob = -1;
    boolean sort_id_ascend = true;
    boolean sort_last_name_ascend = true;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private EmployeeWorkGroupDBWrapper dbGroup;
    private CompanyJobLocationDBWrapper dbCompany;
    private DailyActivityDBWrapper dbActivity;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String caller = getIntent().getStringExtra("Caller");
        if (caller.equals(getText(R.string.supervisor_menu).toString()))
            setTitle(getText(R.string.title_back).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));
        else
            setTitle(getText(R.string.title_back).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        setContentView(R.layout.activity_employee_punch_menu);

        universal_list_view = (ListView) findViewById(R.id.universal_list_view);
        radioGroup = (RadioGroup) findViewById(R.id.selection);
        employeeButton = (RadioButton) findViewById(R.id.radio_employee);
        companyButton = (RadioButton) findViewById(R.id.radio_company);
        locationButton = (RadioButton) findViewById(R.id.radio_loc);
        jobButton = (RadioButton) findViewById(R.id.radio_job);
        feedEmployeeList = new ArrayList<HashMap<String, String>>();
        feedCompanyList = new ArrayList<HashMap<String, String>>();
        feedLocationList = new ArrayList<HashMap<String, String>>();
        feedJobList = new ArrayList<HashMap<String, String>>();
        ArrayList<EmployeeProfileList> all_employee_lists;
        ArrayList<CompanyJobLocationList> all_company_lists;
        context = this;

        DateFormat yf = new SimpleDateFormat("yyyy");
        int year = Integer.parseInt(yf.format(Calendar.getInstance().getTime()));
        dbActivity = new DailyActivityDBWrapper(this, year);
        // database and other data
        dbGroup = new EmployeeWorkGroupDBWrapper(this);
        // retrieve employee lists
        Employee = new EmployeeProfileList();
        all_employee_lists = dbGroup.getAllEmployeeLists();
        int i = 0;
        if (all_employee_lists.size() > 0) {
            do {
                map = new HashMap<String, String>();
                map.put(getText(R.string.employee_selection_item_id).toString(), String.valueOf(all_employee_lists.get(i).getEmployeeID()));
                map.put(getText(R.string.employee_selection_item_last_name).toString(), all_employee_lists.get(i).getLastName());
                map.put(getText(R.string.employee_selection_item_first_name).toString(), all_employee_lists.get(i).getFirstName());
                map.put(getText(R.string.employee_selection_item_group).toString(), all_employee_lists.get(i).getGroup() <= 0 ? "" : String.valueOf(all_employee_lists.get(i).getGroup()));
                map.put(getText(R.string.employee_selection_item_status).toString(), all_employee_lists.get(i).getStatus() == 0 ? getText(R.string.out).toString() : getText(R.string.in).toString());
                feedEmployeeList.add(map);
                if (itemEmployee < 0 && map.get(getText(R.string.employee_selection_item_status).toString()) == getText(R.string.out).toString()) itemEmployee = i;
            } while (++i < all_employee_lists.size());
            if (itemEmployee < 0) itemEmployee = 0;        // all employees are punched in, then display the first one
            Employee = dbGroup.getEmployeeList(all_employee_lists.get(itemEmployee).getEmployeeID());
            employee_item[0] = getText(R.string.employee_selection_item_id).toString();
            employee_item[1] = getText(R.string.employee_selection_item_last_name).toString();
            employee_item[2] = getText(R.string.employee_selection_item_first_name).toString();
            employee_item[3] = getText(R.string.employee_selection_item_group).toString();
            employee_item[4] = getText(R.string.employee_selection_item_status).toString();
            employee_id[0] = R.id.textViewID;
            employee_id[1] = R.id.textViewLastName;
            employee_id[2] = R.id.textViewFirstName;
            employee_id[3] = R.id.textViewGroup;
            employee_id[4] = R.id.textViewStatus;
            universal_list_view.setItemsCanFocus(true);
            // universal_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.employee_punch_header, null, false), null, false);
            adapter_employee = new SimpleAdapter(this, feedEmployeeList, R.layout.employee_punch_view, employee_item, employee_id);
            universal_list_view.setAdapter(adapter_employee);
            universal_list_view.setItemChecked(itemEmployee, true);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        // retrieve company lists
        dbCompany = new CompanyJobLocationDBWrapper(this);
        all_company_lists = dbCompany.getAllCompanyLists();
        Company = new CompanyJobLocationList();
        unique_com = new ArrayList<String>();
        unique_job = new ArrayList<String>();
        unique_loc = new ArrayList<String>();
        if (all_company_lists.size() > 0) {
            i = 0;
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
            getCompanyJobLocation();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.no_company_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
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
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Current_time.setText(currentDateTimeString);
            }
        }.start();

        universal_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                if (radioGroup.getCheckedRadioButtonId() == employeeButton.getId()) {
                    itemEmployee = position;
                    Employee = dbGroup.getEmployeeList(Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.employee_selection_item_id).toString())));
                    getCompanyJobLocation();
                } else if (radioGroup.getCheckedRadioButtonId() == companyButton.getId()) {
                    itemCompany = position;
                    if (unique_com.size() > itemCompany && itemCompany >= 0) Employee.setCompany(unique_com.get(itemCompany));
                    getCompanyJobLocation();
                    dbGroup.updateEmployeeList(Employee);
                } else if (radioGroup.getCheckedRadioButtonId() == jobButton.getId()) {
                    itemJob = position;
                    if (unique_job.size() > itemJob && itemJob >= 0) Employee.setJob(unique_job.get(itemJob));
                    dbGroup.updateEmployeeList(Employee);
                } else if (radioGroup.getCheckedRadioButtonId() == locationButton.getId()) {
                    itemLocation = position;
                    if (unique_loc.size() > itemLocation && itemLocation >= 0) Employee.setLocation(unique_loc.get(itemLocation));
                    dbGroup.updateEmployeeList(Employee);
                }
                // store current location because switching adapter will lose track of the location
                int index = universal_list_view.getFirstVisiblePosition();
                View v = universal_list_view.getChildAt(0);
                int top = (v == null) ? 0 : v.getTop();
                onRadioButtonClicked(view);
                // restore current location
                universal_list_view.setSelectionFromTop(index, top);

                // onRadioButtonClicked(view);
            }
        });
    }

    public void getCompanyJobLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int i;
        if (Employee.getCompany() == null) {
            builder.setMessage(R.string.select_company_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            itemCompany = unique_com.indexOf(Employee.getCompany());
            Company = dbCompany.getCompanyList(unique_com.get(itemCompany));
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
                itemLocation = unique_loc.indexOf(Employee.getLocation());
                itemLocation = itemLocation < 0 ? -1 : itemLocation;
            } else {
                builder.setMessage(R.string.no_location_message).setTitle(R.string.empty_entry_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
                itemJob = unique_job.indexOf(Employee.getJob());
                itemJob = itemJob < 0 ? -1 : itemJob;
            } else {
                builder.setMessage(R.string.no_job_message).setTitle(R.string.empty_entry_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    public void onRadioButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (radioGroup.getCheckedRadioButtonId() == employeeButton.getId()) {
            universal_list_view.setAdapter(adapter_employee);
            universal_list_view.setItemChecked(itemEmployee, true);
        } else if (radioGroup.getCheckedRadioButtonId() == companyButton.getId()) {
            if(!feedCompanyList.isEmpty()) universal_list_view.setAdapter(adapter_com);
            if (itemCompany >= 0) universal_list_view.setItemChecked(itemCompany, true);
        } else if (radioGroup.getCheckedRadioButtonId() == locationButton.getId()) {
            if (itemCompany < 0) {
                builder.setMessage(R.string.no_company_location_job_message).setTitle(R.string.empty_entry_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                universal_list_view.setAdapter(adapter_loc);
                if (itemLocation >= 0) universal_list_view.setItemChecked(itemLocation, true);
            }
        } else if (radioGroup.getCheckedRadioButtonId() == jobButton.getId()) {
            if (itemCompany < 0) {
                builder.setMessage(R.string.no_company_location_job_message).setTitle(R.string.empty_entry_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                universal_list_view.setAdapter(adapter_job);
                if (itemJob >= 0) universal_list_view.setItemChecked(itemJob, true);
            }
        }
    }

    public void onSortIDButtonClicked(View view) {
        if (radioGroup.getCheckedRadioButtonId() == employeeButton.getId()) {
            String Items;
            Items = getText(R.string.employee_selection_item_id).toString();
            General.SortIntegerList(feedEmployeeList, Items, sort_id_ascend);
            sort_last_name_ascend = false;
            sort_id_ascend = !sort_id_ascend;
            adapter_employee.notifyDataSetChanged();
            markSelectedItems();
        }
    }

    public void onSortLastNameButtonClicked(View view) {
        if (radioGroup.getCheckedRadioButtonId() == employeeButton.getId()) {
            String[] Items = new String[2];
            Items[0] = getText(R.string.employee_selection_item_last_name).toString();
            Items[1] = getText(R.string.employee_selection_item_first_name).toString();
            General.SortStringList(feedEmployeeList, Items, sort_last_name_ascend);
            sort_id_ascend = false;
            sort_last_name_ascend = !sort_last_name_ascend;
            adapter_employee.notifyDataSetChanged();
            markSelectedItems();
        }
    }

    public void markSelectedItems() {
        int i=0;
        while(i < feedEmployeeList.size()) {
            if (feedEmployeeList.get(i).get(getText(R.string.employee_selection_item_id).toString()) == String.valueOf(Employee.getEmployeeID())) {
                itemEmployee = i;
                universal_list_view.setItemChecked(itemEmployee, true);   // it is single choice so no need to erase the previous selection
                break;      // only one can be highlighted
            }
            i++;
        }
    }

    public void onPunchInButtonClicked(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (Employee.getStatus() == 1) {
            builder.setMessage(R.string.employee_already_punched_in_message).setTitle(R.string.employee_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        } else if (itemCompany < 0 || itemLocation < 0 || itemJob < 0) {
            builder.setMessage(R.string.no_company_location_job_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
         } else {
            builder.setMessage(R.string.employee_punch_in_message).setTitle(R.string.employee_punch_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                employeePunchIn(view);
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

    public void onPunchOutButtonClicked(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (Employee.getStatus() == 0) {
            builder.setMessage(R.string.employee_not_punched_in_message).setTitle(R.string.employee_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
         } else {
            builder.setMessage(R.string.employee_punch_out_message).setTitle(R.string.employee_punch_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    employeePunchOut(view);
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

    public void onMoveJobButtonClicked(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (itemCompany < 0 || itemLocation < 0 || itemJob < 0) {
            builder.setMessage(R.string.no_company_location_job_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        } else if (Employee.getStatus() == 0) {
            builder.setMessage(R.string.employee_move_anyway_message).setTitle(R.string.employee_punch_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                employeePunchIn(view);
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        } else {
            builder.setMessage(R.string.employee_move_message).setTitle(R.string.employee_punch_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                employeePunchOut(view);
                employeePunchIn(view);
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

    public void employeePunchIn(View view) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        Activity = new DailyActivityList();
        Employee = dbGroup.getEmployeeList(Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.employee_selection_item_id).toString())));
        Employee.setStatus(1);
        if (unique_com.get(itemCompany) != null) Employee.setCompany(unique_com.get(itemCompany));
        if (unique_loc.get(itemLocation) != null) Employee.setLocation(unique_loc.get(itemLocation));
        if (unique_job.get(itemJob) != null) Employee.setJob(unique_job.get(itemJob));
        dbGroup.updateEmployeeList(Employee);

        feedEmployeeList.remove(itemEmployee);
        map = new HashMap<String, String>();
        map.put(getText(R.string.employee_selection_item_id).toString(), String.valueOf(Employee.getEmployeeID()));
        map.put(getText(R.string.employee_selection_item_last_name).toString(), Employee.getLastName());
        map.put(getText(R.string.employee_selection_item_first_name).toString(), Employee.getFirstName());
        map.put(getText(R.string.employee_selection_item_group).toString(), Employee.getGroup() <= 0 ? "" : String.valueOf(Employee.getGroup()));
        map.put(getText(R.string.employee_selection_item_status).toString(), Employee.getStatus() == 0 ? getText(R.string.out).toString() : getText(R.string.in).toString());
        feedEmployeeList.add(itemEmployee, map);
        // store current location
        int index = universal_list_view.getFirstVisiblePosition();
        View v = universal_list_view.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();
        onRadioButtonClicked(view);
        // restore current location
        universal_list_view.setSelectionFromTop(index, top);

        Activity.setEmployeeID(Employee.getEmployeeID());
        Activity.setLastName(Employee.getLastName());
        Activity.setFirstName(Employee.getFirstName());
        if (Employee.getGroup() > 0) Activity.setWorkGroup(dbGroup.getWorkGroupList(Employee.getGroup()).getGroupName());
        if (Employee.getCompany() != null) Activity.setCompany(Employee.getCompany());
        if (Employee.getLocation() != null) Activity.setLocation(Employee.getLocation());
        if (Employee.getJob() != null) Activity.setJob(Employee.getJob());
        Activity.setDate(df.format(Calendar.getInstance().getTime()));
        Activity.setTimeIn(currentDateTimeString);
        dbActivity.createActivityList(Activity);
    }

    public void employeePunchOut(View view) {
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        Activity = new DailyActivityList();
        Employee = dbGroup.getEmployeeList(Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.employee_selection_item_id).toString())));
        Employee.setStatus(0);
        dbGroup.updateEmployeeList(Employee);

        feedEmployeeList.remove(itemEmployee);
        map = new HashMap<String, String>();
        map.put(getText(R.string.employee_selection_item_id).toString(), String.valueOf(Employee.getEmployeeID()));
        map.put(getText(R.string.employee_selection_item_last_name).toString(), Employee.getLastName());
        map.put(getText(R.string.employee_selection_item_first_name).toString(), Employee.getFirstName());
        map.put(getText(R.string.employee_selection_item_group).toString(), Employee.getGroup() <= 0 ? "" : String.valueOf(Employee.getGroup()));
        map.put(getText(R.string.employee_selection_item_status).toString(), Employee.getStatus() == 0 ? getText(R.string.out).toString() : getText(R.string.in).toString());
        feedEmployeeList.add(itemEmployee, map);
        // store current location
        int index = universal_list_view.getFirstVisiblePosition();
        View v = universal_list_view.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();
        onRadioButtonClicked(view);
        // restore current location
        universal_list_view.setSelectionFromTop(index, top);

        Activity = dbActivity.getPunchedInActivityList(Employee.getEmployeeID());
        if (Activity != null && Activity.getEmployeeID() > 0) {
            long diff = General.MinuteDifference(Activity.getTimeIn(), currentDateTimeString);
            diff = diff > 0 && diff > Activity.Lunch ? diff-Activity.Lunch : 0;
            Activity.setHours(diff);
            Activity.setTimeOut(currentDateTimeString);
            dbActivity.updatePunchedInActivityList(Activity);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employee_punch_menu, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
