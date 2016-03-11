package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
    //private SimpleAdapter adapter_employee;
    EmployeeProfileAdapter adapter_employee;
    HashMap<String, String> map;
    private int itemEmployee = -1;
    private ArrayList<EmployeeProfileList> all_employee_lists;
    EmployeeProfileList Employee;
    static final int PICK_NEW_REQUEST = 123;             // The request code
    static final int PICK_UPDATE_REQUEST = 456;          // The request code
    private int Caller;

    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private EmployeeWorkGroupDBWrapper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_supervisor)
            setTitle(getText(R.string.title_back).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));
        else
            setTitle(getText(R.string.title_back).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        setContentView(R.layout.activity_employee_profile_menu);

        employee_list_view = (ListView) findViewById(R.id.employee_profile_list_view);
        sort_id = (Button) findViewById(R.id.sort_id);
        sort_last_name = (Button) findViewById(R.id.sort_last_name);
        feedEmployeeList = new ArrayList<HashMap<String, String>>();

        String [] list_items = {getText(R.string.employee_selection_item_id).toString(), getText(R.string.employee_selection_item_last_name).toString(), getText(R.string.employee_selection_item_first_name).toString()};
        int [] list_id = {R.id.textDisplayID, R.id.textDisplayLastName, R.id.textDisplayFirstName};
        db = new EmployeeWorkGroupDBWrapper(this);
        Employee = new EmployeeProfileList();
        all_employee_lists = db.getAllEmployeeLists();
        int i = 0;
        if (all_employee_lists.size() > 0) {
            do {
                map = new HashMap<String, String>();
                map.put(getText(R.string.employee_selection_item_id).toString(), String.valueOf(all_employee_lists.get(i).getEmployeeID()));
                map.put(getText(R.string.employee_selection_item_last_name).toString(), all_employee_lists.get(i).getLastName());
                map.put(getText(R.string.employee_selection_item_first_name).toString(), all_employee_lists.get(i).getFirstName());
                feedEmployeeList.add(map);
            } while (++i < all_employee_lists.size());
            itemEmployee = 0;       // default selection
         }
        // display selected employees
        // adapter_employee = new SimpleAdapter(this, feedEmployeeList, R.layout.employee_profile_view, list_items, list_id);
        adapter_employee = new EmployeeProfileAdapter(this, feedEmployeeList, R.layout.employee_profile_view, list_items, list_id);
        employee_list_view.setItemsCanFocus(true);
        employee_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.employee_profile_header, null, false), null, false);
        // use adaptor to display, must be done after the header is added
        employee_list_view.setAdapter(adapter_employee);
        HighlightListItem(itemEmployee);

        employee_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                itemEmployee = position - 1;      // offset the row for header
                view.animate().setDuration(100).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                map = feedEmployeeList.get(itemEmployee);
                                Employee = db.getEmployeeList(Integer.parseInt(map.get(getText(R.string.employee_selection_item_id))));
                                HighlightListItem(itemEmployee);
                                view.setAlpha(1);
                            }
                        });
            }
        });

    }
    private void HighlightListItem(int position) {
        adapter_employee.setSelectedItem(position);
        // employee_list_view.setAdapter(adapter_employee);
        adapter_employee.notifyDataSetChanged();
    }

    public void onUpdateButtonClicked(View view) {
        if (itemEmployee >= 0) {
            int [] Data = new int[2];
            Intent intent = new Intent(this, EmployeeDetailActivity.class);
            Data[0] = Caller;
            Data[1] = Employee.getEmployeeID();
            intent.putExtra("EmployeeID", Data);
            startActivityForResult(intent, PICK_UPDATE_REQUEST);
            // itemEmployee = -1;  Remove this line because need to remember previous selection
        } else {
            // put the dialog inside so it will not dim the screen when returns.
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
            builder.setMessage(R.string.no_employee_ID_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void onAddButtonClicked(View view) {
        int [] Data = new int[2];
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
                map.put(getText(R.string.employee_selection_item_id).toString(), String.valueOf(ID));
                map.put(getText(R.string.employee_selection_item_last_name).toString(), Employee.getLastName());
                map.put(getText(R.string.employee_selection_item_first_name).toString(), Employee.getFirstName());
                feedEmployeeList.add(map);

                itemEmployee = General.GetIntegerIndex(feedEmployeeList, getText(R.string.employee_selection_item_id).toString(), ID);
                sort_id_ascend = !sort_id_ascend;   // reverse back to original sort order
                onSortIDButtonClicked(employee_list_view);  // toggle back internally
             } else if (requestCode == PICK_UPDATE_REQUEST) {
                int i = 0;
                if (feedEmployeeList.size() > 0) {
                    do {
                        map = feedEmployeeList.get(i);
                        if (ID == Integer.parseInt(map.get(getText(R.string.employee_selection_item_id).toString()))) {
                            map.put(getText(R.string.employee_selection_item_last_name).toString(), Employee.getLastName());
                            map.put(getText(R.string.employee_selection_item_first_name).toString(), Employee.getFirstName());
                            feedEmployeeList.set(i, map);
                        }
                    } while (++i < feedEmployeeList.size());
                }
            }
            adapter_employee.notifyDataSetChanged();
        }
    }

    public void onDeleteButtonClicked(View view) {
        getWindow().setSoftInputMode ( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        if (itemEmployee >= 0) {
            builder.setMessage(R.string.confirm_delete_employee_message).setTitle(R.string.confirm_delete_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (itemEmployee > 0) HighlightListItem(itemEmployee-1);
                    db.deleteEmployeeList(Employee.getEmployeeID());
                    feedEmployeeList.remove(itemEmployee);
                    employee_list_view.setAdapter(adapter_employee);
                    //adapter_employee.notifyDataSetChanged();
                    itemEmployee--;

                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
        } else {
            builder.setMessage(R.string.no_employee_ID_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onSortIDButtonClicked(View view) {
        String Items;
        Items = getText(R.string.employee_selection_item_id).toString();
        int ID = Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.employee_selection_item_id).toString()));
        General.SortIntegerList(feedEmployeeList, Items, sort_id_ascend);
        itemEmployee = General.GetIntegerIndex(feedEmployeeList, Items, ID);
        adapter_employee.setSelectedItem(itemEmployee);
        sort_last_name_ascend = false;
        sort_id_ascend = !sort_id_ascend;
        adapter_employee.notifyDataSetChanged();
    }


    public void onSortLastNameButtonClicked(View view) {
        String [] Items = new String[2];
        String [] Data = new String[2];
        Items[0] = getText(R.string.employee_selection_item_last_name).toString();
        Items[1] = getText(R.string.employee_selection_item_first_name).toString();
        Data [0] = feedEmployeeList.get(itemEmployee).get(getText(R.string.employee_selection_item_last_name).toString());
        Data [1] =feedEmployeeList.get(itemEmployee).get(getText(R.string.employee_selection_item_first_name).toString());
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

    private class EmployeeProfileAdapter extends ArrayAdapter<HashMap<String, String>> {
        View row;
        ArrayList<HashMap<String, String>> MyFeedList;
        String[] MyListItems;
        int[] MyListIDs;
        int resLayout;
        Context context;
        int SelectedItem;

        public EmployeeProfileAdapter(Context context, ArrayList<HashMap<String, String>> feedList, int textViewResourceId, String[] list_Items, int[] list_IDs) {
            super(context, textViewResourceId, feedList);
            this.context = context;
            resLayout = textViewResourceId;
            this.MyFeedList = feedList;
            this.MyListItems = list_Items;
            this.MyListIDs = list_IDs;
        }

         @Override
        public View getView(int position, View convertView, ViewGroup parent) {
             row = convertView;
             if(row == null)
             {   // inflate our custom layout. resLayout == R.layout.row_employee_layout.xml
                 LayoutInflater ll = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                 row = ll.inflate(resLayout, parent, false);
             }

            HashMap<String, String> item = MyFeedList.get(position); // Produce a row for each employee.

             if(item != null)
             {   // Find our widgets and populate them with the Team data.
                 TextView myEmployeeID = (TextView) row.findViewById(MyListIDs[0]);
                 TextView myLastName = (TextView) row.findViewById(MyListIDs[1]);
                 TextView myFirstName = (TextView) row.findViewById(MyListIDs[2]);

                 if(myEmployeeID != null) myEmployeeID.setText(String.valueOf(item.get(MyListItems[0])));
                 if(myLastName != null) myLastName.setText(item.get(MyListItems[1]));
                 if(myFirstName != null) myFirstName.setText(item.get(MyListItems[2]));

                 highlightItem(position, row);
             }

            return row;
        }

        private void highlightItem(int position, View row) {
            if(position == SelectedItem) {
                // you can define your own color of selected item here
                row.setBackgroundColor(getResources().getColor(R.color.svw_gray));
            } else {
                // you can define your own default selector here
     //           row.setBackground(StringUtils.getDrawableFromResources(R.drawable.abs__list_selector_holo_light));
                row.setBackgroundColor(getResources().getColor(R.color.svw_cyan));
            }
        }

        public void setSelectedItem(int selectedItem) {
            this.SelectedItem = selectedItem;
        }

    }
}

