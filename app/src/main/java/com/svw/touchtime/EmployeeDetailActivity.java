package com.svw.touchtime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Calendar;


public class EmployeeDetailActivity extends ActionBarActivity {
    private EditText LastNameEdit, FirstNameEdit;
    private EditText StreetEdit, CityEdit, StateEdit, ZipCodeEdit;
    private EditText CountryEdit, PhoneEdit, EmailEdit, SSNumberEdit;
    private EditText HourlyRateEdit, PieceRateEdit;
    private EditText EmployeeIDEdit, CommentsEdit;
    private Button DocExpButton, DoBButton, DoHButton;
    private RadioGroup radioGroupActive, radioGroupCurrent;
    private RadioButton activeYesButton, activeNoButton;
    private RadioButton currentYesButton, currentNoButton;
    private ImageView photoView;
    private DatePickerDialog dialog;
    private int dateButtonID;
    private boolean newEmployeeProfile;
    private int Caller;
    EmployeeProfileList Employee;
    private EmployeeGroupCompanyDBWrapper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_employee_profile_menu).toString()));
        setContentView(R.layout.activity_employee_detail);

        Calendar calendar;
        int employeeID;
        LastNameEdit = (EditText) findViewById(R.id.employee_last_name_text);
        FirstNameEdit = (EditText) findViewById(R.id.employee_first_name_text);
        StreetEdit = (EditText) findViewById(R.id.company_street_text);
        CityEdit = (EditText) findViewById(R.id.company_city_text);
        StateEdit = (EditText) findViewById(R.id.company_state_text);
        ZipCodeEdit = (EditText) findViewById(R.id.company_zip_code_text);
        CountryEdit = (EditText) findViewById(R.id.company_country_text);
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

        // get current date information
        calendar = Calendar.getInstance();
        dialog = new DatePickerDialog(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar),
                myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // database and other data
        db = new EmployeeGroupCompanyDBWrapper(this);
        Employee = new EmployeeProfileList();

        int [] Data = new int[2];
        Data = getIntent().getIntArrayExtra("EmployeeID");
        employeeID = Data[1];
        Caller = Data[0];
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
        }
        EmployeeIDEdit.setFocusable(false);   // should not be changed

        if (employeeID > 0) {
            newEmployeeProfile = false;     // receive a valid employeeID, it is for update
            Employee = db.getEmployeeList(employeeID);
        } else {
            newEmployeeProfile = true;     // receive an invalid employeeID, a new one is to be added
        }
        displayEmployeeProfile();
        // hide soft keyboard when start up
        getWindow().setSoftInputMode ( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
    }

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
                            Employee.setDocExp(newDate);
                            DocExpButton.setText(Employee.getDocExp());
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
                    dialog.updateDate(Integer.parseInt(Employee.getDoB().substring(0, 4)),
                            Integer.parseInt(Employee.getDoB().substring(5, 7))-1, Integer.parseInt(Employee.getDoB().substring(8, 10)));
                }
                break;
            case R.id.doh_button:
                if (!Employee.getDoH().isEmpty()) {
                    dialog.updateDate(Integer.parseInt(Employee.getDoH().substring(0, 4)),
                            Integer.parseInt(Employee.getDoH().substring(5, 7))-1, Integer.parseInt(Employee.getDoH().substring(8, 10)));
                }
                break;
            case R.id.doc_exp_button:
                if (!Employee.getDocExp().isEmpty()) {
                    dialog.updateDate(Integer.parseInt(Employee.getDocExp().substring(0, 4)),
                            Integer.parseInt(Employee.getDocExp().substring(5, 7)), Integer.parseInt(Employee.getDocExp().substring(8, 10)));
                }
                break;
        }
        dialog.show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        if (LastNameEdit.getText().toString().isEmpty()) {              // must have the last name
            builder.setMessage(R.string.empty_last_name_message).setTitle(R.string.employee_profile_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        if (FirstNameEdit.getText().toString().isEmpty()) {             // must have the first name
            builder.setMessage(R.string.empty_first_name_message).setTitle(R.string.employee_profile_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;    // must have at least the first name
        }
        if (newEmployeeProfile) {       // add a new employee profile
            int ID = EmployeeIDEdit.getText().toString().isEmpty() ? 1 : Integer.parseInt(EmployeeIDEdit.getText().toString());
            if (db.checkEmployeeID(ID)) {      // employee ID already exists
                // generate a new ID
                Employee.setEmployeeID(db.getAvailableEmployeeID());                        // get an unused ID
                EmployeeIDEdit.setText(String.valueOf(Employee.getEmployeeID()));
                builder.setMessage(R.string.employee_assign_ID_message).setTitle(R.string.employee_profile_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addUpdateProfile(true);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("EmployeeID", Employee.getEmployeeID());
                        setResult(RESULT_OK, returnIntent);
                        db.closeDB();
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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
                        db.closeDB();
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            }
        } else {                        // update the employee profile
            if (db.checkEmployeeID(Integer.parseInt(EmployeeIDEdit.getText().toString()))) {      // employee ID already exists
                builder.setMessage(R.string.employee_update_message).setTitle(R.string.employee_profile_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Employee.setEmployeeID(Integer.parseInt(EmployeeIDEdit.getText().toString()));          // use the new ID that is already entered.
                        addUpdateProfile(false);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("EmployeeID", Employee.getEmployeeID());
                        setResult(RESULT_OK, returnIntent);
                        db.closeDB();
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                         Employee.setEmployeeID(Integer.parseInt(EmployeeIDEdit.getText().toString()));          // use the new ID that is already entered.
                         Intent returnIntent = new Intent();
                         returnIntent.putExtra("EmployeeID", Employee.getEmployeeID());
                         setResult(RESULT_OK, returnIntent);
                         db.closeDB();
                         finish();
                     }
                });
             }
        }
        AlertDialog dialog = builder.create();
        dialog.show();
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
        EmployeeIDEdit.setText(String.valueOf(Employee.getEmployeeID()));
        LastNameEdit.setText(Employee.getLastName().isEmpty() ? "" : Employee.getLastName());
        FirstNameEdit.setText(Employee.getFirstName().isEmpty() ? "" : Employee.getFirstName());
        StreetEdit.setText(Employee.getStreet().isEmpty() ? "" : Employee.getStreet());
        CityEdit.setText(Employee.getCity().isEmpty() ? "" : Employee.getCity());
        StateEdit.setText(Employee.getState().isEmpty() ? "" : Employee.getState());
        ZipCodeEdit.setText(Employee.getZipCode().isEmpty() ? "" : Employee.getZipCode());
        CountryEdit.setText(Employee.getCountry().isEmpty() ? "" : Employee.getCountry());
        PhoneEdit.setText(Employee.getPhone().isEmpty() ? "" : Employee.getPhone());
        EmailEdit.setText(Employee.getEmail().isEmpty() ? "" : Employee.getEmail());
        if (Caller == R.id.caller_administrator) {
            HourlyRateEdit.setText(String.valueOf(Employee.getHourlyRate() <= 0.0 ? 10.0 : Employee.getHourlyRate()));
            PieceRateEdit.setText(String.valueOf(Employee.getPieceRate() <= 0.0 ? 10.0 : Employee.getPieceRate()));
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
        Employee.setCity((CityEdit.getText().toString().isEmpty()) ? "" : CityEdit.getText().toString());
        Employee.setState((StateEdit.getText().toString().isEmpty()) ? "" : StateEdit.getText().toString());
        Employee.setZipCode((ZipCodeEdit.getText().toString().isEmpty()) ? "" : ZipCodeEdit.getText().toString());
        Employee.setCountry((CountryEdit.getText().toString().isEmpty()) ? "" : CountryEdit.getText().toString());
        Employee.setPhone((PhoneEdit.getText().toString().isEmpty()) ? "" : PhoneEdit.getText().toString());
        Employee.setEmail((EmailEdit.getText().toString().isEmpty()) ? "" : EmailEdit.getText().toString());
        if (Caller == R.id.caller_administrator) {
            Employee.setHourlyRate((HourlyRateEdit.getText().toString().isEmpty()) ? 10.0 : Double.parseDouble(HourlyRateEdit.getText().toString()));
            Employee.setPieceRate((PieceRateEdit.getText().toString().isEmpty()) ? 10.0 : Double.parseDouble(PieceRateEdit.getText().toString()));
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
            db.createEmployeeList(Employee);
        } else {
            // if the current id displayed already exists, then update
            db.updateEmployeeList(Employee);
        }
        displayEmployeeProfile();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
                    builder.setMessage(R.string.change_not_saved_message).setTitle(R.string.employee_profile_title);
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
