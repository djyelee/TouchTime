package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeSelectionActivity extends ActionBarActivity {
    ListView employee_list_view;
    TextView Title;
    boolean sort_id_ascend = true;
    boolean sort_last_name_ascend = true;
    boolean sort_group_id_ascend = true;
    ArrayList<HashMap<String, String>> feedList;
    HashMap<String, String> map;
    TouchTimeGeneralAdapter adapter_employee;
    int itemSelected = 0;
    ArrayList<String> employeeIDList;
    int selectedGroup, employeeGroup, displayGroup;
    Context context;
    WorkGroupList WorkGroup;
    ArrayList<EmployeeProfileList> all_employee_lists;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private EmployeeGroupCompanyDBWrapper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_selection);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);

        employee_list_view = (ListView) findViewById(R.id.employee_selection_list_view);
        Title = (TextView) findViewById(R.id.employee_selection_group);
        feedList= new ArrayList<HashMap<String, String>>();

        db = new EmployeeGroupCompanyDBWrapper(this);
        employeeIDList = new ArrayList<String>();
        all_employee_lists = db.getAllEmployeeLists();
        int i = 0;
        if (all_employee_lists.size() > 0) {
            do {
                if (all_employee_lists.get(i).getActive() == 0 || all_employee_lists.get(i).getCurrent() == 0) continue;  // do not list inactive or not current employees
                map = new HashMap<String, String>();
                map.put(getText(R.string.column_key_employee_id).toString(), String.valueOf(all_employee_lists.get(i).getEmployeeID()));
                map.put(getText(R.string.column_key_last_name).toString(), all_employee_lists.get(i).getLastName());
                map.put(getText(R.string.column_key_first_name).toString(), all_employee_lists.get(i).getFirstName());
                map.put(getText(R.string.column_key_group_id).toString(), all_employee_lists.get(i).getGroup() <= 0 ? "" : String.valueOf(all_employee_lists.get(i).getGroup()));
                map.put(getText(R.string.column_key_status).toString(), String.valueOf(0));  // default no change
                feedList.add(map);
            } while (++i < all_employee_lists.size());
        }
        String [] list_items = {getText(R.string.column_key_employee_id).toString(), getText(R.string.column_key_last_name).toString(),
                getText(R.string.column_key_first_name).toString(), getText(R.string.column_key_group_id).toString()};
        int [] list_id = {R.id.textViewID, R.id.textViewLastName, R.id.textViewFirstName, R.id.textViewGroup};
        adapter_employee = new TouchTimeGeneralAdapter(this, feedList, R.layout.employee_selection_view, list_items, list_id, 60);
        // employee_list_viewaddHeaderView(getLayoutInflater().inflate(R.layout.employee_selection_header, null, false), null, false);
        employee_list_view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        employee_list_view.setAdapter(adapter_employee);
        employee_list_view.setItemsCanFocus(true);
        // employee_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.employee_selection_header, null, false));

        // receive selected group
        selectedGroup = getIntent().getIntExtra("SelectedGroup", -1);
        markSelectedItems();
        Title.setText(getText(R.string.group_select_employee).toString() + " # " + selectedGroup);
        WorkGroup = db.getWorkGroupList(selectedGroup);

        context = this;
        employee_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                itemSelected = position;
                String G = feedList.get(itemSelected).get(getText(R.string.column_key_group_id).toString());
                displayGroup = G.isEmpty() ? -1 : Integer.parseInt(G);         // set to -1 if does not belong to a group
                if (selectedGroup != displayGroup) {    // already selected but different or not selected, need to be reset to selected group
                    feedList.get(itemSelected).put(getText(R.string.column_key_group_id).toString(), String.valueOf(selectedGroup));
                } else {        // already selected, need to be reset to 0 or restore to the original group
                    EmployeeProfileList Employee = db.getEmployeeList(Integer.parseInt(feedList.get(itemSelected).get(getText(R.string.column_key_employee_id).toString())));
                    employeeGroup = Employee.getGroup();
                    if (displayGroup == employeeGroup) {
                        feedList.get(itemSelected).put(getText(R.string.column_key_group_id).toString(), displayGroup >= 0 ? "" : String.valueOf(employeeGroup));
                    } else {
                        feedList.get(itemSelected).put(getText(R.string.column_key_group_id).toString(), displayGroup >= 0 ? (employeeGroup >= 0 ? String.valueOf(employeeGroup) : "") : String.valueOf(selectedGroup));
                    }
                }
                feedList.get(itemSelected).put(getText(R.string.column_key_status).toString(), String.valueOf(1));   // mark status = changed when ever it is selected
                markSelectedItems();
            };
        });
    }

    public void markSelectedItems() {
        int i=0;
        while(i < feedList.size()) {
            String G = feedList.get(i).get(getText(R.string.column_key_group_id).toString());
            employee_list_view.setItemChecked(i, !G.isEmpty() && selectedGroup == Integer.parseInt(G));
            i++;
        }
        adapter_employee.setSelectedItem(itemSelected);
        adapter_employee.notifyDataSetChanged();
        employee_list_view.smoothScrollToPosition(itemSelected);

    }

    public void onSortIDButtonClicked(View view) {
        if (feedList.size() == 0) return;
        String Items;
        int ID = 0;
        Items = getText(R.string.column_key_employee_id).toString();
        if (itemSelected >= 0) ID = Integer.parseInt(feedList.get(itemSelected).get(getText(R.string.column_key_employee_id).toString()));
        General.SortIntegerList(feedList, Items, sort_id_ascend);
        if (itemSelected >= 0) {
            itemSelected = General.GetIntegerIndex(feedList, Items, ID);
            adapter_employee.setSelectedItem(itemSelected);
        }
        sort_id_ascend = !sort_id_ascend;
        markSelectedItems();
    }

    public void onSortLastNameButtonClicked(View view) {
        if (feedList.size() == 0) return;
        String [] Items = new String[2];
        String [] Data = new String[2];
        Items[0] = getText(R.string.column_key_last_name).toString();
        Items[1] = getText(R.string.column_key_first_name).toString();
        if (itemSelected >= 0) {
            Data[0] = feedList.get(itemSelected).get(getText(R.string.column_key_last_name).toString());
            Data[1] = feedList.get(itemSelected).get(getText(R.string.column_key_first_name).toString());
        }
        General.SortStringList(feedList, Items, sort_last_name_ascend);
        if (itemSelected >= 0) {
            itemSelected = General.GetStringIndex(feedList, Items, Data);
            adapter_employee.setSelectedItem(itemSelected);
        }
        sort_last_name_ascend = !sort_last_name_ascend;
        markSelectedItems();
    }

    public void onSortGroupIDButtonClicked(View view) {
        if (feedList.size() == 0) return;
        String [] Items = new String [2];
        int ID = 0;
        Items [0] = getText(R.string.column_key_group_id).toString(); // first one is always an integer
        Items [1] = getText(R.string.column_key_last_name).toString();
        if (itemSelected >= 0) ID = Integer.parseInt(feedList.get(itemSelected).get(getText(R.string.column_key_employee_id).toString()));
        General.SortIntegerStringList(feedList, Items, sort_group_id_ascend);
        if (itemSelected >= 0) {
            itemSelected = General.GetIntegerIndex(feedList, getText(R.string.column_key_employee_id).toString(), ID);
            adapter_employee.setSelectedItem(itemSelected);
        }
        sort_group_id_ascend = !sort_group_id_ascend;
        markSelectedItems();
    }

    public void onSaveButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        builder.setMessage(R.string.group_save_selection).setTitle(R.string.group_title);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                employeeIDList.clear();
                EmployeeProfileList Employee;
                String G;
                int i=0, newGroup;
                for (i=0; i< feedList.size(); i++) {
                    if (Integer.parseInt(feedList.get(i).get(getText(R.string.column_key_status).toString())) != 0) {       // group is changed
                        Employee = db.getEmployeeList(Integer.parseInt(feedList.get(i).get(getText(R.string.column_key_employee_id).toString())));
                        G = feedList.get(i).get(getText(R.string.column_key_group_id).toString());
                        newGroup = G.isEmpty() ? 0 : Integer.parseInt(G);
                        // employee group assignment is either changed or newly assigned
                        if (Employee.Group != newGroup) {
                            if (Employee.Group != 0) {  // the employee is reassigned from a different group, group record must be updated
                                ArrayList<String> unique_employee = new ArrayList<String>();
                                WorkGroup = db.getWorkGroupList(Employee.getGroup());       // update removed employee from group record
                                String[] array = WorkGroup.Employees.split(",");
                                for (String s : array) {
                                    String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                                    if (!ss.isEmpty() && Integer.parseInt(ss) != Employee.EmployeeID) {
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
                            Employee.setGroup(newGroup);            // update employee group assignment
                            db.updateEmployeeList(Employee);
                        }
                    }
                    // create a new list of employees selected in the selectedGroup
                    if (!feedList.get(i).get(getText(R.string.column_key_group_id).toString()).isEmpty()
                            && Integer.parseInt(feedList.get(i).get(getText(R.string.column_key_group_id).toString())) == selectedGroup) {
                        employeeIDList.add(feedList.get(i).get(getText(R.string.column_key_employee_id).toString()));
                    }
                }
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
        General.TouchTimeDialog(dialog, view);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setMessage(R.string.change_not_saved_message).setTitle(R.string.group_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    db.closeDB();
                    onBackPressed();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, findViewById(android.R.id.content));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
