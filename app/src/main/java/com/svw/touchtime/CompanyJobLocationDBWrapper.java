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
import java.util.Date;
import java.util.Locale;

public class CompanyJobLocationDBWrapper extends SQLiteOpenHelper {
    private SQLiteDatabase database;

    // Logcat tag
    private static final String LOG = CompanyJobLocationDBWrapper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 13;

    // Database Name
    private static final String DATABASE_NAME = "CompanyJobLocationDB";

    // Table Names
    private static final String TABLE_COMPANY = "company_table";

    // Table - column names
    private static final String KEY_NAME = "name_column";
    private static final String KEY_STREET = "street_column";
    private static final String KEY_CITY = "city_column";
    private static final String KEY_STATE = "state_column";
    private static final String KEY_ZIP_CODE = "zip_code_column";
    private static final String KEY_COUNTRY = "country_column";
    private static final String KEY_PHONE = "phone_column";
    private static final String KEY_CONTACT = "contact_column";
    private static final String KEY_EMAIL= "email_column";
    private static final String KEY_JOB = "job_column";
    private static final String KEY_LOCATION = "location_column";

    // Table Create Statements
    // Company table create statement
    private static final String CREATE_TABLE_COMPANY = "CREATE TABLE "
            + TABLE_COMPANY + "(" + KEY_NAME + " TEXT," + KEY_STREET + " TEXT," + KEY_CITY + " TEXT,"
            + KEY_STATE + " TEXT," + KEY_ZIP_CODE + " TEXT," + KEY_COUNTRY + " TEXT," + KEY_PHONE + " TEXT,"
            + KEY_CONTACT + " TEXT," + KEY_EMAIL + " TEXT," + KEY_JOB + " TEXT," + KEY_LOCATION + " TEXT" + ")";

    public CompanyJobLocationDBWrapper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        open();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_COMPANY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANY);
        // create new tables
        onCreate(db);
    }

    // ------------------------ "company_table" table methods ----------------//

    // Creating a Company List
    public void createCompanyList(CompanyJobLocationList Company) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, Company.Name);
        values.put(KEY_STREET, Company.Street);
        values.put(KEY_CITY, Company.City);
        values.put(KEY_STATE, Company.State);
        values.put(KEY_ZIP_CODE, Company.ZipCode);
        values.put(KEY_COUNTRY, Company.Country);
        values.put(KEY_PHONE, Company.Phone);
        values.put(KEY_CONTACT, Company.Contact);
        values.put(KEY_EMAIL, Company.Email);
        values.put(KEY_JOB, Company.Job);
        values.put(KEY_LOCATION, Company.Location);
        database.insert(TABLE_COMPANY, null, values);
    }

    public CompanyJobLocationList getCompanyList(String Name) {
        String selectQuery = "SELECT  * FROM " + TABLE_COMPANY+ " WHERE "
                + KEY_NAME + " = " + "'" + Name + "'";
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        CompanyJobLocationList Company = new CompanyJobLocationList();
        if (c.moveToFirst()) {
            Company.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            Company.setStreet(c.getString(c.getColumnIndex(KEY_STREET)));
            Company.setCity(c.getString(c.getColumnIndex(KEY_CITY)));
            Company.setState(c.getString(c.getColumnIndex(KEY_STATE)));
            Company.setZipCode(c.getString(c.getColumnIndex(KEY_ZIP_CODE)));
            Company.setCountry(c.getString(c.getColumnIndex(KEY_COUNTRY)));
            Company.setPhone(c.getString(c.getColumnIndex(KEY_PHONE)));
            Company.setContact(c.getString(c.getColumnIndex(KEY_CONTACT)));
            Company.setEmail(c.getString(c.getColumnIndex(KEY_EMAIL)));
            Company.setJob(c.getString(c.getColumnIndex(KEY_JOB)));
            Company.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
        }
        return Company;
    }

    public int getCompanyListPosition (String Name) {
        String selectQuery = "SELECT  * FROM " + TABLE_COMPANY;
        int i = 0;
        boolean found = false;
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex(KEY_NAME)).equals(Name)) {
                    found = true;
                    break;
                } i++;
            } while (c.moveToNext());
        }
        return (found) ? i : -1;
    }

    // getting all company lists
    public ArrayList<CompanyJobLocationList> getAllCompanyLists() {
        ArrayList<CompanyJobLocationList> Companylists = new ArrayList<CompanyJobLocationList>();
        String selectQuery = "SELECT  * FROM " + TABLE_COMPANY;
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
       // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                CompanyJobLocationList Company = new CompanyJobLocationList();
                Company.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                Company.setStreet(c.getString(c.getColumnIndex(KEY_STREET)));
                Company.setCity(c.getString(c.getColumnIndex(KEY_CITY)));
                Company.setState(c.getString(c.getColumnIndex(KEY_STATE)));
                Company.setZipCode(c.getString(c.getColumnIndex(KEY_ZIP_CODE)));
                Company.setCountry(c.getString(c.getColumnIndex(KEY_COUNTRY)));
                Company.setPhone(c.getString(c.getColumnIndex(KEY_PHONE)));
                Company.setContact(c.getString(c.getColumnIndex(KEY_CONTACT)));
                Company.setEmail(c.getString(c.getColumnIndex(KEY_EMAIL)));
                Company.setJob(c.getString(c.getColumnIndex(KEY_JOB)));
                Company.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
                Companylists.add(Company);
            } while (c.moveToNext());
        }

        return Companylists;
    }

    public int updateAllCompanyLists(ArrayList<CompanyJobLocationList> companylist) {
        ContentValues values = new ContentValues();
        int i = 0;
        String selectQuery = "SELECT  * FROM " + TABLE_COMPANY;
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                CompanyJobLocationList Company = new CompanyJobLocationList();
                Company = companylist.get(i++);
                updateCompanyList(Company);         // Name cannot change
            } while (c.moveToNext());
        }
        return 0;
    }

    public int updateCompanyList(CompanyJobLocationList Company) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, Company.Name);
        values.put(KEY_STREET, Company.Street);
        values.put(KEY_CITY, Company.City);
        values.put(KEY_STATE, Company.State);
        values.put(KEY_ZIP_CODE, Company.ZipCode);
        values.put(KEY_COUNTRY, Company.Country);
        values.put(KEY_PHONE, Company.Phone);
        values.put(KEY_CONTACT, Company.Contact);
        values.put(KEY_EMAIL, Company.Email);
        values.put(KEY_JOB, Company.Job);
        values.put(KEY_LOCATION, Company.Location);
        // updating row based on company name so name cannot be changed
        return database.update(TABLE_COMPANY, values, KEY_NAME + " = " + "'" + Company.Name + "'", null);
    }

    // getting company list count
    public int getCompanyListCount() {
        String countQuery = "SELECT  * FROM " + TABLE_COMPANY;
        Cursor cursor = database.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    // Deleting a company list
    public void deleteCompanyList(String Name) {
        database.delete(TABLE_COMPANY, KEY_NAME + " = " + "'" + Name + "'", null);
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
