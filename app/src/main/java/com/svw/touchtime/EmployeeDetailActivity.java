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
    private EditText EmployeeIDEdit, CommentsEdit;
    private Button DocExpButton, DoBButton, DoHButton;
    private RadioGroup radioGroupActive, radioGroupCurrent;
    private RadioButton activeYesButton;
    private RadioButton currentYesButton;
    private ImageView photoView;
    private DatePickerDialog dialog;
    private Calendar calendar;
    private int dateButtonID, employeeID;
    private boolean newEmployeeProfile;

    EmployeeProfileList Employee;
    boolean ret;
    private EmployeeWorkGroupDBWrapper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getText(R.string.title_back).toString().concat(" " + getText(R.string.title_activity_employee_profile_menu).toString()));
        setContentView(R.layout.activity_employee_detail);

        LastNameEdit = (EditText) findViewById(R.id.employee_last_name_text);
        FirstNameEdit = (EditText) findViewById(R.id.employee_first_name_text);
        StreetEdit = (EditText) findViewById(R.id.company_street_text);
        CityEdit = (EditText) findViewById(R.id.company_city_text);
        StateEdit = (EditText) findViewById(R.id.company_state_text);
        ZipCodeEdit = (EditText) findViewById(R.id.company_zip_code_text);
        CountryEdit = (EditText) findViewById(R.id.company_country_text);
        PhoneEdit = (EditText) findViewById(R.id.company_phone_text);
        EmailEdit = (EditText) findViewById(R.id.company_email_text);
        SSNumberEdit = (EditText) findViewById(R.id.employee_ss_text);
        EmployeeIDEdit = (EditText) findViewById(R.id.employee_id_text);
        DocExpButton = (Button) findViewById(R.id.doc_exp_button);
        DoBButton = (Button) findViewById(R.id.dob_button);
        DoHButton = (Button) findViewById(R.id.doh_button);
        CommentsEdit = (EditText) findViewById(R.id.employee_comments_text);
        radioGroupActive = (RadioGroup) findViewById(R.id.selection_active);
        activeYesButton = (RadioButton) findViewById(R.id.radio_active_yes);
        radioGroupCurrent = (RadioGroup) findViewById(R.id.selection_current);
        currentYesButton = (RadioButton) findViewById(R.id.radio_current_yes);
        photoView = (ImageView) findViewById(R.id.photo);

        // get current date information
        calendar = Calendar.getInstance();
        dialog = new DatePickerDialog(this, myDateListener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // database and other data
        db = new EmployeeWorkGroupDBWrapper(this);
        Employee = new EmployeeProfileList();
        employeeID = getIntent().getIntExtra("EmployeeID", -1);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (LastNameEdit.getText().toString().isEmpty()) {              // must have the last name
            builder.setMessage(R.string.last_name_empty_message).setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        if (FirstNameEdit.getText().toString().isEmpty()) {             // must have the first name
            builder.setMessage(R.string.first_name_empty_message).setTitle(R.string.empty_entry_title);
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
                builder.setMessage(R.string.existing_employee_ID_message).setTitle(R.string.employee_profile_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // generate a new ID
                        Employee.setEmployeeID(db.getAvailableEmployeeID());                        // get an unused ID
                        EmployeeIDEdit.setText(String.valueOf(Employee.getEmployeeID()));
                        addUpdateProfile(true);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("EmployeeID", Employee.getEmployeeID());
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else {
                builder.setMessage(R.string.new_employee_ID_message).setTitle(R.string.employee_profile_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Employee.setEmployeeID(Integer.parseInt(EmployeeIDEdit.getText().toString()));
                        addUpdateProfile(true);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("EmployeeID", Employee.getEmployeeID());
                        setResult(RESULT_OK, returnIntent);
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
                builder.setMessage(R.string.update_employee_message).setTitle(R.string.employee_profile_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Employee.setEmployeeID(Integer.parseInt(EmployeeIDEdit.getText().toString()));          // use the new ID that is already entered.
                        addUpdateProfile(false);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("EmployeeID", Employee.getEmployeeID());
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                         Employee.setEmployeeID(Integer.parseInt(EmployeeIDEdit.getText().toString()));          // use the new ID that is already entered.
                         Intent returnIntent = new Intent();
                         returnIntent.putExtra("EmployeeID", Employee.getEmployeeID());
                         setResult(RESULT_OK, returnIntent);
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
        LastNameEdit.setText(Employee.getLastName() == null ? "" : Employee.getLastName());
        FirstNameEdit.setText(Employee.getFirstName() == null ? "" : Employee.getFirstName());
        StreetEdit.setText(Employee.getStreet() == null ? "" : Employee.getStreet());
        CityEdit.setText(Employee.getCity() == null ? "" : Employee.getCity());
        StateEdit.setText(Employee.getState() == null ? "" : Employee.getState());
        ZipCodeEdit.setText(Employee.getZipCode() == null ? "" : Employee.getZipCode());
        CountryEdit.setText(Employee.getCountry() == null ? "" : Employee.getCountry());
        PhoneEdit.setText(Employee.getPhone() == null ? "" : Employee.getPhone());
        EmailEdit.setText(Employee.getEmail() == null ? "" : Employee.getEmail());
        SSNumberEdit.setText(Employee.getSSNumber() == null ? "" : Employee.getSSNumber());
        DoBButton.setText(Employee.getDoB() == null ? "" : Employee.getDoB());
        DoHButton.setText(Employee.getDoH() == null ? "" : Employee.getDoH());
        DocExpButton.setText(Employee.getDocExp() == null ? "" : Employee.getDocExp());
        CommentsEdit.setText(Employee.getComments() == null ? "" : Employee.getComments());
        radioGroupActive.check(Employee.getActive() == 1 ? R.id.radio_active_yes : R.id.radio_active_no);
        radioGroupCurrent.check(Employee.getCurrent() == 1 ? R.id.radio_current_yes : R.id.radio_current_no);
        photoView.setImageBitmap(Employee.getPhoto());
    }

    public void addUpdateProfile(boolean Add) {
        Employee.setLastName((LastNameEdit.getText().toString() == null) ? "" : LastNameEdit.getText().toString());
        Employee.setFirstName((FirstNameEdit.getText().toString() == null) ? "" : FirstNameEdit.getText().toString());
        Employee.setStreet((StreetEdit.getText().toString() == null) ? "" : StreetEdit.getText().toString());
        Employee.setCity((CityEdit.getText().toString() == null) ? "" : CityEdit.getText().toString());
        Employee.setState((StateEdit.getText().toString() == null) ? "" : StateEdit.getText().toString());
        Employee.setZipCode((ZipCodeEdit.getText().toString() == null) ? "" : ZipCodeEdit.getText().toString());
        Employee.setCountry((CountryEdit.getText().toString() == null) ? "" : CountryEdit.getText().toString());
        Employee.setPhone((PhoneEdit.getText().toString() == null) ? "" : PhoneEdit.getText().toString());
        Employee.setEmail((EmailEdit.getText().toString() == null) ? "" : EmailEdit.getText().toString());
        Employee.setSSNumber((SSNumberEdit.getText().toString() == null) ? "" : SSNumberEdit.getText().toString());
        Employee.setDoB((DoBButton.getText().toString() == null) ? "" : DoBButton.getText().toString());
        Employee.setDoH((DoHButton.getText().toString() == null) ? "" : DoHButton.getText().toString());
        Employee.setDocExp((DocExpButton.getText().toString() == null) ? "" : DocExpButton.getText().toString());
        Employee.setComments((CommentsEdit.getText().toString() == null) ? "" : CommentsEdit.getText().toString());
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
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.change_not_saved_message).setTitle(R.string.employee_profile_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
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
