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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class CompanyJobLocationSelectionActivity extends ActionBarActivity {
    private ListView company_list_view;
    private ListView location_list_view;
    private ListView job_list_view;
    private TouchTimeGeneralAdapter adapter_company;
    private TouchTimeGeneralAdapter adapter_location;
    private TouchTimeGeneralAdapter adapter_job;
    ArrayList<HashMap<String, String>> feedCompanyList;
    ArrayList<HashMap<String, String>> feedLocationList;
    ArrayList<HashMap<String, String>> feedJobList;
    HashMap<String, String> map;
    ArrayList<CompanyJobLocationList> all_lists;
    String[] company_list_items = new String[1];
    int[] company_list_id = new int[1];
    String[] location_list_items = new String[1];
    int[] location_list_id = new int[1];
    String[] job_list_items = new String[1];
    int[] job_list_id = new int[1];
    private int itemCompany = -1, itemLocation = -1, itemJob = -1;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    ArrayList<String> CompanyLocationJob = new ArrayList<String>();
    // Database Wrapper
    private EmployeeGroupCompanyDBWrapper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_job_location_selection);

        CompanyLocationJob = getIntent().getStringArrayListExtra("CompanyLocationJob");
        if (CompanyLocationJob.get(0).equals(getText(R.string.title_activity_employee_punch_menu)))     // first item is the caller, then company, location, and job
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_employee_punch_menu).toString()));
        else
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_work_group_punch_menu).toString()));

        company_list_view = (ListView) findViewById(R.id.company_selection_view);
        location_list_view = (ListView) findViewById(R.id.location_selection_view);
        job_list_view = (ListView) findViewById(R.id.job_selection_view);
        feedCompanyList= new ArrayList<HashMap<String, String>>();
        feedLocationList= new ArrayList<HashMap<String, String>>();
        feedJobList= new ArrayList<HashMap<String, String>>();
        // get record from database
        db = new EmployeeGroupCompanyDBWrapper(this);
        all_lists = db.getAllCompanyLists();
        // read the record first company
        if (all_lists.size() > 0) {
            int i=0;
            do {
                map = new HashMap<String, String>();
                map.put(getText(R.string.column_key_company).toString(), all_lists.get(i).getName());
                feedCompanyList.add(map);
            } while (++i < all_lists.size());
            itemCompany = db.getCompanyListPosition(CompanyLocationJob.get(1));     // Company name is stored in (1)
         } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
            builder.setMessage(R.string.no_company_message).setTitle(R.string.employee_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        company_list_items[0] = getText(R.string.column_key_company).toString();
        company_list_id[0] = R.id.singleItemDisplayID;
        company_list_view.setItemsCanFocus(true);
        adapter_company = new TouchTimeGeneralAdapter(this, feedCompanyList, R.layout.general_single_item_view, company_list_items, company_list_id);
        adapter_company.setSelectedItem(itemCompany);
        company_list_view.setAdapter(adapter_company);

        getLocationJob();
        location_list_items[0] = getText(R.string.column_key_location).toString();
        location_list_id[0] = R.id.singleItemDisplayID;
        location_list_view.setItemsCanFocus(true);
        adapter_location = new TouchTimeGeneralAdapter(this, feedLocationList, R.layout.general_single_item_view, location_list_items, location_list_id);
        adapter_location.setSelectedItem(itemLocation);
        location_list_view.setAdapter(adapter_location);

        job_list_items[0] = getText(R.string.column_key_job).toString();
        job_list_id[0] = R.id.singleItemDisplayID;
        job_list_view.setItemsCanFocus(true);
        adapter_job = new TouchTimeGeneralAdapter(this, feedJobList, R.layout.general_single_item_view, job_list_items, job_list_id);
        adapter_job.setSelectedItem(itemJob);
        job_list_view.setAdapter(adapter_job);

        company_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                itemCompany = position;
                view.animate().setDuration(100).alpha(0)   // dim the selection
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                getLocationJob();
                                adapter_company.setSelectedItem(itemCompany);
                                location_list_view.setAdapter(adapter_location);
                                company_list_view.setAdapter(adapter_company);
                                job_list_view.setAdapter(adapter_job);
                                view.setAlpha(1);
                            }
                        });
            }
        });

        location_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                itemLocation= position;
                view.animate().setDuration(100).alpha(0)   // dim the selection
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                adapter_location.setSelectedItem(itemLocation);
                                adapter_location.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
            }
        });

        job_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                itemJob = position;
                view.animate().setDuration(100).alpha(0)   // dim the selection
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                adapter_job.setSelectedItem(itemJob);
                                adapter_job.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
            }
        });

    }

    private void getLocationJob() {
        int index;
        feedLocationList.clear();
        feedJobList.clear();
        if (itemCompany < 0) return;
        if (!all_lists.get(itemCompany).getLocation().isEmpty()) {
            index = 0;
            for (String s : all_lists.get(itemCompany).Location.split(",")) {
                String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                if (!ss.isEmpty()) {
                    if (ss.equals(CompanyLocationJob.get(2))) itemLocation = index;     // location is passed into this activity as the second item
                    map = new HashMap<String, String>();
                    map.put(getText(R.string.column_key_location).toString(), ss);
                    feedLocationList.add(map);
                    index++;
                }
            }
        } else itemLocation = -1;
        if (!all_lists.get(itemCompany).getJob().isEmpty()) {
            index = 0;
            for (String s : all_lists.get(itemCompany).Job.split(",")) {
                String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                if (!ss.isEmpty()) {
                    if (ss.equals(CompanyLocationJob.get(3))) itemJob = index;   // location is passed into this activity as the third item
                    map = new HashMap<String, String>();
                    map.put(getText(R.string.column_key_job).toString(), ss);
                    feedJobList.add(map);
                    index++;
                }
            }
        } else itemJob = -1;
    }

    public void onSelectJobButtonClicked (View view) {
        Intent returnIntent = new Intent();
        ArrayList<String> CompanyLocationJob = new ArrayList<String>();
        CompanyLocationJob.add(getText(R.string.title_activity_employee_punch_menu).toString());        // caller
        CompanyLocationJob.add((itemCompany >= 0) ? feedCompanyList.get(itemCompany).get(getText(R.string.column_key_company).toString()) : "");              // company
        CompanyLocationJob.add((itemLocation >= 0) ? feedLocationList.get(itemLocation).get(getText(R.string.column_key_location).toString()) : "");           // location
        CompanyLocationJob.add((itemJob >= 0) ? feedJobList.get(itemJob).get(getText(R.string.column_key_job).toString()) : "");                         // job
        returnIntent.putStringArrayListExtra("CompanyLocationJob", CompanyLocationJob);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_company_job_location_selection_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
            builder.setMessage(R.string.change_not_saved_message).setTitle(R.string.employee_punch_title);
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
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}