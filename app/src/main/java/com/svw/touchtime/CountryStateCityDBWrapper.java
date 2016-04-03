package com.svw.touchtime;

/**
 * Created by djlee on 4/2/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CountryStateCityDBWrapper extends SQLiteOpenHelper {
    private SQLiteDatabase database;

    // Logcat tag
    private static final String LOG = CountryStateCityDBWrapper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "CountryStateCityDB";

    // Table Names
    private static final String TABLE_COUNTRY_STATE_CITY = "country_state_city_table";

    // CountryStateCity Table - column names
    private static final String KEY_COUNTRY = "country_column";
    private static final String KEY_STATE = "state_column";
    private static final String KEY_CITY = "city_column";

    // Table Create Statements
    // Activity table create statement
    private static final String CREATE_TABLE_COUNTRY_STATE_CITY = "CREATE TABLE "
            + TABLE_COUNTRY_STATE_CITY + "(" + KEY_COUNTRY + " TEXT," + KEY_STATE + " TEXT," + KEY_CITY + " TEXT" + ")";

    public CountryStateCityDBWrapper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
 //       open();         // go to getWritableDatabase that will create and/or open a database
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_COUNTRY_STATE_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRY_STATE_CITY);
        // create new tables
        onCreate(db);
    }

    /*
    // ------------------------ "country state city" table methods ----------------//

    // Creating a Activity List
    public void createActivityList(DailyActivityList Activity) {
        database.insert(TABLE_COUNTRY_STATE_CITY, null, storeActivity(Activity));
    }

    // get activity
    public DailyActivityList getActivityListID(int EmployeeID) {
        String selectQuery = "SELECT  * FROM " + TABLE_COUNTRY_STATE_CITY+ " WHERE " + KEY_EMPLOYEE_ID + " = " + EmployeeID;
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        if (c.moveToFirst())
            return retrieveActivity(EmployeeID, c);        // retrieve based on ID
        else
            return null;
    }

    // get activity
    public DailyActivityList getPunchedInActivityList(int EmployeeID) {
        String Empty = "" + "";
        Cursor c = database.rawQuery("SELECT  * FROM " + TABLE_COUNTRY_STATE_CITY+ " WHERE " + KEY_EMPLOYEE_ID + " = ?"
                        + " AND " + KEY_TIME_OUT + " = ?",
                new String[] { String.valueOf(EmployeeID), Empty});
        if (c.moveToFirst())
            return retrieveActivity(EmployeeID, c);     // retrieve based on ID
        else
            return null;
    }

    public boolean checkEmployeeID(int EmployeeID) {
        String selectQuery = "SELECT  * FROM " + TABLE_COUNTRY_STATE_CITY + " WHERE "
                + KEY_EMPLOYEE_ID + " = " + EmployeeID;
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        if (c.getCount() <= 0) return false;
        return true;
    }

    // getting all Activity lists
    public ArrayList<DailyActivityList> getAllActivityLists() {
        ArrayList<DailyActivityList> activity_lists = new ArrayList<DailyActivityList>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITY;
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                activity_lists.add(retrieveActivity(0, c));
            } while (c.moveToNext());
        }
        return activity_lists;
    }

    // getting Activity lists according to the date
    public ArrayList<DailyActivityList> getActivityLists(String[] Column, String[] Compare, String[] Values) {
        ArrayList<DailyActivityList> activity_lists = new ArrayList<DailyActivityList>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITY+ " WHERE " + Column[0] + Compare[0] + "'" + Values[0] + "'";
        int i;
        String Empty = "" + "";
        for (i=1; i < Column.length && Column[i] != null; i++) {
            selectQuery = selectQuery + " AND " + Column[i] + Compare[i] + "'" + Values[i] + "'";
        }
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                activity_lists.add(retrieveActivity(0, c));
            } while (c.moveToNext());
        }
        return activity_lists;
    }

    public int updateActivityList(DailyActivityList Activity, String[] Column, String[] Values) {
        String selectQuery = Column[0] + " = " + "'" + Values[0] + "'";
        int i;
        String Empty = "" + "";
        for (i=1; i < Column.length && Column[i] != null; i++) {
            selectQuery = selectQuery + " AND " + Column[i] + " = " + "'" + Values[i] + "'";
        }
        Log.e(LOG, selectQuery);
        return database.update(TABLE_ACTIVITY, storeActivity(Activity), selectQuery, null);
    }

    public int updatePunchedInActivityList(DailyActivityList Activity) {
        String Empty = "" + "";
        String selection = KEY_EMPLOYEE_ID + " = ?" + " AND " + KEY_TIME_OUT + " = ?";
        return database.update(TABLE_ACTIVITY, storeActivity(Activity), selection,
                new String[] { String.valueOf(Activity.EmployeeID), Empty});
    }

    // getting Activity list count
    public int getActivityListCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ACTIVITY;
        Cursor cursor = database.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    // Deleting an Activity list
    public void deleteIDActivityList(int EmployeeID) {
        database.delete(TABLE_ACTIVITY, KEY_EMPLOYEE_ID + " = " + "'" + EmployeeID + "'", null);
    }

    public void deletePunchedInActivityList(int EmployeeID, String TimeIn) {
        database.delete(TABLE_ACTIVITY, KEY_EMPLOYEE_ID + " = " + "'" + EmployeeID + "'"
                + " AND " + KEY_TIME_IN + " = " + "'" + TimeIn + "'", null);
    }
    // ------------------------ "basic database methods ----------------//

    public void open() throws SQLException {
        database = getWritableDatabase();
    }

    public DailyActivityList retrieveActivity(int EmployeeID, Cursor c) {
        DailyActivityList Activity = new DailyActivityList();
        if (EmployeeID > 0) {
            Activity.setEmployeeID(EmployeeID);
        } else {
            Activity.setEmployeeID(c.getInt(c.getColumnIndex(KEY_EMPLOYEE_ID)));
        }
        Activity.setLastName(c.getString(c.getColumnIndex(KEY_LAST_NAME)));
        Activity.setFirstName(c.getString(c.getColumnIndex(KEY_FIRST_NAME)));
        Activity.setWorkGroup(c.getString(c.getColumnIndex(KEY_WORK_GROUP)));
        Activity.setCompany(c.getString(c.getColumnIndex(KEY_COMPANY)));
        Activity.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
        Activity.setJob(c.getString(c.getColumnIndex(KEY_JOB)));
        Activity.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
        Activity.setTimeIn(c.getString(c.getColumnIndex(KEY_TIME_IN)));
        Activity.setTimeOut(c.getString(c.getColumnIndex(KEY_TIME_OUT)));
        Activity.setLunch(c.getLong(c.getColumnIndex(KEY_LUNCH)));
        Activity.setHours(c.getLong(c.getColumnIndex(KEY_HOURS)));
        Activity.setSupervisor(c.getString(c.getColumnIndex(KEY_SUPERVISOR)));
        Activity.setComments(c.getString(c.getColumnIndex(KEY_COMMENTS)));
        return Activity;
    }

    public ContentValues storeActivity(DailyActivityList Activity) {
        ContentValues values = new ContentValues();
        values.put(KEY_EMPLOYEE_ID, Activity.EmployeeID);
        values.put(KEY_LAST_NAME, Activity.LastName);
        values.put(KEY_FIRST_NAME, Activity.FirstName);
        values.put(KEY_WORK_GROUP, Activity.WorkGroup);
        values.put(KEY_COMPANY, Activity.Company);
        values.put(KEY_LOCATION, Activity.Location);
        values.put(KEY_JOB, Activity.Job);
        values.put(KEY_DATE, Activity.Date);
        values.put(KEY_TIME_IN, Activity.TimeIn);
        values.put(KEY_TIME_OUT, Activity.TimeOut);
        values.put(KEY_LUNCH, Activity.Lunch);
        values.put(KEY_HOURS, Activity.Hours);
        values.put(KEY_SUPERVISOR, Activity.Supervisor);
        values.put(KEY_COMMENTS, Activity.Comments);
        return values;
    }

    public String getIDColumnKey() { return KEY_EMPLOYEE_ID;}
    public String getLastNameColumnKey() { return KEY_LAST_NAME;}
    public String getFirstNameColumnKey() { return KEY_FIRST_NAME;}
    public String getWorkGroupColumnKey() { return KEY_WORK_GROUP;}
    public String getCompanyColumnKey() { return KEY_COMPANY;}
    public String getLocationColumnKey() { return KEY_LOCATION;}
    public String getJobColumnKey() { return KEY_JOB;}
    public String getDateColumnKey() { return KEY_DATE;}
    public String getTimeInColumnKey() { return KEY_TIME_IN;}
    public String getTimeOutColumnKey() { return KEY_TIME_OUT;}

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
*/

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

