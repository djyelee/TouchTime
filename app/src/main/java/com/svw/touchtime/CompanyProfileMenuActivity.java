package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.svw.touchtime.R.layout.general_edit_text_view;

public class CompanyProfileMenuActivity extends SettingActivity {
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
    ArrayList<String> list_com;
    HashMap<String, String> map;
    String[] list_items = new String[5];
    int[] list_id = new int[5];
    private int itemCompany = -1;
    private int itemCountry, itemState, itemCity;
    String FileName = "Company Profile";
    CompanyJobLocationList Company;
    Context context;
    File NewProfileFile;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private EmployeeGroupCompanyDBWrapper dbGroup;
    private CountryStateCityDBWrapper dbCountry;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile_menu);
        int Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);        // prevent soft keyboard from squeezing the EditTex Box

        company_list_view = (ListView) findViewById(R.id.company_profile_list_view);
        NameEdit = (EditText) findViewById(R.id.company_name_text);
        StreetEdit = (EditText) findViewById(R.id.company_street_text);
        ZipCodeEdit = (EditText) findViewById(R.id.company_zip_code_text);
        PhoneEdit = (EditText) findViewById(R.id.company_phone_text);
        ContactEdit = (EditText) findViewById(R.id.company_contact_text);
        EmailEdit = (EditText) findViewById(R.id.company_email_text);
        feedCompanyList = new ArrayList<HashMap<String, String>>();

        ReadSettings();
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

        ReadDisplayCompany();
        deleteCSVFiles();

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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
            }
            if (parent == CitySpinner) {
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

    public void ReadDisplayCompany() {
        int i = 0;
        list_com.clear();
        unique_com.clear();
        feedCompanyList.clear();
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
            i = 0;
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
    }

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
                                company_list_view.smoothScrollToPosition(unique_com.indexOf(Company.getName())); // scroll to the newly added item
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
                    }
                    ;
                    adapter_com.notifyDataSetChanged();
                    all_lists = dbGroup.getAllCompanyLists();
                    if (all_lists.size() > 0) {
                        itemCompany = (itemCompany > 0) ? itemCompany - 1 : 0;
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

    public void onImportButtonClicked(View view) {
        boolean FileFound = false;
            File Folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File list[] = Folder.listFiles();
            // for (File s : list) s.delete();
            final File file = new File(Folder, FileName + ".csv");
            for (File f : list) {
                if (f.equals(file)) {
                    FileFound = true;
                    break;
                }
            }
            if (!FileFound) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                builder.setMessage(R.string.company_file_not_found).setTitle(R.string.company_profile_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                General.TouchTimeDialog(dialog, view);
                deleteCSVFiles();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                builder.setMessage(R.string.company_file_will_be_lost).setTitle(R.string.company_profile_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        readCsvFile(file);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                General.TouchTimeDialog(dialog, view);
            }
    }

    public void readCsvFile(File file) {
        Pattern pattern = Pattern.compile("\"(.*?)\"");     // match "  "
        Matcher matcher;
        int noRecords = 0;
        String line;
        String Location = "", Job = "";
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            for (int i = 0; i < all_lists.size(); i++) {
                dbGroup.deleteCompanyList(all_lists.get(i).getName());
            }
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;       // skip empty lines
                noRecords ++;
                if (noRecords > 2) {  // the first two are header and column names
                    Location = "";
                    Job = "";
                    matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        Location = matcher.group(1);
                        Location = Location.replace(",", "\",\"");   // replace , with ","
                        Location = "[\"" + Location + "\"]";
                    }
                    if (matcher.find()) {
                        Job = matcher.group(1);
                        Job = Job.replace(",", "\",\"");
                        Job = "[\"" + Job + "\"]";
                    }
                    String[] item = line.replace("\"", "").split(",");
                    CompanyJobLocationList C = new CompanyJobLocationList();
                    if (item.length > 0) C.setName(item[0]);
                    else C.setName("No Name");
                    if (item.length > 1) C.setStreet(item[1]);
                    else C.setStreet("");
                    if (item.length > 2) C.setCity(item[2]);
                    else C.setCity("");
                    if (item.length > 3) C.setState(item[3]);
                    else C.setState("");
                    if (item.length > 4) C.setZipCode(item[4]);
                    else C.setZipCode("");
                    if (item.length > 5) C.setCountry(item[5]);
                    else C.setCountry("");
                    if (item.length > 6) C.setContact(item[6]);
                    else C.setContact("");
                    if (item.length > 7) C.setPhone(item[7]);
                    else C.setPhone("");
                    if (item.length > 8) C.setEmail(item[8]);
                    else C.setEmail("");
                    C.setLocation(Location);
                    C.setJob(Job);
                    dbGroup.createCompanyList(C);
                }
            }
            all_lists = dbGroup.getAllCompanyLists();
            ReadDisplayCompany();
            if (all_lists.size() > 0) {
                itemCompany = 0;
                displayCompanyProfile();
            }
            br.close();
            deleteCSVFiles();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void onExportButtonClicked(View view) {
        String to = SettingEmail;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("plain/text");
        try {
            deleteCSVFiles();
//          NewTimeSheetFile = new File(context.getExternalCacheDir(), Subject + ".csv");   // app private folder
            NewProfileFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FileName + ".csv");  // download folder
            if (!NewProfileFile.exists()) {
                if (NewProfileFile.createNewFile()) {
                    generateCsvFile(NewProfileFile);
                    i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(NewProfileFile));
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                    i.putExtra(Intent.EXTRA_SUBJECT, FileName);
                    i.putExtra(Intent.EXTRA_TEXT, FileName);
                    startActivity(Intent.createChooser(i, "E-mail"));
                    dbGroup.closeDB();
                    finish();           // force it to quit to remove the file because email intent is asynchronous
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateCsvFile(File sFileName) {
        int i, j;
        FileWriter writer = null;
        String Header = "";
        String ColumnNames = "";
        String Entries;

        Header = getText(R.string.company_profile_title).toString();
        Header = "\"" + Header + "\"" + "\n";
        ColumnNames = getText(R.string.name_title_text).toString() + "," +
                getText(R.string.street_title_text).toString() + "," +
                getText(R.string.city_title_text).toString() + "," +
                getText(R.string.state_title_text).toString() + "," +
                getText(R.string.zip_code_title_text).toString() + "," +
                getText(R.string.country_title_text).toString() + "," +
                getText(R.string.contact_title_text).toString() + "," +
                getText(R.string.phone_title_text).toString() + "," +
                getText(R.string.email_title_text).toString() + "," +
                getText(R.string.location_title_text).toString() + "," +
                getText(R.string.job_title_text).toString() + "\n";

        try {
            writer = new FileWriter(sFileName);
            writer.append(Header);
            writer.append(ColumnNames);
            for (i = 0; i < all_lists.size(); i++) {
                String Location = all_lists.get(i).getLocation().replace("\"", "").replace("[", "").replace("]", "");
                String Job = all_lists.get(i).getJob().replace("\"", "").replace("[", "").replace("]", "");
                Entries = all_lists.get(i).getName() + "," +
                        all_lists.get(i).getStreet() + "," +
                        all_lists.get(i).getCity() + "," +
                        all_lists.get(i).getState() + "," +
                        all_lists.get(i).getZipCode() + "," +
                        all_lists.get(i).getCountry() + "," +
                        all_lists.get(i).getContact() + "," +
                        all_lists.get(i).getPhone() + "," +
                        all_lists.get(i).getEmail() + "," +
                        "\"" + Location + "\"" + "," +      // include comma
                        "\"" + Job + "\"" + "\n";
                writer.append(Entries);
            }
            writer.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void deleteCSVFiles() {
        File Folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File list[] = Folder.listFiles();
        for (File f : list) {
            String name = f.getName();
            if (name.substring(name.lastIndexOf(".") + 1, name.length()).equals("csv")) {
                f.delete();
            }
        }
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
            deleteCSVFiles();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CompanyProfileMenu Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.svw.touchtime/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CompanyProfileMenu Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.svw.touchtime/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
