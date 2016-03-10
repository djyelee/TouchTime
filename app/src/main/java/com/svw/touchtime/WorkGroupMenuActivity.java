package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;


public class WorkGroupMenuActivity extends ActionBarActivity {
    private ListView work_group_list_view;
    private ListView employee_list_view;
    private EditText GroupNameEdit, SupervisorEdit, ShiftNameEdit;
    private EditText HourlyRateEdit, PieceRateEdit;
    private ArrayList<WorkGroupList> all_work_group_lists;
    private ArrayList<String> unique_group;
    private ArrayList<String> unique_employee;
    private SimpleAdapter adapter_group;
    private SimpleAdapter adapter_employee;
    ArrayList<HashMap<String, String>> feedGroupList;
    ArrayList<HashMap<String, String>> feedEmployeeList;
    HashMap<String, String> map;
    private int itemWorkGroup = 0;
    WorkGroupList WorkGroup;
    String[] group_item = new String[5];
    int[] group_id = new int[5];
    String[] employee_item = new String[5];
    int[] employee_id = new int[5];
    static final int PICK_GROUP_REQUEST = 123;          // The request code

    private EmployeeWorkGroupDBWrapper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String caller = getIntent().getStringExtra("Caller");
        if (caller.equals(getText(R.string.supervisor_menu).toString()))
            setTitle(getText(R.string.title_back).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));
        else
            setTitle(getText(R.string.title_back).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        setContentView(R.layout.activity_work_group_menu);

        work_group_list_view = (ListView) findViewById(R.id.work_group_list_view);
        employee_list_view = (ListView) findViewById(R.id.employee_list_view);
        GroupNameEdit = (EditText) findViewById(R.id.work_group_name_text);
        SupervisorEdit = (EditText) findViewById(R.id.supervisor_name_text);
        ShiftNameEdit = (EditText) findViewById(R.id.shift_name_text);
        HourlyRateEdit = (EditText) findViewById(R.id.hourly_rate_text);
        PieceRateEdit = (EditText) findViewById(R.id.piece_rate_text);
        feedGroupList= new ArrayList<HashMap<String, String>>();
        feedEmployeeList= new ArrayList<HashMap<String, String>>();
        // database and other data
        db = new EmployeeWorkGroupDBWrapper(this);

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
            unique_group = removeDuplicates(unique_group);
            Collections.sort(unique_group, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
            i = 0;
            while (i < unique_group.size()) {
                map = new HashMap<String, String>();
                map.put(getText(R.string.group_selection_item_name).toString(), unique_group.get(i++));
                feedGroupList.add(map);
            };
            WorkGroup = db.getWorkGroupList(Integer.parseInt(unique_group.get(0)));
            itemWorkGroup = 1;
        }
        group_item[0] = getText(R.string.group_selection_item_name).toString();
        group_id[0] = R.id.groupDisplayID;
        work_group_list_view.setItemsCanFocus(true);
        work_group_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.group_display_header, null, false), null, false);
        adapter_group = new SimpleAdapter(this, feedGroupList, R.layout.group_display_view, group_item, group_id);
        work_group_list_view.setAdapter(adapter_group);
        displayWorkGroup();

        // retrieve employee lists
        unique_employee = new ArrayList<String>();
        // display selected employees
        employee_item[0] = getText(R.string.employee_selection_item_id).toString();
        employee_item[1] = getText(R.string.employee_selection_item_last_name).toString();
        employee_item[2] = getText(R.string.employee_selection_item_first_name).toString();
        employee_id[0] = R.id.textDisplayID;
        employee_id[1] = R.id.textDisplayLastName;
        employee_id[2] = R.id.textDisplayFirstName;
        employee_list_view.setItemsCanFocus(true);
        employee_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.employee_display_header, null, false), null, false);
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
                                WorkGroup = db.getWorkGroupList(Integer.parseInt(unique_group.get(itemWorkGroup-1)));
                                displayWorkGroup();
                                displayEmployee();
                                view.setAlpha(1);
                            }
                        });
            }

        });
    }

    public void onEmployeeButtonClicked(View view) {
         if (itemWorkGroup > 0) {
            Intent intent = new Intent(this, EmployeeSelectionActivity.class);
            intent.putExtra("SelectedGroup", itemWorkGroup);
            startActivityForResult(intent, PICK_GROUP_REQUEST);
        } else {
             // put the dialog inside so it will not dim the screen when returns.
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
             builder.setMessage(R.string.no_work_group_message).setTitle(R.string.empty_entry_title);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (GroupNameEdit.getText().toString().isEmpty()) {              // must have the group name
            builder.setMessage(R.string.group_name_empty_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        WorkGroup.setGroupName(GroupNameEdit.getText().toString());
        WorkGroup.setSupervisor((SupervisorEdit.getText() == null) ? "" : SupervisorEdit.getText().toString());
        WorkGroup.setShiftName((ShiftNameEdit.getText() == null) ? "" : ShiftNameEdit.getText().toString());
        WorkGroup.setHourlyRate((HourlyRateEdit.getText() == null) ? 1.0 : Float.parseFloat(HourlyRateEdit.getText().toString()));
        WorkGroup.setPieceRate((PieceRateEdit.getText() == null) ? 1.0 : Float.parseFloat(PieceRateEdit.getText().toString()));
        if (view.getId() == R.id.add_work_group) {
            builder.setMessage(R.string.new_work_group_message).setTitle(R.string.work_group_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    WorkGroup.setGroupID(db.getAvailableWorkGroupID());
                    WorkGroup.setEmployees("");             // no employees assigned yet
                    WorkGroup.setCompany("");               // no company assigned yet
                    WorkGroup.setLocation("");              // no location assigned yet
                    WorkGroup.setJob("");                   // no job assigned yet
                    unique_group.add(String.valueOf(WorkGroup.getGroupID()));
                    Collections.sort(unique_group, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareToIgnoreCase(o2);
                        }
                    });
                    db.createWorkGroupList(WorkGroup);
                    itemWorkGroup = unique_group.indexOf(String.valueOf(WorkGroup.getGroupID())) + 1;
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
            dialog.show();
        } else if (view.getId() == R.id.update_work_group) {
            if (itemWorkGroup <= 0) {
                builder.setMessage(R.string.select_work_group_message).setTitle(R.string.empty_entry_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else if (WorkGroup.getStatus() != 0) {
                builder.setMessage(R.string.group_must_punch_out_message).setTitle(R.string.confirm_delete_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else if (db.checkWorkGroupID(itemWorkGroup)) {
                builder.setMessage(R.string.update_work_group_message).setTitle(R.string.work_group_title);
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
            dialog.show();
        }
    }

    public void onDeleteButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (itemWorkGroup > 0) {
            if (WorkGroup.getStatus() != 0) {
                builder.setMessage(R.string.group_must_punch_out_message).setTitle(R.string.confirm_delete_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else {
                builder.setMessage(R.string.confirm_delete_work_group_message).setTitle(R.string.confirm_delete_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // reset employee group to 0 before the group is deleted
                        if (WorkGroup.getEmployees() != null) {
                            String[] array = WorkGroup.Employees.split(",");
                            for (String s : array) {
                                String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                                if (!ss.isEmpty()) {
                                    if (db.checkEmployeeID(Integer.parseInt(ss))) {         // make sure employee is still available
                                        EmployeeProfileList Employee = new EmployeeProfileList();
                                        Employee = db.getEmployeeList(Integer.parseInt(ss));
                                        Employee.setGroup(0);
                                        db.updateEmployeeList(Employee);
                                    }
                                }
                            }
                        }
                        // delete group and update list
                        db.deleteWorkGroupList(Integer.parseInt(unique_group.get(itemWorkGroup - 1)));
                        unique_group.remove(itemWorkGroup - 1);
                        adapter_group.notifyDataSetChanged();
                        all_work_group_lists = db.getAllWorkGroupLists();
                        if (all_work_group_lists.size() > 0) {
                            itemWorkGroup = 1;
                            WorkGroup = db.getWorkGroupList(itemWorkGroup);
                        } else {
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
            builder.setMessage(R.string.no_work_group_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void displayWorkGroup() {
        int i = 0;
        GroupNameEdit.setText(WorkGroup.getGroupName() == null ? "" : WorkGroup.getGroupName());
        SupervisorEdit.setText(WorkGroup.getSupervisor() == null ? "" : WorkGroup.getSupervisor());
        ShiftNameEdit.setText(WorkGroup.getShiftName() == null ? "" : WorkGroup.getShiftName());
        HourlyRateEdit.setText(String.valueOf(WorkGroup.getHourlyRate()));
        PieceRateEdit.setText(String.valueOf(WorkGroup.getPieceRate()));
        feedGroupList.clear();     // clear the old list
        while (i < unique_group.size()) {
            map = new HashMap<String, String>();
            map.put(getText(R.string.group_selection_item_name).toString(), unique_group.get(i++));
            feedGroupList.add(map);
        };
        adapter_group.notifyDataSetChanged();
        work_group_list_view.setItemChecked(itemWorkGroup, true);
    }

    public void displayEmployee() {
        feedEmployeeList.clear();     // clear the old list
        unique_employee.clear();      // c lear the old list
        if (WorkGroup.getEmployees() != null) {
            String[] array = WorkGroup.getEmployees().split(",");
            for (String s : array) {
                String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                if (!ss.isEmpty()) {
                    if (db.checkEmployeeID(Integer.parseInt(ss))) {         // make sure employee is still available
                        unique_employee.add(ss);
                    }
                }
            }
            // update employee list in the group
            JSONArray JobArray = new JSONArray(unique_employee);
            WorkGroup.setEmployees(JobArray.toString());
            db.updateWorkGroupList(WorkGroup);
            for (String s : unique_employee) {
                map = new HashMap<String, String>();
                map.put(getText(R.string.employee_selection_item_id).toString(), String.valueOf(db.getEmployeeList(Integer.parseInt(s)).getEmployeeID()));
                map.put(getText(R.string.employee_selection_item_last_name).toString(), db.getEmployeeList(Integer.parseInt(s)).getLastName());
                map.put(getText(R.string.employee_selection_item_first_name).toString(), db.getEmployeeList(Integer.parseInt(s)).getFirstName());
                feedEmployeeList.add(map);
            }
        }
        adapter_employee.notifyDataSetChanged();
    }

    static int checkDuplicates(ArrayList<String> list, String testString) {
        int nDuplicates = 0;
        // Loop over argument list.
        for (String item : list) {
            if (item.equals(testString)) {
                nDuplicates++;
            }
        }
        return nDuplicates;
    }

    static ArrayList<String> removeDuplicates(ArrayList<String> list) {
        // Store unique items in result.
        ArrayList<String> result = new ArrayList<String>();
        // Record encountered Strings in HashSet.
        HashSet<String> set = new HashSet<String>();
        // Loop over argument list.
        for (String item : list) {
            // If String is not in set, add it to the list and the set.
            if (!set.contains(item)) {
                result.add(item);
                set.add(item);
            }
        }
        return result;
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
        }

        return super.onOptionsItemSelected(item);
    }
}
