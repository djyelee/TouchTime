package com.svw.touchtime;

/**
 * Created by djlee on 4/2/16.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class CountryStateCityDBWrapper extends SQLiteOpenHelper {
    private SQLiteDatabase database;
    private Context context;
    private TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();

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
            + TABLE_COUNTRY_STATE_CITY + "(" + KEY_COUNTRY + " TEXT,"
            + KEY_STATE + " TEXT," + KEY_CITY + " TEXT" + ")";

    public CountryStateCityDBWrapper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        this.context = context;
        open();         // go to getWritableDatabase that will create and/or open a database
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

    // ------------------------ "country state city" table methods ----------------//

    public void clearAllList() {
        String selectQuery = "DELETE FROM " + TABLE_COUNTRY_STATE_CITY;
        Log.e(LOG, selectQuery);
        database.execSQL(selectQuery);
    }

    public void addMissingList(String Country, String State, String City) {
        String selectQuery = "SELECT  * FROM " + TABLE_COUNTRY_STATE_CITY+ " WHERE "
                + KEY_COUNTRY + " = " + "'" + Country + "'" + " AND "
                + KEY_STATE + " = " + "'" + State + "'" + " AND "
                + KEY_CITY + " = " + "'" + City + "'";
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        if (c.getCount() <= 0) {        // does not exist
            if (!State.isEmpty()) {
                if (!City.isEmpty()) {
                    createCityList(Country, State, City);
                } else {
                    createCityList(Country, State, City);
                }
            }
        }
    }

    public void createCountryList(String Country) {
        ContentValues values = new ContentValues();
        values.put(KEY_COUNTRY, Country);
        values.put(KEY_STATE, "");
        values.put(KEY_CITY, "");
        database.insert(TABLE_COUNTRY_STATE_CITY, null, values);
    }

    public ArrayList<String> getCountryList(ArrayList<String> Country_List) {       // pass in the list so no new one will be created
        Country_List.clear();
        String selectQuery = "SELECT  * FROM " + TABLE_COUNTRY_STATE_CITY;
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Country_List.add(c.getString(c.getColumnIndex(KEY_COUNTRY)));
            } while (c.moveToNext());
        }
        General.sortString(Country_List);
        Country_List.add(0, "");
        Country_List.add(1, context.getString(R.string.employee_add_new_message));
        Country_List = General.removeDuplicates(Country_List);
        return Country_List;
    }

    public boolean checkStateList(String Country, String State) {
        String selectQuery = "SELECT  * FROM " + TABLE_COUNTRY_STATE_CITY+ " WHERE "
                + KEY_COUNTRY + " = " + "'" + Country + "'" + " AND "
                + KEY_STATE + " = " + "'" + State + "'";
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        if (c.getCount() <= 0) return false;
        return true;
    }

    public void createStateList(String Country, String State) {
        ContentValues values = new ContentValues();
        values.put(KEY_COUNTRY, Country);
        values.put(KEY_STATE, State);
        values.put(KEY_CITY, "");
        database.insert(TABLE_COUNTRY_STATE_CITY, null, values);
    }

    public ArrayList<String> getStateList(String Country, ArrayList<String> State_List) {
        State_List.clear();
        String selectQuery = "SELECT  * FROM " + TABLE_COUNTRY_STATE_CITY + " WHERE "
                + KEY_COUNTRY + " = " + "'" + Country + "'" ;
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                State_List.add(c.getString(c.getColumnIndex(KEY_STATE)));
            } while (c.moveToNext());
        }
        General.sortString(State_List);
        State_List.add(0, "");
        State_List.add(1, context.getString(R.string.employee_add_new_message));
        State_List = General.removeDuplicates(State_List);
        return State_List;
    }

    public void deleteStateList(String Country, String State) {
        database.delete(TABLE_COUNTRY_STATE_CITY, KEY_COUNTRY + " = " + "'" + Country + "'"
                + " AND " + KEY_STATE + " = " + "'" + State + "'", null);
    }

    public boolean checkCityList(String Country, String State, String City) {
        String selectQuery = "SELECT  * FROM " + TABLE_COUNTRY_STATE_CITY+ " WHERE "
                + KEY_COUNTRY + " = " + "'" + Country + "'" + " AND "
                + KEY_STATE + " = " + "'" + State + "'" + " AND "
                + KEY_CITY + " = " + "'" + City + "'";
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        if (c.getCount() <= 0) return false;
        return true;
    }

    public void createCityList(String Country, String State, String City) {
        ContentValues values = new ContentValues();
        values.put(KEY_COUNTRY, Country);
        values.put(KEY_STATE, State);
        values.put(KEY_CITY, City);
        database.insert(TABLE_COUNTRY_STATE_CITY, null, values);
    }

    public ArrayList<String> getCityList(String Country, String State, ArrayList<String> City_List) {
        City_List.clear();
        String selectQuery = "SELECT  * FROM " + TABLE_COUNTRY_STATE_CITY + " WHERE "
                + KEY_COUNTRY + " = " + "'" + Country + "'" + " AND "
                + KEY_STATE + " = " + "'" + State + "'";
        Log.e(LOG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                City_List.add(c.getString(c.getColumnIndex(KEY_CITY)));
            } while (c.moveToNext());
        }
        General.sortString(City_List);
        City_List.add(0, "");
        City_List.add(1, context.getString(R.string.employee_add_new_message));
        City_List = General.removeDuplicates(City_List);
        return City_List;
    }

    public void deleteCityList(String Country, String State, String City) {
        database.delete(TABLE_COUNTRY_STATE_CITY, KEY_COUNTRY + " = " + "'" + Country + "'"
                + " AND " + KEY_STATE + " = " + "'" + State + "'"
                + " AND " + KEY_CITY + " = " + "'" + City + "'", null);
    }
    // ------------------------ "basic database methods ----------------//

    public String getCountryColumnKey() { return KEY_COUNTRY;}
    public String getStateColumnKey() { return KEY_STATE;}
    public String getCityColumnKey() { return KEY_CITY;}

    // open database
    public void open() throws SQLException {
        database = getWritableDatabase();
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}

