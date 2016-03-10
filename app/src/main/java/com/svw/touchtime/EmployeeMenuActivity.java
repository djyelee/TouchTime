package com.svw.touchtime;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import static com.svw.touchtime.R.layout.general_edit_text_view;


public class EmployeeMenuActivity extends ActionBarActivity {
    public TextView Current_time;
    private EditText employeeID;
    private TextView Local;
    private ArrayAdapter<String> LocationAdapter;
    private ArrayAdapter<String> JobAdapter;
    String[] Location_List = {"Royal Medjool", "Coneio Sur", "Coneio Norte", "Narranias",
            "Norte Grande", "Jaime", "Robinson", "Perez"};
    String[] Job_List = {"Dethorning", "Pollination", "Tie Fruit Arms Sown", "Cut Centers",
            "Thinning", "Tie Up Fruit Arms", "Bags and Rings", "Tie Bottom of Bag"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_menu);
        addListenerOnButton();

/*      List<String> list = new ArrayList<String>();
        list.add("list 1");
        list.add("list 2");
        list.add("list 3");
        */

        Spinner LocationSpinner = (Spinner) findViewById(R.id.location_spinner);
        LocationAdapter = new ArrayAdapter<String>(this, general_edit_text_view, Location_List);
        LocationSpinner.setAdapter(LocationAdapter);

        Spinner JobSpinner = (Spinner) findViewById(R.id.job_spinner);
        JobAdapter = new ArrayAdapter<String>(this, general_edit_text_view, Job_List);
        JobSpinner.setAdapter(JobAdapter);

        Current_time = (TextView) findViewById(R.id.current_time);
        CountDownTimer uy = new CountDownTimer(2000000000, 1000) {
            public void onFinish()
            {
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
        Local = (TextView) findViewById(R.id.LocalTextView);
        employeeID = (EditText) findViewById(R.id.EmployeeID);
        employeeID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String message = employeeID.getText().toString();
                    // Enable and disable will make the soft keyboard disappear
                    employeeID.setEnabled(false);
                    employeeID.setEnabled(true);
                    checkEmployeeID(message);
                    handled = true;
                }
                return handled;
            }
        });
    }

    public void checkEmployeeID(String message) {
        // check ID match
        boolean ID_Match = true;
        if (ID_Match)
            message = getText(R.string.id_success) + " " + message + " ?";
        else
            message = getText(R.string.id_fail) + " ";
        Local.setText(message);
    }

    public AdapterView.OnItemSelectedListener OnCatSpinnerCL = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
            ((TextView) parent.getChildAt(0)).setTextSize(50);

        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

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
        }

        return super.onOptionsItemSelected(item);
    }
}