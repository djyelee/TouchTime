package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static com.svw.touchtime.R.layout.general_edit_text_view;

public class CompanyProfileMenuActivity extends ActionBarActivity {
    private ListView company_list_view;
    private EditText NameEdit, StreetEdit, ZipCodeEdit;
    private EditText PhoneEdit, ContactEdit, EmailEdit;
    Spinner CountrySpinner, StateSpinner, CitySpinner;
    ArrayAdapter<String> adapter_country, adapter_state, adapter_city;
    ArrayList<String> Countries, States, Cities;
    private ArrayList<CompanyJobLocationList> all_lists;
    private ArrayList<String> unique_com;
    private SimpleAdapter adapter_com;
    ArrayList<HashMap<String, String>> feedCompanyList;
    HashMap<String, String> map;
    String[] list_items = new String[5];
    int[] list_id = new int[5];
    private int itemCompany = -1;
    private int itemCountry, itemState, itemCity;
    CompanyJobLocationList Company;
    Context context;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private EmployeeGroupCompanyDBWrapper dbGroup;
    private CountryStateCityDBWrapper dbCountry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile_menu);
        int Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);        // prevent soft keyboard from squeezing the EditTex Box

        company_list_view = (ListView) findViewById(R.id.company_profile_list_view);
        NameEdit = (EditText) findViewById(R.id.company_name_text);
        StreetEdit = (EditText) findViewById(R.id.company_street_text);
        ZipCodeEdit = (EditText) findViewById(R.id.company_zip_code_text);
        PhoneEdit = (EditText) findViewById(R.id.company_phone_text);
        ContactEdit = (EditText) findViewById(R.id.company_contact_text);
        EmailEdit = (EditText) findViewById(R.id.company_email_text);
        ArrayList<String> list_com;
        feedCompanyList= new ArrayList<HashMap<String, String>>();

        // database and other data
        dbGroup = new EmployeeGroupCompanyDBWrapper(this);
        dbCountry = new CountryStateCityDBWrapper(this);
        Countries = new ArrayList<String>();
        States = new ArrayList<String>();
        Cities = new ArrayList<String>();

        context = this;
        CountrySpinner = (Spinner) findViewById(R.id.country_spinner);
        Countries = dbCountry.getCountryList(Countries);        // get an empty list
        adapter_country = new ArrayAdapter<String>(context, general_edit_text_view, Countries);
        CountrySpinner.setAdapter(adapter_country);
        CountrySpinner.setSelection(0, false);                  // this prevents the first firing
        CountrySpinner.setOnItemSelectedListener(OnSpinnerCL);

        StateSpinner = (Spinner) findViewById(R.id.state_spinner);
        States = dbCountry.getStateList("", States);        // get an empty list
        adapter_state = new ArrayAdapter<String>(context, general_edit_text_view, States);
        StateSpinner.setAdapter(adapter_state);
        StateSpinner.setSelection(0, false);                  // this prevents the first firing
        StateSpinner.setOnItemSelectedListener(OnSpinnerCL);

        CitySpinner = (Spinner) findViewById(R.id.city_spinner);
        Cities = dbCountry.getCityList("", "", Cities);     // get an empty list
        adapter_city = new ArrayAdapter<String>(context, general_edit_text_view, Cities);
        CitySpinner.setAdapter(adapter_city);
        CitySpinner.setSelection(0, false);                  // this prevents the first firing
        CitySpinner.setOnItemSelectedListener(OnSpinnerCL);

        // retrieve jobs and locations from database
        all_lists = dbGroup.getAllCompanyLists();
        list_com = new ArrayList<String>();
        unique_com = new ArrayList<String>();
        Company = new CompanyJobLocationList();

        int i = 0;
        dbCountry.clearAllList();           // clear the database for country, state, city spinner
        if (all_lists.size() > 0) {
            do {
                list_com.add(all_lists.get(i).getName());
                // building database for country, state, city spinner from company list
                dbCountry.addMissingList(all_lists.get(i).getCountry(), all_lists.get(i).getState(), all_lists.get(i).getCity());
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
                map.put(getText(R.string.column_key_company).toString(), unique_com.get(i));
                feedCompanyList.add(map);
            } while (++i < unique_com.size());
            Company = dbGroup.getCompanyList(unique_com.get(0));
            // building database for country, state, city spinner from employee list
            ArrayList<EmployeeProfileList> Lists;
            Lists = dbGroup.getAllEmployeeLists();
            if (Lists.size() > 0) {
                for (i = 0; i < Lists.size(); i++) {
                    dbCountry.addMissingList(Lists.get(i).getCountry(), Lists.get(i).getState(), Lists.get(i).getCity());
                }
            }
        }

        list_items[0] = getText(R.string.column_key_company).toString();
        list_id[0] = R.id.companyDisplayID;
        company_list_view.setItemsCanFocus(true);
        // company_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.company_display_header, null, false), null, false);
        adapter_com = new SimpleAdapter(this, feedCompanyList, R.layout.company_display_view, list_items, list_id);
        company_list_view.setAdapter(adapter_com);
        if (all_lists.size() > 0) {
            itemCompany = 0;
            displayCompanyProfile();
        }

        company_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                itemCompany = position;
                view.animate().setDuration(30).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                Company = dbGroup.getCompanyList(feedCompanyList.get(itemCompany).get(getText(R.string.column_key_company).toString()));
                                displayCompanyProfile();
                                view.setAlpha(1);
                            }
                        });
            }
        });
    }

    public AdapterView.OnItemSelectedListener OnSpinnerCL = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            LayoutInflater factory = LayoutInflater.from(context);
            final View textEntryView = factory.inflate(R.layout.spinner_edit_text_view, null);
            final EditText dialog_text = (EditText) textEntryView.findViewById(R.id.spinner_edit_text);
            if (parent == CountrySpinner) {
                if (pos == 1) {             // 0 is blank, 1 is add new
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                    builder.setMessage(R.string.employee_enter_country_message).setTitle(R.string.company_profile_title).setView(textEntryView);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // int select = adapter_country.getPosition(Employee.getCountry());
                            Countries.remove("");       // remove items first
                            Countries.remove(getText(R.string.employee_add_new_message).toString());
                            Countries.add(0, dialog_text.getText().toString());
                            General.sortString(Countries);
                            Countries = General.removeDuplicates(Countries);
                            Countries.add(0, getText(R.string.employee_add_new_message).toString());
                            Countries.add(0, "");       // remove items first
                            adapter_country.notifyDataSetChanged();
                            dbCountry.addMissingList(dialog_text.getText().toString(), States.get(itemState), Cities.get(itemCity));
                            CountrySpinner.setSelection(Countries.indexOf(dialog_text.getText().toString()));
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CountrySpinner.setSelection(Countries.indexOf(Company.getCountry()));
                        }
                    });
                    AlertDialog dialog = builder.create();
                    General.TouchTimeDialog(dialog, view);
                } else {
                    itemCountry = pos;
                    if (Countries.size() > itemCountry) {
                        String currentState = States.get(itemState);        // get it before losing it
                        States = dbCountry.getStateList(Countries.get(itemCountry), States);
                        if (!States.contains(currentState)) {
                            States.remove("");       // remove items first
                            States.remove(getText(R.string.employee_add_new_message).toString());
                            States.add(0, currentState);
                            General.sortString(States);
                            States.add(0, getText(R.string.employee_add_new_message).toString());
                            States.add(0, "");       // remove items first
                            itemState = States.indexOf(currentState);
                            StateSpinner.setSelection(itemState);
                        }
                        adapter_state.notifyDataSetChanged();
                        if (States.size() > itemState) {
                            String currentCity = Cities.get(itemCity);      // get it before losing it
                            Cities = dbCountry.getCityList(Countries.get(itemCountry), States.get(itemState), Cities);
                            if (!Cities.contains(currentCity)) {
                                Cities.remove("");
                                Cities.remove(getText(R.string.employee_add_new_message).toString());
                                Cities.add(0, currentCity);
                                General.sortString(Cities);
                                Cities.add(0, getText(R.string.employee_add_new_message).toString());
                                Cities.add(0, "");       // remove items first
                                itemCity = Cities.indexOf(currentCity);
                                CitySpinner.setSelection(itemCity);
                            }
                            adapter_city.notifyDataSetChanged();
                        }
                    }
                }
            } else if (parent == StateSpinner) {
                if (pos == 1) {             // 0 is blank, 1 is add new
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                    builder.setMessage(R.string.employee_enter_state_message).setTitle(R.string.company_profile_title).setView(textEntryView);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // int select = adapter_country.getPosition(Employee.getCountry());
                            States.remove("");       // remove items first
                            States.remove(getText(R.string.employee_add_new_message).toString());
                            States.add(0, dialog_text.getText().toString());
                            General.sortString(States);
                            States = General.removeDuplicates(States);
                            States.add(0, getText(R.string.employee_add_new_message).toString());
                            States.add(0, "");       // remove items first
                            adapter_state.notifyDataSetChanged();
                            dbCountry.addMissingList(Countries.get(itemCountry), dialog_text.getText().toString(), Cities.get(itemCity));
                            StateSpinner.setSelection(States.indexOf(dialog_text.getText().toString()));
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            StateSpinner.setSelection(States.indexOf(Company.getState()));
                        }
                    });
                    AlertDialog dialog = builder.create();
                    General.TouchTimeDialog(dialog, view);
                } else {
                    itemState = pos;
                    if (Countries.size() > itemCountry) {
                        if (States.size() > itemState) {
                            String currentCity = Cities.get(itemCity);      // get it before losing it
                            Cities = dbCountry.getCityList(Countries.get(itemCountry), States.get(itemState), Cities);
                            if (!Cities.contains(currentCity)) {
                                Cities.remove("");
                                Cities.remove(getText(R.string.employee_add_new_message).toString());
                                Cities.add(0, currentCity);
                                General.sortString(Cities);
                                Cities.add(0, getText(R.string.employee_add_new_message).toString());
                                Cities.add(0, "");       // remove items first
                                itemCity = Cities.indexOf(currentCity);
                                CitySpinner.setSelection(itemCity);
                            }
                            adapter_city.notifyDataSetChanged();
                        }
                    }
                }
            } if (parent == CitySpinner) {
                if (pos == 1) {             // 0 is blank, 1 is add new
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                    builder.setMessage(R.string.employee_enter_city_message).setTitle(R.string.company_profile_title).setView(textEntryView);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // int select = adapter_country.getPosition(Employee.getCountry());
                            Cities.remove("");       // remove items first
                            Cities.remove(getText(R.string.employee_add_new_message).toString());
                            Cities.add(0, dialog_text.getText().toString());
                            General.sortString(Cities);
                            Cities = General.removeDuplicates(Cities);
                            Cities.add(0, getText(R.string.employee_add_new_message).toString());
                            Cities.add(0, "");       // remove items first
                            adapter_city.notifyDataSetChanged();
                            dbCountry.addMissingList(Countries.get(itemCountry), States.get(itemState), dialog_text.getText().toString());
                            CitySpinner.setSelection(Cities.indexOf(dialog_text.getText().toString()));
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CitySpinner.setSelection(Cities.indexOf(Company.getCity()));
                        }
                    });
                    AlertDialog dialog = builder.create();
                    General.TouchTimeDialog(dialog, view);
                } else {
                    itemCity = pos;
                }
            }
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    public void displayCompanyProfile() {
        NameEdit.setText(Company.getName());
        StreetEdit.setText(Company.getStreet());
        ZipCodeEdit.setText(Company.getZipCode());
        PhoneEdit.setText(Company.getPhone());
        ContactEdit.setText(Company.getContact());
        EmailEdit.setText(Company.getEmail());
        company_list_view.setItemChecked(itemCompany, true);

        itemCountry = adapter_country.getPosition(Company.getCountry());
        if (itemCountry > 0) {
            CountrySpinner.setSelection(itemCountry);
            States = dbCountry.getStateList(Company.getCountry(), States);
            adapter_state.notifyDataSetChanged();
        }
        itemState = adapter_state.getPosition(Company.getState());
        if (itemState > 0) {
            StateSpinner.setSelection(itemState);
            Cities = dbCountry.getCityList(Company.getCountry(), Company.getState(), Cities);
            adapter_city.notifyDataSetChanged();
        }
        itemCity = adapter_city.getPosition(Company.getCity());
        if (itemCity > 0) CitySpinner.setSelection(itemCity);
    }

    public void onNewUpdateButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (NameEdit.getText().toString().isEmpty()) {
            builder.setMessage(R.string.no_company_message).setTitle(R.string.company_profile_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        } else {
            Company.setName(NameEdit.getText().toString());
            Company.setStreet((StreetEdit.getText().toString().isEmpty()) ? "" : StreetEdit.getText().toString());
            Company.setZipCode((ZipCodeEdit.getText().toString().isEmpty()) ? "" : ZipCodeEdit.getText().toString());
            Company.setPhone((PhoneEdit.getText().toString().isEmpty()) ? "" : PhoneEdit.getText().toString());
            Company.setContact((ContactEdit.getText().toString().isEmpty()) ? "" : ContactEdit.getText().toString());
            Company.setEmail((EmailEdit.getText().toString().isEmpty()) ? "" : EmailEdit.getText().toString());
            Company.setCountry(itemCountry < 2 ? "" : Countries.get(itemCountry));
            Company.setState(itemState < 2 ? "" : States.get(itemState));
            Company.setCity(itemCity < 2 ? "" : Cities.get(itemCity));
            if (unique_com.size() > 0) {        // check if empty
                // AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                if (General.checkDuplicates(unique_com, Company.getName()) == 0) {
                    if (view.getId() == R.id.btn_new) {   // no duplicate
                        builder.setMessage(R.string.company_new_message).setTitle(R.string.company_profile_title);
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
                                    map.put(getText(R.string.column_key_company).toString(), unique_com.get(i));
                                    feedCompanyList.add(map);
                                } while (++i < unique_com.size());
                                Company.Job = "";                           // it is a new company
                                Company.Location = "";
                                dbGroup.createCompanyList(Company);
                                adapter_com.notifyDataSetChanged();
                                itemCompany = unique_com.indexOf(Company.getName());       // get the index of the new company
                                displayCompanyProfile();
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                    } else if (view.getId() == R.id.btn_upd) {
                        builder.setMessage(R.string.company_update_message).setTitle(R.string.company_profile_title);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // add new name to the list
                                dbGroup.deleteCompanyList(unique_com.get(itemCompany));
                                unique_com.remove(itemCompany);
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
                                    map.put(getText(R.string.column_key_company).toString(), unique_com.get(i));
                                    feedCompanyList.add(map);
                                } while (++i < unique_com.size());
                                dbGroup.createCompanyList(Company);
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
                        builder.setMessage(R.string.company_duplicate_message).setTitle(R.string.company_profile_title);
                        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                    } else if (view.getId() == R.id.btn_upd) {
                        builder.setMessage(R.string.company_update_message).setTitle(R.string.company_profile_title);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                displayCompanyProfile();                             // name is already there
                                dbGroup.updateCompanyList(Company);
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                    }
                }
                AlertDialog dialog = builder.create();
                General.TouchTimeDialog(dialog, view);
            } else {
                // It is empty.  No checking duplicates and no sorting are necessary
                unique_com.add(Company.getName());
                feedCompanyList.clear();
                map = new HashMap<String, String>();
                map.put(getText(R.string.column_key_company).toString(), unique_com.get(0));
                feedCompanyList.add(map);
                dbGroup.createCompanyList(Company);
                all_lists = dbGroup.getAllCompanyLists();
            }
            adapter_com.notifyDataSetChanged();
            displayCompanyProfile();             // display after SetAdapter to show the checked item
        }
     }

    public void onDeleteButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (unique_com.size() > 0) {
            builder.setMessage(R.string.company_delete_confirm_message).setTitle(R.string.company_profile_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dbGroup.deleteCompanyList(feedCompanyList.get(itemCompany).get(getText(R.string.column_key_company).toString()));
                // whether the group or employee is already punched in or not they must be cleared
                dbGroup.clearEmployeeListCompany(feedCompanyList.get(itemCompany).get(getText(R.string.column_key_company).toString()));
                dbGroup.clearWorkGroupListCompany(feedCompanyList.get(itemCompany).get(getText(R.string.column_key_company).toString()));
                unique_com.remove(itemCompany);
                int i = 0;
                    feedCompanyList.clear();
                    while (i < unique_com.size()) {
                        map = new HashMap<String, String>();
                        map.put(getText(R.string.column_key_company).toString(), unique_com.get(i++));
                        feedCompanyList.add(map);
                    } ;
                    adapter_com.notifyDataSetChanged();
                    all_lists = dbGroup.getAllCompanyLists();
                    if (all_lists.size() > 0) {
                        itemCompany = (itemCompany > 0) ? itemCompany-1 : 0;
                        Company = dbGroup.getCompanyList(all_lists.get(itemCompany).getName());
                        displayCompanyProfile();
                    } else {
                        NameEdit.setText("");
                        StreetEdit.setText("");
                        ZipCodeEdit.setText("");
                        PhoneEdit.setText("");
                        ContactEdit.setText("");
                        EmailEdit.setText("");
                        CountrySpinner.setSelection(0);
                        StateSpinner.setSelection(0);
                        CitySpinner.setSelection(0);
                    }
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                }
            });
        } else {
            builder.setMessage(R.string.no_company_message).setTitle(R.string.company_profile_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog dialog = builder.create();
        General.TouchTimeDialog(dialog, view);
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
            dbGroup.closeDB();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
