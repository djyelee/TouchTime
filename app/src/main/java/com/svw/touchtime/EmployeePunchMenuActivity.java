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
import android.widget.ListView;
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
    EmployeeProfileList Employee;
    private TouchTimeGeneralAdapter adapter_employee;
    ArrayList<HashMap<String, String>> feedEmployeeList;
    private ArrayList<String> unique_employee;
    ArrayList<String> feedSelectedList;
    HashMap<String, String> map;
    DailyActivityList Activity;

    String[] employee_item = new String[8];
    int[] employee_id = new int[8];
    private int itemEmployee = -1;
    boolean sort_id_ascend = true;
    boolean sort_status_ascend = true;
    boolean sort_group_ascend = true;
    boolean sort_company_ascend = true;
    boolean sort_last_name_ascend = true;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private EmployeeGroupCompanyDBWrapper dbGroup;
    private DailyActivityDBWrapper dbActivity;
    Context context;
    static final int PICK_JOB_REQUEST = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_punch_menu);
        int Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));

        universal_list_view = (ListView) findViewById(R.id.universal_list_view);
        feedEmployeeList = new ArrayList<HashMap<String, String>>();
        unique_employee = new ArrayList<String>();
        ArrayList<EmployeeProfileList> all_employee_lists;
        context = this;

        DateFormat yf = new SimpleDateFormat("yyyy");
        int year = Integer.parseInt(yf.format(Calendar.getInstance().getTime()));
        dbActivity = new DailyActivityDBWrapper(this, year);
        // database and other data
        dbGroup = new EmployeeGroupCompanyDBWrapper(this);
        // retrieve employee lists
        Employee = new EmployeeProfileList();
        all_employee_lists = dbGroup.getAllEmployeeLists();
        employee_item[0] = getText(R.string.column_key_id).toString();
        employee_item[1] = getText(R.string.column_key_status).toString();
        employee_item[2] = getText(R.string.column_key_group).toString();
        employee_item[3] = getText(R.string.column_key_last).toString();
        employee_item[4] = getText(R.string.column_key_first).toString();
        employee_item[5] = getText(R.string.column_key_company).toString();
        employee_item[6] = getText(R.string.column_key_location).toString();
        employee_item[7] = getText(R.string.column_key_job).toString();
        employee_id[0] = R.id.textViewID;
        employee_id[1] = R.id.textViewStatus;
        employee_id[2] = R.id.textViewGroup;
        employee_id[3] = R.id.textViewLastName;
        employee_id[4] = R.id.textViewFirstName;
        employee_id[5] = R.id.textViewCompany;
        employee_id[6] = R.id.textViewLocation;
        employee_id[7] = R.id.textViewJob;
        int i = 0;
        if (all_employee_lists.size() > 0) {
            do {
                map = new HashMap<String, String>();
                map.put(getText(R.string.column_key_id).toString(), String.valueOf(all_employee_lists.get(i).getEmployeeID()));
                map.put(getText(R.string.column_key_status).toString(), all_employee_lists.get(i).getStatus() == 0 ? "" : getText(R.string.y).toString());
                map.put(getText(R.string.column_key_group).toString(), all_employee_lists.get(i).getGroup() <= 0 ? "" : String.valueOf(all_employee_lists.get(i).getGroup()));
                map.put(getText(R.string.column_key_last).toString(), all_employee_lists.get(i).getLastName());
                map.put(getText(R.string.column_key_first).toString(), all_employee_lists.get(i).getFirstName());
                map.put(getText(R.string.column_key_company).toString(), all_employee_lists.get(i).getCompany());
                map.put(getText(R.string.column_key_location).toString(), all_employee_lists.get(i).getLocation());
                map.put(getText(R.string.column_key_job).toString(), all_employee_lists.get(i).getJob());
                feedEmployeeList.add(map);
                if (itemEmployee < 0 && map.get(getText(R.string.column_key_status).toString()).equals(getText(R.string.out).toString())) itemEmployee = i;
            } while (++i < all_employee_lists.size());
            if (itemEmployee < 0) itemEmployee = 0;        // all employees are punched in, then display the first one
            Employee = dbGroup.getEmployeeList(all_employee_lists.get(itemEmployee).getEmployeeID());
            universal_list_view.setItemsCanFocus(true);
            // universal_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.employee_punch_header, null, false), null, false);
            adapter_employee = new TouchTimeGeneralAdapter(this, feedEmployeeList, R.layout.employee_punch_view, employee_item, employee_id);
            // universal_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.employee_punch_header, null, false), null, false);
            universal_list_view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            universal_list_view.setAdapter(adapter_employee);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dbGroup.closeDB();
                    dbActivity.closeDB();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
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
               itemEmployee = position;
               String G = feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_id).toString());
                if (!G.isEmpty()) {     // selected something
                    if (universal_list_view.isItemChecked(itemEmployee)) {      // item is checked
                        Employee = dbGroup.getEmployeeList(Integer.parseInt(G));
                        unique_employee.add(G);
                    } else {                                                    // item is unchecked
                        unique_employee.remove(unique_employee.indexOf(G));
                    }
                }
                HighlightListItem(itemEmployee);
                getCompanyJobLocation();
            }
        });
    }

    private void HighlightListItem(int position) {
        adapter_employee.setSelectedItem(position);
        adapter_employee.notifyDataSetChanged();
    }

    public void getCompanyJobLocation() {
 /*       AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int i;
        if (Employee.getCompany().isEmpty()) {
            builder.setMessage(R.string.company_select_message).setTitle(R.string.empty_entry_title);
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
                itemJob = unique_job.indexOf(Employee.getJob());
                itemJob = itemJob < 0 ? -1 : itemJob;
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
        */
    }

    public void onSortIDButtonClicked(View view) {
        if (feedEmployeeList.size() == 0) return;
        String Items;
        Items = getText(R.string.column_key_id).toString();
        int lastSelectedID = 0;

        if (itemEmployee >= 0) lastSelectedID = Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_id).toString()));
        General.SortIntegerList(feedEmployeeList, Items, sort_id_ascend);
        if (itemEmployee >= 0) {
            itemEmployee = General.GetIntegerIndex(feedEmployeeList, getText(R.string.column_key_id).toString(), lastSelectedID);
            adapter_employee.setSelectedItem(itemEmployee);
        }
        markSelectedItems();
        sort_id_ascend = !sort_id_ascend;
        adapter_employee.notifyDataSetChanged();
    }

    public void onSortStatusButtonClicked(View view) {
        if (feedEmployeeList.size() == 0) return;
        String [] Items = new String [1];
        Items[0] = getText(R.string.column_key_status).toString();
        int lastSelectedID = 0;

        if (itemEmployee >= 0) lastSelectedID = Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_id).toString()));
        General.SortStringList(feedEmployeeList, Items, sort_status_ascend);
        if (itemEmployee >= 0) {
            itemEmployee = General.GetIntegerIndex(feedEmployeeList, getText(R.string.column_key_id).toString(), lastSelectedID);
            adapter_employee.setSelectedItem(itemEmployee);
        }
        markSelectedItems();
        sort_status_ascend = !sort_status_ascend;
        adapter_employee.notifyDataSetChanged();
    }

    public void onSortGroupButtonClicked(View view) {
       if (feedEmployeeList.size() == 0) return;
        String Items;
        Items = getText(R.string.column_key_group).toString();
        int lastSelectedID = 0;

        if (itemEmployee >= 0) lastSelectedID = Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_id).toString()));
        General.SortIntegerList(feedEmployeeList, Items, sort_group_ascend);
        if (itemEmployee >= 0) {
            itemEmployee = General.GetIntegerIndex(feedEmployeeList, getText(R.string.column_key_id).toString(), lastSelectedID);
            adapter_employee.setSelectedItem(itemEmployee);
        }
        markSelectedItems();
        sort_group_ascend = !sort_group_ascend;
        adapter_employee.notifyDataSetChanged();
    }

    public void onSortCompanyButtonClicked(View view) {
        if (feedEmployeeList.size() == 0) return;
        String [] Items = new String [3];
        Items[0] = getText(R.string.column_key_company).toString();
        Items[1] = getText(R.string.column_key_location).toString();
        Items[2] = getText(R.string.column_key_job).toString();
        int lastSelectedID = 0;

        if (itemEmployee >= 0) lastSelectedID = Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_id).toString()));
        General.SortStringList(feedEmployeeList, Items, sort_company_ascend);
        if (itemEmployee >= 0) {
            itemEmployee = General.GetIntegerIndex(feedEmployeeList, getText(R.string.column_key_id).toString(), lastSelectedID);
            adapter_employee.setSelectedItem(itemEmployee);
        }
        markSelectedItems();
        sort_company_ascend = !sort_company_ascend;
        adapter_employee.notifyDataSetChanged();
    }

    public void onSortLastNameButtonClicked(View view) {
        if (feedEmployeeList.size() == 0) return;
        String [] Items = new String[2];
        String [] Data = new String[2];
        Items[0] = getText(R.string.column_key_last).toString();
        Items[1] = getText(R.string.column_key_first).toString();
        if (itemEmployee >= 0) {
            Data [0] = feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_last).toString());
            Data [1] = feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_first).toString());
        }
        General.SortStringList(feedEmployeeList, Items, sort_last_name_ascend);
        if (itemEmployee >= 0) {
            itemEmployee = General.GetStringIndex(feedEmployeeList, Items, Data);
            adapter_employee.setSelectedItem(itemEmployee);
        }
        markSelectedItems();
        sort_last_name_ascend = !sort_last_name_ascend;
        adapter_employee.notifyDataSetChanged();
    }

    public void markSelectedItems () {
        for (int j = 0; j < feedEmployeeList.size(); j++) {
            for (int i=0; i<unique_employee.size(); i++) {
                if (feedEmployeeList.get(j).get(getText(R.string.column_key_id).toString()).equals(unique_employee.get(i))) {
                    universal_list_view.setItemChecked(j, true);
                    break;   // quit after checked
                } else {
                    universal_list_view.setItemChecked(j, false);
                }
            }
        }
    }

    public void onPunchInButtonClicked(final View view) {
  /*      AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
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
        */
    }

    public void onPunchOutButtonClicked(final View view) {
 /*       AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
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
        */
    }

    public void onSelectJobButtonClicked(final View view) {
        if (itemEmployee >= 0 && unique_employee.size() > 0) {
            Intent intent = new Intent(this, CompanyJobLocationSelectionActivity.class);
            ArrayList<String> CompanyLocationJob = new ArrayList<>();
            CompanyLocationJob.add(getText(R.string.title_activity_employee_punch_menu).toString());        // caller
            // use the last selected one as default
            CompanyLocationJob.add(Employee.getCompany());              // company
            CompanyLocationJob.add(Employee.getLocation());             // location
            CompanyLocationJob.add(Employee.getJob());                  // job
            intent.putStringArrayListExtra("CompanyLocationJob", CompanyLocationJob);
            startActivityForResult(intent, PICK_JOB_REQUEST);
        } else {
            // put the dialog inside so it will not dim the screen when returns.
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void onRemoveJobButtonClicked(final View view) {
        if (itemEmployee >= 0 && unique_employee.size() > 0) {
            Employee.setCompany("");
            Employee.setLocation("");
            Employee.setJob("");
            EmployeeProfileList emp = new EmployeeProfileList();
            for (int i = 0; i < unique_employee.size(); i++) {
                emp = dbGroup.getEmployeeList(Integer.parseInt(unique_employee.get(i)));
                emp.setCompany("");
                emp.setLocation("");
                emp.setJob("");
                map = new HashMap<String, String>();
                map.put(getText(R.string.column_key_id).toString(), String.valueOf(emp.getEmployeeID()));
                map.put(getText(R.string.column_key_status).toString(), emp.getStatus() == 0 ? "" : getText(R.string.y).toString());
                map.put(getText(R.string.column_key_group).toString(), emp.getGroup() <= 0 ? "" : String.valueOf(emp.getGroup()));
                map.put(getText(R.string.column_key_last).toString(), emp.getLastName());
                map.put(getText(R.string.column_key_first).toString(), emp.getFirstName());
                map.put(getText(R.string.column_key_company).toString(), emp.getCompany());
                map.put(getText(R.string.column_key_location).toString(), emp.getLocation());
                map.put(getText(R.string.column_key_job).toString(), emp.getJob());
                for (int j = 0; j < feedEmployeeList.size(); j++) {
                    if (feedEmployeeList.get(j).get(getText(R.string.column_key_id).toString()).equals(unique_employee.get(i))) {
                        feedEmployeeList.set(j, map);
                        universal_list_view.setItemChecked(j, false);
                    }
                }
                dbGroup.updateEmployeeList(emp);
            }
            unique_employee.clear();
            itemEmployee = -1;                                    // set to -1 to display without highlight
            adapter_employee.setSelectedItem(itemEmployee);       // set to -1 to display without highlight
            adapter_employee.notifyDataSetChanged();
        } else {
            // put the dialog inside so it will not dim the screen when returns.
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
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
                EmployeeProfileList emp = new EmployeeProfileList();
                // update the selected employee
                Employee.setCompany(CompanyLocationJob.get(1));
                Employee.setLocation(CompanyLocationJob.get(2));
                Employee.setJob(CompanyLocationJob.get(3));
                // update all that are selected
                for (int i=0; i<unique_employee.size(); i++) {
                    map = new HashMap<String, String>();
                    emp = dbGroup.getEmployeeList(Integer.parseInt(unique_employee.get(i)));
                    emp.setCompany(CompanyLocationJob.get(1));
                    emp.setLocation(CompanyLocationJob.get(2));
                    emp.setJob(CompanyLocationJob.get(3));
                    map.put(getText(R.string.column_key_id).toString(), String.valueOf(emp.getEmployeeID()));
                    map.put(getText(R.string.column_key_status).toString(), emp.getStatus() == 0 ? "" : getText(R.string.y).toString());
                    map.put(getText(R.string.column_key_group).toString(), emp.getGroup() <= 0 ? "" : String.valueOf(emp.getGroup()));
                    map.put(getText(R.string.column_key_last).toString(), emp.getLastName());
                    map.put(getText(R.string.column_key_first).toString(), emp.getFirstName());
                    map.put(getText(R.string.column_key_company).toString(), emp.getCompany());
                    map.put(getText(R.string.column_key_location).toString(), emp.getLocation());
                    map.put(getText(R.string.column_key_job).toString(), emp.getJob());
                    for (int j = 0; j < feedEmployeeList.size(); j++) {
                        if (feedEmployeeList.get(j).get(getText(R.string.column_key_id).toString()).equals(unique_employee.get(i))) {
                            feedEmployeeList.set(j, map);
                            universal_list_view.setItemChecked(j, true);
                            break;
                        }
                    }
                    dbGroup.updateEmployeeList(emp);
                }
                adapter_employee.setSelectedItem(itemEmployee);
                adapter_employee.notifyDataSetChanged();
             }
        }
    }


    public void employeePunchIn(View view) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        Activity = new DailyActivityList();
        Employee = dbGroup.getEmployeeList(Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_id).toString())));
        Employee.setStatus(1);
        dbGroup.updateEmployeeList(Employee);

        feedEmployeeList.remove(itemEmployee);
        map = new HashMap<String, String>();
        map.put(getText(R.string.column_key_id).toString(), String.valueOf(Employee.getEmployeeID()));
        map.put(getText(R.string.column_key_last).toString(), Employee.getLastName());
        map.put(getText(R.string.column_key_first).toString(), Employee.getFirstName());
        map.put(getText(R.string.column_key_group).toString(), Employee.getGroup() <= 0 ? "" : String.valueOf(Employee.getGroup()));
        map.put(getText(R.string.column_key_status).toString(), Employee.getStatus() == 0 ? getText(R.string.out).toString() : getText(R.string.in).toString());
        feedEmployeeList.add(itemEmployee, map);

        Activity.setEmployeeID(Employee.getEmployeeID());
        Activity.setLastName(Employee.getLastName());
        Activity.setFirstName(Employee.getFirstName());
        if (Employee.getGroup() > 0) Activity.setWorkGroup(dbGroup.getWorkGroupList(Employee.getGroup()).getGroupName());
        if (!Employee.getCompany().isEmpty()) Activity.setCompany(Employee.getCompany());
        if (!Employee.getLocation().isEmpty()) Activity.setLocation(Employee.getLocation());
        if (!Employee.getJob().isEmpty()) Activity.setJob(Employee.getJob());
        Activity.setDate(df.format(Calendar.getInstance().getTime()));
        Activity.setTimeIn(currentDateTimeString);
        dbActivity.createActivityList(Activity);
    }

    public void employeePunchOut(View view) {
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        Activity = new DailyActivityList();
        Employee = dbGroup.getEmployeeList(Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_id).toString())));
        Employee.setStatus(0);
        dbGroup.updateEmployeeList(Employee);

        feedEmployeeList.remove(itemEmployee);
        map = new HashMap<String, String>();
        map.put(getText(R.string.column_key_id).toString(), String.valueOf(Employee.getEmployeeID()));
        map.put(getText(R.string.column_key_last).toString(), Employee.getLastName());
        map.put(getText(R.string.column_key_first).toString(), Employee.getFirstName());
        map.put(getText(R.string.column_key_group).toString(), Employee.getGroup() <= 0 ? "" : String.valueOf(Employee.getGroup()));
        map.put(getText(R.string.column_key_status).toString(), Employee.getStatus() == 0 ? getText(R.string.out).toString() : getText(R.string.in).toString());
        feedEmployeeList.add(itemEmployee, map);

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
        } else if (id == android.R.id.home) {
            dbGroup.closeDB();
            dbActivity.closeDB();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
