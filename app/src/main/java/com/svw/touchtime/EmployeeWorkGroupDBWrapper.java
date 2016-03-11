package com.svw.touchtime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class EmployeeWorkGroupDBWrapper extends SQLiteOpenHelper {
    private SQLiteDatabase database;

    // Logcat tag
    private static final String LOG = EmployeeWorkGroupDBWrapper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 7;

    // Database Name
    private static final String DATABASE_NAME = "EmployeeWorkGroupDB";

    // Table Names
    private static final String TABLE_EMPLOYEE = "employee_table";
    private static final String TABLE_WORK_GROUP = "work_group_table";

    // Employee Table - column names
    private static final String KEY_EMPLOYEE_ID = "employee_id_column";
    private static final String KEY_LAST_NAME = "last_name_column";
    private static final String KEY_FIRST_NAME = "first_name_column";
    private static final String KEY_STREET = "street_column";
    private static final String KEY_CITY = "city_column";
    private static final String KEY_STATE = "state_column";
    private static final String KEY_ZIP_CODE = "zip_code_column";
    private static final String KEY_COUNTRY = "country_column";
    private static final String KEY_PHONE = "phone_column";
    private static final String KEY_EMAIL= "email_column";
    private static final String KEY_PIECE_RATE = "piece_rate_column";
    private static final String KEY_HOURLY_RATE = "hourly_rate_column";
    private static final String KEY_EMPLOYEES = "employees_column";
    private static final String KEY_DOB = "dob_column";
    private static final String KEY_SS_NUMBER = "ss_number_column";
    private static final String KEY_DOH = "doh_column";
    private static final String KEY_ACTIVE = "active_column";
    private static final String KEY_DOC_EXP = "doc_exp_column";
    private static final String KEY_CURRENT = "current_column";
    private static final String KEY_COMMENTS = "comments_column";
    private static final String KEY_PHOTO = "photo_column";

    // Work Group Table - column names
    private static final String KEY_WORK_GROUP_ID = "work_group_id_column";
    private static final String KEY_GROUP_NAME = "group_name_column";
    private static final String KEY_SUPERVISOR = "supervisor_column";
    private static final String KEY_SHIFT_NAME = "shift_name_column";
    private static final String KEY_GROUP = "group_column";
    private static final String KEY_COMPANY = "company_column";
    private static final String KEY_LOCATION = "location_column";
    private static final String KEY_JOB = "job_column";
    private static final String KEY_STATUS = "status_column";

    // Table Create Statements
    // Employee table create statement
    private static final String CREATE_TABLE_EMPLOYEE = "CREATE TABLE "
            + TABLE_EMPLOYEE + "("
            + KEY_EMPLOYEE_ID + " integer primary key,"
            + KEY_LAST_NAME + " TEXT not null,"
            + KEY_FIRST_NAME + " TEXT not null,"
            + KEY_STREET + " TEXT DEFAULT Street, "
            + KEY_CITY + " TEXT DEFAULT City, "
            + KEY_STATE + " TEXT DEFAULT State, "
            + KEY_ZIP_CODE + " TEXT DEFAULT Zip_Code, "
            + KEY_COUNTRY + " TEXT DEFAULT USA, "
            + KEY_PHONE + " TEXT DEFAULT Phone, "
            + KEY_EMAIL + " TEXT DEFAULT Email, "
            + KEY_HOURLY_RATE + " REAL DEFAULT 1.0, "
            + KEY_PIECE_RATE + " REAL DEFAULT 1.0, "
            + KEY_SS_NUMBER + " TEXT DEFAULT SS_Number, "
            + KEY_DOB + " TEXT DEFAULT Date_of_Birth, "
            + KEY_DOH + " TEXT DEFAULT Date_of_Hire, "
            + KEY_ACTIVE + " INTEGER DEFAULT 0, "
            + KEY_DOC_EXP + " TEXT DEFAULT Expiration, "
            + KEY_CURRENT + " INTEGER DEFAULT 0, "
            + KEY_COMMENTS + " TEXT DEFAULT Comments, "
            + KEY_GROUP + " INTEGER DEFAULT 0, "
            + KEY_COMPANY + " TEXT DEFAULT Company, "
            + KEY_LOCATION + " TEXT DEFAULT Location, "
            + KEY_JOB + " TEXT DEFAULT Job, "
            + KEY_STATUS + " TEXT DEFAULT Status, "
            + KEY_PHOTO + " blob"
            + ")";

    // Work Group table create statement
    private static final String CREATE_TABLE_WORK_GROUP = "CREATE TABLE "
            + TABLE_WORK_GROUP + "("
            + KEY_WORK_GROUP_ID + " integer primary key,"
            + KEY_GROUP_NAME + " TEXT not null,"
            + KEY_SUPERVISOR + " TEXT not null,"
            + KEY_SHIFT_NAME + " TEXT DEFAULT Shift, "
            + KEY_COMPANY + " TEXT DEFAULT Company, "
            + KEY_LOCATION + " TEXT DEFAULT Location, "
            + KEY_JOB + " TEXT DEFAULT Job, "
            + KEY_STATUS + " TEXT DEFAULT Status, "
            + KEY_EMPLOYEES + " TEXT"
            + ")";

    public EmployeeWorkGroupDBWrapper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        open();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_EMPLOYEE);
        db.execSQL(CREATE_TABLE_WORK_GROUP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORK_GROUP);
        // create new tables
        onCreate(db);
    }

    // ------------------------ "employee" table methods ----------------//

    // Creating a Employee List
    public void createEmployeeList(EmployeeProfileList Employee) {
        ContentValues values = new ContentValues();
        values.put(KEY_EMPLOYEE_ID, Employee.EmployeeID);
        values.put(KEY_LAST_NAME, Employee.LastName);
        values.put(KEY_FIRST_NAME, Employee.FirstName);
        values.put(KEY_STREET, Employee.Street);
        values.put(KEY_CITY, Employee.City);
        values.put(KEY_STATE, Employee.State);
        values.put(KEY_ZIP_CODE, Employee.ZipCode);
        values.put(KEY_COUNTRY, Employee.Country);
        values.put(KEY_PHONE, Employee.Phone);
        values.put(KEY_EMAIL, Employee.Email);
        values.put(KEY_HOURLY_RATE, Employee.HourlyRate);
        values.put(KEY_PIECE_RATE, Employee.PieceRate);
        values.put(KEY_SS_NUMBER, Employee.SSNumber);
        values.put(KEY_DOB, Employee.DoB);
        values.put(KEY_DOH, Employee.DoH);
        values.put(KEY_ACTIVE, Employee.Active);
        values.put(KEY_DOC_EXP, Employee.DocExp);
        values.put(KEY_CURRENT, Employee.Current);
        values.put(KEY_COMMENTS, Employee.Comments);
        values.put(KEY_GROUP, Employee.Group);
        values.put(KEY_COMPANY, Employee.Company);
        values.put(KEY_LOCATION, Employee.Location);
        values.put(KEY_JOB, Employee.Job);
        values.put(KEY_STATUS, Employee.Status);
        values.put(KEY_PHOTO, (Employee.Photo != null) ? Employee.getBytes(Employee.getPhoto()) : null);
        database.insert(TABLE_EMPLOYEE, null, values);
    }

    public EmployeeProfileList getEmployeeList(int EmployeeID) {
        String selectQuery = "SELECT  * FROM " + TABLE_EMPLOYEE+ " WHERE "
                + KEY_EMPLOYEE_ID + " = " + "'" + EmployeeID + "'";
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        EmployeeProfileList Employee = new EmployeeProfileList();
        if (c.moveToFirst()) {
            Employee.setEmployeeID(EmployeeID);
            Employee.setLastName(c.getString(c.getColumnIndex(KEY_LAST_NAME)));
            Employee.setFirstName(c.getString(c.getColumnIndex(KEY_FIRST_NAME)));
            Employee.setStreet(c.getString(c.getColumnIndex(KEY_STREET)));
            Employee.setCity(c.getString(c.getColumnIndex(KEY_CITY)));
            Employee.setState(c.getString(c.getColumnIndex(KEY_STATE)));
            Employee.setZipCode(c.getString(c.getColumnIndex(KEY_ZIP_CODE)));
            Employee.setCountry(c.getString(c.getColumnIndex(KEY_COUNTRY)));
            Employee.setPhone(c.getString(c.getColumnIndex(KEY_PHONE)));
            Employee.setEmail(c.getString(c.getColumnIndex(KEY_EMAIL)));
            Employee.setHourlyRate(c.getDouble(c.getColumnIndex(KEY_HOURLY_RATE)));
            Employee.setPieceRate(c.getDouble(c.getColumnIndex(KEY_PIECE_RATE)));
            Employee.setSSNumber(c.getString(c.getColumnIndex(KEY_SS_NUMBER)));
            Employee.setDoB(c.getString(c.getColumnIndex(KEY_DOB)));
            Employee.setDoH(c.getString(c.getColumnIndex(KEY_DOH)));
            Employee.setActive(c.getInt(c.getColumnIndex(KEY_ACTIVE)));
            Employee.setDocExp(c.getString(c.getColumnIndex(KEY_DOC_EXP)));
            Employee.setCurrent(c.getInt(c.getColumnIndex(KEY_CURRENT)));
            Employee.setComments(c.getString(c.getColumnIndex(KEY_COMMENTS)));
            Employee.setGroup(c.getInt(c.getColumnIndex(KEY_GROUP)));
            Employee.setCompany(c.getString(c.getColumnIndex(KEY_COMPANY)));
            Employee.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
            Employee.setJob(c.getString(c.getColumnIndex(KEY_JOB)));
            Employee.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
            Employee.setPhoto(Employee.getBitmap(c.getBlob(c.getColumnIndex(KEY_PHOTO))));
        }
        return Employee;
    }

    public boolean checkEmployeeID(int EmployeeID) {
        String selectQuery = "SELECT  * FROM " + TABLE_EMPLOYEE + " WHERE "
                + KEY_EMPLOYEE_ID + " = " + "'" + EmployeeID + "'";
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        if (c.getCount() <= 0) return false;
        return true;
    }

    public int getAvailableEmployeeID() {
        String selectQuery = "Select * FROM " + TABLE_EMPLOYEE;
        ArrayList<String> EmployeeID_list = new ArrayList<String>();
        int i = 0;
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                EmployeeID_list.add(c.getString(c.getColumnIndex(KEY_EMPLOYEE_ID)));
            } while (c.moveToNext());
            Collections.sort(EmployeeID_list, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    int First = Integer.parseInt(o1);
                    int Second = Integer.parseInt(o2);
                    return (First > Second ? 1 : -1);
                }
            });
            for(i=0; i<EmployeeID_list.size(); i++) {    // checking unused ID
                if (Integer.parseInt(EmployeeID_list.get(i)) > i+1) return i+1;
            }
        }
        if (i > 0) {
            return Integer.parseInt(EmployeeID_list.get(i - 1)) + 1;    // the last one on the list plus 1
        } else {
            return 1;
        }
    }

    // getting all Employee lists
    public ArrayList<EmployeeProfileList> getAllEmployeeLists() {
        ArrayList<EmployeeProfileList> employeelists = new ArrayList<EmployeeProfileList>();
        String selectQuery = "SELECT  * FROM " + TABLE_EMPLOYEE;
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                EmployeeProfileList Employee = new EmployeeProfileList();
                Employee.setEmployeeID(c.getInt(c.getColumnIndex(KEY_EMPLOYEE_ID)));
                Employee.setLastName(c.getString(c.getColumnIndex(KEY_LAST_NAME)));
                Employee.setFirstName(c.getString(c.getColumnIndex(KEY_FIRST_NAME)));
                Employee.setStreet(c.getString(c.getColumnIndex(KEY_STREET)));
                Employee.setCity(c.getString(c.getColumnIndex(KEY_CITY)));
                Employee.setState(c.getString(c.getColumnIndex(KEY_STATE)));
                Employee.setZipCode(c.getString(c.getColumnIndex(KEY_ZIP_CODE)));
                Employee.setCountry(c.getString(c.getColumnIndex(KEY_COUNTRY)));
                Employee.setPhone(c.getString(c.getColumnIndex(KEY_PHONE)));
                Employee.setEmail(c.getString(c.getColumnIndex(KEY_EMAIL)));
                Employee.setHourlyRate(c.getDouble(c.getColumnIndex(KEY_HOURLY_RATE)));
                Employee.setPieceRate(c.getDouble(c.getColumnIndex(KEY_PIECE_RATE)));
                Employee.setSSNumber(c.getString(c.getColumnIndex(KEY_SS_NUMBER)));
                Employee.setDoB(c.getString(c.getColumnIndex(KEY_DOB)));
                Employee.setDoH(c.getString(c.getColumnIndex(KEY_DOH)));
                Employee.setActive(c.getInt(c.getColumnIndex(KEY_ACTIVE)));
                Employee.setDocExp(c.getString(c.getColumnIndex(KEY_DOC_EXP)));
                Employee.setCurrent(c.getInt(c.getColumnIndex(KEY_CURRENT)));
                Employee.setComments(c.getString(c.getColumnIndex(KEY_COMMENTS)));
                Employee.setGroup(c.getInt(c.getColumnIndex(KEY_GROUP)));
                Employee.setCompany(c.getString(c.getColumnIndex(KEY_COMPANY)));
                Employee.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
                Employee.setJob(c.getString(c.getColumnIndex(KEY_JOB)));
                Employee.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                Employee.setPhoto(Employee.getBitmap(c.getBlob(c.getColumnIndex(KEY_PHOTO))));
                employeelists.add(Employee);
            } while (c.moveToNext());
        }
        return employeelists;
    }

    public int updateAllEmployeeLists(ArrayList<EmployeeProfileList> employeelist) {
        ContentValues values = new ContentValues();
        int i = 0;
        String selectQuery = "SELECT  * FROM " + TABLE_EMPLOYEE;
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                EmployeeProfileList Employee = new EmployeeProfileList();
                Employee = employeelist.get(i++);
                updateEmployeeList(Employee);
            } while (c.moveToNext());
        }
        return 0;
    }

    public int updateEmployeeList(EmployeeProfileList Employee) {
        ContentValues values = new ContentValues();
        values.put(KEY_EMPLOYEE_ID, Employee.EmployeeID);
        values.put(KEY_LAST_NAME, Employee.LastName);
        values.put(KEY_FIRST_NAME, Employee.FirstName);
        values.put(KEY_STREET, Employee.Street);
        values.put(KEY_CITY, Employee.City);
        values.put(KEY_STATE, Employee.State);
        values.put(KEY_ZIP_CODE, Employee.ZipCode);
        values.put(KEY_COUNTRY, Employee.Country);
        values.put(KEY_PHONE, Employee.Phone);
        values.put(KEY_EMAIL, Employee.Email);
        values.put(KEY_HOURLY_RATE, Employee.HourlyRate);
        values.put(KEY_PIECE_RATE, Employee.PieceRate);
        values.put(KEY_SS_NUMBER, Employee.SSNumber);
        values.put(KEY_DOB, Employee.DoB);
        values.put(KEY_DOH, Employee.DoH);
        values.put(KEY_ACTIVE, Employee.Active);
        values.put(KEY_DOC_EXP, Employee.DocExp);
        values.put(KEY_CURRENT, Employee.Current);
        values.put(KEY_COMMENTS, Employee.Comments);
        values.put(KEY_GROUP, Employee.Group);
        values.put(KEY_COMPANY, Employee.Company);
        values.put(KEY_LOCATION, Employee.Location);
        values.put(KEY_JOB, Employee.Job);
        values.put(KEY_STATUS, Employee.Status);
        values.put(KEY_PHOTO, (Employee.Photo != null) ? Employee.getBytes(Employee.Photo) : null);
        // updating row based on employee id so cannot be changed
        return database.update(TABLE_EMPLOYEE, values, KEY_EMPLOYEE_ID + " = " + "'" + Employee.EmployeeID + "'", null);
    }

    // getting Employee list count
    public int getEmployeeListCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EMPLOYEE;
        Cursor cursor = database.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    // Deleting a Employee list
    public void deleteEmployeeList(int ID) {
        database.delete(TABLE_EMPLOYEE, KEY_EMPLOYEE_ID + " = " + "'" + ID + "'", null);
    }

    // ------------------------ "work group" table methods ----------------//

    // Creating a Work Group List
    public void createWorkGroupList(WorkGroupList WorkGroup) {
        ContentValues values = new ContentValues();
        values.put(KEY_WORK_GROUP_ID, WorkGroup.GroupID);
        values.put(KEY_GROUP_NAME, WorkGroup.GroupName);
        values.put(KEY_SUPERVISOR, WorkGroup.Supervisor);
        values.put(KEY_SHIFT_NAME, WorkGroup.ShiftName);
        values.put(KEY_COMPANY, WorkGroup.Company);
        values.put(KEY_LOCATION, WorkGroup.Location);
        values.put(KEY_JOB, WorkGroup.Job);
        values.put(KEY_STATUS, WorkGroup.Status);
        values.put(KEY_EMPLOYEES, WorkGroup.Employees);
        database.insert(TABLE_WORK_GROUP, null, values);
    }

    public WorkGroupList getWorkGroupList(int GroupID) {
        String selectQuery = "SELECT  * FROM " + TABLE_WORK_GROUP+ " WHERE "
                + KEY_WORK_GROUP_ID + " = " + "'" + GroupID + "'";
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        WorkGroupList WorkGroup = new WorkGroupList();
        if (c.moveToFirst()) {
            WorkGroup.setGroupID(GroupID);
            WorkGroup.setGroupName(c.getString(c.getColumnIndex(KEY_GROUP_NAME)));
            WorkGroup.setSupervisor(c.getString(c.getColumnIndex(KEY_SUPERVISOR)));
            WorkGroup.setShiftName(c.getString(c.getColumnIndex(KEY_SHIFT_NAME)));
            WorkGroup.setCompany(c.getString(c.getColumnIndex(KEY_COMPANY)));
            WorkGroup.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
            WorkGroup.setJob(c.getString(c.getColumnIndex(KEY_JOB)));
            WorkGroup.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
            WorkGroup.setEmployees(c.getString(c.getColumnIndex(KEY_EMPLOYEES)));
        }
        return WorkGroup;
    }

    public boolean checkWorkGroupID(int GroupID) {
        String selectQuery = "SELECT  * FROM " + TABLE_WORK_GROUP + " WHERE "
                + KEY_WORK_GROUP_ID + " = " + "'" + GroupID + "'";
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        if (c.getCount() <= 0) return false;
        return true;
    }

    public int getAvailableWorkGroupID() {
        String selectQuery = "Select * FROM " + TABLE_WORK_GROUP;
        ArrayList<String> WorkGroupID_list = new ArrayList<String>();
        int i = 0;
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                WorkGroupID_list.add(c.getString(c.getColumnIndex(KEY_WORK_GROUP_ID)));
            } while (c.moveToNext());
            Collections.sort(WorkGroupID_list, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
            for(i=0; i<WorkGroupID_list.size(); i++) {    // checking unused ID
                if (Integer.parseInt(WorkGroupID_list.get(i)) > i+1) return i+1;
            }
        }
        if (i > 0) {
            return Integer.parseInt(WorkGroupID_list.get(i - 1)) + 1;    // the last one on the list plus 1
        } else {
            return 1;
        }
    }

    // getting all WorkGroup lists
    public ArrayList<WorkGroupList> getAllWorkGroupLists() {
        ArrayList<WorkGroupList> workgrouplists = new ArrayList<WorkGroupList>();
        String selectQuery = "SELECT  * FROM " + TABLE_WORK_GROUP;
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                WorkGroupList WorkGroup = new WorkGroupList();
                WorkGroup.setGroupID(c.getInt(c.getColumnIndex(KEY_WORK_GROUP_ID)));
                WorkGroup.setGroupName(c.getString(c.getColumnIndex(KEY_GROUP_NAME)));
                WorkGroup.setSupervisor(c.getString(c.getColumnIndex(KEY_SUPERVISOR)));
                WorkGroup.setShiftName(c.getString(c.getColumnIndex(KEY_SHIFT_NAME)));
                WorkGroup.setCompany(c.getString(c.getColumnIndex(KEY_COMPANY)));
                WorkGroup.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
                WorkGroup.setJob(c.getString(c.getColumnIndex(KEY_JOB)));
                WorkGroup.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                WorkGroup.setEmployees(c.getString(c.getColumnIndex(KEY_EMPLOYEES)));
                workgrouplists.add(WorkGroup);
            } while (c.moveToNext());
        }

        return workgrouplists;
    }

    public int updateAllWorkGroupLists(ArrayList<WorkGroupList> workgrouplist) {
        ContentValues values = new ContentValues();
        int i = 0;
        String selectQuery = "SELECT  * FROM " + TABLE_WORK_GROUP;
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                WorkGroupList WorkGroup = new WorkGroupList();
                WorkGroup = workgrouplist.get(i++);
                updateWorkGroupList(WorkGroup);
            } while (c.moveToNext());
        }
        return 0;
    }

    public int updateWorkGroupList(WorkGroupList WorkGroup) {
        ContentValues values = new ContentValues();
        values.put(KEY_WORK_GROUP_ID, WorkGroup.GroupID);
        values.put(KEY_GROUP_NAME, WorkGroup.GroupName);
        values.put(KEY_SUPERVISOR, WorkGroup.Supervisor);
        values.put(KEY_SHIFT_NAME, WorkGroup.ShiftName);
        values.put(KEY_COMPANY, WorkGroup.Company);
        values.put(KEY_LOCATION, WorkGroup.Location);
        values.put(KEY_JOB, WorkGroup.Job);
        values.put(KEY_STATUS, WorkGroup.Status);
        values.put(KEY_EMPLOYEES, WorkGroup.Employees);
        // updating row based on work group id so cannot be changed
        return database.update(TABLE_WORK_GROUP, values, KEY_WORK_GROUP_ID + " = " + "'" + WorkGroup.GroupID + "'", null);
    }

    // getting WorkGroup list count
    public int getWorkGroupCount() {
        String countQuery = "SELECT  * FROM " + TABLE_WORK_GROUP;
        Cursor cursor = database.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    // Deleting a WorkGroup list
    public void deleteWorkGroupList(int GroupID) {
        database.delete(TABLE_WORK_GROUP, KEY_WORK_GROUP_ID + " = " + "'" + GroupID + "'", null);
    }

    // ------------------------ "basic database methods ----------------//

    public void open() throws SQLException {
        database = getWritableDatabase();
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
