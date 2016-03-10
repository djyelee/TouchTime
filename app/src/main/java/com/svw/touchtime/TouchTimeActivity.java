package com.svw.touchtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class TouchTimeActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "com.svw.touchtime.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_time);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_touch_time, menu);
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

    public void AdministratorMenu(View view) {
        Intent intent = new Intent(this, AdministratorMenuActivity.class);
/*        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
*/
        startActivity(intent);
    }

    public void SupervisorMenu(View view) {
        Intent intent = new Intent(this, SupervisorMenuActivity.class);
        startActivity(intent);
    }

    public void EmployeeMenu(View view) {
        Intent intent = new Intent(this, EmployeeMenuActivity.class);
        startActivity(intent);
    }
}
