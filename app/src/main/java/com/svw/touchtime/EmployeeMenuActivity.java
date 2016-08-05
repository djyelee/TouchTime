package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class EmployeeMenuActivity extends ActionBarActivity {
    public TextView Current_date;
    public TextView Current_time;
    public EditText Employee_ID;
    public TextView Last_Name_View, First_Name_View, Group_View, Group_Name_View;
    public TextView Status_View, Company_View, Location_View, Job_View;
    public TextView Active_View,Current_View;
    public TextView DOH_View, DOE_View;
    private ImageView photoView;
    EmployeeProfileList Employee;
    DailyActivityList Activity;
    TouchTimeGeneralFunctions General;
    DateFormat dtFormat;
    public int employeeID;
    private EmployeeGroupCompanyDBWrapper dbGroup;
    private DailyActivityDBWrapper dbActivity;
    static final int PICK_JOB_REQUEST = 123;
    static final int MOVE_JOB_REQUEST = 456;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_menu);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);

        addListenerOnButton();
        Status_View = (TextView) findViewById(R.id.current_status);
        Last_Name_View = (TextView) findViewById(R.id.employee_last_name_text);
        First_Name_View = (TextView) findViewById(R.id.employee_first_name_text);
        Group_View = (TextView) findViewById(R.id.employee_group_text);
        Group_Name_View = (TextView) findViewById(R.id.employee_group_name_text);
        Company_View = (TextView) findViewById(R.id.employee_company_text);
        Location_View = (TextView) findViewById(R.id.employee_location_text);
        Job_View = (TextView) findViewById(R.id.employee_job_text);
        DOH_View = (TextView) findViewById(R.id.employee_hire_date_text);
        DOE_View = (TextView) findViewById(R.id.employee_doc_exp_text);
        Active_View = (TextView) findViewById(R.id.employee_active_text);
        Current_View = (TextView) findViewById(R.id.employee_current_text);
        photoView = (ImageView) findViewById(R.id.photo);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);        // prevent soft keyboard from squeezing the EditTex Box
        getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        DateFormat yf = new SimpleDateFormat("yyyy");
        dtFormat = new SimpleDateFormat(getText(R.string.date_time_format).toString());
        int year = Integer.parseInt(yf.format(Calendar.getInstance().getTime()));
        dbActivity = new DailyActivityDBWrapper(this, year);
        dbGroup = new EmployeeGroupCompanyDBWrapper(this);
        // retrieve employee lists
        Employee = new EmployeeProfileList();
        Activity = new DailyActivityList();
        General = new TouchTimeGeneralFunctions();
        context = this;
        if(dbGroup.getEmployeeListCount() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_menu_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dbGroup.closeDB();
                    dbActivity.closeDB();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, findViewById(android.R.id.content));
        }

        Current_date = (TextView) findViewById(R.id.current_date);
        Current_time = (TextView) findViewById(R.id.current_time);
        DateFormat df = new SimpleDateFormat(getText(R.string.date_MDY_format).toString());       // Current_date.setText(df.getDateInstance().format(new Date()));
        Current_date.setText(df.format(Calendar.getInstance().getTime()));
        CountDownTimer uy = new CountDownTimer(2000000000, 1000) {
            public void onFinish() {
                Current_time.setText("Finish");
            }
            @Override
            public void onTick(long l) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Current_time.setText(currentDateTimeString);
            }
        }.start();
    }

    public void addListenerOnButton() {
        Employee_ID = (EditText) findViewById(R.id.EmployeeID);
        Employee_ID.setOnTouchListener(new TextView.OnTouchListener() {         // set blank whenever touched
            public boolean onTouch(View v, MotionEvent event) {
                Employee_ID.setText("");
              return false;
            }
        });
        Employee_ID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    employeeID = Employee_ID.getText().toString().isEmpty() ? 0 : Integer.parseInt(Employee_ID.getText().toString());
                    // Enable and disable will make the soft keyboard disappear
                    Employee_ID.setEnabled(false);
                    Employee_ID.setEnabled(true);
                    checkID();
                    handled = true;
                }
                return handled;
            }
        });
    }

    public void checkID() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (dbGroup.checkEmployeeID(employeeID)) {          // a valid ID is entered
            Employee = dbGroup.getEmployeeList(employeeID);

            Last_Name_View.setText(Employee.getLastName().isEmpty() ? "" : Employee.getLastName());
            First_Name_View.setText(Employee.getFirstName().isEmpty() ? "" : Employee.getFirstName());
            Group_View.setText(Employee.getGroup() <= 0 ? "" : String.valueOf(Employee.getGroup()));
            if (Employee.getGroup() > 0) {          // belongs to a group otherwise 0
                WorkGroupList Group = new WorkGroupList();
                Group = dbGroup.getWorkGroupList(Employee.getGroup());
                Group_Name_View.setText(Group.getGroupName().isEmpty() ? "" : Group.getGroupName());
            }
            Company_View.setText(Employee.getCompany().isEmpty() ? "" : Employee.getCompany());
            Location_View.setText(Employee.getLocation().isEmpty() ? "" : Employee.getLocation());
            Job_View.setText(Employee.getJob().isEmpty() ? "" : Employee.getJob());
            DOH_View.setText(Employee.getDoH().isEmpty() ? "" : General.convertYMDtoMDY(Employee.getDoH()));
            DOE_View.setText(Employee.getDocExp().isEmpty() ? "" : General.convertYMDtoMDY(Employee.getDocExp()));
            Active_View.setText(Employee.getActive() == 0 ? getText(R.string.no) : getText(R.string.yes));
            Current_View.setText(Employee.getCurrent() == 0 ? getText(R.string.no) : getText(R.string.yes));
            photoView.setImageBitmap(Employee.getPhoto());
            Status_View.setText(Employee.getStatus() == 0 ? getText(R.string.out).toString() : getText(R.string.in).toString());
        } else {
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_menu_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Employee_ID.setText("");
                    Employee_ID.setEnabled(true);
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, this.findViewById(android.R.id.content));
        }
    }

    public void onPunchInButtonClicked(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        DateFormat df = new SimpleDateFormat(getText(R.string.date_YMD_format).toString());
        if (dbGroup.checkEmployeeID(employeeID)) {
            if (dbGroup.getEmployeeListStatus(Employee.getEmployeeID()) == 1) {
                builder.setMessage(getText(R.string.employee_already_punched_in_message).toString()).setTitle(R.string.employee_menu_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                });
            } else if (Employee.getCompany().isEmpty() || Employee.getLocation().isEmpty() || Employee.getJob().isEmpty()) {
                builder.setMessage(getText(R.string.no_company_location_job_message).toString()).setTitle(R.string.employee_menu_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                });
            } else if (Employee.Active == 0) {
                builder.setMessage(getText(R.string.employee_not_active).toString()).setTitle(R.string.employee_menu_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else if (Employee.Current == 0 || Employee.getDocExp().compareTo(df.format(Calendar.getInstance().getTime()))<0) {
                builder.setMessage(getText(R.string.employee_doc_expire).toString()).setTitle(R.string.employee_menu_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else {
                if (Employee.getGroup() > 0) {                  // employee belongs to a group
                    WorkGroupList EmployeeWorkGroup = new WorkGroupList();
                    EmployeeWorkGroup = dbGroup.getWorkGroupList(Employee.getGroup());
                    if (EmployeeWorkGroup.getStatus() == 0) {          // and the group is not punched in, punch in employee?
                        builder.setMessage(getText(R.string.employee_group_not_punch_in_message).toString() + " Employee #" + String.valueOf(Employee.getEmployeeID())).setTitle(R.string.employee_menu_title);
                    } else if (Employee.getCompany().equals(EmployeeWorkGroup.getCompany())
                            && Employee.getLocation().equals(EmployeeWorkGroup.getLocation())
                            && Employee.getJob().equals(EmployeeWorkGroup.getJob())) {            // same job as the company
                        builder.setMessage(getText(R.string.employee_punch_in_same_job_message).toString() + " Employee #" + String.valueOf(Employee.getEmployeeID())).setTitle(R.string.employee_menu_title);
                    } else if (!Employee.getCompany().equals(EmployeeWorkGroup.getCompany())
                            || !Employee.getLocation().equals(EmployeeWorkGroup.getLocation())
                            || !Employee.getJob().equals(EmployeeWorkGroup.getJob())) {            // same job as the company
                        builder.setMessage(getText(R.string.employee_punch_in_different_job_message).toString() + " Employee #" + String.valueOf(Employee.getEmployeeID())).setTitle(R.string.employee_menu_title);
                    }
                } else {
                    builder.setMessage(getText(R.string.employee_punch_in_message).toString() + " # " + String.valueOf(Employee.getEmployeeID())).setTitle(R.string.employee_menu_title);
                }
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        employeePunchIn(view);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            }
        } else {
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog dialog = builder.create();
        General.TouchTimeDialog(dialog, view);
    }

    public void onPunchOutButtonClicked(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (dbGroup.checkEmployeeID(employeeID)) {
            if (dbGroup.getEmployeeListStatus(Employee.getEmployeeID()) == 0) {
                builder.setMessage(getText(R.string.employee_already_punched_out_message).toString()).setTitle(R.string.employee_menu_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else {
                if (Employee.getGroup() > 0) {           // belongs to a group
                    WorkGroupList Group = new WorkGroupList();
                    Group = dbGroup.getWorkGroupList(Employee.getGroup());
                    if (Group.getStatus() == 0) {          // and the group already punched out, punch out employee?
                        builder.setMessage(getText(R.string.employee_group_already_punch_out_message).toString() + " Employee #" + String.valueOf(Employee.getEmployeeID())).setTitle(R.string.employee_punch_title);
                    } else if (Employee.getCompany().equals(Group.getCompany())
                            && Employee.getLocation().equals(Group.getLocation())
                            && Employee.getJob().equals(Group.getJob())) {            // same job as the company
                        builder.setMessage(getText(R.string.employee_punch_out_same_job_message).toString() + " Employee #" + String.valueOf(Employee.getEmployeeID())).setTitle(R.string.employee_punch_title);
                    } else if (!Employee.getCompany().equals(Group.getCompany())
                            || !Employee.getLocation().equals(Group.getLocation())
                            || !Employee.getJob().equals(Group.getJob())) {            // same job as the company
                        builder.setMessage(getText(R.string.employee_punch_out_different_job_message).toString() + " Employee #" + String.valueOf(Employee.getEmployeeID())).setTitle(R.string.employee_punch_title);
                    }
                } else {
                    builder.setMessage(getText(R.string.employee_punch_out_message).toString() + " Employee #" + String.valueOf(Employee.getEmployeeID())).setTitle(R.string.employee_punch_title);
                }
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        employeePunchOut(view);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            }
       } else {
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_menu_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog dialog = builder.create();
        General.TouchTimeDialog(dialog, view);
    }

    public void employeePunchIn(View view) {
        DateFormat df = new SimpleDateFormat(getText(R.string.date_YMD_format).toString());
        DateFormat tf = new SimpleDateFormat(getText(R.string.date_time_format).toString());
        String currentDateString = df.format(new Date());
        String currentDateTimeString = tf.format(new Date());
        dbGroup.updateEmployeeListStatus(employeeID, 1);
        Activity = new DailyActivityList();
        Activity.setEmployeeID(Employee.getEmployeeID());
        Activity.setLastName(Employee.getLastName());
        Activity.setFirstName(Employee.getFirstName());
        if (Employee.getGroup() > 0) Activity.setWorkGroup(String.valueOf(dbGroup.getWorkGroupList(Employee.getGroup()).getGroupID()));
        if (!Employee.getCompany().isEmpty()) Activity.setCompany(Employee.getCompany());
        if (!Employee.getLocation().isEmpty()) Activity.setLocation(Employee.getLocation());
        if (!Employee.getJob().isEmpty()) Activity.setJob(Employee.getJob());
        Activity.setDate(currentDateString);        // store time in date for indexing purpose
        Activity.setTimeIn(currentDateTimeString);
        dbActivity.createActivityList(Activity);
        Status_View.setText(getText(R.string.in).toString());
    }

    public void employeePunchOut(View view) {
        DateFormat tf = new SimpleDateFormat(getText(R.string.date_time_format).toString());
        String currentDateTimeString = tf.format(new Date());
        dbGroup.updateEmployeeListStatus(employeeID, 0);
        Activity = dbActivity.getPunchedInActivityList(employeeID);
        if (Activity != null && Activity.getEmployeeID() > 0) {
            long diff = General.MinuteDifference(dtFormat, Activity.getTimeIn(), currentDateTimeString);
            diff = diff > 0 && diff > Activity.Lunch ? diff-Activity.Lunch : 0;
            Activity.setHours(diff);
            Activity.setTimeOut(currentDateTimeString);
            dbActivity.updatePunchedInActivityList(Activity);
        }
        Status_View.setText(getText(R.string.out).toString());
    }

    public void onSelectJobButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (dbGroup.checkEmployeeID(employeeID)) {          // a valid ID is entered
            Employee = dbGroup.getEmployeeList(employeeID);
            if (dbGroup.getEmployeeListStatus(employeeID) == 1) {
                builder.setMessage(getText(R.string.employee_already_punched_in_message).toString()).setTitle(R.string.employee_menu_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                General.TouchTimeDialog(dialog, view);
            } else {
                Intent intent = new Intent(this, CompanyJobLocationSelectionActivity.class);
                ArrayList<String> CompanyLocationJob = new ArrayList<>();
                CompanyLocationJob.add(getText(R.string.title_activity_employee_menu).toString());        // caller
                // use the last selected one as default
                CompanyLocationJob.add(Employee.getCompany());              // company
                CompanyLocationJob.add(Employee.getLocation());             // location
                CompanyLocationJob.add(Employee.getJob());                  // job
                intent.putStringArrayListExtra("CompanyLocationJob", CompanyLocationJob);
                startActivityForResult(intent, PICK_JOB_REQUEST);
            }
        } else {
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        }
    }

    public void onMoveJobButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (dbGroup.checkEmployeeID(employeeID)) {          // a valid ID is entered
            Employee = dbGroup.getEmployeeList(employeeID);
            if (dbGroup.getEmployeeListStatus(employeeID) == 1) {
                builder.setMessage(getText(R.string.employee_move_job).toString()).setTitle(R.string.employee_menu_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(context, CompanyJobLocationSelectionActivity.class);
                        ArrayList<String> CompanyLocationJob = new ArrayList<>();
                        CompanyLocationJob.add(getText(R.string.title_activity_employee_menu).toString());        // caller
                        // use the last selected one as default
                        CompanyLocationJob.add(Employee.getCompany());              // company
                        CompanyLocationJob.add(Employee.getLocation());             // location
                        CompanyLocationJob.add(Employee.getJob());                  // job
                        intent.putStringArrayListExtra("CompanyLocationJob", CompanyLocationJob);
                        startActivityForResult(intent, MOVE_JOB_REQUEST);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else {
                builder.setMessage(getText(R.string.employee_must_punch_in_message).toString()).setTitle(R.string.employee_menu_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        employeePunchIn(findViewById(android.R.id.content));
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            }
        } else {
            builder.setMessage(R.string.no_employee_message).setTitle(R.string.employee_punch_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog dialog = builder.create();
        General.TouchTimeDialog(dialog, view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        // Make sure the request was successful
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_JOB_REQUEST || requestCode == MOVE_JOB_REQUEST) {
                ArrayList<String> CompanyLocationJob = new ArrayList<String>();
                CompanyLocationJob = data.getStringArrayListExtra("CompanyLocationJob");
                // update the selected employee
                if (!Employee.getCompany().equals(CompanyLocationJob.get(1)) ||
                        !Employee.getLocation().equals(CompanyLocationJob.get(2)) ||
                        !Employee.getJob().equals(CompanyLocationJob.get(3))) {
                    Employee.setCompany(CompanyLocationJob.get(1));
                    Employee.setLocation(CompanyLocationJob.get(2));
                    Employee.setJob(CompanyLocationJob.get(3));
                    Company_View.setText(Employee.getCompany().isEmpty() ? "" : Employee.getCompany());
                    Location_View.setText(Employee.getLocation().isEmpty() ? "" : Employee.getLocation());
                    Job_View.setText(Employee.getJob().isEmpty() ? "" : Employee.getJob());
                    dbGroup.updateEmployeeListCompanyLocationJob(employeeID, CompanyLocationJob.get(1), CompanyLocationJob.get(2), CompanyLocationJob.get(3));
                    if (requestCode == MOVE_JOB_REQUEST) {
                        employeePunchOut(findViewById(android.R.id.content));
                        employeePunchIn(findViewById(android.R.id.content));
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                    builder.setMessage(getText(R.string.employee_same_job).toString()).setTitle(R.string.employee_menu_title);
                    builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    General.TouchTimeDialog(dialog, findViewById(android.R.id.content));
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employee_menu, menu);
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
            dbActivity.closeDB();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}