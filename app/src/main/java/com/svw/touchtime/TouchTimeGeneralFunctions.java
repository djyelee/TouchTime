package com.svw.touchtime;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

/**
 * Created by djlee on 5/10/15.
 */
public class TouchTimeGeneralFunctions extends ActionBarActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public TouchTimeGeneralFunctions() {
    }

    public void TouchTimeDialog(AlertDialog dialog, View view) {
        dialog.show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(view.getResources().getInteger(R.integer.dialog_message_size));
        textView.setTextColor(view.getResources().getColor(R.color.dialog_message_color));
        textView.setTypeface(null, Typeface.BOLD_ITALIC);
        // dialog.getWindow().setLayout(600, 400);
        // textView.setText(MessageID);
        // dialog.getButton(dialog.BUTTON_NEUTRAL).setTextSize(view.getResources().getInteger(R.integer.dialog_button_size));
        // dialog.getButton(dialog.BUTTON_POSITIVE).setTextSize(view.getResources().getInteger(R.integer.dialog_button_size));
        // dialog.getButton(dialog.BUTTON_NEGATIVE).setTextSize(view.getResources().getInteger(R.integer.dialog_button_size));
    }

    public ArrayList<String> getCountries(String msg) {
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length()>0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);
        ArrayList<String> list = new ArrayList<String>();
        list.add("");
        list.add(msg);
        countries.remove("United States");      // remove use from the list
        for (int i=0; i<countries.size(); i++) list.add(countries.get(i));
        return list;
    }

    public String TimeDifference(String OldTime, String NewTime) {
        DateFormat tf = new SimpleDateFormat("HH:mm:ss");
        if (OldTime == null || NewTime == null) return null;
        long diffMinutes = 0;
        long diffHours = 0;
        try {
            Date d1 = tf.parse(OldTime);
            Date d2 = tf.parse(NewTime);
            long diff = d2.getTime() - d1.getTime();
            diffMinutes = diff / (60 * 1000) % 60;
            diffHours = diff / (60 * 60 * 1000) % 24;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("%2s:%2s", String.valueOf(diffHours), String.valueOf(diffMinutes)).replace(' ', '0');     // return in hh:mm format
    }

    public long MinuteDifference(String OldTime, String NewTime) {
        DateFormat tf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (OldTime == null || NewTime == null) return 0;
        long diffMinutes = 0;
        try {
            Date d1 = tf.parse(OldTime);
            Date d2 = tf.parse(NewTime);
            long diff = d2.getTime() - d1.getTime();
            diff = (diff < 0) ? -diff : diff;   // make sure it is always positive of the difference between two times
            diffMinutes = diff / (60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return diffMinutes;     // return in number of minutes
    }

    public void SortIntegerList(ArrayList<HashMap<String, String>> List, String Items, boolean ascend) {
        final String ItemList = Items;
        final boolean Ascend = ascend;          // 1 ascend -1: descend
        Collections.sort(List, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                int First;
                int Second;
                int i = 0;
                if (ItemList != null) {
                    First = (o1.get(ItemList).isEmpty()) ? 0 : Integer.parseInt(o1.get(ItemList));
                    Second = (o2.get(ItemList).isEmpty()) ? 0 : Integer.parseInt(o2.get(ItemList));
                    if (Ascend) {
                        if (First == 0)
                            return 1;
                        else if (Second == 0)
                            return -1;
                        return (First < Second ? -1 : (First == Second ? 0 : 1));
                    } else {
                        return (First < Second ? 1 : (First == Second ? 0 : -1));
                    }
                }
                return 0;
            }
        });
    }

    // Return index of the item on the list that matches the input integer
    public int GetIntegerIndex(ArrayList<HashMap<String, String>> List, String Items, int Data) {
        int i;
        if (Items == null) return -1;  // item not specified
        for (i = 0; i < List.size(); i++) {
            if (!List.get(i).get(Items).isEmpty() && Data == Integer.parseInt(List.get(i).get(Items)))
                return i;
        }
        return -1;
    }

    public void SortStringList(ArrayList<HashMap<String, String>> List, String[] Items, boolean ascend) {
        final String[] ItemList = Items;
        final boolean Ascend = ascend;          // 1 ascend -1: descend
        Collections.sort(List, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                int i = 0, result = 0;
                String First[] = new String[ItemList.length];
                String Second[] = new String[ItemList.length];
                for (i = 0; i < ItemList.length && ItemList[i] != null; i++) {
                    First[i] = o1.get(ItemList[i]);
                    Second[i] = o2.get(ItemList[i]);
                    if (First[i].isEmpty() && Second[i].isEmpty()) return 0;
                    if (Ascend) {
                        if (First[i].isEmpty() && !Second[i].isEmpty()) return 1;
                        if (!First[i].isEmpty() && Second[i].isEmpty()) return -1;
                        result = First[i].compareToIgnoreCase(Second[i]);
                    } else {
                        if (First[i].isEmpty() && !Second[i].isEmpty()) return 1;
                        if (!First[i].isEmpty() && Second[i].isEmpty()) return -1;
                        result = Second[i].compareToIgnoreCase(First[i]);
                    }
                    if (result != 0) return result;
                }
                return 0;
            }
        });
    }

    // Return index of the item on the list that matches the input two strings
    public int GetStringIndex(ArrayList<HashMap<String, String>> List, String[] Items, String[] Data) {
        boolean matched;
        int i, j;
        for (i = 0; i < Items.length; i++) if (Items[i] == null) return -1;  // item not specified
        for (i = 0; i < List.size(); i++) {
            matched = true;
            for (j = 0; j < Data.length; j++) {
                if (!List.get(i).get(Items[j]).isEmpty() && !Data[j].equals(List.get(i).get(Items[j]))) {
                    matched = false;
                    break;
                }
            }
            if (matched) return i;
        }
        return -1;
    }

    public void SortIntegerStringList(ArrayList<HashMap<String, String>> List, String[] Items, boolean ascend) {
        final String[] ItemList = Items;
        final boolean Ascend = ascend;          // 1 ascend -1: descend
        Collections.sort(List, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                int i = 0, result = 0;
                String First[] = new String[ItemList.length];
                String Second[] = new String[ItemList.length];
                for (i = 0; i<ItemList.length && ItemList[i] != null; i++) {
                    if (i == 0) {
                        int one = (o1.get(ItemList[i]).isEmpty()) ? 0 : Integer.parseInt(o1.get(ItemList[i]));
                        int two = (o2.get(ItemList[i]).isEmpty()) ? 0 : Integer.parseInt(o2.get(ItemList[i]));
                        if (Ascend) {
                            if (one == 0)
                                return 1;
                            else if (two == 0)
                                return -1;
                            result = (one < two ? -1 : (one == two ? 0 : 1));
                        } else {
                            result = (one < two ? 1 : (one == two ? 0 : -1));
                        }
                        if (result != 0) return result;
                    } else {
                        First[i] = o1.get(ItemList[i]);
                        Second[i] = o2.get(ItemList[i]);
                        if (First[i].isEmpty() && Second[i].isEmpty()) return 0;
                        if (Ascend) {
                            if (First[i].isEmpty() && !Second[i].isEmpty()) return 1;
                            if (!First[i].isEmpty() && Second[i].isEmpty()) return -1;
                            result = First[i].compareToIgnoreCase(Second[i]);
                        } else {
                            if (First[i].isEmpty() && !Second[i].isEmpty()) return 1;
                            if (!First[i].isEmpty() && Second[i].isEmpty()) return -1;
                            result = Second[i].compareToIgnoreCase(First[i]);
                        }
                        if (result != 0) return result;
                    }
                }
                return 0;
            }
        });
    }

    public int checkDuplicates(ArrayList<String> list, String testString) {
        int nDuplicates = 0;
        // Loop over argument list.
        for (String item : list) {
            if (item.equals(testString)) {
                nDuplicates++;
            }
        }
        return nDuplicates;
    }

    public ArrayList<String> sortString(ArrayList<String> list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.isEmpty() && o2.isEmpty()) return 0;
                if (o1.isEmpty() && !o2.isEmpty()) return 1;
                if (!o1.isEmpty() && o2.isEmpty()) return -1;
                return o1.compareToIgnoreCase(o2);
            }
        });
        return null;
    }

    public ArrayList<String> removeDuplicates(ArrayList<String> list) {
        int size = list.size();         // duplicates will be removed, so must preserve the size
        // Record encountered Strings in HashSet.
        HashSet<String> set = new HashSet<String>();
        // Loop over argument list.
        for (int i = 0; i<size; i++) {
            // If String is not in set, add it to the list and the set.
            if (!set.contains(list.get(i))) {
                set.add(list.get(i));
            } else {
                list.remove(i);     // remove duplicate
                size--;             // decrease the size
                i--;
            }
        }
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TouchTimeGeneralFunctions Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.svw.touchtime/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TouchTimeGeneralFunctions Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.svw.touchtime/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

