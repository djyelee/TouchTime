package com.svw.touchtime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DailyActivityMenuActivity extends ActionBarActivity {
    private ListView daily_activity_list_view;
    private EditText LunchMinuteEdit, SupervisorEdit, CommentsEdit;
    private SimpleAdapter adapter_activity;
    ArrayList<HashMap<String, String>> feedActivityList;
    HashMap<String, String> map;
    DailyActivityList Activity;
    String[] employee_item = new String[20];
    int[] employee_id = new int[20];
    boolean sort_id_ascend = false;
    boolean sort_last_name_ascend = false;
    boolean sort_group_ascend = false;
    boolean sort_company_ascend = false;
    boolean sort_date_ascend = false;
    Context context;
    TouchTimeGeneralFunctions General = new TouchTimeGeneralFunctions();
    private DailyActivityDBWrapper dbActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int Caller = getIntent().getIntExtra("Caller", -1);
        if (Caller == R.id.caller_supervisor)
            setTitle(getText(R.string.title_back).toString().concat(" " + getText(R.string.title_activity_supervisor_menu).toString()));
        else
            setTitle(getText(R.string.title_back).toString().concat(" " + getText(R.string.title_activity_administrator_menu).toString()));
        setContentView(R.layout.activity_daily_activity_menu);

        daily_activity_list_view = (ListView) findViewById(R.id.daily_activity_list_view);
        LunchMinuteEdit = (EditText) findViewById(R.id.activity_lunch_text);
        SupervisorEdit = (EditText) findViewById(R.id.activity_supervisor_text);
        CommentsEdit = (EditText) findViewById(R.id.activity_comment_text);
        feedActivityList = new ArrayList<HashMap<String, String>>();
        ArrayList<DailyActivityList> all_activity_lists;
        DateFormat yf = new SimpleDateFormat("yyyy");
        int year = Integer.parseInt(yf.format(Calendar.getInstance().getTime()));
        dbActivity = new DailyActivityDBWrapper(this, year);      // open database of the year and create if not exist
        DateFormat datef = new SimpleDateFormat("yyyy-MM-dd");

        // retrieve activity record only on the current date
        String [] Column = new String[5];
        String [] Compare = new String[5];
        String [] Values = new String[5];
        Column[0] = dbActivity.getDateColumnKey();
        Compare[0] = "=";
        Values[0] = datef.format(Calendar.getInstance().getTime());
        all_activity_lists = dbActivity.getActivityLists(Column, Compare, Values);

        employee_item[0] = getText(R.string.employee_selection_item_id).toString();
        employee_item[1] = getText(R.string.employee_selection_item_last_name).toString();
        employee_item[2] = getText(R.string.employee_selection_item_first_name).toString();
        employee_item[3] = getText(R.string.employee_selection_item_group).toString();
        employee_item[4] = getText(R.string.employee_selection_item_company).toString();
        employee_item[5] = getText(R.string.employee_selection_item_location).toString();
        employee_item[6] = getText(R.string.employee_selection_item_job).toString();
        employee_item[7] = getText(R.string.employee_selection_item_date).toString();
        employee_item[8] = getText(R.string.employee_selection_item_timein).toString();
        employee_item[9] = getText(R.string.employee_selection_item_timeout).toString();
        employee_item[10] = getText(R.string.employee_selection_item_lunch).toString();
        employee_item[11] = getText(R.string.employee_selection_item_hours).toString();
        employee_item[12] = getText(R.string.employee_selection_item_supervisor).toString();
        employee_item[13] = getText(R.string.employee_selection_item_comments).toString();
        employee_id[0] = R.id.textViewID;
        employee_id[1] = R.id.textViewLastName;
        employee_id[2] = R.id.textViewFirstName;
        employee_id[3] = R.id.textViewGroup;
        employee_id[4] = R.id.textViewCompany;
        employee_id[5] = R.id.textViewLocation;
        employee_id[6] = R.id.textViewJob;
        employee_id[7] = R.id.textViewDate;
        employee_id[8] = R.id.textViewTimeIn;
        employee_id[9] = R.id.textViewTimeOut;
        employee_id[10] = R.id.textViewLunch;
        employee_id[11] = R.id.textViewHours;
        employee_id[12] = R.id.textViewSupervisor;
        employee_id[13] = R.id.textViewComments;

        int i = 0;
        if (all_activity_lists.size() > 0) {
            do {
                map = new HashMap<String, String>();
                map.put(getText(R.string.employee_selection_item_id).toString(), String.valueOf(all_activity_lists.get(i).getEmployeeID()));
                map.put(getText(R.string.employee_selection_item_last_name).toString(), all_activity_lists.get(i).getLastName());
                map.put(getText(R.string.employee_selection_item_first_name).toString(), all_activity_lists.get(i).getFirstName());
                map.put(getText(R.string.employee_selection_item_group).toString(), all_activity_lists.get(i).getWorkGroup());
                map.put(getText(R.string.employee_selection_item_company).toString(), all_activity_lists.get(i).getCompany());
                map.put(getText(R.string.employee_selection_item_location).toString(), all_activity_lists.get(i).getLocation());
                map.put(getText(R.string.employee_selection_item_job).toString(), all_activity_lists.get(i).getJob());
                map.put(getText(R.string.employee_selection_item_date).toString(), all_activity_lists.get(i).getDate());
                map.put(getText(R.string.employee_selection_item_timein).toString(), all_activity_lists.get(i).getTimeIn());
                map.put(getText(R.string.employee_selection_item_timeout).toString(), all_activity_lists.get(i).getTimeOut());
                // convert from number of minutes to hh:mm
                map.put(getText(R.string.employee_selection_item_lunch).toString(), String.format("%2s:%2s", String.valueOf(all_activity_lists.get(i).getLunch()/60%24),
                       String.valueOf(all_activity_lists.get(i).getLunch()%60)).replace(' ', '0'));
                // convert from number of minutes to hh:mm
                map.put(getText(R.string.employee_selection_item_hours).toString(), String.format("%2s:%2s", String.valueOf(all_activity_lists.get(i).getHours()/60%24),
                       String.valueOf(all_activity_lists.get(i).getHours()%60)).replace(' ', '0'));
                map.put(getText(R.string.employee_selection_item_supervisor).toString(), all_activity_lists.get(i).getSupervisor());
                map.put(getText(R.string.employee_selection_item_comments).toString(), all_activity_lists.get(i).getComments());
                feedActivityList.add(map);
            } while (++i < all_activity_lists.size());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
            builder.setMessage(getText(R.string.no_daily_activity_message) + " for " + Values[0] + " !").setTitle(R.string.empty_entry_title);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        daily_activity_list_view.setItemsCanFocus(true);
//      daily_activity_list_view.addHeaderView(getLayoutInflater().inflate(R.layout.daily_activity_header, null, false), null, false);
        adapter_activity = new SimpleAdapter(this, feedActivityList, R.layout.daily_activity_view, employee_item, employee_id);
        daily_activity_list_view.setAdapter(adapter_activity);

        context = this;
        daily_activity_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, final View view, int position, long arg3) {
                final int item = position;
                view.animate().setDuration(1000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(R.string.delete_daily_activity_message).setTitle(R.string.confirm_delete_title);
                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        int ID = 0;
                                        String G = feedActivityList.get(item).get(getText(R.string.employee_selection_item_id).toString());
                                        if (G != null && !G.isEmpty()) ID = Integer.parseInt(G);
                                        if (ID > 0) {
                                            String TI = feedActivityList.get(item).get(getText(R.string.employee_selection_item_timein).toString());
                                            String DT = feedActivityList.get(item).get(getText(R.string.employee_selection_item_date).toString());
                                            dbActivity.deletePunchedInActivityList(ID, DT, TI);
                                            feedActivityList.remove(item);
                                            adapter_activity.notifyDataSetChanged();
                                        }
                                    }
                                });
                                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                view.setAlpha(1);
                            }
                        });
               return false;
            }
        });
        daily_activity_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final int pos = position;
                view.animate().setDuration(30).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                int ID = 0;
                                String Lunch = LunchMinuteEdit.getText().toString();
                                String Supervisor = SupervisorEdit.getText().toString();
                                String Comments = CommentsEdit.getText().toString();
                                String G = feedActivityList.get(pos).get(getText(R.string.employee_selection_item_id).toString());
                                boolean updated = false;
                                if (G != null && !G.isEmpty()) ID = Integer.parseInt(G);
                                if (ID > 0) {
                                    ArrayList<DailyActivityList> ActivityList;
                                    Activity = new DailyActivityList();
                                    String [] Column = new String[5];
                                    String [] Compare = new String[5];
                                    String [] Values = new String[5];
                                    Column[0] = dbActivity.getIDColumnKey();
                                    Column[1] = dbActivity.getDateColumnKey();
                                    Column[2] = dbActivity.getTimeInColumnKey();
                                    Values[0] = String.valueOf(ID);
                                    Values[1] = feedActivityList.get(pos).get(getText(R.string.employee_selection_item_date).toString());
                                    Values[2] = feedActivityList.get(pos).get(getText(R.string.employee_selection_item_timein).toString());
                                    Compare[0] = Compare[1] = Compare[2] = "=";
                                    ActivityList = dbActivity.getActivityLists(Column, Compare, Values);
                                    Activity = ActivityList.get(0);   // should only return one, take the first one
                                    if (!Lunch.isEmpty() && Long.parseLong(Lunch) >= 0) {
                                        if (!Activity.TimeIn.isEmpty()) {
                                            long diff = General.MinuteDifference(Activity.getTimeIn(), Activity.getTimeOut());
                                            diff = (diff > 0 && diff >= Long.parseLong(Lunch)) ? diff-Long.parseLong(Lunch) : 0;
                                            Activity.setHours(diff);
                                            feedActivityList.get(pos).put(getText(R.string.employee_selection_item_hours).toString(),
                                                    String.format("%2s:%2s", String.valueOf(Activity.Hours/60%24),
                                                            String.valueOf(Activity.Hours%60)).replace(' ', '0'));
                                        }
                                        Activity.setLunch(Long.parseLong(Lunch));
                                        feedActivityList.get(pos).put(getText(R.string.employee_selection_item_lunch).toString(),
                                            String.format("%2s:%2s", String.valueOf(Activity.Lunch/60%24),
                                                    String.valueOf(Activity.Lunch%60)).replace(' ', '0'));
                                        // LunchMinuteEdit.setText("");
                                        updated = true;
                                    }
                                    if (!Supervisor.isEmpty()) {
                                        feedActivityList.get(pos).put(getText(R.string.employee_selection_item_supervisor).toString(), Supervisor);
                                        // SupervisorEdit.setText("");
                                        Activity.setSupervisor(Supervisor);
                                        updated = true;
                                    }
                                    if (!Comments.isEmpty()) {
                                        feedActivityList.get(pos).put(getText(R.string.employee_selection_item_comments).toString(), Comments);
                                        // CommentsEdit.setText("");
                                        Activity.setComments(Comments);
                                        updated = true;
                                    }
                                    if (updated) {
                                        dbActivity.updateActivityList(Activity, Column, Values);
                                        adapter_activity.notifyDataSetChanged();
                                    }
                                }
                                view.setAlpha(1);
                            }
                        });
            }
        });
    }

    public void onSortIDButtonClicked(View view) {
        String [] Items = new String [10];
        Items [0] = getText(R.string.employee_selection_item_id).toString();
        Items [1] = getText(R.string.employee_selection_item_last_name).toString();
        Items [2] = getText(R.string.employee_selection_item_first_name).toString();
        Items [3] = getText(R.string.employee_selection_item_group).toString();
        Items [4] = getText(R.string.employee_selection_item_company).toString();
        Items [5] = getText(R.string.employee_selection_item_date).toString();
        Items [6] = getText(R.string.employee_selection_item_timein).toString();
        General.SortIntegerStringList(feedActivityList, Items, sort_id_ascend);
        sort_last_name_ascend = sort_group_ascend = sort_company_ascend = sort_date_ascend = false;
        sort_id_ascend = !sort_id_ascend;
        daily_activity_list_view.setAdapter(adapter_activity);
    }

    public void onSortLastNameButtonClicked(View view) {
        String [] Items = new String [10];
        Items [0] = getText(R.string.employee_selection_item_last_name).toString();
        Items [1] = getText(R.string.employee_selection_item_first_name).toString();
        Items [2] = getText(R.string.employee_selection_item_group).toString();
        Items [3] = getText(R.string.employee_selection_item_company).toString();
        Items [4] = getText(R.string.employee_selection_item_date).toString();
        Items [5] = getText(R.string.employee_selection_item_timein).toString();
        General.SortStringList(feedActivityList, Items, sort_last_name_ascend);
        sort_id_ascend = sort_group_ascend = sort_company_ascend = sort_date_ascend = false;
        sort_last_name_ascend = !sort_last_name_ascend;
        daily_activity_list_view.setAdapter(adapter_activity);
    }

    public void onSortGroupButtonClicked(View view) {
        String [] Items = new String [10];
        Items [0] = getText(R.string.employee_selection_item_group).toString();
        Items [1] = getText(R.string.employee_selection_item_last_name).toString();
        Items [2] = getText(R.string.employee_selection_item_first_name).toString();
        Items [3] = getText(R.string.employee_selection_item_company).toString();
        Items [4] = getText(R.string.employee_selection_item_date).toString();
        Items [5] = getText(R.string.employee_selection_item_timein).toString();
        General.SortStringList(feedActivityList, Items, sort_group_ascend);
        sort_id_ascend = sort_last_name_ascend = sort_company_ascend = sort_date_ascend = false;
        sort_group_ascend = !sort_group_ascend;
        daily_activity_list_view.setAdapter(adapter_activity);
    }

    public void onSortCompanyButtonClicked(View view) {
        String [] Items = new String [10];
        Items [0] = getText(R.string.employee_selection_item_company).toString();
        Items [1] = getText(R.string.employee_selection_item_group).toString();
        Items [2] = getText(R.string.employee_selection_item_last_name).toString();
        Items [3] = getText(R.string.employee_selection_item_first_name).toString();
        Items [4] = getText(R.string.employee_selection_item_date).toString();
        Items [5] = getText(R.string.employee_selection_item_timein).toString();
        General.SortStringList(feedActivityList, Items, sort_company_ascend);
        sort_id_ascend = sort_last_name_ascend = sort_group_ascend = sort_date_ascend = false;
        sort_company_ascend = !sort_company_ascend;
        daily_activity_list_view.setAdapter(adapter_activity);
    }

    public void onSortDateButtonClicked(View view) {
        String [] Items = new String [10];
        Items [0] = getText(R.string.employee_selection_item_date).toString();
        Items [1] = getText(R.string.employee_selection_item_timein).toString();
        Items [2] = getText(R.string.employee_selection_item_last_name).toString();
        Items [3] = getText(R.string.employee_selection_item_first_name).toString();
        Items [4] = getText(R.string.employee_selection_item_group).toString();
        Items [5] = getText(R.string.employee_selection_item_company).toString();
        General.SortStringList(feedActivityList, Items, sort_date_ascend);
        sort_id_ascend = sort_last_name_ascend = sort_group_ascend = sort_company_ascend = false;
        sort_date_ascend = !sort_date_ascend;
        daily_activity_list_view.setAdapter(adapter_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_activity_menu, menu);
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
            dbActivity.closeDB();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
