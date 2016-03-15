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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;


public class EmployeeProfileMenuActivity extends ActionBarActivity {
    ListView employee_list_view;
    Button sort_id;
    Button sort_last_name;
    boolean sort_id_ascend = true;
    boolean sort_last_name_ascend = true;
    boolean selectEmployee = true;
    ArrayList<HashMap<String, String>> feedEmployeeList;
    TouchTimeGeneralAdapter adapter_employee;
    HashMap<String, String> map;
    private int itemEmployee = -1;
    private ArrayList<EmployeeProfileList> all_employee_lists;
    EmployeeProfileList Employee;
    static final int PICK_NEW_REQUEST = 123;             // The request code
    static final int PICK_UPDATE_REQUEST = 456;          // The request code
    private int Caller;

    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private EmployeeGroupCompanyDBWrapper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile_menu);
        Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else {
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));
            Button delete_employee = (Button) findViewById(R.id.delete_employee);
            delete_employee.setClickable(false);
            delete_employee.setBackgroundColor(getResources().getColor(R.color.svw_dark_gray));
        }

        employee_list_view = (ListView) findViewById(R.id.employee_profile_list_view);
        sort_id = (Button) findViewById(R.id.sort_id);
        sort_last_name = (Button) findViewById(R.id.sort_last_name);
        feedEmployeeList = new ArrayList<HashMap<String, String>>();

        String[] list_items = {getText(R.string.column_key_id).toString(), getText(R.string.column_key_last).toString(), getText(R.string.column_key_first).toString()};
        int[] list_id = {R.id.textDisplayID, R.id.textDisplayLastName, R.id.textDisplayFirstName};
        db = new EmployeeGroupCompanyDBWrapper(this);
        Employee = new EmployeeProfileList();
        all_employee_lists = db.getAllEmployeeLists();
        int i = 0;
        if (all_employee_lists.size() > 0) {
            do {
                map = new HashMap<String, String>();
                map.put(getText(R.string.column_key_id).toString(), String.valueOf(all_employee_lists.get(i).getEmployeeID()));
                map.put(getText(R.string.column_key_last).toString(), all_employee_lists.get(i).getLastName());
                map.put(getText(R.string.column_key_first).toString(), all_employee_lists.get(i).getFirstName());
                feedEmployeeList.add(map);
            } while (++i < all_employee_lists.size());
            itemEmployee = 0;       // default selection
        }
        // display selected employees
        // adapter_employee = new SimpleAdapter(this, feedEmployeeList, R.layout.employee_profile_view, list_items, list_id);
        adapter_employee = new TouchTimeGeneralAdapter(this, feedEmployeeList, R.layout.employee_profile_view, list_items, list_id);
        employee_list_view.setItemsCanFocus(true);
        // employee_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.employee_profile_header, null, false), null, false);
        // use adaptor to display, must be done after the header is added
        employee_list_view.setAdapter(adapter_employee);
        HighlightListItem(itemEmployee);

        employee_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                itemEmployee = position;      // offset the row for header
                view.animate().setDuration(100).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                map = feedEmployeeList.get(itemEmployee);
                                Employee = db.getEmployeeList(Integer.parseInt(map.get(getText(R.string.column_key_id))));
                                HighlightListItem(itemEmployee);
                                view.setAlpha(1);
                            }
                        });
            }
        });
    }

    private void HighlightListItem(int position) {
        adapter_employee.setSelectedItem(position);
        adapter_employee.notifyDataSetChanged();
    }

    public void onUpdateButtonClicked(View view) {
        if (itemEmployee >= 0) {
            int[] Data = new int[2];
            Intent intent = new Intent(this, EmployeeDetailActivity.class);
            Data[0] = Caller;
            Data[1] = Employee.getEmployeeID();
            intent.putExtra("EmployeeID", Data);
            startActivityForResult(intent, PICK_UPDATE_REQUEST);
            // itemEmployee = -1;  Remove this line because need to remember previous selection
        } else {
            // put the dialog inside so it will not dim the screen when returns.
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
            builder.setMessage(R.string.employee_no_ID_message).setTitle(R.string.employee_profile_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void onAddButtonClicked(View view) {
        int[] Data = new int[2];
        Intent intent = new Intent(this, EmployeeDetailActivity.class);
        Data[0] = Caller;
        Data[1] = 0;
        intent.putExtra("EmployeeID", Data);
        startActivityForResult(intent, PICK_NEW_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure the request was successful
        if (resultCode == RESULT_OK) {
            map = new HashMap<String, String>();
            int ID = data.getIntExtra("EmployeeID", -1);
            Employee = db.getEmployeeList(ID);
            // Check which request we're responding to
            if (requestCode == PICK_NEW_REQUEST) {
                map.put(getText(R.string.column_key_id).toString(), String.valueOf(ID));
                map.put(getText(R.string.column_key_last).toString(), Employee.getLastName());
                map.put(getText(R.string.column_key_first).toString(), Employee.getFirstName());
                feedEmployeeList.add(map);

                itemEmployee = General.GetIntegerIndex(feedEmployeeList, getText(R.string.column_key_id).toString(), ID);
                sort_id_ascend = !sort_id_ascend;   // reverse back to original sort order
                onSortIDButtonClicked(employee_list_view);  // toggle back internally
            } else if (requestCode == PICK_UPDATE_REQUEST) {
                int i = 0;
                if (feedEmployeeList.size() > 0) {
                    do {
                        map = feedEmployeeList.get(i);
                        if (ID == Integer.parseInt(map.get(getText(R.string.column_key_id).toString()))) {
                            map.put(getText(R.string.column_key_last).toString(), Employee.getLastName());
                            map.put(getText(R.string.column_key_first).toString(), Employee.getFirstName());
                            feedEmployeeList.set(i, map);
                        }
                    } while (++i < feedEmployeeList.size());
                }
            }
            adapter_employee.notifyDataSetChanged();
        }
    }

    public void onDeleteButtonClicked(View view) {
        //       if (Caller != R.id.caller_administrator) return;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        if (itemEmployee >= 0) {
            builder.setMessage(R.string.employee_confirm_delete_message).setTitle(R.string.employee_profile_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (itemEmployee > 0) HighlightListItem(itemEmployee - 1);
                    db.deleteEmployeeList(Employee.getEmployeeID());
                    feedEmployeeList.remove(itemEmployee);
                    employee_list_view.setAdapter(adapter_employee);
                    //adapter_employee.notifyDataSetChanged();
                    itemEmployee = (itemEmployee == 0 && feedEmployeeList.size() > 0) ? 0 : itemEmployee-1;
                    if (itemEmployee >= 0 && feedEmployeeList.size() > 0) {
                        map = feedEmployeeList.get(itemEmployee);
                        Employee = db.getEmployeeList(Integer.parseInt(map.get(getText(R.string.column_key_id))));
                    }
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
        } else {
            builder.setMessage(R.string.employee_no_ID_message).setTitle(R.string.employee_profile_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onSortIDButtonClicked(View view) {
        if (feedEmployeeList.size() == 0) return;
        String Items;
        Items = getText(R.string.column_key_id).toString();
        int ID = Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_id).toString()));
        General.SortIntegerList(feedEmployeeList, Items, sort_id_ascend);
        itemEmployee = General.GetIntegerIndex(feedEmployeeList, Items, ID);
        adapter_employee.setSelectedItem(itemEmployee);
        sort_last_name_ascend = false;
        sort_id_ascend = !sort_id_ascend;
        adapter_employee.notifyDataSetChanged();
    }


    public void onSortLastNameButtonClicked(View view) {
        if (feedEmployeeList.size() == 0) return;
        String[] Items = new String[2];
        String[] Data = new String[2];
        Items[0] = getText(R.string.column_key_last).toString();
        Items[1] = getText(R.string.column_key_first).toString();
        Data[0] = feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_last).toString());
        Data[1] = feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_first).toString());
        General.SortStringList(feedEmployeeList, Items, sort_last_name_ascend);
        itemEmployee = General.GetStringIndex(feedEmployeeList, Items, Data);
        adapter_employee.setSelectedItem(itemEmployee);
        sort_id_ascend = false;
        sort_last_name_ascend = !sort_last_name_ascend;
        adapter_employee.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employee_profile_menu, menu);
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