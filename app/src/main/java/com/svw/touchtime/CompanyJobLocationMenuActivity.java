package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static com.svw.touchtime.R.layout.company_job_location_listview;

public class CompanyJobLocationMenuActivity extends ActionBarActivity {
    private EditText JobLocationEdit;
    private ListView job_location_list_view;
    private ListView company_list_view;
    private ArrayAdapter<String> adapter_job;
    private ArrayAdapter<String> adapter_loc;
    private ArrayList<String> unique_job;
    private ArrayList<String> unique_loc;
    private RadioGroup radioGroup;
    private RadioButton jobButton, locationButton;
    private String item_job_location;
    ArrayList<HashMap<String, String>> feedCompanyList;
    HashMap<String, String> map;
    String[] list_items = new String[5];
    int[] list_id = new int[5];
    private int item = -1, olditem = -1;
    private boolean copy_flag = false;
    CompanyJobLocationList Company;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    // Database Wrapper
    private EmployeeGroupCompanyDBWrapper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_job_location_menu);
        int Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));
        // layout id information
        Button button_new;
        ArrayList<CompanyJobLocationList> all_lists;
        SimpleAdapter adapter_com;
        ArrayList<String> unique_com;

        JobLocationEdit = (EditText) findViewById(R.id.job_location_text);
        button_new = (Button) findViewById(R.id.btn_new);
        job_location_list_view = (ListView) findViewById(R.id.job_location_list_view);
        company_list_view = (ListView) findViewById(R.id.company_list_view);
        radioGroup = (RadioGroup) findViewById(R.id.selection);
        jobButton = (RadioButton) findViewById(R.id.radio_job);
        locationButton = (RadioButton) findViewById(R.id.radio_loc);
        feedCompanyList= new ArrayList<HashMap<String, String>>();

        // get record from database
        db = new EmployeeGroupCompanyDBWrapper(this);
        all_lists = db.getAllCompanyLists();
        unique_com = new ArrayList<String>();
        unique_job = new ArrayList<String>();
        unique_loc = new ArrayList<String>();
        Company = new CompanyJobLocationList();
        // read the record first company
        if (all_lists.size() > 0) {
            int i=0;
            do {
                unique_com.add(all_lists.get(i++).getName());
            } while (i < all_lists.size());
            if (!all_lists.get(0).getJob().isEmpty()) {
                for (String s : all_lists.get(0).Job.split(",")) {
                    String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                    if (!ss.isEmpty()) unique_job.add(ss);
                }
            }
            if (!all_lists.get(0).getLocation().isEmpty()) {
                for (String s: all_lists.get(0).Location.split(",")) {
                    String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                    if (!ss.isEmpty()) unique_loc.add(ss);
                }
            }
            // remove duplicates from both lists and resort alphabetically ignoring case
            unique_com = General.removeDuplicates(unique_com);
            unique_job = General.removeDuplicates(unique_job);
            unique_loc = General.removeDuplicates(unique_loc);
            Collections.sort(unique_com, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
                }
                });
            Collections.sort(unique_job, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
                    }
                });
            Collections.sort(unique_loc, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
                    }
                });
            i=0;
            do {
                map = new HashMap<String, String>();
                map.put(getText(R.string.column_key_company).toString(), unique_com.get(i));
                feedCompanyList.add(map);
            } while (++i < unique_com.size());
            adapter_job = new ArrayAdapter<String>(this, R.layout.company_job_location_listview, unique_job);
            adapter_loc = new ArrayAdapter<String>(this, R.layout.company_job_location_listview, unique_loc);
            // use adaptor to display
            radioGroup.check(locationButton.getId());       // default to check location
            if (radioGroup.getCheckedRadioButtonId() == jobButton.getId()) {
                job_location_list_view.setAdapter(adapter_job);
            } else if (radioGroup.getCheckedRadioButtonId() == locationButton.getId()) {
                job_location_list_view.setAdapter(adapter_loc);
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            // AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.no_company_message).setTitle(R.string.job_location_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, this.findViewById(android.R.id.content));
        }
        list_items[0] = getText(R.string.column_key_company).toString();
        list_id[0] = R.id.companyDisplayID;
        company_list_view.setItemsCanFocus(true);
        // company_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.company_display_header, null, false), null, false);
        adapter_com = new SimpleAdapter(this, feedCompanyList, R.layout.company_display_view, list_items, list_id);
        company_list_view.setAdapter(adapter_com);
        if (all_lists.size() > 0) {
            item = olditem = 0;
            company_list_view.setItemChecked(item, true);
            Company = db.getCompanyList(unique_com.get(0));
        }

        company_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                olditem = item;
                item = position;
                company_list_view.setItemChecked(position, true);
                view.animate().setDuration(100).alpha(0)   // dim the selection
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                Company = db.getCompanyList(feedCompanyList.get(item).get(getText(R.string.column_key_company).toString()));
                                if (copy_flag) {
                                    CompanyJobLocationList OldCompany;
                                    // OldCompany = new CompanyJobLocationList();
                                    OldCompany = db.getCompanyList(feedCompanyList.get(olditem).get(getText(R.string.column_key_company).toString()));
                                    Company.Job = OldCompany.getJob();
                                    Company.Location = OldCompany.getLocation();
                                    db.updateCompanyList(Company);
                                    copy_flag = false;
                                }
                                String[] array = Company.Job.split(",");
                                unique_job.clear();
                                for (String s: array) {
                                    String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                                    if (!ss.isEmpty()) unique_job.add(ss);
                                }
                                Collections.sort(unique_job, new Comparator<String>() {
                                    @Override
                                    public int compare(String o1, String o2) {
                                        return o1.compareToIgnoreCase(o2);
                                    }
                                });
                                array = Company.Location.split(",");
                                unique_loc.clear();
                                for (String s: array) {
                                    String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                                    if (!ss.isEmpty()) unique_loc.add(ss);
                                }
                                Collections.sort(unique_loc, new Comparator<String>() {
                                    @Override
                                    public int compare(String o1, String o2) {
                                        return o1.compareToIgnoreCase(o2);
                                    }
                                });
                                if (radioGroup.getCheckedRadioButtonId() == jobButton.getId()) {
                                    job_location_list_view.setAdapter(adapter_job);
                                } else if (radioGroup.getCheckedRadioButtonId() == locationButton.getId()) {
                                    job_location_list_view.setAdapter(adapter_loc);
                                }
                                view.setAlpha(1);
                            }
                        });
            }
        });

        job_location_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                item_job_location = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(100).alpha(0)   // dim the selection
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                view.setAlpha(1);
                            }
                        });
            }
        });

        button_new.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Hide the soft keyboard after click
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if (item >= 0) {
                    String testString;
                    testString = JobLocationEdit.getText().toString();
                    if (radioGroup.getCheckedRadioButtonId() == jobButton.getId()) {
                        // check if duplicate
                        if (General.checkDuplicates(unique_job, testString) == 0 && !testString.isEmpty()) {
                            // not duplicates, add new item to the list
                            unique_job.add(testString);
                        }
                        Collections.sort(unique_job, new Comparator<String>() {
                            @Override
                            public int compare(String o1, String o2) {
                                return o1.compareToIgnoreCase(o2);
                            }
                        });
                        // get a new adapter
                        adapter_job = new ArrayAdapter<String>(getApplicationContext(), company_job_location_listview, unique_job);
                        job_location_list_view.setAdapter(adapter_job);
                        JSONArray JobArray = new JSONArray(unique_job);
                        Company.setJob(JobArray.toString());
                    } else if (radioGroup.getCheckedRadioButtonId() == locationButton.getId()) {
                        // check if duplicate
                        if (General.checkDuplicates(unique_loc, testString) == 0 && !testString.isEmpty()) {
                            // not duplicates, add new item to the list
                            unique_loc.add(testString);
                        }
                        Collections.sort(unique_loc, new Comparator<String>() {
                            @Override
                            public int compare(String o1, String o2) {
                                return o1.compareToIgnoreCase(o2);
                            }
                        });
                        // get a new adapter
                        adapter_loc = new ArrayAdapter<String>(getApplicationContext(), company_job_location_listview, unique_loc);
                        job_location_list_view.setAdapter(adapter_loc);
                        JSONArray LocationArray = new JSONArray(unique_loc);
                        Company.setLocation(LocationArray.toString());
                    }
                    // update the database
                    db.updateCompanyList(Company);
                    // empty the text will display the hint
                    JobLocationEdit.setText("");
                }
            }
        });
    }

    public void onDeleteButtonClicked(View view) {
        if (item >= 0) {
            if (radioGroup.getCheckedRadioButtonId() == jobButton.getId()) {
                unique_job.remove(item_job_location);
                // if (unique_job.isEmpty()) unique_job.add("Empty");
                adapter_job.notifyDataSetChanged();
                JSONArray JobArray = new JSONArray(unique_job);
                Company.setJob(JobArray.toString());
            } else if (radioGroup.getCheckedRadioButtonId() == locationButton.getId()) {
                unique_loc.remove(item_job_location);
                // if (unique_loc.isEmpty()) unique_loc.add("Empty");
                adapter_loc.notifyDataSetChanged();
                JSONArray LocationArray = new JSONArray(unique_loc);
                Company.setLocation(LocationArray.toString());
            }
            db.updateCompanyList(Company);
        }
    }

    public void onClearButtonClicked(View view) {
        if (item >= 0) {
            unique_job.clear();
            JSONArray JobArray = new JSONArray(unique_job);
            Company.setJob(JobArray.toString());

            unique_loc.clear();
            JSONArray LocationArray = new JSONArray(unique_loc);
            Company.setLocation(LocationArray.toString());

            db.updateCompanyList(Company);

            if (radioGroup.getCheckedRadioButtonId() == jobButton.getId()) {
                adapter_job.notifyDataSetChanged();
            } else if (radioGroup.getCheckedRadioButtonId() == locationButton.getId()) {
                adapter_loc.notifyDataSetChanged();
            }
        }
    }

    public void onCopyToButtonClicked(View view) {
        if (item >= 0) {
            copy_flag = true;
        }
    }

    public void onRadioButtonClicked(View view) {
        if (radioGroup.getCheckedRadioButtonId() == jobButton.getId()) {
            JobLocationEdit.setHint(R.string.hint_add_job_item);
            adapter_job = new ArrayAdapter<String>(this, company_job_location_listview, unique_job);
            job_location_list_view.setAdapter(adapter_job);
        } else if (radioGroup.getCheckedRadioButtonId() == locationButton.getId()) {
            JobLocationEdit.setHint(R.string.hint_add_location_item);
            adapter_loc = new ArrayAdapter<String>(this, company_job_location_listview, unique_loc);
            job_location_list_view.setAdapter(adapter_loc);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_company_job_location_menu, menu);
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