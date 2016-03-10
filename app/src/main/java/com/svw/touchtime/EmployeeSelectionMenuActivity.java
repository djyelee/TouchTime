package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class EmployeeSelectionMenuActivity extends ActionBarActivity {
    ListView lv;
    Button sort_id;
    Button sort_last_name;
    boolean sort_id_ascend = true;
    boolean sort_last_name_ascend = true;
    boolean selectEmployee = true;
    ArrayList<HashMap<String, String>> feedList;
    HashMap<String, String> map;
    EmployeeSelectionAdapter simpleAdapter;
    private ArrayList<EmployeeProfileList> all_lists;
    ArrayList<String> employeeIDList;
    private EmployeeWorkGroupDBWrapper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_selection_menu);

        lv = (ListView) findViewById(R.id.employee_selection_list_view);
        sort_id = (Button) findViewById(R.id.sort_id);
        sort_last_name = (Button) findViewById(R.id.sort_last_name);
        feedList= new ArrayList<HashMap<String, String>>();

        db = new EmployeeWorkGroupDBWrapper(this);
        all_lists = db.getAllEmployeeLists();
        int i = 0;
        if (all_lists.size() > 0) {
            do {
                map = new HashMap<String, String>();
                map.put(getText(R.string.employee_selection_item_id).toString(), String.valueOf(all_lists.get(i).getEmployeeID()));
                map.put(getText(R.string.employee_selection_item_last_name).toString(), all_lists.get(i).getLastName());
                map.put(getText(R.string.employee_selection_item_first_name).toString(), all_lists.get(i).getFirstName());
                map.put(getText(R.string.employee_selection_item_active).toString(), all_lists.get(i).getActive() == 0 ? getText(R.string.no).toString() : getText(R.string.yes).toString());
                map.put(getText(R.string.employee_selection_item_current).toString(), all_lists.get(i).getCurrent() == 0 ? getText(R.string.no).toString() : getText(R.string.yes).toString());
                feedList.add(map);
            } while (++i < all_lists.size());
        }
        String [] list_items = {getText(R.string.employee_selection_item_id).toString(), getText(R.string.employee_selection_item_last_name).toString(),
                getText(R.string.employee_selection_item_first_name).toString(), getText(R.string.employee_selection_item_active).toString(),
                getText(R.string.employee_selection_item_current).toString()};
        int [] list_id = {R.id.textViewID, R.id.textViewLastName, R.id.textViewFirstName, R.id.textViewActive, R.id.textViewCurrent};
        simpleAdapter = new EmployeeSelectionAdapter(this, R.layout.employee_selection_view, feedList, list_items, list_id);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(simpleAdapter);
        lv.setItemsCanFocus(true);
        lv.addHeaderView(getLayoutInflater().inflate(R.layout.employee_selection_header, null, false));

        ArrayList<String> employeeIDList = new ArrayList<String>();
        employeeIDList = getIntent().getStringArrayListExtra("SelectedEmployees");
        if (employeeIDList.size() != 0) {
            for (String s : employeeIDList) lv.setItemChecked(Integer.parseInt(s), true);
        }
    }

    public void onSortIDButtonClicked(View view) {
        Collections.sort(feedList, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                int First = Integer.parseInt(o1.get(getText(R.string.employee_selection_item_id).toString()));
                int Second = Integer.parseInt(o2.get(getText(R.string.employee_selection_item_id).toString()));
                if (!sort_id_ascend) {
                    return (First < Second ? -1 : (First == Second ? 0 : 1));
                } else {
                    return (First < Second ? 1 : (First == Second ? 0 : -1));
                }
            }
        });
        sort_id_ascend = !sort_id_ascend;
        lv.setAdapter(simpleAdapter);
   }

    public void onSortLastNameButtonClicked(View view) {
        Collections.sort(feedList, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                String First = o1.get(getText(R.string.employee_selection_item_last_name).toString());
                String Second = o2.get(getText(R.string.employee_selection_item_last_name).toString());
                if (!sort_last_name_ascend) {
                    return First.compareToIgnoreCase(Second);
                } else {
                    return Second.compareToIgnoreCase(First);
                }
            }
        });
        sort_last_name_ascend = !sort_last_name_ascend;
        lv.setAdapter(simpleAdapter);
    }

    public void onSubmitButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final SparseBooleanArray checkedItems = lv.getCheckedItemPositions();
        int checkedItemsCount = 0;
        boolean validEmployee = false;
        employeeIDList = new ArrayList<String>();
        while (checkedItemsCount < checkedItems.size()) {
            // check if employee is current and inactive?
            validEmployee = feedList.get(checkedItemsCount).get(getText(R.string.employee_selection_item_active).toString()).equalsIgnoreCase(getText(R.string.yes).toString()) &&
            feedList.get(checkedItemsCount).get(getText(R.string.employee_selection_item_current).toString()).equalsIgnoreCase(getText(R.string.yes).toString());
            employeeIDList.add(String.valueOf(checkedItems.keyAt(checkedItemsCount++)));
        }
        if (!validEmployee) {   // employee is noy current or invalid
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
         } else {
            Intent returnIntent = new Intent();
            returnIntent.putStringArrayListExtra("SelectedEmployees", employeeIDList);
            setResult(RESULT_OK, returnIntent);
            finish();
         }
         AlertDialog dialog = builder.create();
         dialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();     // Always call the superclass method first

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employee_selection_menu, menu);
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
