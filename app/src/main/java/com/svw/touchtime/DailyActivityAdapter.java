package com.svw.touchtime;

/**
 * Created by djlee on 5/13/15.
 */

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class DailyActivityAdapter extends ArrayAdapter<HashMap<String, String>> {
    View row;
    ArrayList<HashMap<String, String>> MyFeedList;
    String[] MyListItems;
    int[] MyListIDs;
    int resLayout;
    Context context;

    public DailyActivityAdapter(Context context, ArrayList<HashMap<String, String>> feedList, int textViewResourceId, String[] list_Items, int[] list_IDs) {
        super(context, textViewResourceId, feedList);
        this.context = context;
        resLayout = textViewResourceId;
        this.MyFeedList = feedList;
        this.MyListItems = list_Items;
        this.MyListIDs = list_IDs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resLayout, null);
            holder.editText1 = (EditText) convertView.findViewById(MyListIDs[13]);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> item = MyFeedList.get(position); // Produce a row for each employee.

        if (item != null) {   // Find our widgets and populate them with the Team data.
            int i;
            TextView[] text_view = new TextView[MyFeedList.size()];
            for (i = 0; MyListIDs[i + 1] > 0 && MyListItems[i + 1] != null; i++) {
                text_view[i] = (TextView) convertView.findViewById(MyListIDs[i]);
                if (text_view[i] != null && item.get(MyListItems[i]) != null)
                    text_view[i].setText(String.valueOf(item.get(MyListItems[i])));
            }

            holder.ref = position;
            holder.editText1.setText(MyFeedList.get(holder.ref).get(MyListItems[13]));
            holder.editText1.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub

                }
                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }
                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                       MyFeedList.get(holder.ref).put(MyListItems[13], arg0.toString());
                }
            });
        }
        return convertView;
    }

    private class ViewHolder {
        EditText editText1;
        int ref;
    }
}