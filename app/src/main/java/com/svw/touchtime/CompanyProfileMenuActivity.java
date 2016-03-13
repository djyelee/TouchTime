package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class CompanyProfileMenuActivity extends ActionBarActivity {
    private ListView company_list_view;
    private EditText NameEdit, StreetEdit, CityEdit, StateEdit, ZipCodeEdit;
    private EditText CountryEdit, PhoneEdit, ContactEdit, EmailEdit;
    private ArrayList<CompanyJobLocationList> all_lists;
    private ArrayList<String> unique_com;
    private SimpleAdapter adapter_com;
    ArrayList<HashMap<String, String>> feedCompanyList;
    HashMap<String, String> map;
    String[] list_items = new String[5];
    int[] list_id = new int[5];
    private int item = -1;
    CompanyJobLocationList Company;

    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private CompanyJobLocationDBWrapper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile_menu);
        int Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.title_back).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else
            setTitle(getText(R.string.title_back).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));

        company_list_view = (ListView) findViewById(R.id.company_profile_list_view);
        NameEdit = (EditText) findViewById(R.id.company_name_text);
        StreetEdit = (EditText) findViewById(R.id.company_street_text);
        CityEdit = (EditText) findViewById(R.id.company_city_text);
        StateEdit = (EditText) findViewById(R.id.company_state_text);
        ZipCodeEdit = (EditText) findViewById(R.id.company_zip_code_text);
        CountryEdit = (EditText) findViewById(R.id.company_country_text);
        PhoneEdit = (EditText) findViewById(R.id.company_phone_text);
        ContactEdit = (EditText) findViewById(R.id.company_contact_text);
        EmailEdit = (EditText) findViewById(R.id.company_email_text);
        ArrayList<String> list_com;
        feedCompanyList= new ArrayList<HashMap<String, String>>();

        // database and other data
        db = new CompanyJobLocationDBWrapper(this);
        // retrieve jobs and locations from database
        all_lists = db.getAllCompanyLists();
        list_com = new ArrayList<String>();
        unique_com = new ArrayList<String>();
        Company = new CompanyJobLocationList();

        if (all_lists.size() > 0) {
            int i = 0;
            do {
                list_com.add(all_lists.get(i).getName());
             } while (++i < all_lists.size());
            // remove duplicates from both lists and resort alphabetically ignoring case
            unique_com = General.removeDuplicates(list_com);
            Collections.sort(unique_com, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
            i=0;
            do {
                map = new HashMap<String, String>();
                map.put(getText(R.string.employee_selection_item_company).toString(), unique_com.get(i));
                feedCompanyList.add(map);
            } while (++i < unique_com.size());
            Company = db.getCompanyList(unique_com.get(0));
        }
        list_items[0] = getText(R.string.employee_selection_item_company).toString();
        list_id[0] = R.id.companyDisplayID;
        company_list_view.setItemsCanFocus(true);
        // company_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.company_display_header, null, false), null, false);
        adapter_com = new SimpleAdapter(this, feedCompanyList, R.layout.company_display_view, list_items, list_id);
        company_list_view.setAdapter(adapter_com);
        if (all_lists.size() > 0) {
            item = 0;
            displayCompanyProfile();
        }

        company_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                item = position;
                view.animate().setDuration(30).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                Company = db.getCompanyList(feedCompanyList.get(item).get(getText(R.string.employee_selection_item_company).toString()));
                                displayCompanyProfile();
                                view.setAlpha(1);
                            }
                        });
            }
        });
    }

    public void displayCompanyProfile() {
        NameEdit.setText(Company.getName());
        StreetEdit.setText(Company.getStreet());
        CityEdit.setText(Company.getCity());
        StateEdit.setText(Company.getState());
        ZipCodeEdit.setText(Company.getZipCode());
        CountryEdit.setText(Company.getCountry());
        PhoneEdit.setText(Company.getPhone());
        ContactEdit.setText(Company.getContact());
        EmailEdit.setText(Company.getEmail());
        company_list_view.setItemChecked(item, true);
    }

    public void onNewUpdateButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        if (NameEdit.getText().toString().isEmpty()) {
            builder.setMessage(R.string.no_company_name_message).setTitle(R.string.company_profile_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        } else {
            Company.setName(NameEdit.getText().toString());
            Company.setStreet((StreetEdit.getText().toString().isEmpty()) ? "" : StreetEdit.getText().toString());
            Company.setCity((CityEdit.getText().toString().isEmpty()) ? "" : CityEdit.getText().toString());
            Company.setState((StateEdit.getText().toString().isEmpty()) ? "" : StateEdit.getText().toString());
            Company.setZipCode((ZipCodeEdit.getText().toString().isEmpty()) ? "" : ZipCodeEdit.getText().toString());
            Company.setCountry((CountryEdit.getText().toString().isEmpty()) ? "" : CountryEdit.getText().toString());
            Company.setPhone((PhoneEdit.getText().toString().isEmpty()) ? "" : PhoneEdit.getText().toString());
            Company.setContact((ContactEdit.getText().toString().isEmpty()) ? "" : ContactEdit.getText().toString());
            Company.setEmail((EmailEdit.getText().toString().isEmpty()) ? "" : EmailEdit.getText().toString());
            Company.Job = "";
            Company.Location = "";

            if (unique_com.size() > 0) {        // check if empty
                //            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
                if (General.checkDuplicates(unique_com, Company.getName()) == 0) {
                    if (view.getId() == R.id.btn_new) {   // no duplicate
                        builder.setMessage(R.string.new_company_message).setTitle(R.string.company_profile_title);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // add new name to the list
                                unique_com.add(Company.getName());
                                Collections.sort(unique_com, new Comparator<String>() {
                                    @Override
                                    public int compare(String o1, String o2) {
                                        return o1.compareToIgnoreCase(o2);
                                    }
                                });
                                int i = 0;
                                feedCompanyList.clear();
                                do {
                                    map = new HashMap<String, String>();
                                    map.put(getText(R.string.employee_selection_item_company).toString(), unique_com.get(i));
                                    feedCompanyList.add(map);
                                } while (++i < unique_com.size());
                                db.createCompanyList(Company);
                                adapter_com.notifyDataSetChanged();
                                item = unique_com.indexOf(Company.getName());       // get the index of the new company
                                displayCompanyProfile();
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                    } else if (view.getId() == R.id.btn_upd) {
                        builder.setMessage(R.string.change_company_message).setTitle(R.string.company_profile_title);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // add new name to the list
                                db.deleteCompanyList(unique_com.get(item));
                                unique_com.remove(item);
                                unique_com.add(Company.getName());
                                Collections.sort(unique_com, new Comparator<String>() {
                                    @Override
                                    public int compare(String o1, String o2) {
                                        return o1.compareToIgnoreCase(o2);
                                    }
                                });
                                int i = 0;
                                feedCompanyList.clear();
                                do {
                                    map = new HashMap<String, String>();
                                    map.put(getText(R.string.employee_selection_item_company).toString(), unique_com.get(i));
                                    feedCompanyList.add(map);
                                } while (++i < unique_com.size());
                                db.createCompanyList(Company);
                                adapter_com.notifyDataSetChanged();
                                displayCompanyProfile();
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                    }
                } else {
                    if (view.getId() == R.id.btn_new) {
                        builder.setMessage(R.string.duplicate_company_message).setTitle(R.string.company_profile_title);
                        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                    } else if (view.getId() == R.id.btn_upd) {
                        builder.setMessage(R.string.update_company_message).setTitle(R.string.company_profile_title);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                displayCompanyProfile();                             // name is already there
                                db.updateCompanyList(Company);
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                    }
                }
            } else {
                // It is empty.  No checking duplicates and no sorting are necessary
                unique_com.add(Company.getName());
                feedCompanyList.clear();
                map = new HashMap<String, String>();
                map.put(getText(R.string.employee_selection_item_company).toString(), unique_com.get(0));
                feedCompanyList.add(map);
                db.createCompanyList(Company);
                all_lists = db.getAllCompanyLists();
            }
            adapter_com.notifyDataSetChanged();
            displayCompanyProfile();             // display after SetAdapter to show the checked item
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onDeleteButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        if (unique_com.size() > 0) {
            builder.setMessage(R.string.confirm_delete_company_message).setTitle(R.string.confirm_delete_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                    db.deleteCompanyList(feedCompanyList.get(item).get(getText(R.string.employee_selection_item_company).toString()));
                    unique_com.remove(item);
                    int i = 0;
                    feedCompanyList.clear();
                    while (i < unique_com.size()) {
                        map = new HashMap<String, String>();
                        map.put(getText(R.string.employee_selection_item_company).toString(), unique_com.get(i++));
                        feedCompanyList.add(map);
                    } ;
                    adapter_com.notifyDataSetChanged();
                    all_lists = db.getAllCompanyLists();
                    if (all_lists.size() > 0) {
                        item = (item > 0) ? item-1 : 0;
                        Company = db.getCompanyList(all_lists.get(item).getName());
                        displayCompanyProfile();
                    } else {
                        NameEdit.setText("");
                        StreetEdit.setText("");
                        CityEdit.setText("");
                        StateEdit.setText("");
                        ZipCodeEdit.setText("");
                        CountryEdit.setText("");
                        PhoneEdit.setText("");
                        ContactEdit.setText("");
                        EmailEdit.setText("");
                    }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_company_profile_menu, menu);
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
