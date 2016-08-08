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


public class EmployeePunchMenuActivity extends ActionBarActivity {
    public TextView Current_date;
    public TextView Current_time;
    private ListView universal_list_view;
    EmployeeProfileList Employee;
    EmployeeProfileList EmployeeSelected;
    private TouchTimeGeneralAdapter adapter_employee;
    ArrayList<HashMap<String, String>> feedEmployeeList;
    private ArrayList<Integer> unique_employeeID;
    private ArrayList<Integer> valid_employeeID;
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
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private EmployeeGroupCompanyDBWrapper dbGroup;
    private DailyActivityDBWrapper dbActivity;
    Context context;
    static final int PICK_JOB_REQUEST = 123;
    static final int MOVE_JOB_REQUEST = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_punch_menu);
        int Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);

        btn_select_all = (Button) findViewById(R.id.employee_select_all);
        universal_list_view = (ListView) findViewById(R.id.universal_list_view);
        feedEmployeeList = new ArrayList<HashMap<String, String>>();
        unique_employeeID = new ArrayList<Integer>();
        valid_employeeID = new ArrayList<Integer>();
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
                if (all_employee_lists.get(i).getActive() == 0 || all_employee_lists.get(i).getCurrent() == 0) continue;  // do not list inactive or not current employees
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
            adapter_employee = new TouchTimeGeneralAdapter(this, feedEmployeeList, R.layout.employee_punch_view, employee_item, employee_id, 60);
            // universal_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.employee_punch_header, null, false), null, false);
            universal_list_view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            universal_list_view.setAdapter(adapter_employee);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
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
        DateFormat df = new SimpleDateFormat(getText(R.string.date_MDY_format).toString());
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
                        unique_employeeID.add(Integer.parseInt(G));
                        unique_itemNumber.add(itemEmployee);
                    } else {                                                    // item is unchecked
                        unique_employeeID.remove(unique_employeeID.indexOf(Integer.parseInt(G)));
                        unique_itemNumber.remove(unique_itemNumber.indexOf(itemEmployee));
                    }
                }
                HighlightListItem(itemEmployee);
            }
        });
    }

    public void onPunchInButtonClicked(final View view) {
        if (itemEmployee >= 0 && unique_employeeID.size() > 0) {
            DateFormat df = new SimpleDateFormat(getText(R.string.date_YMD_format).toString());
            LinkedList validEmployee = new LinkedList();
            final LinkedList listIndex = new LinkedList();
            String Name;
            for (int i = 0; i < unique_employeeID.size(); i++) {      // check if anyone is already punched in
                EmployeeSelected = dbGroup.getEmployeeList(unique_employeeID.get(i));
                Name = EmployeeSelected.getLastName() + ", " + EmployeeSelected.getFirstName() + ", " + String.valueOf(EmployeeSelected.getEmployeeID());
                if (dbGroup.checkEmployeeID(EmployeeSelected.getEmployeeID())) {    // This always returns true. It is needed to include others in the scope.
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                    if (EmployeeSelected.getStatus() == 1) {
                        validEmployee.add(false);
                        builder.setMessage(getText(R.string.employee_already_punched_in_message).toString() + " - " + Name).setTitle(R.string.employee_punch_title);
                    } else if (EmployeeSelected.getCompany().isEmpty() || EmployeeSelected.getLocation().isEmpty() || EmployeeSelected.getJob().isEmpty()) {
                        validEmployee.add(false);
                        builder.setMessage(getText(R.string.no_company_location_job_message).toString() + " - " + Name).setTitle(R.string.employee_punch_title);
                    } else if (EmployeeSelected.getActive() == 0) {
                        validEmployee.add(false);
                        builder.setMessage(getText(R.string.employee_not_active).toString() + " - " + Name).setTitle(R.string.employee_punch_title);
                    } else if (EmployeeSelected.getCurrent() == 0 || EmployeeSelected.getDocExp().compareTo(df.format(Calendar.getInstance().getTime())) < 0) {
                        validEmployee.add(false);
                        builder.setMessage(getText(R.string.employee_doc_expire).toString() + " - " + Name).setTitle(R.string.employee_punch_title);
                    } else {
                        validEmployee.add(true);
                    }
                    if (validEmployee.getLast() == false) {
                        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        General.TouchTimeDialog(dialog, view);
                    }
                }
                if (validEmployee.getLast() == true) {
                    /// AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                    if (EmployeeSelected.getGroup() > 0) {      // employee belongs to a group
                        WorkGroupList EmployeeWorkGroup = new WorkGroupList();
                        EmployeeWorkGroup = dbGroup.getWorkGroupList(EmployeeSelected.getGroup());
                        boolean involve_group = false;
                        if (EmployeeWorkGroup.getStatus() == 0) {          // and the group is not punched in, punch in employee?
                            // builder.setMessage(getText(R.string.employee_group_not_punch_in_message).toString() + " - " + Name).setTitle(R.string.employee_punch_title);
                            involve_group = true;
                        } else if (EmployeeSelected.getCompany().equals(EmployeeWorkGroup.getCompany())
                                && EmployeeSelected.getLocation().equals(EmployeeWorkGroup.getLocation())
                                && EmployeeSelected.getJob().equals(EmployeeWorkGroup.getJob())) {            // same job as the company
                            // builder.setMessage(getText(R.string.employee_punch_in_same_job_message).toString() + " - " + Name).setTitle(R.string.employee_punch_title);
                            involve_group = true;
                        } else if (!EmployeeSelected.getCompany().equals(EmployeeWorkGroup.getCompany())
                                || !EmployeeSelected.getLocation().equals(EmployeeWorkGroup.getLocation())
                                || !EmployeeSelected.getJob().equals(EmployeeWorkGroup.getJob())) {            // different job as the company
                            // builder.setMessage(getText(R.string.employee_punch_in_different_job_message).toString() + " - " + Name).setTitle(R.string.employee_punch_title);
                             involve_group = true;
                        }
                        if (involve_group) {
                            listIndex.add(i);                       // store indexes
                            //builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            //    public void onClick(DialogInterface dialog, int id) {
                                    punchInDailyActivity((int) listIndex.removeLast());
                            //    }
                            //});
                            //builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            //    public void onClick(DialogInterface dialog, int id) {
                            //        listIndex.removeLast();         // still need to be removed even it is not used
                            //    }
                            //});
                        }
                    } else {        // does not send a confirmation message
                        listIndex.add(i);                           // store indexes
                        //builder.setMessage(getText(R.string.employee_punch_in_message).toString() + " - " + Name).setTitle(R.string.employee_punch_title);
                        //builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        //    public void onClick(DialogInterface dialog, int id) {
                                punchInDailyActivity((int) listIndex.removeLast());
                        //    }
                        //});
                        //builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        //    public void onClick(DialogInterface dialog, int id) {
                        //        listIndex.removeLast();         // still need to be removed even it is not used
                        //   }
                        //});
                    }
                    //AlertDialog dialog = builder.create();
                    //General.TouchTimeDialog(dialog, view);
                }
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_punch_title);
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
        if (itemEmployee >= 0 && unique_employeeID.size() > 0) {
            LinkedList validEmployee = new LinkedList();
            final LinkedList listIndex = new LinkedList();
            String Name;
            for (int i = 0; i < unique_employeeID.size(); i++) {      // check if anyone is already punched in
                EmployeeSelected = dbGroup.getEmployeeList(unique_employeeID.get(i));
                Name = EmployeeSelected.getLastName() + ", " + EmployeeSelected.getFirstName() + ", " + String.valueOf(EmployeeSelected.getEmployeeID());
                if (dbGroup.checkEmployeeID(EmployeeSelected.getEmployeeID())) {    // This always returns true. It is needed to include others in the scope.
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                    if (EmployeeSelected.getStatus() == 0) {
                        validEmployee.add(false);
                        builder.setMessage(getText(R.string.employee_already_punched_out_message).toString() + " - " + Name).setTitle(R.string.employee_punch_title);
                     } else {
                        validEmployee.add(true);
                    }
                    if (validEmployee.getLast() == false) {
                        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        General.TouchTimeDialog(dialog, view);
                    }
                }
                if (validEmployee.getLast() == true) {
                    //AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                    if (EmployeeSelected.getGroup() > 0) {      // employee belongs to a group
                        WorkGroupList EmployeeWorkGroup = new WorkGroupList();
                        EmployeeWorkGroup = dbGroup.getWorkGroupList(EmployeeSelected.getGroup());
                        boolean involve_group = false;
                        if (EmployeeWorkGroup.getStatus() == 0) {          // and the group already punched out, punch out employee?
                            //builder.setMessage(getText(R.string.employee_group_already_punch_out_message).toString() + " - " + Name).setTitle(R.string.employee_punch_title);
                            involve_group = true;
                        } else if (EmployeeSelected.getCompany().equals(EmployeeWorkGroup.getCompany())
                                && EmployeeSelected.getLocation().equals(EmployeeWorkGroup.getLocation())
                                && EmployeeSelected.getJob().equals(EmployeeWorkGroup.getJob())) {            // same job as the company
                            //builder.setMessage(getText(R.string.employee_punch_out_same_job_message).toString() + " - " + Name).setTitle(R.string.employee_punch_title);
                            involve_group = true;
                        } else if (!EmployeeSelected.getCompany().equals(EmployeeWorkGroup.getCompany())
                                || !EmployeeSelected.getLocation().equals(EmployeeWorkGroup.getLocation())
                                || !EmployeeSelected.getJob().equals(EmployeeWorkGroup.getJob())) {            // different job as the company
                            //builder.setMessage(getText(R.string.employee_punch_out_different_job_message).toString() + " - " + Name).setTitle(R.string.employee_punch_title);
                            involve_group = true;
                        }
                        if (involve_group) {
                            listIndex.add(i);                       // store indexes
                            //builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            //    public void onClick(DialogInterface dialog, int id) {
                                    punchOutDailyActivity((int) listIndex.removeLast());
                            //    }
                            //});
                            //builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            //   public void onClick(DialogInterface dialog, int id) {
                            //        listIndex.removeLast();         // still need to be removed even it is not used
                            //    }
                            //});
                         }
                    } else {
                        listIndex.add(i);                       // store indexes
                        //builder.setMessage(getText(R.string.employee_punch_out_message).toString() + " - " + Name).setTitle(R.string.employee_punch_title);
                        //builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        //    public void onClick(DialogInterface dialog, int id) {
                                punchOutDailyActivity((int) listIndex.removeLast());
                        //    }
                        //});
                        //builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        //    public void onClick(DialogInterface dialog, int id) {
                        //        listIndex.removeLast();         // still need to be removed even it is not used
                        //    }
                        //});
                    }
                    //AlertDialog dialog = builder.create();
                    //General.TouchTimeDialog(dialog, view);
                }
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        }
        markSelectedItems();
    }

    public void punchInDailyActivity(int index) {
        DateFormat df = new SimpleDateFormat(getText(R.string.date_YMD_format).toString());
        DateFormat tf = new SimpleDateFormat(getText(R.string.date_time_format).toString());
        String currentDateString = df.format(new Date());
        String currentDateTimeString = tf.format(new Date());
        EmployeeProfileList EmployeeNew = new EmployeeProfileList();
        EmployeeNew = dbGroup.getEmployeeList(unique_employeeID.get(index));

        feedEmployeeList.get(unique_itemNumber.get(index)).put(getText(R.string.column_key_status).toString(), getText(R.string.in).toString());
        dbGroup.updateEmployeeListStatus(unique_employeeID.get(index), 1);
        Activity = new DailyActivityList();
        Activity.setEmployeeID(EmployeeNew.getEmployeeID());
        Activity.setLastName(EmployeeNew.getLastName());
        Activity.setFirstName(EmployeeNew.getFirstName());
        if (EmployeeNew.getGroup() > 0) {
            Activity.setWorkGroup(String.valueOf(dbGroup.getWorkGroupList(EmployeeNew.getGroup()).getGroupID()));
            Activity.setSupervisor(String.valueOf(dbGroup.getWorkGroupList(EmployeeNew.getGroup()).getSupervisor()));
        }
        if (!EmployeeNew.getCompany().isEmpty()) Activity.setCompany(EmployeeNew.getCompany());
        if (!EmployeeNew.getLocation().isEmpty()) Activity.setLocation(EmployeeNew.getLocation());
        if (!EmployeeNew.getJob().isEmpty()) Activity.setJob(EmployeeNew.getJob());
        Activity.setDate(currentDateString);        // store time in date for indexing purpose
        Activity.setTimeIn(currentDateTimeString);
        dbActivity.createActivityList(Activity);
        adapter_employee.notifyDataSetChanged();
    }

    public void punchOutDailyActivity(int index) {
        DateFormat tf = new SimpleDateFormat(getText(R.string.date_time_format).toString());
        String currentDateTimeString = tf.format(new Date());
        feedEmployeeList.get(unique_itemNumber.get(index)).put(getText(R.string.column_key_status).toString(), getText(R.string.out).toString());
        dbGroup.updateEmployeeListStatus(unique_employeeID.get(index), 0);
        Activity = dbActivity.getPunchedInActivityList(unique_employeeID.get(index));
        if (Activity != null && Activity.getEmployeeID() > 0) {
            long diff = General.MinuteDifference(tf, Activity.getTimeIn(), currentDateTimeString);
            diff = diff > 0 && diff > Activity.Lunch ? diff-Activity.Lunch : 0;
            Activity.setHours(diff);
            Activity.setTimeOut(currentDateTimeString);
            dbActivity.updatePunchedInActivityList(Activity);
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
                if (!ID.isEmpty()) unique_employeeID.add(Integer.parseInt(ID));
            }
        }
        markSelectedItems();     // after clearing unique_employeeID all will be unchecked
        select_all = !select_all;
    }

    public void onSelectJobButtonClicked(final View view) {
        if (feedEmployeeList.size() == 0) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (itemEmployee >= 0 && unique_employeeID.size() > 0) {
            valid_employeeID.clear();
            for (int i = 0; i < unique_employeeID.size(); i++) {      // check if anyone is already punched in
                if (dbGroup.getEmployeeListStatus(unique_employeeID.get(i)) == 0) {
                    valid_employeeID.add(unique_employeeID.get(i));
                }
            }
            if (valid_employeeID.size() > 0) {
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
                builder.setMessage(getText(R.string.employee_already_punched_in_message).toString()).setTitle(R.string.employee_punch_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                General.TouchTimeDialog(dialog, view);
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

    public void onMoveJobButtonClicked(final View view) {
        if (feedEmployeeList.size() == 0) return;
        if (itemEmployee >= 0 && unique_employeeID.size() > 0) {
            valid_employeeID.clear();
            for (int i = 0; i < unique_employeeID.size(); i++) {      // check if anyone is already punched in
                if (dbGroup.getEmployeeListStatus(unique_employeeID.get(i)) == 0) {
                    valid_employeeID.add(unique_employeeID.get(i));
                }
            }
            Intent intent = new Intent(context, CompanyJobLocationSelectionActivity.class);
            ArrayList<String> CompanyLocationJob = new ArrayList<>();
            CompanyLocationJob.add(getText(R.string.title_activity_employee_punch_menu).toString());        // caller
            // use the last selected one as default
            CompanyLocationJob.add(Employee.getCompany());              // company
            CompanyLocationJob.add(Employee.getLocation());             // location
            CompanyLocationJob.add(Employee.getJob());                  // job
            intent.putStringArrayListExtra("CompanyLocationJob", CompanyLocationJob);
            startActivityForResult(intent, MOVE_JOB_REQUEST);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_punch_title);
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
        if (resultCode == RESULT_OK) {              // Make sure the request was successful
            ArrayList<String> CompanyLocationJob = new ArrayList<String>();
            CompanyLocationJob = data.getStringArrayListExtra("CompanyLocationJob");
            if (!Employee.getCompany().equals(CompanyLocationJob.get(1)) ||
                    !Employee.getLocation().equals(CompanyLocationJob.get(2)) ||
                    !Employee.getJob().equals(CompanyLocationJob.get(3))) {
                Employee.setCompany(CompanyLocationJob.get(1));
                Employee.setLocation(CompanyLocationJob.get(2));
                Employee.setJob(CompanyLocationJob.get(3));

                if (requestCode == PICK_JOB_REQUEST && valid_employeeID.size() < unique_employeeID.size()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                    builder.setMessage(getText(R.string.employee_are_not_changed_message).toString()).setTitle(R.string.employee_punch_title);
                    builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    General.TouchTimeDialog(dialog, findViewById(android.R.id.content));
                } else if (requestCode == MOVE_JOB_REQUEST && valid_employeeID.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                    builder.setMessage(getText(R.string.employee_out_punch_in_message).toString()).setTitle(R.string.employee_punch_title);
                    builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    General.TouchTimeDialog(dialog, findViewById(android.R.id.content));
                }

                if (requestCode == MOVE_JOB_REQUEST) {
                    // update all that are selected
                    for (int i=0; i<unique_employeeID.size(); i++) {               // all selected employees will change job
                        dbGroup.updateEmployeeListCompanyLocationJob(unique_employeeID.get(i), CompanyLocationJob.get(1),
                                CompanyLocationJob.get(2), CompanyLocationJob.get(3));
                        for (int j = 0; j < feedEmployeeList.size(); j++) {
                            if (feedEmployeeList.get(j).get(getText(R.string.column_key_employee_id).toString()).equals(String.valueOf(unique_employeeID.get(i)))) {
                                feedEmployeeList.get(j).put(getText(R.string.column_key_company).toString(), CompanyLocationJob.get(1));
                                feedEmployeeList.get(j).put(getText(R.string.column_key_location).toString(), CompanyLocationJob.get(2));
                                feedEmployeeList.get(j).put(getText(R.string.column_key_job).toString(), CompanyLocationJob.get(3));
                                if (feedEmployeeList.get(j).get(getText(R.string.column_key_status).toString()).equals(getText(R.string.in))) {
                                    punchOutDailyActivity(i);   // already punched in, must punch out and punch in
                                }
                                punchInDailyActivity(i);
                                universal_list_view.setItemChecked(j, false);
                                break;
                            }
                        }
                    }
                    unique_employeeID.clear();
                    unique_itemNumber.clear();
                    adapter_employee.notifyDataSetChanged();
                } else if (requestCode == PICK_JOB_REQUEST) {
                    // update all that are selected
                    for (int i=0; i<valid_employeeID.size(); i++) {          // employees already punched will not be included
                        for (int j = 0; j < feedEmployeeList.size(); j++) {
                            if (feedEmployeeList.get(j).get(getText(R.string.column_key_employee_id).toString()).equals(String.valueOf(valid_employeeID.get(i))) &&
                                    feedEmployeeList.get(j).get(getText(R.string.column_key_status).toString()).equals(getText(R.string.out))) {
                                // feedEmployeeList.set(j, map);
                                feedEmployeeList.get(j).put(getText(R.string.column_key_company).toString(), CompanyLocationJob.get(1));
                                feedEmployeeList.get(j).put(getText(R.string.column_key_location).toString(), CompanyLocationJob.get(2));
                                feedEmployeeList.get(j).put(getText(R.string.column_key_job).toString(), CompanyLocationJob.get(3));
                                universal_list_view.setItemChecked(j, false);
                                unique_employeeID.remove(unique_employeeID.indexOf(valid_employeeID.get(i)));
                                unique_itemNumber.remove(unique_itemNumber.indexOf(j));
                                break;
                            }
                        }
                        dbGroup.updateEmployeeListCompanyLocationJob(valid_employeeID.get(i), CompanyLocationJob.get(1),
                                CompanyLocationJob.get(2), CompanyLocationJob.get(3));
                    }
                    adapter_employee.notifyDataSetChanged();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                builder.setMessage(getText(R.string.employee_same_job).toString()).setTitle(R.string.employee_menu_title);
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
    }

    public void markSelectedItems () {
        unique_itemNumber.clear();    // clear first
        for (int i=0; i<unique_employeeID.size(); i++) unique_itemNumber.add(i);    // add dummy indexes as place holders
        for (int j = 0; j < feedEmployeeList.size(); j++) {
            universal_list_view.setItemChecked(j, false);   // set to unchecked by default
            for (int i=0; i<unique_employeeID.size(); i++) {
                if (feedEmployeeList.get(j).get(getText(R.string.column_key_employee_id).toString()).equals(String.valueOf(unique_employeeID.get(i)))) {
                    unique_itemNumber.set(i, j);               // don't clear.  instead, set new values so they sync with unique_employeeID
                    universal_list_view.setItemChecked(j, true);
                    break;   // quit after checked
                }
            }
        }
        adapter_employee.notifyDataSetChanged();
        universal_list_view.smoothScrollToPosition(itemEmployee);
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
