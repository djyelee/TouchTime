package com.svw.touchtime;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeSelectionAdapter extends ArrayAdapter<HashMap<String, String>> {
    View row;
    ArrayList<HashMap<String, String>> MyFeedList;
    String[] MyListItems;
    int[] MyListIDs;
    int resLayout;
    Context context;

    public EmployeeSelectionAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String, String>> feedList, String[] list_Items, int[] list_IDs) {
        super(context, textViewResourceId, feedList);
        this.context = context;
        resLayout = textViewResourceId;
        this.MyFeedList = feedList;
        this.MyListItems = list_Items;
        this.MyListIDs = list_IDs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        row = convertView;
        if(row == null)
        {   // inflate our custom layout. resLayout == R.layout.row_employee_layout.xml
            LayoutInflater ll = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = ll.inflate(resLayout, parent, false);
        }

        HashMap<String, String> item = MyFeedList.get(position); // Produce a row for each employee.

        if(item != null)
        {   // Find our widgets and populate them with the Team data.
            TextView myEmployeeID = (TextView) row.findViewById(MyListIDs[0]);
            TextView myLastName = (TextView) row.findViewById(MyListIDs[1]);
            TextView myFirstName = (TextView) row.findViewById(MyListIDs[2]);
            TextView myGroup = (TextView) row.findViewById(MyListIDs[3]);
            TextView myActive = (TextView) row.findViewById(MyListIDs[4]);
            TextView myCurrent= (TextView) row.findViewById(MyListIDs[5]);

            if(myEmployeeID != null) myEmployeeID.setText(String.valueOf(item.get(MyListItems[0])));
            if(myLastName != null) myLastName.setText(item.get(MyListItems[1]));
            if(myFirstName != null) myFirstName.setText(item.get(MyListItems[2]));
            if(myGroup != null) myGroup.setText(item.get(MyListItems[3]));
            if(myActive != null) myActive.setText(item.get(MyListItems[4]));
            if(myCurrent != null) myCurrent.setText(item.get(MyListItems[5]));
        }
        return row;
    }
}