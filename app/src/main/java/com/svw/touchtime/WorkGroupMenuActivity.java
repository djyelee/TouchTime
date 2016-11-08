package com.svw.touchtime;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WorkGroupMenuActivity extends ActionBarActivity {
    private ListView work_group_list_view;
    private EditText GroupNameEdit, SupervisorEdit, ShiftNameEdit;
    boolean sort_id_ascend = true;
    boolean sort_last_name_ascend = true;
    private ArrayList<WorkGroupList> all_work_group_lists;
    private SimpleAdapter adapter_group;
    private SimpleAdapter adapter_employee;
    ArrayList<HashMap<String, String>> feedGroupList;
    ArrayList<HashMap<String, String>> feedEmployeeList;
    HashMap<String, String> map;
    private int itemWorkGroup = -1;
    WorkGroupList WorkGroup;
    static final int PICK_GROUP_REQUEST = 123;          // The request code
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private EmployeeGroupCompanyDBWrapper db;
    String FileName = "Workgroup";
    File NewGroupFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_group_menu);
        int Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_administrator)
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        else
            setTitle(getText(R.string.back_to).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);

        ListView employee_list_view;
        work_group_list_view = (ListView) findViewById(R.id.work_group_list_view);
        employee_list_view = (ListView) findViewById(R.id.employee_list_view);
        GroupNameEdit = (EditText) findViewById(R.id.work_group_name_text);
        SupervisorEdit = (EditText) findViewById(R.id.supervisor_name_text);
        ShiftNameEdit = (EditText) findViewById(R.id.shift_name_text);
        feedGroupList= new ArrayList<HashMap<String, String>>();
        feedEmployeeList= new ArrayList<HashMap<String, String>>();
        // database and other data
        db = new EmployeeGroupCompanyDBWrapper(this);
        WorkGroup = new WorkGroupList();
        String[] group_item = {getText(R.string.column_key_group_id).toString()};
        int[] group_id = {R.id.groupDisplayID};
        work_group_list_view.setItemsCanFocus(true);
        // work_group_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.group_display_header, null, false), null, false);
        adapter_group = new SimpleAdapter(this, feedGroupList, R.layout.group_display_view, group_item, group_id);
        work_group_list_view.setAdapter(adapter_group);
        itemWorkGroup = 0;
        readWorkGroup(true);
        displayWorkGroup();
        // display selected employees
        String[] employee_item = {getText(R.string.column_key_employee_id).toString(), getText(R.string.column_key_last_name).toString(), getText(R.string.column_key_first_name).toString()};
        int[] employee_id = {R.id.textDisplayID, R.id.textDisplayLastName, R.id.textDisplayFirstName};
        employee_list_view.setItemsCanFocus(true);
        // employee_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.employee_display_header, null, false), null, false);
        adapter_employee = new SimpleAdapter(this, feedEmployeeList, R.layout.employee_display_view, employee_item, employee_id);
        employee_list_view.setAdapter(adapter_employee);
        displayEmployee();
        deleteCSVFiles();

        work_group_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                itemWorkGroup = position;
                view.animate().setDuration(100).alpha(0).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                WorkGroup = db.getWorkGroupList(Integer.parseInt(feedGroupList.get(itemWorkGroup).get(getText(R.string.column_key_group_id).toString())));
                                displayWorkGroup();
                                displayEmployee();
                                view.setAlpha(1);
                            }
                        });
            }

        });
    }

    public void readWorkGroup(boolean RemoveDuplicates) {
        ArrayList employeeID = new ArrayList<String>();
        all_work_group_lists = db.getAllWorkGroupLists();

        DateFormat df = new SimpleDateFormat(getText(R.string.date_YMD_format).toString());
        String CurrentDate = df.format(Calendar.getInstance().getTime());
        feedGroupList.clear();
        if (all_work_group_lists.size() > 0) {
            int i;
            WorkGroupList WorkGroup = new WorkGroupList();
            if (RemoveDuplicates) db.resetAllEmployeeGroup(0);    // reset them all to group 0
            for (i=0; i < all_work_group_lists.size(); i++) {
                WorkGroup = all_work_group_lists.get(i);
                map = new HashMap<String, String>();
                map.put(getText(R.string.column_key_group_id).toString(), String.valueOf(WorkGroup.GroupID));
                feedGroupList.add(map);
                // don't have to update group for all employees because they can be updated later in Workgroup Punch
                // just need to check if multiple groups have the same employee
                if (RemoveDuplicates) {
                     String[] array = WorkGroup.getEmployees().split(",");
                     for (String s : array) {
                         String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                         if (!ss.isEmpty()) {
                             db.updateEmployeeGroup(Integer.parseInt(ss), WorkGroup.GroupID);       // set them to be the same as workgroup
                             employeeID.add(ss);
                         }
                     }
                }
            }
            General.SortIntegerList(feedGroupList, getText(R.string.column_key_group_id).toString(), true);     // sort ascend
            if (RemoveDuplicates) {
                ArrayList<String> duplicates = General.returnDuplicates(employeeID);
                if (duplicates.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
                    String Name = "Employee IDs " + duplicates + " " + getText(R.string.group_multiple_groups);
                    builder.setMessage(Name).setTitle(R.string.group_punch_title);
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

    public void displayWorkGroup() {
        if (all_work_group_lists.size() <= 0) return;
        WorkGroup = db.getWorkGroupList(Integer.parseInt(feedGroupList.get(itemWorkGroup).get(getText(R.string.column_key_group_id).toString())));
        GroupNameEdit.setText(WorkGroup.getGroupName().isEmpty() ? "" : WorkGroup.getGroupName());
        SupervisorEdit.setText(WorkGroup.getSupervisor().isEmpty() ? "" : WorkGroup.getSupervisor());
        ShiftNameEdit.setText(WorkGroup.getShiftName().isEmpty() ? "" : WorkGroup.getShiftName());
        adapter_group.notifyDataSetChanged();
        work_group_list_view.setItemChecked(itemWorkGroup, true);
    }

    public void displayEmployee() {
        if (all_work_group_lists.size() <= 0) return;
        EmployeeProfileList Employee;
        feedEmployeeList.clear();     // clear the old list
        if (!WorkGroup.getEmployees().isEmpty()) {
            String[] array = WorkGroup.getEmployees().split(",");
            for (String s : array) {
                String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                if (!ss.isEmpty()) {
                    Employee = db.getEmployeeList(Integer.parseInt(ss));
                    map = new HashMap<String, String>();
                    map.put(getText(R.string.column_key_employee_id).toString(), String.valueOf(Employee.EmployeeID));
                    map.put(getText(R.string.column_key_last_name).toString(), Employee.LastName);
                    map.put(getText(R.string.column_key_first_name).toString(), Employee.FirstName);
                    feedEmployeeList.add(map);
                }
            }
         }
        adapter_employee.notifyDataSetChanged();
    }

    public void onEmployeeButtonClicked(View view) {
         if (itemWorkGroup >= 0) {
            Intent intent = new Intent(this, EmployeeSelectionActivity.class);
            intent.putExtra("SelectedGroup", Integer.parseInt(feedGroupList.get(itemWorkGroup).get(getText(R.string.column_key_group_id).toString())));
            startActivityForResult(intent, PICK_GROUP_REQUEST);
        } else {
             // put the dialog inside so it will not dim the screen when returns.
             AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
             builder.setMessage(R.string.group_select_message).setTitle(R.string.group_title);
             builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
             AlertDialog dialog = builder.create();
             General.TouchTimeDialog(dialog, view);
         }
     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == PICK_GROUP_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                ArrayList<String> EmployeeIDList = new ArrayList<String>();
                EmployeeIDList = data.getStringArrayListExtra("SelectedEmployees");
                JSONArray JobArray = new JSONArray(EmployeeIDList);
                WorkGroup.setEmployees(JobArray.toString());  // Workgroup is alrerady updated when
                db.updateWorkGroupList(WorkGroup);
                readWorkGroup(false);
                displayWorkGroup();
                displayEmployee();
            }
        }
    }

    public void onAddUpdateButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (GroupNameEdit.getText().toString().isEmpty()) {              // must have the group name
            builder.setMessage(R.string.group_name_empty_message).setTitle(R.string.group_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
            return;
        }
        WorkGroup.setGroupName(GroupNameEdit.getText().toString());
        WorkGroup.setSupervisor((SupervisorEdit.getText().toString().isEmpty()) ? "" : SupervisorEdit.getText().toString());
        WorkGroup.setShiftName((ShiftNameEdit.getText().toString().isEmpty()) ? "" : ShiftNameEdit.getText().toString());
         if (view.getId() == R.id.add_work_group) {
            builder.setMessage(R.string.group_new_message).setTitle(R.string.group_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    WorkGroup.setGroupID(db.getAvailableWorkGroupID());
                    WorkGroup.setEmployees("");             // no employees assigned yet
                    WorkGroup.setCompany("");               // no company assigned yet
                    WorkGroup.setLocation("");              // no location assigned yet
                    WorkGroup.setJob("");                   // no job assigned yet
                    WorkGroup.setStatus(0);
                    db.createWorkGroupList(WorkGroup);
                    readWorkGroup(false);
                    displayWorkGroup();
                    displayEmployee();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        } else if (view.getId() == R.id.update_work_group) {
            if (itemWorkGroup < 0) {
                builder.setMessage(R.string.group_select_message).setTitle(R.string.group_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else if (WorkGroup.getStatus() != 0) {
                builder.setMessage(R.string.group_must_punch_out_message).setTitle(R.string.group_title);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
           } else if (db.checkWorkGroupID(Integer.parseInt(feedGroupList.get(itemWorkGroup).get(getText(R.string.column_key_group_id).toString())))) {
                builder.setMessage(R.string.group_update_message).setTitle(R.string.group_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.updateWorkGroupList(WorkGroup);
                        readWorkGroup(false);
                        displayWorkGroup();                             // name is already there
                        displayEmployee();
                        }
                    });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            }
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
        }
    }

    public void onDeleteButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
        if (itemWorkGroup >= 0) {
            if (WorkGroup.getStatus() != 0) {
                builder.setMessage(R.string.group_must_punch_out_message).setTitle(R.string.group_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            } else {
                builder.setMessage(R.string.group_confirm_delete_message).setTitle(R.string.group_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // reset employee group to 0 before the group is deleted
                        if (!WorkGroup.getEmployees().isEmpty()) {
                            String[] array = WorkGroup.Employees.split(",");
                            for (String s : array) {
                                String ss = s.replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
                                if (!ss.isEmpty()) {
                                    if (db.checkEmployeeID(Integer.parseInt(ss))) {                 // make sure employee is still available
                                        db.updateEmployeeGroup(Integer.parseInt(ss), 0); // group is removed, employee record must be updated as well to set group to 0
                                    }
                                }
                            }
                        }
                        // delete group and update list
                        db.deleteWorkGroupList(Integer.parseInt(feedGroupList.get(itemWorkGroup).get(getText(R.string.column_key_group_id).toString())));
                        feedGroupList.remove(itemWorkGroup);
                        adapter_group.notifyDataSetChanged();
                        all_work_group_lists = db.getAllWorkGroupLists();
                        if (all_work_group_lists.size() > 0) {
                            itemWorkGroup = itemWorkGroup >= 1 ? itemWorkGroup-1 : 0;
                            WorkGroup = db.getWorkGroupList(Integer.parseInt(feedGroupList.get(itemWorkGroup).get(getText(R.string.column_key_group_id).toString())));
                        } else {
                            itemWorkGroup = -1;
                            WorkGroup = new WorkGroupList();
                        }
                        readWorkGroup(false);
                        displayWorkGroup();
                        displayEmployee();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
            }
        } else {
            builder.setMessage(R.string.no_work_group_message).setTitle(R.string.group_title);
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
        String Items = getText(R.string.column_key_employee_id).toString();
        General.SortIntegerList(feedEmployeeList, Items, sort_id_ascend);
        sort_id_ascend = !sort_id_ascend;
        adapter_employee.notifyDataSetChanged();
    }

    public void onSortLastNameButtonClicked(View view) {
        if (feedEmployeeList.size() == 0) return;
        String [] Items = {getText(R.string.column_key_last_name).toString(), getText(R.string.column_key_first_name).toString()};
        General.SortStringList(feedEmployeeList, Items, sort_last_name_ascend);
        sort_last_name_ascend = !sort_last_name_ascend;
        adapter_employee.notifyDataSetChanged();
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
            builder.setMessage(R.string.group_not_found).setTitle(R.string.group_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            General.TouchTimeDialog(dialog, view);
            deleteCSVFiles();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.TouchTimeDialog));
            builder.setMessage(R.string.group_will_be_lost).setTitle(R.string.company_profile_title);
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
        String Employee = "";
        ArrayList<String> ID_list = new ArrayList<>();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                WorkGroupList G = new WorkGroupList();
                // db.resetAllEmployeeGroup();         // remove group association by resetting all employees to group 0
                if (line.isEmpty()) continue;       // skip empty lines
                noRecords ++;
                if (noRecords > 2) {  // the first two are header and column names
                    Employee = "";
                    matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        Employee = matcher.group(1);
                        Employee = Employee.replace(",", "\",\"");   // replace , with ","
                        Employee = "[\"" + Employee + "\"]";
                    }
                    String[] item = line.replace("\"", "").split(",");
                    if (item.length > 0) G = db.getWorkGroupList(Integer.parseInt(item[0]));                // Return ID=0 if a new ID
                    if (item.length > 0) G.setGroupID(Integer.parseInt(item[0])); else G.setGroupID(0);   // override the return id
                    if (item.length > 1) G.setGroupName(item[1]); else G.setGroupName("No Name");
                    if (item.length > 2) G.setSupervisor(item[2]); else G.setSupervisor("");
                    if (item.length > 3) G.setShiftName(item[3]); else G.setShiftName("");
                    if (item.length > 4) G.setCompany(item[4]); else G.setCompany("");
                    if (item.length > 5) G.setLocation(item[5]); else G.setLocation("");
                    if (item.length > 6) G.setJob(item[6]); else G.setJob("");
                    G.setStatus(0);     // all imported groups must be default to punched out
                    G.setEmployees(Employee);
                    if (db.checkWorkGroupID(G.getGroupID())) { // ID already exists
                        db.updateWorkGroupList(G);
                    } else {
                        db.createWorkGroupList(G);
                    }
                    ID_list.add(item[0]);   // keep a list for checking removed ID later
                }
            }
            // check employees that have been removed
            String ID;
            boolean found = false;
            for (int i=0; i<feedGroupList.size(); i++) {
                ID = feedGroupList.get(i).get(getText(R.string.column_key_group_id).toString());
                found = false;
                for (String s : ID_list) {
                    if (ID.equals(s)) {
                        found = true;
                        break;
                    }
                }
                if (!found) db.deleteWorkGroupList(Integer.parseInt(ID));
            }
            readWorkGroup(false);
            itemWorkGroup = 0;
            displayWorkGroup();
            displayEmployee();
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
//          NewTimeSheetFile = new File(context.getExternalCacheDir(), Subject + ".csv");   // app private folder
            NewGroupFile= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FileName + ".csv");  // download folder
            if (!NewGroupFile.exists()) {
                if (NewGroupFile.createNewFile()) {
                    generateCsvFile(NewGroupFile);
                    i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(NewGroupFile));
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                    i.putExtra(Intent.EXTRA_SUBJECT, FileName);
                    i.putExtra(Intent.EXTRA_TEXT, FileName);
                    startActivity(Intent.createChooser(i, "E-mail"));
                    db.closeDB();
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

        Header = getText(R.string.group_title).toString();
        Header = "\"" + Header + "\"" + "\n";
        ColumnNames = getText(R.string.column_view_group_id).toString() + "," +
                getText(R.string.group_name_title_text).toString() + "," +
                getText(R.string.supervisor_title_text).toString() + "," +
                getText(R.string.shift_title_text).toString() + "," +
                getText(R.string.column_view_company).toString() + "," +
                getText(R.string.location_title_text).toString() + "," +
                getText(R.string.job_title_text).toString() + "," +
                getText(R.string.column_view_status).toString() + "," +
                getText(R.string.column_view_employee_id).toString() + "\n";
        try {
            writer = new FileWriter(sFileName);
            writer.append(Header);
            writer.append(ColumnNames);
            for (i = 0; i < all_work_group_lists.size(); i++) {
                String Employee = all_work_group_lists.get(i).getEmployees().replace("\"", "").replace("[", "").replace("]", "");
                Entries = all_work_group_lists.get(i).getGroupID() + "," +
                        all_work_group_lists.get(i).getGroupName() + "," +
                        all_work_group_lists.get(i).getSupervisor() + "," +
                        all_work_group_lists.get(i).getShiftName() + "," +
                        all_work_group_lists.get(i).getCompany() + "," +
                        all_work_group_lists.get(i).getLocation() + "," +
                        all_work_group_lists.get(i).getJob() + "," +
                        String.valueOf(all_work_group_lists.get(i).getStatus()) + "," +
                        "\"" + Employee + "\"" + "\n";
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
        getMenuInflater().inflate(R.menu.menu_work_group_menu, menu);
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
            db.closeDB();
            deleteCSVFiles();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
