package com.svw.touchtime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class TouchTimeGeneralAdapter extends ArrayAdapter<HashMap<String, String>> {
        View row;
        ArrayList<HashMap<String, String>> MyFeedList;
        String[] MyListItems;
        int[] MyListIDs;
        int resLayout;
        Context context;
        int SelectedItem;
        int Height;

        public TouchTimeGeneralAdapter(Context context, ArrayList<HashMap<String, String>> feedList, int textViewResourceId, String[] list_Items, int[] list_IDs, int Height) {
            super(context, textViewResourceId, feedList);
            this.context = context;
            resLayout = textViewResourceId;
            this.MyFeedList = feedList;
            this.MyListItems = list_Items;
            this.MyListIDs = list_IDs;
            this.Height = Height;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            row = convertView;
            if(row == null)
            {   // inflate our custom layout. resLayout == R.layout.row_employee_layout.xml
                LayoutInflater ll = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = ll.inflate(resLayout, parent, false);
            }

            HashMap<String, String> item = MyFeedList.get(position); // Produce a row for each employee.

            if(item != null)
            {   // Find our widgets and populate them with the Team data.
                for (int i = 0; i < MyListItems.length; i++) {
                    TextView myItemView = (TextView) row.findViewById(MyListIDs[i]);
                    if(myItemView != null) {
                        myItemView.setMinimumHeight(Height);
                        myItemView.setText(String.valueOf(item.get(MyListItems[i])));
                    }
                }
                highlightItem(position, row);
            }
            return row;
        }

        private void highlightItem(int position, View row) {
            if(position == SelectedItem) {
                // you can define your own color of selected item here
                row.setBackgroundColor(row.getResources().getColor(R.color.svw_view_selector));
            } else {
                // you can define your own default selector here
                row.setBackgroundColor(row.getResources().getColor(R.color.svw_view_background));
            }
        }

        public void setSelectedItem(int selectedItem) {
            this.SelectedItem = selectedItem;
        }
        public void setRowHeight(int height) {
        this.Height = height;
    }
}


