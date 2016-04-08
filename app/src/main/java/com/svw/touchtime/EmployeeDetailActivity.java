package com.svw.touchtime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.svw.touchtime.R.layout.general_edit_text_view;


public class EmployeeDetailActivity extends ActionBarActivity {
    private EditText LastNameEdit, FirstNameEdit;
    private EditText StreetEdit, ZipCodeEdit;
    private EditText PhoneEdit, EmailEdit, SSNumberEdit;
    private EditText HourlyRateEdit, PieceRateEdit;
    private EditText EmployeeIDEdit, CommentsEdit;
    Spinner CountrySpinner, StateSpinner, CitySpinner;
    ArrayAdapter<String> adapter_country, adapter_state, adapter_city;
    private Button DocExpButton, DoBButton, DoHButton;
    private RadioGroup radioGroupActive, radioGroupCurrent;
    private RadioButton activeYesButton, activeNoButton;
    private RadioButton currentYesButton, currentNoButton;
    private ImageView photoView;
    private DatePickerDialog CalendarDialog;
    private int dateButtonID;
    private int itemCountry, itemState, itemCity;
    private int Caller, employeeID;
    EmployeeProfileList Employee;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private EmployeeGroupCompanyDBWrapper dbGroup;
    private CountryStateCityDBWrapper dbCountry;
    static final int PICK_NEW_REQUEST = 123;             // The request code
    static final int PICK_UPDATE_REQUEST = 456;          // The request code
    static final int PICK_COPY_REQUEST = 789;          // The request code
    private int Request;
    ArrayList<String> Countries;
    ArrayList<String> States;
    ArrayList<String> Cities;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_employee_profile_menu).toString()));
        setContentView(R.layout.activity_employee_detail);

        Calendar calendar;
        LastNameEdit = (EditText) findViewById(R.id.employee_last_name_text);
        FirstNameEdit = (EditText) findViewById(R.id.employee_first_name_text);
        StreetEdit = (EditText) findViewById(R.id.company_street_text);
        ZipCodeEdit = (EditText) findViewById(R.id.company_zip_code_text);
        PhoneEdit = (EditText) findViewById(R.id.company_phone_text);
        EmailEdit = (EditText) findViewById(R.id.company_email_text);
        HourlyRateEdit = (EditText) findViewById(R.id.employee_hourly_rate_text);
        PieceRateEdit = (EditText) findViewById(R.id.employee_piece_rate_text);
        SSNumberEdit = (EditText) findViewById(R.id.employee_ss_text);
        EmployeeIDEdit = (EditText) findViewById(R.id.employee_id_text);
        DocExpButton = (Button) findViewById(R.id.doc_exp_button);
        DoBButton = (Button) findViewById(R.id.dob_button);
        DoHButton = (Button) findViewById(R.id.doh_button);
        CommentsEdit = (EditText) findViewById(R.id.employee_comments_text);
        radioGroupActive = (RadioGroup) findViewById(R.id.selection_active);
        activeYesButton = (RadioButton) findViewById(R.id.radio_active_yes);
        activeNoButton = (RadioButton) findViewById(R.id.radio_active_no);
        radioGroupCurrent = (RadioGroup) findViewById(R.id.selection_current);
        currentYesButton = (RadioButton) findViewById(R.id.radio_current_yes);
        currentNoButton = (RadioButton) findViewById(R.id.radio_current_no);
        photoView = (ImageView) findViewById(R.id.photo);

        // database and other data
        dbGroup = new EmployeeGroupCompanyDBWrapper(this);
        dbCountry = new CountryStateCityDBWrapper(this);
        Employee = new EmployeeProfileList();
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

        // get current date information
        calendar = Calendar.getInstance();
        CalendarDialog = new DatePickerDialog(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar),
                myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        int [] Data = new int[2];
        Data = getIntent().getIntArrayExtra("EmployeeID");
        Caller = Data[0];
        Request = Data[1];
        employeeID = Data[2];
        if (Caller != R.id.caller_administrator) {
            HourlyRateEdit.setFocusable(false);
            PieceRateEdit.setFocusable(false);
            SSNumberEdit.setFocusable(false);
            HourlyRateEdit.setBackgroundColor(getResources().getColor(R.color.svw_gray));
            PieceRateEdit.setBackgroundColor(getResources().getColor(R.color.svw_gray));
            SSNumberEdit.setBackgroundColor(getResources().getColor(R.color.svw_gray));
            activeYesButton.setClickable(false);
            currentYesButton.setClickable(false);
            activeNoButton.setClickable(false);
            currentNoButton.setClickable(false);
            radioGroupActive.setBackgroundColor(getResources().getColor(R.color.svw_gray));
            radioGroupCurrent.setBackgroundColor(getResources().getColor(R.color.svw_gray));
        }

        // building database for country, state, city spinner from employee list
        ArrayList<EmployeeProfileList> all_employee_lists;
        all_employee_lists = dbGroup.getAllEmployeeLists();
        int i = 0;
        dbCountry.clearAllList();           // clear the database for country, state, city spinner
        if (all_employee_lists.size() > 0) {
            do {
                // building database for country, state, city spinner from employee list
                dbCountry.addMissingList(all_employee_lists.get(i).getCountry(), all_employee_lists.get(i).getState(), all_employee_lists.get(i).getCity());
            } while (++i < all_employee_lists.size());
        }
        // building database for country, state, city spinner from company list
        ArrayList<CompanyJobLocationList> Lists;
        Lists = dbGroup.getAllCompanyLists();
        if (Lists.size() > 0) {
            for (i = 0; i < Lists.size(); i++) {
                dbCountry.addMissingList(Lists.get(i).getCountry(), Lists.get(i).getState(), Lists.get(i).getCity());
            }
        }

        if (Request == PICK_UPDATE_REQUEST) EmployeeIDEdit.setFocusable(false);   // should not be changed
        if (Request == PICK_UPDATE_REQUEST || Request == PICK_COPY_REQUEST) {   // display what is selected
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Employee = dbGroup.getEmployeeList(employeeID);
            if (Employee.getDocExp().compareTo(df.format(Calendar.getInstance().getTime())) < 0) {
                radioGroupCurrent.check(R.id.radio_current_no);
                Employee.setCurrent(0);
            } else {
                radioGroupCurrent.check(R.id.radio_current_yes);
                Employee.setCurrent(1);
            }
            itemCountry = adapter_country.getPosition(Employee.getCountry());
            if (itemCountry > 0) {
                CountrySpinner.setSelection(itemCountry);
                States = dbCountry.getStateList(Employee.getCountry(), States);
                adapter_state.notifyDataSetChanged();
            }
            itemState = adapter_state.getPosition(Employee.getState());
            if (itemState > 0) {
                StateSpinner.setSelection(itemState);
                Cities = dbCountry.getCityList(Employee.getCountry(), Employee.getState(), Cities);
                adapter_city.notifyDataSetChanged();
            }
            itemCity = adapter_city.getPosition(Employee.getCity());
            if (itemCity > 0) CitySpinner.setSelection(itemCity);
            dbGroup.updateEmployeeList(Employee);
        }
        displayEmployeeProfile();
        // hide soft keyboard when start up
        getWindow().setSoftInputMode ( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
    }

    public AdapterView.OnItemSelectedListener OnSpinnerCL = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            LayoutInflater factory = LayoutInflater.from(context);
            final View textEntryView = factory.inflate(R.layout.spinner_edit_text_view, null);
            final EditText dialog_text = (EditText) textEntryView.findViewById(R.id.spinner_edit_text);
            if (parent == CountrySpinner) {
                if (pos == 1) {             // 0 is blank, 1 is add new
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.TouchTimeDialog));
                    builder.setMessage(R.string.employee_enter_country_message).setTitle(R.string.employee_profile_title).setView(textEntryView);
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
                            CountrySpinner.setSelection(Countries.indexOf(Employee.getCountry()));
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
                            States.add(0, "");       // add items first
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
                    builder.setMessage(R.string.employee_enter_state_message).setTitle(R.string.employee_profile_title).setView(textEntryView);
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
                            StateSpinner.setSelection(States.indexOf(Employee.getState()));
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
                    builder.setMessage(R.string.employee_enter_city_message).setTitle(R.string.employee_profile_title).setView(textEntryView);
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
                            CitySpinner.setSelection(Cities.indexOf(Employee.getCity()));
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

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (view.isShown()) {           // somehow onDateSet is called twice in higher version of Android, use this to avoid doing it the second time.
                String newDate = String.format("%4s-%2s-%2s", year, ++monthOfYear, dayOfMonth).replace(' ', '0');   // monthOfYear starts from 0
                switch (dateButtonID) {
                    case R.id.dob_button:
                        if (!newDate.equals(Employee.getDoB())) {
                            Employee.setDoB(newDate);
                            DoBButton.setText(newDate);
                        }
                        break;
                    case R.id.doh_button:
                        if (!newDate.equals(Employee.getDoH())) {
                            Employee.setDoH(newDate);
                            DoHButton.setText(newDate);
                        }
                        break;
                    case R.id.doc_exp_button:
                        if (!newDate.equals(Employee.getDocExp())) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            Employee.setDocExp(newDate);
                            DocExpButton.setText(Employee.getDocExp());
                            if (newDate.compareTo(df.format(Calendar.getInstance().getTime())) < 0) {
                                radioGroupCurrent.check(R.id.radio_current_no);
                                Employee.setCurrent(0);
                            } else {
                                radioGroupCurrent.check(R.id.radio_current_yes);
                                Employee.setCurrent(1);
                            }
                        }
                        break;
                }
            }
        }
    };

    public void onSetDateButtonClicked(View view) {
        dateButtonID = view.getId();
        switch (dateButtonID) {
            case R.id.dob_button:
                if (!Employee.getDoB().isEmpty()) {
                    int y = Integer.parseInt(Employee.getDoB().substring(0, 4));
                    int m = Integer.parseInt(Employee.getDoB().substring(5, 7));
                    int d = Integer.parseInt(Employee.getDoB().substring(8, 10));
                    CalendarDialog.updateDate(Integer.parseInt(Employee.getDoB().substring(0, 4)),
                            Integer.parseInt(Employee.getDoB().substring(5, 7))-1, Integer.parseInt(Employee.getDoB().substring(8, 10)));
                }
                break;
            case R.id.doh_button:
                if (!Employee.getDoH().isEmpty()) {
                    CalendarDialog.updateDate(Integer.parseInt(Employee.getDoH().substring(0, 4)),
                            Integer.parseInt(Employee.getDoH().substring(5, 7))-1, Integer.parseInt(Employee.getDoH().substring(8, 10)));
                }
                break;
            case R.id.doc_exp_button:
                if (!Employee.getDocExp().isEmpty()) {
                    CalendarDialog.updateDate(Integer.parseInt(Employee.getDocExp().substring(0, 4)),
                            Integer.parseInt(Employee.getDocExp().substring(5, 7)), Integer.parseInt(Employee.getDocExp().substring(8, 10)));
                }
                break;
        }
        CalendarDialog.show();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public void onTakePictureClicked(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photoView.setImageBitmap(imageBitmap);
            Employee.setPhoto(imageBitmap);
        }
    }
    private void setPic() {
        // Get the dimensions of the View
        int targetW = photoView.getWidth();
        int targetH = photoView.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //       Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        //       photoView.setImageBitmap(bitmap);
    }

     public void onDoneButtonClicked(View view) {
        getWindow().setSoftInputMode ( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
         int ID = EmployeeIDEdit.getText().toString().isEmpty() ? 0 : Integer.parseInt(EmployeeIDEdit.getText().toString());
         if (ID == 0) {      // employee ID is not entered
             // generate a new ID
             // Employee.setEmployeeID(db.getAvailableEmployeeID());                        // get an unused ID
             // EmployeeIDEdit.setText(String.valueOf(Employee.getEmployeeID()));
             builder.setMessage(R.string.employee_assign_ID_message).setTitle(R.string.employee_profile_title);
             builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int id) {
                     //addUpdateProfile(true);
                     //Intent returnIntent = new Intent();
                     //returnIntent.putExtra("EmployeeID", Employee.getEmployeeID());
                     //setResult(RESULT_OK, returnIntent);
                     //db.closeDB();
                     //finish();
                 }
             });
             AlertDialog dialog = builder.create();
             General.TouchTimeDialog(dialog, view);
             return;
         }
         if (LastNameEdit.getText().toString().isEmpty()) {              // must have the last name
            builder.setMessage(R.string.empty_last_name_message).setTitle(R.string.employee_profile_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
            return;
        }
        if (FirstNameEdit.getText().toString().isEmpty()) {             // must have the first name
            builder.setMessage(R.string.empty_first_name_message).setTitle(R.string.employee_profile_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
            return;    // must have at least the first name
        }
         if (Employee.getPhoto() == null) {             // must have a photo
             builder.setMessage(R.string.empty_take_photo_message).setTitle(R.string.employee_profile_title);
             builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int id) {
                 }
             });
             AlertDialog dialog = builder.create();
             General.TouchTimeDialog(dialog, view);
             return;    // must have at least the first name
         }
         if (Countries.size() > itemCountry) Employee.setCountry(Countries.get(itemCountry));
         if (States.size() > itemState) Employee.setState(States.get(itemState));
         if (Cities.size() > itemCity) Employee.setCity(Cities.get(itemCity));
 /*
          if (Countries.size() > itemCountry && States.size() > itemState && Cities.size() > itemCity &&
                 !dbCountry.checkCityList(Countries.get(itemCountry), States.get(itemState), Cities.get(itemCity))) {
           if (!Employee.getCountry().isEmpty() && !Employee.getState().isEmpty()) {
                  if (!Employee.getCity().isEmpty()) {
                     builder.setMessage(R.string.employee_new_city_message).setTitle(R.string.employee_profile_title);
                     builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int id) {
                         dbCountry.addMissingList(Employee.getCountry(), Employee.getState(), Employee.getCity());
                         }
                     });
                     builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int id) {
                         }
                     });
                 }
                 if (!Employee.getState().isEmpty()){
                     builder.setMessage(R.string.employee_new_state_message).setTitle(R.string.employee_profile_title);
                     builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int id) {
                         dbCountry.addMissingList(Employee.getCountry(), Employee.getState(), Employee.getCity());
                         }
                     });
                     builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int id) {
                         }
                     });
                 }
                 AlertDialog dialog = builder.create();
                 General.TouchTimeDialog(dialog, view);
                 return;
             }
         }
         */
         if (Request == PICK_NEW_REQUEST || Request == PICK_COPY_REQUEST) {       // add a new employee profile
             if (dbGroup.checkEmployeeID(ID)) {        // ID already exists
                 builder.setMessage(R.string.employee_assign_ID_message).setTitle(R.string.employee_profile_title);
                 builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                     }
                 });
             } else {
                 Employee.setEmployeeID(Integer.parseInt(EmployeeIDEdit.getText().toString()));
                 builder.setMessage(R.string.employee_new_ID_message).setTitle(R.string.employee_profile_title);
                 builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            addUpdateProfile(true);
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("EmployeeID", Employee.getEmployeeID());
                            setResult(RESULT_OK, returnIntent);
                            dbGroup.closeDB();
                            finish();
                        }
                 });
                 builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                 });
            }
        } else if (Request == PICK_UPDATE_REQUEST){              // update the employee profile
            if (dbGroup.checkEmployeeID(Integer.parseInt(EmployeeIDEdit.getText().toString()))) {      // employee ID already exists
                builder.setMessage(R.string.employee_update_message).setTitle(R.string.employee_profile_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Employee.setEmployeeID(Integer.parseInt(EmployeeIDEdit.getText().toString()));          // use the new ID that is already entered.
                        addUpdateProfile(false);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("EmployeeID", Employee.getEmployeeID());
                        setResult(RESULT_OK, returnIntent);
                        dbGroup.closeDB();
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                         Employee.setEmployeeID(Integer.parseInt(EmployeeIDEdit.getText().toString()));          // use the new ID that is already entered.
                         Intent returnIntent = new Intent();
                         returnIntent.putExtra("EmployeeID", Employee.getEmployeeID());
                         setResult(RESULT_OK, returnIntent);
                         dbGroup.closeDB();
                         finish();
                     }
                });
             }
        }
        AlertDialog dialog = builder.create();
        General.TouchTimeDialog(dialog, view);
     }

    public void onRadioButtonClicked(View view) {
        int checkedActive = radioGroupActive.getCheckedRadioButtonId() == activeYesButton.getId() ? 1 : 0;
        int checkedCurrent = radioGroupActive.getCheckedRadioButtonId() == currentYesButton.getId() ? 1 : 0;
        if (checkedActive != Employee.getActive()) {
            Employee.setActive(checkedActive);
        }
        if (checkedCurrent != Employee.getCurrent()) {
            Employee.setCurrent(checkedCurrent);
        }
    }

    public void displayEmployeeProfile() {
        EmployeeIDEdit.setText(Employee.getEmployeeID() <= 0 ? "" : String.valueOf(Employee.getEmployeeID()));
        if (Request == PICK_COPY_REQUEST) {     // some things cannot be copied
            Employee.setLastName("");
            Employee.setFirstName("");
            Employee.setStatus(0);
            Employee.setDoB("");
            Employee.setSSNumber("");
            Employee.setDoB("");
            Employee.setDocExp("");
            Employee.setCompany("");
            Employee.setLocation("");
            Employee.setJob("");
            Employee.setGroup(0);
        }
        LastNameEdit.setText(Employee.getLastName().isEmpty() ? "" : Employee.getLastName());
        FirstNameEdit.setText(Employee.getFirstName().isEmpty() ? "" : Employee.getFirstName());
        StreetEdit.setText(Employee.getStreet().isEmpty() ? "" : Employee.getStreet());
        ZipCodeEdit.setText(Employee.getZipCode().isEmpty() ? "" : Employee.getZipCode());
        PhoneEdit.setText(Employee.getPhone().isEmpty() ? "" : Employee.getPhone());
        EmailEdit.setText(Employee.getEmail().isEmpty() ? "" : Employee.getEmail());
        if (Caller == R.id.caller_administrator) {
            HourlyRateEdit.setText(Employee.getHourlyRate() <= 0.0 ? "" : String.valueOf(Employee.getHourlyRate()));
            PieceRateEdit.setText(Employee.getPieceRate() <= 0.0 ? "" : String.valueOf(Employee.getPieceRate()));
            SSNumberEdit.setText(Employee.getSSNumber().isEmpty() ? "" : Employee.getSSNumber());
        }
        DoBButton.setText(Employee.getDoB().isEmpty() ? "" : Employee.getDoB());
        DoHButton.setText(Employee.getDoH().isEmpty() ? "" : Employee.getDoH());
        DocExpButton.setText(Employee.getDocExp().isEmpty() ? "" : Employee.getDocExp());
        CommentsEdit.setText(Employee.getComments().isEmpty() ? "" : Employee.getComments());
        radioGroupActive.check(Employee.getActive() == 1 ? R.id.radio_active_yes : R.id.radio_active_no);
        radioGroupCurrent.check(Employee.getCurrent() == 1 ? R.id.radio_current_yes : R.id.radio_current_no);
        photoView.setImageBitmap(Employee.getPhoto());
    }

    public void addUpdateProfile(boolean Add) {
        Employee.setLastName((LastNameEdit.getText().toString().isEmpty()) ? "" : LastNameEdit.getText().toString());
        Employee.setFirstName((FirstNameEdit.getText().toString().isEmpty()) ? "" : FirstNameEdit.getText().toString());
        Employee.setStreet((StreetEdit.getText().toString().isEmpty()) ? "" : StreetEdit.getText().toString());
        Employee.setZipCode((ZipCodeEdit.getText().toString().isEmpty()) ? "" : ZipCodeEdit.getText().toString());
        Employee.setPhone((PhoneEdit.getText().toString().isEmpty()) ? "" : PhoneEdit.getText().toString());
        Employee.setEmail((EmailEdit.getText().toString().isEmpty()) ? "" : EmailEdit.getText().toString());
        if (Caller == R.id.caller_administrator) {
            Employee.setHourlyRate((HourlyRateEdit.getText().toString().isEmpty()) ? 0.0 : Double.parseDouble(HourlyRateEdit.getText().toString()));
            Employee.setPieceRate((PieceRateEdit.getText().toString().isEmpty()) ? 0.0 : Double.parseDouble(PieceRateEdit.getText().toString()));
            Employee.setSSNumber((SSNumberEdit.getText().toString().isEmpty()) ? "" : SSNumberEdit.getText().toString());
        }
        Employee.setDoB((DoBButton.getText().toString().isEmpty()) ? "" : DoBButton.getText().toString());
        Employee.setDoH((DoHButton.getText().toString().isEmpty()) ? "" : DoHButton.getText().toString());
        Employee.setDocExp((DocExpButton.getText().toString().isEmpty()) ? "" : DocExpButton.getText().toString());
        Employee.setComments((CommentsEdit.getText().toString().isEmpty()) ? "" : CommentsEdit.getText().toString());
        Employee.setActive(radioGroupActive.getCheckedRadioButtonId() == activeYesButton.getId() ? 1 : 0);
        Employee.setCurrent(radioGroupCurrent.getCheckedRadioButtonId() == currentYesButton.getId() ? 1 : 0);
        BitmapDrawable drawable = (BitmapDrawable) photoView.getDrawable();
        Employee.setPhoto((drawable == null) ? null : drawable.getBitmap());
        if (Add) {
            // add the new one to the end of the list
            dbGroup.createEmployeeList(Employee);
        } else {
            // if the current id displayed already exists, then update
            dbGroup.updateEmployeeList(Employee);
        }
        // displayEmployeeProfile();
    }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                //       android.support.v7.app.ActionBar actionBar = getSupportActionBar();
                //       if (actionBar != null) {
                //         actionBar.setHomeButtonEnabled(false); // disable the button
                //          actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
                //          actionBar.setDisplayShowHomeEnabled(false); // remove the icon
                //       }
//  Add the above lines to remove back button on action bar

                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.menu_employee_detail, menu);
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
                    builder.setMessage(R.string.change_not_saved_message).setTitle(R.string.employee_profile_title);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dbGroup.closeDB();
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
