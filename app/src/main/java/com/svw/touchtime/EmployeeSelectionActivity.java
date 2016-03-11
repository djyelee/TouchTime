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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeSelectionActivity extends ActionBarActivity {
    ListView lv;
    TextView Title;
    Button sort_id;
    Button sort_last_name;
    boolean sort_id_ascend = true;
    boolean sort_last_name_ascend = true;
    ArrayList<HashMap<String, String>> feedList;
    HashMap<String, String> map;
    EmployeeSelectionAdapter adapter_employee;
    ArrayList<String> employeeIDList;
    int selectedGroup, employeeGroup, displayGroup;
    WorkGroupList WorkGroup;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private EmployeeWorkGroupDBWrapper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_selection);

        lv = (ListView) findViewById(R.id.employee_selection_list_view);
        Title = (TextView) findViewById(R.id.employee_selection_group);
        sort_id = (Button) findViewById(R.id.sort_id);
        sort_last_name = (Button) findViewById(R.id.sort_last_name);
        feedList= new ArrayList<HashMap<String, String>>();

        ArrayList<EmployeeProfileList> all_lists;
        db = new EmployeeWorkGroupDBWrapper(this);
        employeeIDList = new ArrayList<String>();
        all_lists = db.getAllEmployeeLists();
        int i = 0;
        if (all_lists.size() > 0) {
            do {
                map = new HashMap<String, String>();
                map.put(getText(R.string.employee_selection_item_id).toString(), String.valueOf(all_lists.get(i).getEmployeeID()));
                map.put(getText(R.string.employee_selection_item_last_name).toString(), all_lists.get(i).getLastName());
                map.put(getText(R.string.employee_selection_item_first_name).toString(), all_lists.get(i).getFirstName());
                map.put(getText(R.string.employee_selection_item_group).toString(), all_lists.get(i).getGroup() <= 0 ? "" : String.valueOf(all_lists.get(i).getGroup()));
                map.put(getText(R.string.employee_selection_item_active).toString(), all_lists.get(i).getActive() == 0 ? getText(R.string.no).toString() : getText(R.string.yes).toString());
                map.put(getText(R.string.employee_selection_item_current).toString(), all_lists.get(i).getCurrent() == 0 ? getText(R.string.no).toString() : getText(R.string.yes).toString());
                feedList.add(map);
            } while (++i < all_lists.size());
        }
        String [] list_items = {getText(R.string.employee_selection_item_id).toString(), getText(R.string.employee_selection_item_last_name).toString(),
                getText(R.string.employee_selection_item_first_name).toString(), getText(R.string.employee_selection_item_group).toString(),
                getText(R.string.employee_selection_item_active).toString(), getText(R.string.employee_selection_item_current).toString()};
        int [] list_id = {R.id.textViewID, R.id.textViewLastName, R.id.textViewFirstName, R.id.textViewGroup, R.id.textViewActive, R.id.textViewCurrent};
        adapter_employee = new EmployeeSelectionAdapter(this, R.layout.employee_selection_view, feedList, list_items, list_id);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(adapter_employee);
        lv.setItemsCanFocus(true);
        // lv.addHeaderView(getLayoutInflater().inflate(R.layout.employee_selection_header, null, false));

        // receive selected group
        selectedGroup = getIntent().getIntExtra("SelectedGroup", -1);
        markSelectedItems();
        Title.setText(getText(R.string.employee_selection_title).toString() + " # " + selectedGroup);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                String G = feedList.get(position).get(getText(R.string.employee_selection_item_group).toString());
                displayGroup = G.isEmpty() ? -1 : Integer.parseInt(G);
                if (selectedGroup != displayGroup) {   // already selected but different or not selected, need to be reset to selected group
                    feedList.get(position).put(getText(R.string.employee_selection_item_group).toString(), String.valueOf(selectedGroup));
                } else {        // already selected, need to be reset to 0 or restore to the original group
                    EmployeeProfileList Employee = db.getEmployeeList(Integer.parseInt(feedList.get(position).get(getText(R.string.employee_selection_item_id).toString())));
                    employeeGroup = Employee.getGroup();
                    if (displayGroup == employeeGroup) {
                        feedList.get(position).put(getText(R.string.employee_selection_item_group).toString(), displayGroup >= 0 ? "" : String.valueOf(employeeGroup));
                    } else {
                        feedList.get(position).put(getText(R.string.employee_selection_item_group).toString(), displayGroup >= 0 ? (employeeGroup >= 0 ? String.valueOf(employeeGroup) : "") : String.valueOf(selectedGroup));
                    }
                }
                markSelectedItems();
            };
        });
    }

    public void markSelectedItems() {
        int i=0;
        while(i < feedList.size()) {
            String G = feedList.get(i).get(getText(R.string.employee_selection_item_group).toString());
            lv.setItemChecked(i, !G.isEmpty() && selectedGroup == Integer.parseInt(G));
            i++;
        }
    }

    public void onSortIDButtonClicked(View view) {
        String Items;
        Items = getText(R.string.employee_selection_item_id).toString();
        General.SortIntegerList(feedList, Items, sort_id_ascend);
        sort_last_name_ascend = false;
        sort_id_ascend = !sort_id_ascend;
        adapter_employee.notifyDataSetChanged();
        markSelectedItems();
    }

    public void onSortLastNameButtonClicked(View view) {
        String [] Items = new String[2];
        Items [0] = getText(R.string.employee_selection_item_last_name).toString();
        Items [1] = getText(R.string.employee_selection_item_first_name).toString();
        General.SortStringList(feedList, Items, sort_last_name_ascend);
        sort_id_ascend = false;
        sort_last_name_ascend = !sort_last_name_ascend;
        adapter_employee.notifyDataSetChanged();
        markSelectedItems();
    }

    public void onSubmitButtonClicked(View view) {
        boolean validEmployee = true;
        boolean reassignEmployee = false;
        employeeIDList = new ArrayList<String>();
        EmployeeProfileList Employee = new EmployeeProfileList();
        String G;
        int i=0, newGroup;
        if (feedList.size() > 0) {
            do {
                Employee = db.getEmployeeList(Integer.parseInt(feedList.get(i).get(getText(R.string.employee_selection_item_id).toString())));
                G = feedList.get(i).get(getText(R.string.employee_selection_item_group).toString());
                newGroup = G.isEmpty() ? 0 : Integer.parseInt(G);
                // employee group assignment is either changed or newly assigned
                if (Employee.getGroup() != newGroup) {
                    // the employee is reassigned from a different group, group record must be updated
                    if (Employee.getGroup() != 0) {
                        ArrayList<String> unique_employee = new ArrayList<String>();
                        WorkGroup = db.getWorkGroupList(Employee.getGroup());       // update removed employee from group record
                        String[] array = WorkGroup.Employees.split(",");
                        for (String s : array) {
                            String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                            if (!ss.isEmpty() && Integer.parseInt(ss) != Employee.getEmployeeID()) {
                                if (db.checkEmployeeID(Integer.parseInt(ss))) {         // make sure employee is still available
                                    unique_employee.add(ss);
                                }
                            }
                        }
                        // update employee list in the group
                        JSONArray JobArray = new JSONArray(unique_employee);
                        WorkGroup.setEmployees(JobArray.toString());
                        db.updateWorkGroupList(WorkGroup);
                    }
                    reassignEmployee = true;             // indicate group is changed
                    Employee.setGroup(newGroup);         // update employee group assignment
                    db.updateEmployeeList(Employee);
                }
                // create a new list of selected employees to be passed back
                if (newGroup == selectedGroup) {
                    if (validEmployee) validEmployee = Employee.getCurrent() != 0 && Employee.getActive() != 0;    // stop checking if already false
                    employeeIDList.add(String.valueOf(Employee.getEmployeeID()));
                }
           } while (++i < feedList.size());
        }
        if (reassignEmployee) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
            builder.setMessage(R.string.reassign_employee_selection).setTitle(R.string.employee_selection_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent returnIntent = new Intent();
                    returnIntent.putStringArrayListExtra("SelectedEmployees", employeeIDList);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (!validEmployee) {   // employee is not current or invalid
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
            builder.setMessage(R.string.invalid_employee_selection).setTitle(R.string.employee_selection_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent returnIntent = new Intent();
                    returnIntent.putStringArrayListExtra("SelectedEmployees", employeeIDList);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putStringArrayListExtra("SelectedEmployees", employeeIDList);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();     // Always call the superclass method first

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employee_selection, menu);
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
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
