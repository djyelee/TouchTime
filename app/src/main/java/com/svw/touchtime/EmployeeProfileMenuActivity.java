package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class EmployeeProfileMenuActivity extends ActionBarActivity {
    ListView employee_list_view;
    Button sort_id;
    Button sort_last_name;
    boolean sort_id_ascend = false;
    boolean sort_last_name_ascend = false;
    ArrayList<HashMap<String, String>> feedEmployeeList;
    TouchTimeGeneralAdapter adapter_employee;
    HashMap<String, String> map;
    ArrayList<EmployeeProfileList> all_employee_lists;
    private int itemEmployee = -1;
    EmployeeProfileList Employee;
    static final int PICK_NEW_REQUEST = 123;             // The request code
    static final int PICK_UPDATE_REQUEST = 456;          // The request code
    static final int PICK_COPY_REQUEST = 789;          // The request code
    private int Caller;
    Context context;
    String FileName = "Employee Profile";
    File NewProfileFile;

    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private EmployeeGroupCompanyDBWrapper dbGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile_menu);
        Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else {
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));
            Button delete_employee = (Button) findViewById(R.id.delete_employee);
            delete_employee.setClickable(false);
            delete_employee.setBackgroundColor(getResources().getColor(R.color.svw_dark_gray));
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);

        employee_list_view = (ListView) findViewById(R.id.employee_profile_list_view);
        sort_id = (Button) findViewById(R.id.sort_id);
        sort_last_name = (Button) findViewById(R.id.sort_last_name);
        feedEmployeeList = new ArrayList<HashMap<String, String>>();
        context = this;
        String[] list_items = {getText(R.string.column_key_employee_id).toString(), getText(R.string.column_key_last_name).toString(), getText(R.string.column_key_first_name).toString(),
                getText(R.string.column_key_active).toString(), getText(R.string.column_key_current).toString()};
        int[] list_id = {R.id.textDisplayID, R.id.textDisplayLastName, R.id.textDisplayFirstName, R.id.textDisplayActive, R.id.textDisplayCurrent};
        dbGroup = new EmployeeGroupCompanyDBWrapper(this);
        Employee = new EmployeeProfileList();
        all_employee_lists = dbGroup.getAllEmployeeLists();

        readDisplayEmployee();
        deleteCSVFiles();

         // display selected employees
        // adapter_employee = new SimpleAdapter(this, feedEmployeeList, R.layout.employee_profile_view, list_items, list_id);
        adapter_employee = new TouchTimeGeneralAdapter(this, feedEmployeeList, R.layout.employee_profile_view, list_items, list_id, 40);
        employee_list_view.setItemsCanFocus(true);
        // employee_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.employee_profile_header, null, false), null, false);
        // use adaptor to display, must be done after the header is added
        employee_list_view.setAdapter(adapter_employee);
        HighlightListItem(itemEmployee);

        employee_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                itemEmployee = position;      // offset the row for header
                view.animate().setDuration(100).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                map = feedEmployeeList.get(itemEmployee);
                                Employee = dbGroup.getEmployeeList(Integer.parseInt(map.get(getText(R.string.column_key_employee_id))));
                                HighlightListItem(itemEmployee);
                                view.setAlpha(1);
                            }
                        });
            }
        });
    }

    private void HighlightListItem(int position) {
        adapter_employee.setSelectedItem(position);
        adapter_employee.notifyDataSetChanged();
    }

    public void readDisplayEmployee() {
        feedEmployeeList.clear();
        int i = 0;
        if (all_employee_lists.size() > 0) {
            do {
                map = new HashMap<String, String>();
                map.put(getText(R.string.column_key_employee_id).toString(), String.valueOf(all_employee_lists.get(i).getEmployeeID()));
                map.put(getText(R.string.column_key_last_name).toString(), all_employee_lists.get(i).getLastName());
                map.put(getText(R.string.column_key_first_name).toString(), all_employee_lists.get(i).getFirstName());
                map.put(getText(R.string.column_key_active).toString(), all_employee_lists.get(i).getActive() == 0 ? getText(R.string.no).toString() : getText(R.string.yes).toString());
                map.put(getText(R.string.column_key_current).toString(), all_employee_lists.get(i).getCurrent() == 0 ? getText(R.string.no).toString() : getText(R.string.yes).toString());
                feedEmployeeList.add(map);
            } while (++i < all_employee_lists.size());
            itemEmployee = 0;       // default selection
            Employee = dbGroup.getEmployeeList(all_employee_lists.get(itemEmployee).getEmployeeID());
        }
    }

    public void onUpdateButtonClicked(View view) {
        if (itemEmployee >= 0) {
            int[] Data = new int[3];
            Intent intent = new Intent(this, EmployeeDetailActivity.class);
            Data[0] = Caller;
            Data[1] = PICK_UPDATE_REQUEST;
            Data[2] = Employee.getEmployeeID();
            intent.putExtra("EmployeeID", Data);
            startActivityForResult(intent, PICK_UPDATE_REQUEST);
            // itemEmployee = -1;  Remove this line because need to remember previous selection
        } else {
            // put the dialog inside so it will not dim the screen when returns.
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setMessage(R.string.employee_no_ID_message).setTitle(R.string.employee_profile_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        }
    }

    public void onAddButtonClicked(View view) {
        int[] Data = new int[3];
        Intent intent = new Intent(this, EmployeeDetailActivity.class);
        Data[0] = Caller;
        if (view.getId() == R.id.btn_new) {
            Data[1] = PICK_NEW_REQUEST;
            Data[2] = 0;                            // send the one that is currently selected to be modified
        } else if (view.getId() == R.id.btn_copy_to) {
            Data[1] = PICK_COPY_REQUEST;
            Data[2] = Employee.getEmployeeID();     // send the one that is currently selected to be copied from
        }
        intent.putExtra("EmployeeID", Data);
        startActivityForResult(intent, PICK_NEW_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure the request was successful
        if (resultCode == RESULT_OK) {
            map = new HashMap<String, String>();
            int ID = data.getIntExtra("EmployeeID", -1);
            Employee = dbGroup.getEmployeeList(ID);
            // Check which request we're responding to
            if (requestCode == PICK_NEW_REQUEST) {
                map.put(getText(R.string.column_key_employee_id).toString(), String.valueOf(ID));
                map.put(getText(R.string.column_key_last_name).toString(), Employee.getLastName());
                map.put(getText(R.string.column_key_first_name).toString(), Employee.getFirstName());
                map.put(getText(R.string.column_key_active).toString(), Employee.getActive() == 0 ? getText(R.string.no).toString() : getText(R.string.yes).toString());
                map.put(getText(R.string.column_key_current).toString(), Employee.getCurrent() == 0 ? getText(R.string.no).toString() : getText(R.string.yes).toString());
                feedEmployeeList.add(map);

                itemEmployee = General.GetIntegerIndex(feedEmployeeList, getText(R.string.column_key_employee_id).toString(), ID);
                sort_id_ascend = !sort_id_ascend;
                onSortIDButtonClicked(employee_list_view);  // toggle back internally
                employee_list_view.smoothScrollToPosition(itemEmployee); // scroll to the newly added item
            } else if (requestCode == PICK_UPDATE_REQUEST) {
                int i = 0;
                if (feedEmployeeList.size() > 0) {
                    do {
                        map = feedEmployeeList.get(i);
                        if (ID == Integer.parseInt(map.get(getText(R.string.column_key_employee_id).toString()))) {
                            map.put(getText(R.string.column_key_last_name).toString(), Employee.getLastName());
                            map.put(getText(R.string.column_key_first_name).toString(), Employee.getFirstName());
                            map.put(getText(R.string.column_key_active).toString(), Employee.getActive() == 0 ? getText(R.string.no).toString() : getText(R.string.yes).toString());
                            map.put(getText(R.string.column_key_current).toString(), Employee.getCurrent() == 0 ? getText(R.string.no).toString() : getText(R.string.yes).toString());
                            feedEmployeeList.set(i, map);
                        }
                    } while (++i < feedEmployeeList.size());
                    itemEmployee = General.GetIntegerIndex(feedEmployeeList, getText(R.string.column_key_employee_id).toString(), ID);
                    employee_list_view.smoothScrollToPosition(itemEmployee); // scroll to the newly added item
                }
            }
            all_employee_lists = dbGroup.getAllEmployeeLists();
            adapter_employee.notifyDataSetChanged();
        }
    }

    public void onDeleteButtonClicked(View view) {
        //       if (Caller != R.id.caller_administrator) return;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (itemEmployee >= 0) {
            if (Employee.getStatus() != 0) {
                builder.setMessage(R.string.employee_must_punch_out_message).setTitle(R.string.employee_profile_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else {
                builder.setMessage(R.string.employee_confirm_delete_message).setTitle(R.string.employee_profile_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (itemEmployee > 0) HighlightListItem(itemEmployee - 1);
                        dbGroup.deleteEmployeeList(Employee.getEmployeeID());
                        feedEmployeeList.remove(itemEmployee);
                        adapter_employee.notifyDataSetChanged();
                        // remove employee ID that is already assigned to a group
                        if (Employee.getGroup() != 0) {             // already assigned to a group
                            dbGroup.removeWorkGroupListEmployee(Employee.getEmployeeID(), Employee.getGroup());
                        }
                        itemEmployee = (itemEmployee == 0 && feedEmployeeList.size() > 0) ? 0 : itemEmployee - 1;
                        employee_list_view.smoothScrollToPosition(itemEmployee);        // scroll to the selected item
                        if (itemEmployee >= 0 && feedEmployeeList.size() > 0) {
                            map = feedEmployeeList.get(itemEmployee);
                            Employee = dbGroup.getEmployeeList(Integer.parseInt(map.get(getText(R.string.column_key_employee_id))));
                        }
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
            }
        } else {
            builder.setMessage(R.string.employee_no_ID_message).setTitle(R.string.employee_profile_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog dialog = builder.create();
        General.TouchTimeDialog(dialog, view);
    }

    public void onSortIDButtonClicked(View view) {
        if (feedEmployeeList.size() == 0) return;
        String Items;
        int ID = 0;
        Items = getText(R.string.column_key_employee_id).toString();
        if (itemEmployee >= 0) ID = Integer.parseInt(feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_employee_id).toString()));
        General.SortIntegerList(feedEmployeeList, Items, sort_id_ascend);
        if (itemEmployee >= 0) {
            itemEmployee = General.GetIntegerIndex(feedEmployeeList, Items, ID);
            adapter_employee.setSelectedItem(itemEmployee);
        }
        sort_last_name_ascend = false;
        sort_id_ascend = !sort_id_ascend;
        adapter_employee.notifyDataSetChanged();
        employee_list_view.smoothScrollToPosition(itemEmployee);
    }


    public void onSortLastNameButtonClicked(View view) {
        if (feedEmployeeList.size() == 0) return;
        String[] Items = new String[2];
        String[] Data = new String[2];
        Items[0] = getText(R.string.column_key_last_name).toString();
        Items[1] = getText(R.string.column_key_first_name).toString();
        if (itemEmployee >= 0) {
            Data[0] = feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_last_name).toString());
            Data[1] = feedEmployeeList.get(itemEmployee).get(getText(R.string.column_key_first_name).toString());
        }
        General.SortStringList(feedEmployeeList, Items, sort_last_name_ascend);
        if (itemEmployee >= 0) {
            itemEmployee = General.GetStringIndex(feedEmployeeList, Items, Data);
            adapter_employee.setSelectedItem(itemEmployee);
        }
        sort_id_ascend = false;
        sort_last_name_ascend = !sort_last_name_ascend;
        adapter_employee.notifyDataSetChanged();
        employee_list_view.smoothScrollToPosition(itemEmployee);
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
            builder.setMessage(R.string.employee_file_not_found).setTitle(R.string.employee_profile_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
            deleteCSVFiles();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setMessage(R.string.employee_file_will_be_lost).setTitle(R.string.employee_profile_title);
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
        String line;
        int noRecords = 0;
        ArrayList<String> ID_list = new ArrayList<>();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            EmployeeProfileList E = new EmployeeProfileList();
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;       // skip empty lines
                noRecords++;
                if (noRecords > 2) {  // the first two are header and column names
                    String[] item = line.split(",");
                    if (item.length > 0) E = dbGroup.getEmployeeList(Integer.parseInt(item[0]));                // Return ID=0 if a new ID
                    if (item.length > 0) E.setEmployeeID(Integer.parseInt(item[0])); else E.setEmployeeID(0);   // override the return id
                    if (item.length > 1) E.setLastName(item[1]); else E.setLastName("No Name");
                    if (item.length > 2) E.setFirstName(item[2]); else E.setFirstName("No Name");
                    if (item.length > 3) E.setStreet(item[3]); else E.setStreet("");
                    if (item.length > 4) E.setCity(item[4]); else E.setCity("");
                    if (item.length > 5) E.setState(item[5]); else E.setState("");
                    if (item.length > 6) E.setZipCode(item[6]); else E.setZipCode("");
                    if (item.length > 7) E.setCountry(item[7]); else E.setCountry("");
                    if (item.length > 8) E.setPhone(item[8]); else E.setPhone("");
                    if (item.length > 9) E.setEmail(item[9]); else E.setEmail("");
                    if (item.length > 10) E.setHourlyRate(Double.parseDouble(item[10])); else E.setHourlyRate(10.0);
                    if (item.length > 11) E.setPieceRate(Double.parseDouble(item[11])); else E.setPieceRate(10.0);
                    if (item.length > 12) E.setSSNumber(item[12]); else E.setSSNumber("");
                    if (item.length > 13) E.setDoB(General.convertMDYtoYMD(item[13].replace("[", "").replace("]", ""))); else E.setDoB("");
                    if (item.length > 14) E.setDoH(General.convertMDYtoYMD(item[14].replace("[", "").replace("]", ""))); else E.setDoH("");
                    if (item.length > 15) E.setDocExp(General.convertMDYtoYMD(item[15].replace("[", "").replace("]", ""))); else E.setDocExp("");
                    if (item.length > 16) E.setActive(Integer.parseInt(item[16])); else E.setActive(0);
                    if (item.length > 17) E.setCurrent(Integer.parseInt(item[17])); else E.setCurrent(0);
                    if (item.length > 18) E.setComments(item[18]); else E.setComments("");
                    if (item.length > 19) E.setGroup(Integer.parseInt(item[19])); else E.setGroup(0);
                    if (item.length > 20) E.setCompany(item[20]); else E.setCompany("");
                    if (item.length > 21) E.setLocation(item[21]); else E.setLocation("");
                    if (item.length > 22) E.setJob(item[22]); else E.setJob("");
                    E.setStatus(0);         // all import individuals must be default to punched out
                    // do not update photo, photo is null if a new ID
                    if (dbGroup.checkEmployeeID(E.getEmployeeID())) { // ID already exists
                        dbGroup.updateEmployeeList(E);
                    } else {
                        dbGroup.createEmployeeList(E);
                    }
                    ID_list.add(item[0]);   // keep a list for checking removed ID later
                }
            }
            // check employees that have been removed
            String ID;
            boolean found = false;
            for (int i=0; i<feedEmployeeList.size(); i++) {
                ID = feedEmployeeList.get(i).get(getText(R.string.column_key_employee_id).toString());
                found = false;
                for (String s : ID_list) {
                    if (ID.equals(s)) {
                        found = true;
                        break;
                    }
                }
                if (!found) dbGroup.deleteEmployeeList(Integer.parseInt(ID));
            }
            all_employee_lists = dbGroup.getAllEmployeeLists();
            readDisplayEmployee();
            HighlightListItem(itemEmployee);
            adapter_employee.notifyDataSetChanged();
            br.close();
            deleteCSVFiles();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void onExportButtonClicked(View view) {
        String to = "svwtouchtime@gmail.com";
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("plain/text");
        try {
            deleteCSVFiles();
 //          NewProfileFile = new File(context.getExternalCacheDir(), FileName + ".csv");   // use app private folder
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

        Header = getText(R.string.employee_profile_title).toString();
        Header = "\"" + Header + "\"" + "\n";
        ColumnNames = getText(R.string.column_view_employee_id).toString() + ":" + "," +
                getText(R.string.column_view_last_name).toString() + ":" + "," +
                getText(R.string.column_view_first_name).toString() + ":" + "," +
                getText(R.string.street_title_text).toString() + "," +
                getText(R.string.city_title_text).toString() + "," +
                getText(R.string.state_title_text).toString() + "," +
                getText(R.string.zip_code_title_text).toString() + "," +
                getText(R.string.country_title_text).toString() + "," +
                getText(R.string.phone_title_text).toString() + "," +
                getText(R.string.email_title_text).toString() + "," +
                getText(R.string.hourly_rate_title_text).toString() + "," +
                getText(R.string.piece_rate_title_text).toString() + "," +
                getText(R.string.ss_number_title_text).toString() + "," +
                getText(R.string.birth_date_title_text).toString() + "," +
                getText(R.string.hire_date_title_text).toString() + "," +
                getText(R.string.expiration_title_text).toString() + "," +
                getText(R.string.active_title_text).toString() + "," +
                getText(R.string.current_title_text).toString() + "," +
                getText(R.string.hint_employee_comments_text).toString() + "," +
                getText(R.string.group_id_view).toString() + ":" + "," +
                getText(R.string.column_view_company).toString() + "," +
                getText(R.string.location_title_text).toString() + "," +
                getText(R.string.job_title_text).toString() + "," +
                getText(R.string.column_view_status).toString() + "\n";
        try {
            writer = new FileWriter(sFileName);
            writer.append(Header);
            writer.append(ColumnNames);
            for (i = 0; i < all_employee_lists.size(); i++) {
                Entries = String.valueOf(all_employee_lists.get(i).getEmployeeID()) + "," +
                        all_employee_lists.get(i).getLastName() + "," +
                        all_employee_lists.get(i).getFirstName() + "," +
                        all_employee_lists.get(i).getStreet() + "," +
                        all_employee_lists.get(i).getCity() + "," +
                        all_employee_lists.get(i).getState() + "," +
                        all_employee_lists.get(i).getZipCode() + "," +
                        all_employee_lists.get(i).getCountry() + "," +
                        all_employee_lists.get(i).getPhone() + "," +
                        all_employee_lists.get(i).getEmail() + "," +
                        String.valueOf(all_employee_lists.get(i).getHourlyRate()) + "," +
                        String.valueOf(all_employee_lists.get(i).getPieceRate()) + "," +
                        all_employee_lists.get(i).getSSNumber() + "," +
                        "[" + General.convertYMDtoMDY(all_employee_lists.get(i).getDoB()) + "]" + "," +              // add [] so Excel won't change the date format
                        "[" + General.convertYMDtoMDY(all_employee_lists.get(i).getDoH()) + "]" + "," +
                        "[" + General.convertYMDtoMDY(all_employee_lists.get(i).getDocExp()) + "]" + "," +
                        String.valueOf(all_employee_lists.get(i).getActive()) + "," +
                        String.valueOf(all_employee_lists.get(i).getCurrent()) + "," +
                        all_employee_lists.get(i).getComments() + "," +
                        String.valueOf(all_employee_lists.get(i).getGroup()) + "," +
                        all_employee_lists.get(i).getCompany() + "," +
                        all_employee_lists.get(i).getLocation() + "," +
                        all_employee_lists.get(i).getJob() + "," +
                        String.valueOf(all_employee_lists.get(i).getStatus()) + "\n";
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
        getMenuInflater().inflate(R.menu.menu_employee_profile_menu, menu);
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
}