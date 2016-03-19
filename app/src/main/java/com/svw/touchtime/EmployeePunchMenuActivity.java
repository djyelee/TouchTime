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
import android.view.WindowManager;
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


public class EmployeePunchMenuActivity extends ActionBarActivity {
    public TextView Current_date;
    public TextView Current_time;
    private ListView universal_list_view;
    EmployeeProfileList Employee;
    private TouchTimeGeneralAdapter adapter_employee;
    ArrayList<HashMap<String, String>> feedEmployeeList;
    private ArrayList<String> unique_employeeID;
    private ArrayList<Integer> unique_itemNumber;
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
    boolean select_all = false;
    Button btn_select_all;
    boolean punch_in = false;
    int index;
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

        btn_select_all = (Button) findViewById(R.id.employee_select_all);
        universal_list_view = (ListView) findViewById(R.id.universal_list_view);
        feedEmployeeList = new ArrayList<HashMap<String, String>>();
        unique_employeeID = new ArrayList<String>();
        unique_itemNumber = new ArrayList<Integer>();
        ArrayList<EmployeeProfileList> all_employee_lists;
        context = this;

        btn_select_all.setText(getText(R.string.button_select_all));
        DateFormat yf = new SimpleDateFormat("yyyy");
        int year = Integer.parseInt(yf.format(Calendar.getInstance().getTime()));
        dbActivity = new DailyActivityDBWrapper(this, year);
        // database and other data
        dbGroup = new EmployeeGroupCompanyDBWrapper(this);
        // retrieve employee lists
        Employee = new EmployeeProfileList();
        Activity = new DailyActivityList();
        all_employee_lists = dbGroup.getAllEmployeeLists();
        employee_item[0] = getText(R.string.column_key_employee_id).toString();
        employee_item[1] = getText(R.string.column_key_status).toString();
        employee_item[2] = getText(R.string.column_key_group_id).toString();
        employee_item[3] = getText(R.string.column_key_last_name).toString();
        employee_item[4] = getText(R.string.column_key_first_name).toString();
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
                map.put(getText(R.string.column_key_employee_id).toString(), String.valueOf(all_employee_lists.get(i).getEmployeeID()));
                map.put(getText(R.string.column_key_status).toString(), all_employee_lists.get(i).getStatus() == 0 ? getText(R.string.out).toString() : getText(R.string.in).toString());
                map.put(getText(R.string.column_key_group_id).toString(), all_employee_lists.get(i).getGroup() <= 0 ? "" : String.valueOf(all_employee_lists.get(i).getGroup()));
                map.put(getText(R.string.column_key_last_name).toString(), all_employee_lists.get(i).getLastName());
                map.put(getText(R.string.column_key_first_name).toString(), all_employee_lists.get(i).getFirstName());
                map.put(getText(R.string.column_key_company).toString(), all_employee_lists.get(i).getCompany());
                map.put(getText(R.string.column_key_location).toString(), all_employee_lists.get(i).getLocation());
                map.put(getText(R.string.column_key_job).toString(), all_employee_lists.get(i).getJob());
                map.put(getText(R.string.column_key_active).toString(), String.valueOf(all_employee_lists.get(i).getActive()));
                map.put(getText(R.string.column_key_current).toString(), String.valueOf(all_employee_lists.get(i).getCurrent()));
                map.put(getText(R.string.column_key_doc_exp).toString(), all_employee_lists.get(i).getDocExp());
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
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_punch_title);
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
               itemEmployee = position;
               String G = feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_employee_id).toString());
                if (!G.isEmpty()) {     // selected something
                    if (universal_list_view.isItemChecked(itemEmployee)) {      // item is checked
                        Employee = dbGroup.getEmployeeList(Integer.parseInt(G));
                        unique_employeeID.add(G);
                        unique_itemNumber.add(itemEmployee);
                    } else {                                                    // item is unchecked
                        unique_employeeID.remove(unique_employeeID.indexOf(G));
                        unique_itemNumber.remove(unique_itemNumber.indexOf(itemEmployee));
                    }
                }
                HighlightListItem(itemEmployee);
            }
        });
    }

    public void onPunchInButtonClicked(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (itemEmployee >= 0 && unique_employeeID.size() > 0) {
            boolean already_punched_in = false, already_has_job = true;
            boolean employee_active = true, employee_current = true;
            for (int i = 0; i < unique_employeeID.size(); i++) {      // check if anyone is already punched in
                if (feedEmployeeList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_status)).equals(getText(R.string.in))) {
                    already_punched_in = true;
                    break;
                }
            }
            for (int i = 0; i < unique_employeeID.size(); i++) {      // check if anyone is already punched in
                if (feedEmployeeList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_company)).isEmpty() ||
                        feedEmployeeList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_location)).isEmpty() ||
                        feedEmployeeList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_job)).isEmpty()) {
                already_has_job = false;
                    break;
                }
            }
            for (int i = 0; i < unique_employeeID.size(); i++) {      // check if anyone is already punched in
                if (Integer.parseInt(feedEmployeeList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_active))) == 0) {
                    employee_active = false;
                    break;
                }
            }
            for (int i = 0; i < unique_employeeID.size(); i++) {      // check if anyone is already punched in
                if (Integer.parseInt(feedEmployeeList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_current))) == 0 ||
                    feedEmployeeList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_doc_exp)).compareTo(df.format(Calendar.getInstance().getTime()))<0) {
                    employee_current = false;
                    break;
                }
            }
           if (already_punched_in) {   // since they are all punched in, they should all have a job
                builder.setMessage(getText(R.string.employee_already_punched_in_message).toString()).setTitle(R.string.employee_punch_title);
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
               employeePunchIn();
            }
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        } else {
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_punch_title);
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
        if (itemEmployee >= 0 && unique_employeeID.size() > 0) {
            boolean already_punched_out = false;
            for (int i = 0; i < unique_employeeID.size(); i++) {      // check if anyone is already punched in
                if (feedEmployeeList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_status)).equals(getText(R.string.out))) {
                    already_punched_out = true;
                    break;
                }
            }
             if (already_punched_out) {
                 builder.setMessage(getText(R.string.employee_already_punched_out_message).toString()).setTitle(R.string.employee_punch_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else {
                builder.setMessage(getText(R.string.employee_punch_out_message).toString()).setTitle(R.string.employee_punch_title);
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
            General.TouchTimeDialog(dialog, view);
        } else {
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        }
    }

    public void employeePunchIn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
        builder.setMessage(getText(R.string.employee_group_not_punch_in_message).toString()).setTitle(R.string.employee_punch_title);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        General.TouchTimeDialog(dialog, this.findViewById(android.R.id.content));

//          showMessageBox(view);




/*
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        WorkGroupList WorkGroupNew = new WorkGroupList();
        EmployeeProfileList EmployeeNew = new EmployeeProfileList();
        for (int i=0; i<unique_employeeID.size(); i++) {      // check if anyone is already punched in
            EmployeeNew = dbGroup.getEmployeeList(Integer.parseInt(feedEmployeeList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_employee_id).toString())));
            WorkGroupNew = dbGroup.getWorkGroupList(EmployeeNew.getGroup());
            if (EmployeeNew.getGroup() > 0) {                                               // employee belongs to a group
                if (WorkGroupNew.getStatus() == 0) {          // and the group is not punched in, then punch in
                    builder.setMessage(getText(R.string.employee_group_not_punch_in_message).toString()).setTitle(R.string.employee_punch_title);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            updateDailyActivity(i);
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                } else if (EmployeeNew.getCompany() == WorkGroupNew.getCompany() && EmployeeNew.getLocation() == WorkGroupNew.getLocation() && EmployeeNew.getJob() == WorkGroupNew.getJob()){
                    builder.setMessage(getText(R.string.employee_group_not_punch_in_message).toString()).setTitle(R.string.employee_punch_title);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            updateDailyActivity(i);
                        }
                    });
                } else {
                    builder.setMessage(getText(R.string. employee_punch_in_different_job_message).toString()).setTitle(R.string.employee_punch_title);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            updateDailyActivity(i);
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                }
             } else if (EmployeeNew.getGroup() <= 0){
                builder.setMessage(getText(R.string.employee_punch_in_message).toString()).setTitle(R.string.employee_punch_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateDailyActivity(i);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            }
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        }
        adapter_employee.notifyDataSetChanged();
        */
    }

    public void showMessageBox(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        builder.setMessage(getText(R.string.employee_group_not_punch_in_message).toString()).setTitle(R.string.employee_punch_title);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        General.TouchTimeDialog(dialog, view);
    }

    public void updateDailyActivity(int index) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        EmployeeProfileList EmployeeNew = new EmployeeProfileList();
        EmployeeNew = dbGroup.getEmployeeList(Integer.parseInt(feedEmployeeList.get(unique_itemNumber.get(index)).get(getText(R.string.column_key_employee_id).toString())));

        feedEmployeeList.get(unique_itemNumber.get(index)).put(getText(R.string.column_key_status).toString(), getText(R.string.in).toString());
        dbGroup.updateEmployeeListStatus(Integer.parseInt(unique_employeeID.get(index)), 1);
        Activity = new DailyActivityList();
        Activity.setEmployeeID(EmployeeNew.getEmployeeID());
        Activity.setLastName(EmployeeNew.getLastName());
        Activity.setFirstName(EmployeeNew.getFirstName());
        if (EmployeeNew.getGroup() > 0)
            Activity.setWorkGroup(dbGroup.getWorkGroupList(EmployeeNew.getGroup()).getGroupName());
        if (!EmployeeNew.getCompany().isEmpty())
            Activity.setCompany(EmployeeNew.getCompany());
        if (!EmployeeNew.getLocation().isEmpty())
            Activity.setLocation(EmployeeNew.getLocation());
        if (!EmployeeNew.getJob().isEmpty()) Activity.setJob(EmployeeNew.getJob());
        Activity.setDate(df.format(Calendar.getInstance().getTime()));
        Activity.setTimeIn(currentDateTimeString);
        dbActivity.createActivityList(Activity);
    }

    public void employeePunchOut(View view) {
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        for (int i=0; i<unique_employeeID.size(); i++) {      // check if anyone is already punched in
            feedEmployeeList.get(unique_itemNumber.get(i)).put(getText(R.string.column_key_status).toString(), getText(R.string.out).toString());
            dbGroup.updateEmployeeListStatus(Integer.parseInt(unique_employeeID.get(i)), 0);

            // employee belongs to a group ?
            //      No, then go ahead and punch out
            //      Yes, check if the group is already punched out
            //          Yes, then punch out and send a message  -- Group is punched out, punch out as well!
            //          No, is the job the same as the group?
            //              No, punch out and send a message    -- Group is still punched in to a different job, punch out employee anyway?
            //              Yes, punch out and send a message   -- Group is still punched in to the same job, punch out employee anyway?

            Activity = dbActivity.getPunchedInActivityList(Integer.parseInt(unique_employeeID.get(i)));
            if (Activity != null && Activity.getEmployeeID() > 0) {
                long diff = General.MinuteDifference(Activity.getTimeIn(), currentDateTimeString);
                diff = diff > 0 && diff > Activity.Lunch ? diff-Activity.Lunch : 0;
                Activity.setHours(diff);
                Activity.setTimeOut(currentDateTimeString);
                dbActivity.updatePunchedInActivityList(Activity);
            }
        }
        adapter_employee.notifyDataSetChanged();
    }

    public void onSelectAllButtonClicked(final View view) {
        if (feedEmployeeList.size() == 0) return;
        if (select_all) {
            btn_select_all.setText(getText(R.string.button_select_all));
            unique_employeeID.clear();
        } else {
            String ID;
            unique_employeeID.clear();
            btn_select_all.setText(getText(R.string.button_deselect_all));
            for (int i=0; i<feedEmployeeList.size(); i++) {
                ID = feedEmployeeList.get(i).get(getText(R.string.column_key_employee_id).toString());
                if (!ID.isEmpty()) unique_employeeID.add(ID);
            }
        }
        markSelectedItems();     // after clearing unique_employeeID all will be unchecked
        select_all = !select_all;
    }

    public void onSelectJobButtonClicked(final View view) {
        if (feedEmployeeList.size() == 0) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (itemEmployee >= 0 && unique_employeeID.size() > 0) {
            boolean already_punched_in = false;
            for (int i = 0; i < unique_employeeID.size(); i++) {      // check if anyone is lready punched in
                if (feedEmployeeList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_status)).equals(getText(R.string.in))) {
                    already_punched_in = true;
                    break;
                }
            }
            if (already_punched_in) {
                builder.setMessage(getText(R.string.employee_already_punched_in_message).toString()).setTitle(R.string.employee_punch_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                General.TouchTimeDialog(dialog, view);
            } else {
                Intent intent = new Intent(this, CompanyJobLocationSelectionActivity.class);
                ArrayList<String> CompanyLocationJob = new ArrayList<>();
                CompanyLocationJob.add(getText(R.string.title_activity_employee_punch_menu).toString());        // caller
                // use the last selected one as default
                CompanyLocationJob.add(Employee.getCompany());              // company
                CompanyLocationJob.add(Employee.getLocation());             // location
                CompanyLocationJob.add(Employee.getJob());                  // job
                intent.putStringArrayListExtra("CompanyLocationJob", CompanyLocationJob);
                startActivityForResult(intent, PICK_JOB_REQUEST);
            }
        } else {
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        }
    }

    public void onRemoveJobButtonClicked(final View view) {
        if (feedEmployeeList.size() == 0) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (itemEmployee >= 0 && unique_employeeID.size() > 0) {
            boolean already_punched_in = false;
            for (int i = 0; i < unique_employeeID.size(); i++) {      // check if anyone is lready punched in
                if (feedEmployeeList.get(unique_itemNumber.get(i)).get(getText(R.string.column_key_status)).equals(getText(R.string.in))) {
                    already_punched_in = true;
                    break;
                }
            }
            if (already_punched_in) {
                builder.setMessage(getText(R.string.employee_already_punched_in_message).toString()).setTitle(R.string.employee_punch_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                General.TouchTimeDialog(dialog, view);
            } else {
                Employee.setCompany("");
                Employee.setLocation("");
                Employee.setJob("");
                for (int i = 0; i < unique_employeeID.size(); i++) {
                     for (int j = 0; j < feedEmployeeList.size(); j++) {
                        if (feedEmployeeList.get(j).get(getText(R.string.column_key_employee_id).toString()).equals(unique_employeeID.get(i))) {
                            // feedEmployeeList.set(j, map);
                            feedEmployeeList.get(j).put(getText(R.string.column_key_company).toString(), "");
                            feedEmployeeList.get(j).put(getText(R.string.column_key_location).toString(), "");
                            feedEmployeeList.get(j).put(getText(R.string.column_key_job).toString(), "");
                            universal_list_view.setItemChecked(j, false);
                        }
                    }
                    dbGroup.clearEmployeeListCompanyLocationJob(Integer.parseInt(unique_employeeID.get(i)));
                }
                unique_employeeID.clear();
                itemEmployee = -1;                                    // set to -1 to display without highlight
                adapter_employee.setSelectedItem(itemEmployee);       // set to -1 to display without highlight
                adapter_employee.notifyDataSetChanged();
            }
        } else {
            // put the dialog inside so it will not dim the screen when returns.
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        }
    }

    private void HighlightListItem(int position) {
        adapter_employee.setSelectedItem(position);
        adapter_employee.notifyDataSetChanged();
    }

    public void onSortIDButtonClicked(View view) {
        if (feedEmployeeList.size() == 0) return;
        String Items;
        Items = getText(R.string.column_key_employee_id).toString();
        int lastSelectedID = 0;

        if (itemEmployee >= 0) lastSelectedID = Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_employee_id).toString()));
        General.SortIntegerList(feedEmployeeList, Items, sort_id_ascend);
        if (itemEmployee >= 0) {
            itemEmployee = General.GetIntegerIndex(feedEmployeeList, getText(R.string.column_key_employee_id).toString(), lastSelectedID);
            adapter_employee.setSelectedItem(itemEmployee);
        }
        markSelectedItems();
        sort_id_ascend = !sort_id_ascend;
        adapter_employee.notifyDataSetChanged();
    }

    public void onSortStatusButtonClicked(View view) {
        if (feedEmployeeList.size() == 0) return;
        String [] Items = new String [7];
        Items[0] = getText(R.string.column_key_status).toString();
        Items[1] = getText(R.string.column_key_company).toString();
        Items[2] = getText(R.string.column_key_location).toString();
        Items[3] = getText(R.string.column_key_job).toString();
        Items[4] = getText(R.string.column_key_group_id).toString();
        Items[5] = getText(R.string.column_key_last_name).toString();
        Items[6] = getText(R.string.column_key_first_name).toString();
        int lastSelectedID = 0;

        if (itemEmployee >= 0) lastSelectedID = Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_employee_id).toString()));
        General.SortStringList(feedEmployeeList, Items, sort_status_ascend);
        if (itemEmployee >= 0) {
            itemEmployee = General.GetIntegerIndex(feedEmployeeList, getText(R.string.column_key_employee_id).toString(), lastSelectedID);
            adapter_employee.setSelectedItem(itemEmployee);
        }
        markSelectedItems();
        sort_status_ascend = !sort_status_ascend;
        adapter_employee.notifyDataSetChanged();
    }

    public void onSortGroupButtonClicked(View view) {
        if (feedEmployeeList.size() == 0) return;
        String [] Items = new String [7];
        Items[0] = getText(R.string.column_key_group_id).toString();
        Items[1] = getText(R.string.column_key_company).toString();
        Items[2] = getText(R.string.column_key_location).toString();
        Items[3] = getText(R.string.column_key_job).toString();
        Items[4] = getText(R.string.column_key_status).toString();
        Items[5] = getText(R.string.column_key_last_name).toString();
        Items[6] = getText(R.string.column_key_first_name).toString();
        int lastSelectedID = 0;

        if (itemEmployee >= 0) lastSelectedID = Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_employee_id).toString()));
        General.SortStringList(feedEmployeeList, Items, sort_group_ascend);
        if (itemEmployee >= 0) {
            itemEmployee = General.GetIntegerIndex(feedEmployeeList, getText(R.string.column_key_employee_id).toString(), lastSelectedID);
            adapter_employee.setSelectedItem(itemEmployee);
        }
        markSelectedItems();
        sort_group_ascend = !sort_group_ascend;
        adapter_employee.notifyDataSetChanged();
    }

    public void onSortCompanyButtonClicked(View view) {
        if (feedEmployeeList.size() == 0) return;
        String [] Items = new String [6];
        Items[0] = getText(R.string.column_key_company).toString();
        Items[1] = getText(R.string.column_key_location).toString();
        Items[2] = getText(R.string.column_key_job).toString();
        Items[3] = getText(R.string.column_key_status).toString();
        Items[4] = getText(R.string.column_key_last_name).toString();
        Items[5] = getText(R.string.column_key_first_name).toString();
        int lastSelectedID = 0;

        if (itemEmployee >= 0) lastSelectedID = Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_employee_id).toString()));
        General.SortStringList(feedEmployeeList, Items, sort_company_ascend);
        if (itemEmployee >= 0) {
            itemEmployee = General.GetIntegerIndex(feedEmployeeList, getText(R.string.column_key_employee_id).toString(), lastSelectedID);
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
        Items[0] = getText(R.string.column_key_last_name).toString();
        Items[1] = getText(R.string.column_key_first_name).toString();
        if (itemEmployee >= 0) {
            Data [0] = feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_last_name).toString());
            Data [1] = feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_first_name).toString());
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
        unique_itemNumber.clear();
        for (int j = 0; j < feedEmployeeList.size(); j++) {
            universal_list_view.setItemChecked(j, false);   // set to unchecked by default
            for (int i=0; i<unique_employeeID.size(); i++) {
                if (feedEmployeeList.get(j).get(getText(R.string.column_key_employee_id).toString()).equals(unique_employeeID.get(i))) {
                    unique_itemNumber.add(j);
                    universal_list_view.setItemChecked(j, true);
                    break;   // quit after checked
                }
            }
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
                Employee.setCompany(CompanyLocationJob.get(1));
                Employee.setLocation(CompanyLocationJob.get(2));
                Employee.setJob(CompanyLocationJob.get(3));
                // update all that are selected
                for (int i=0; i<unique_employeeID.size(); i++) {
                    for (int j = 0; j < feedEmployeeList.size(); j++) {
                        if (feedEmployeeList.get(j).get(getText(R.string.column_key_employee_id).toString()).equals(unique_employeeID.get(i))) {
                            // feedEmployeeList.set(j, map);
                            feedEmployeeList.get(j).put(getText(R.string.column_key_company).toString(), CompanyLocationJob.get(1));
                            feedEmployeeList.get(j).put(getText(R.string.column_key_location).toString(), CompanyLocationJob.get(2));
                            feedEmployeeList.get(j).put(getText(R.string.column_key_job).toString(), CompanyLocationJob.get(3));
                            universal_list_view.setItemChecked(j, true);
                            break;
                        }
                    }
                    dbGroup.updateEmployeeListCompanyLocationJob(Integer.parseInt(unique_employeeID.get(i)), CompanyLocationJob.get(1),
                            CompanyLocationJob.get(2), CompanyLocationJob.get(3));
                }
                adapter_employee.setSelectedItem(itemEmployee);
                adapter_employee.notifyDataSetChanged();
             }
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
