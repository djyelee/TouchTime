package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class WorkGroupMenuActivity extends ActionBarActivity {
    private ListView work_group_list_view;
    private EditText GroupNameEdit, SupervisorEdit, ShiftNameEdit;
    private ArrayList<WorkGroupList> all_work_group_lists;
    private ArrayList<String> unique_group;
    private ArrayList<String> unique_employee;
    private SimpleAdapter adapter_group;
    private SimpleAdapter adapter_employee;
    ArrayList<HashMap<String, String>> feedGroupList;
    ArrayList<HashMap<String, String>> feedEmployeeList;
    HashMap<String, String> map;
    private int itemWorkGroup = -1;
    WorkGroupList WorkGroup;
    String[] group_item = new String[5];
    int[] group_id = new int[5];
    String[] employee_item = new String[5];
    int[] employee_id = new int[5];
    static final int PICK_GROUP_REQUEST = 123;          // The request code
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private EmployeeGroupCompanyDBWrapper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_group_menu);
        int Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);

        ListView employee_list_view;

        work_group_list_view = (ListView) findViewById(R.id.work_group_list_view);
        employee_list_view = (ListView) findViewById(R.id.employee_list_view);
        GroupNameEdit = (EditText) findViewById(R.id.work_group_name_text);
        SupervisorEdit = (EditText) findViewById(R.id.supervisor_name_text);
        ShiftNameEdit = (EditText) findViewById(R.id.shift_name_text);
        feedGroupList= new ArrayList<HashMap<String, String>>();
        feedEmployeeList= new ArrayList<HashMap<String, String>>();
        // database and other data
        db = new EmployeeGroupCompanyDBWrapper(this);

        // retrieve work group lists
        all_work_group_lists = db.getAllWorkGroupLists();
        unique_group = new ArrayList<String>();
        WorkGroup = new WorkGroupList();
        if (all_work_group_lists.size() > 0) {
            int i = 0;
            do {
                unique_group.add(String.valueOf(all_work_group_lists.get(i++).getGroupID()));
            } while (i < all_work_group_lists.size());
            // remove duplicates from work group list and resort alphabetically ignoring case
            unique_group = General.removeDuplicates(unique_group);
            General.sortString(unique_group);
            i = 0;
            while (i < unique_group.size()) {
                map = new HashMap<String, String>();
                map.put(getText(R.string.column_key_group_id).toString(), unique_group.get(i++));
                feedGroupList.add(map);
            };
            WorkGroup = db.getWorkGroupList(Integer.parseInt(unique_group.get(0)));
            itemWorkGroup = 0;
        }
        group_item[0] = getText(R.string.column_key_group_id).toString();
        group_id[0] = R.id.groupDisplayID;
        work_group_list_view.setItemsCanFocus(true);
        // work_group_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.group_display_header, null, false), null, false);
        adapter_group = new SimpleAdapter(this, feedGroupList, R.layout.group_display_view, group_item, group_id);
        work_group_list_view.setAdapter(adapter_group);
        displayWorkGroup();

        // retrieve employee lists
        unique_employee = new ArrayList<String>();
        // display selected employees
        employee_item[0] = getText(R.string.column_key_employee_id).toString();
        employee_item[1] = getText(R.string.column_key_last_name).toString();
        employee_item[2] = getText(R.string.column_key_first_name).toString();
        employee_id[0] = R.id.textDisplayID;
        employee_id[1] = R.id.textDisplayLastName;
        employee_id[2] = R.id.textDisplayFirstName;
        employee_list_view.setItemsCanFocus(true);
        // employee_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.employee_display_header, null, false), null, false);
        adapter_employee = new SimpleAdapter(this, feedEmployeeList, R.layout.employee_display_view, employee_item, employee_id);
        employee_list_view.setAdapter(adapter_employee);
        displayEmployee();

        work_group_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                itemWorkGroup = position;
                view.animate().setDuration(100).alpha(0).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                WorkGroup = db.getWorkGroupList(Integer.parseInt(unique_group.get(itemWorkGroup)));
                                displayWorkGroup();
                                displayEmployee();
                                view.setAlpha(1);
                            }
                        });
            }

        });
    }

    public void onEmployeeButtonClicked(View view) {
         if (itemWorkGroup >= 0) {
            Intent intent = new Intent(this, EmployeeSelectionActivity.class);
            intent.putExtra("SelectedGroup", Integer.parseInt(unique_group.get(itemWorkGroup)));
            startActivityForResult(intent, PICK_GROUP_REQUEST);
        } else {
             // put the dialog inside so it will not dim the screen when returns.
             AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
             builder.setMessage(R.string.group_select_message).setTitle(R.string.group_title);
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
        if (requestCode == PICK_GROUP_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                ArrayList<String> EmployeeIDList = new ArrayList<String>();
                EmployeeIDList = data.getStringArrayListExtra("SelectedEmployees");
                JSONArray JobArray = new JSONArray(EmployeeIDList);
                WorkGroup.setEmployees(JobArray.toString());
                db.updateWorkGroupList(WorkGroup);
                displayWorkGroup();
                displayEmployee();
            }
        }
    }

    public void onAddUpdateButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (GroupNameEdit.getText().toString().isEmpty()) {              // must have the group name
            builder.setMessage(R.string.group_name_empty_message).setTitle(R.string.group_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
            return;
        }
        WorkGroup.setGroupName(GroupNameEdit.getText().toString());
        WorkGroup.setSupervisor((SupervisorEdit.getText().toString().isEmpty()) ? "" : SupervisorEdit.getText().toString());
        WorkGroup.setShiftName((ShiftNameEdit.getText().toString().isEmpty()) ? "" : ShiftNameEdit.getText().toString());
         if (view.getId() == R.id.add_work_group) {
            builder.setMessage(R.string.group_new_message).setTitle(R.string.group_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    WorkGroup.setGroupID(db.getAvailableWorkGroupID());
                    WorkGroup.setEmployees("");             // no employees assigned yet
                    WorkGroup.setCompany("");               // no company assigned yet
                    WorkGroup.setLocation("");              // no location assigned yet
                    WorkGroup.setJob("");                   // no job assigned yet
                    WorkGroup.setStatus(0);
                    unique_group.add(String.valueOf(WorkGroup.getGroupID()));
                    Collections.sort(unique_group, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareToIgnoreCase(o2);
                        }
                    });
                    db.createWorkGroupList(WorkGroup);
                    itemWorkGroup = unique_group.indexOf(String.valueOf(WorkGroup.getGroupID()));
                    displayWorkGroup();
                    displayEmployee();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        } else if (view.getId() == R.id.update_work_group) {
            if (itemWorkGroup < 0) {
                builder.setMessage(R.string.group_select_message).setTitle(R.string.group_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else if (WorkGroup.getStatus() != 0) {
                builder.setMessage(R.string.group_must_punch_out_message).setTitle(R.string.group_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
           } else if (db.checkWorkGroupID(Integer.parseInt(unique_group.get(itemWorkGroup)))) {
                builder.setMessage(R.string.group_update_message).setTitle(R.string.group_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.updateWorkGroupList(WorkGroup);
                        displayWorkGroup();                             // name is already there
                        displayEmployee();
                        }
                    });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            }
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        }
    }

    public void onDeleteButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (itemWorkGroup >= 0) {
            if (WorkGroup.getStatus() != 0) {
                builder.setMessage(R.string.group_must_punch_out_message).setTitle(R.string.group_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else {
                builder.setMessage(R.string.group_confirm_delete_message).setTitle(R.string.group_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // reset employee group to 0 before the group is deleted
                        if (!WorkGroup.getEmployees().isEmpty()) {
                            String[] array = WorkGroup.Employees.split(",");
                            for (String s : array) {
                                String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                                if (!ss.isEmpty()) {
                                    if (db.checkEmployeeID(Integer.parseInt(ss))) {                 // make sure employee is still available
                                        db.updateEmployeeListGroup(Integer.parseInt(ss), 0); // group is removed, employee record must be updated as well to set group to 0
                                    }
                                }
                            }
                        }
                        // delete group and update list
                        db.deleteWorkGroupList(Integer.parseInt(unique_group.get(itemWorkGroup)));
                        unique_group.remove(itemWorkGroup);
                        feedGroupList.remove(itemWorkGroup);
                        adapter_group.notifyDataSetChanged();
                        all_work_group_lists = db.getAllWorkGroupLists();
                        if (all_work_group_lists.size() > 0) {
                            itemWorkGroup = itemWorkGroup >= 1 ? itemWorkGroup-1 : 0;
                            WorkGroup = db.getWorkGroupList(Integer.parseInt(unique_group.get(itemWorkGroup)));
                        } else {
                            itemWorkGroup = -1;
                            WorkGroup = new WorkGroupList();
                        }
                        displayWorkGroup();
                        displayEmployee();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
            }
        } else {
            builder.setMessage(R.string.no_work_group_message).setTitle(R.string.group_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog dialog = builder.create();
        General.TouchTimeDialog(dialog, view);
    }

    public void displayWorkGroup() {
        int i = 0;
        GroupNameEdit.setText(WorkGroup.getGroupName().isEmpty() ? "" : WorkGroup.getGroupName());
        SupervisorEdit.setText(WorkGroup.getSupervisor().isEmpty() ? "" : WorkGroup.getSupervisor());
        ShiftNameEdit.setText(WorkGroup.getShiftName().isEmpty() ? "" : WorkGroup.getShiftName());
        feedGroupList.clear();                  // clear the old list
        while (i < unique_group.size()) {
            map = new HashMap<String, String>();
            map.put(getText(R.string.column_key_group_id).toString(), unique_group.get(i++));
            feedGroupList.add(map);
        };
        adapter_group.notifyDataSetChanged();
        work_group_list_view.setItemChecked(itemWorkGroup, true);
    }

    public void displayEmployee() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        unique_employee.clear();      // c lear the old list
        if (!WorkGroup.getEmployees().isEmpty()) {
            String[] array = WorkGroup.getEmployees().split(",");
            for (String s : array) {
                String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                if (!ss.isEmpty()) {
                    if (db.checkEmployeeID(Integer.parseInt(ss))) {         // make sure employee is still available
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        if (db.getEmployeeList(Integer.parseInt(ss)).getActive() == 0 ||
                                db.getEmployeeList(Integer.parseInt(ss)).getCurrent() == 0 ||
                                db.getEmployeeList(Integer.parseInt(ss)).getDocExp().compareTo(df.format(Calendar.getInstance().getTime()))<0) {
                            builder.setMessage(R.string.group_inactive_employee_message).setTitle(R.string.group_title);
                            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            AlertDialog dialog = builder.create();
                            General.TouchTimeDialog(dialog, this.findViewById(android.R.id.content));
                            db.updateEmployeeListGroup(Integer.parseInt(ss), 0);
                        } else {
                            unique_employee.add(ss);
                        }
                    }
                }
            }
            // update employee list in the group
            JSONArray JobArray = new JSONArray(unique_employee);
            WorkGroup.setEmployees(JobArray.toString());
            db.updateWorkGroupList(WorkGroup);
        }
        feedEmployeeList.clear();     // clear the old list
        for (String s : unique_employee) {
            map = new HashMap<String, String>();
            map.put(getText(R.string.column_key_employee_id).toString(), String.valueOf(db.getEmployeeList(Integer.parseInt(s)).getEmployeeID()));
            map.put(getText(R.string.column_key_last_name).toString(), db.getEmployeeList(Integer.parseInt(s)).getLastName());
            map.put(getText(R.string.column_key_first_name).toString(), db.getEmployeeList(Integer.parseInt(s)).getFirstName());
            feedEmployeeList.add(map);
        }
        adapter_employee.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_work_group_menu, menu);
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
            db.closeDB();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
