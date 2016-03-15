package com.svw.touchtime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by djlee on 5/10/15.
 */
public class TouchTimeGeneralFunctions {

    public TouchTimeGeneralFunctions() {
    }

    public String TimeDifference(String OldTime, String NewTime) {
        DateFormat tf = new SimpleDateFormat("hh:mm:ss aa");
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
        DateFormat tf = new SimpleDateFormat("hh:mm:ss aa");
        if (OldTime == null || NewTime == null) return 0;
        long diffMinutes = 0;
        try {
            Date d1 = tf.parse(OldTime);
            Date d2 = tf.parse(NewTime);
            long diff = d2.getTime() - d1.getTime();
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
                    if (!Ascend) {
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
        for (i = 0; i< List.size(); i++) {
            if (Data == Integer.parseInt(List.get(i).get(Items))) return i;
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
                for (i = 0; i<ItemList.length; i++) {
                    First[i] = o1.get(ItemList[i]);
                    Second[i] = o2.get(ItemList[i]);
                    if (First[i].isEmpty() && Second[i].isEmpty()) return 0;
                    if (!Ascend) {
                        if (First[i].isEmpty() && !Second[i].isEmpty()) return 1;
                        if (!First[i].isEmpty() && Second[i].isEmpty()) return -1;
                        result = First[i].compareToIgnoreCase(Second[i]);
                    } else {
                        if (First[i].isEmpty() && !Second[i].isEmpty()) return -1;
                        if (!First[i].isEmpty() && Second[i].isEmpty()) return 1;
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
        int i;
        for (i = 0; i< Items.length; i++) if (Items[i] == null) return -1;  // item not specified
        for (i = 0; i< List.size(); i++) {
            if (Data[0].equals(List.get(i).get(Items[0])) && Data[1].equals(List.get(i).get(Items[1]))) return i;
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
                for (i = 0; ItemList[i] != null; i++) {
                    if (i == 0) {
                        int one = (o1.get(ItemList).isEmpty()) ? 0 : Integer.parseInt(o1.get(ItemList[i]));
                        int two = (o2.get(ItemList).isEmpty()) ? 0 : Integer.parseInt(o2.get(ItemList[i]));
                        if (!Ascend) {
                            result = (one < two ? -1 : (one == two ? 0 : 1));
                        } else {
                            result = (one < two ? 1 : (one == two ? 0 : -1));
                        }
                        if (result != 0) return result;
                    } else {
                        First[i] = o1.get(ItemList[i]);
                        Second[i] = o2.get(ItemList[i]);
                        if (First[i].isEmpty() && Second[i].isEmpty()) return 0;
                        if (!Ascend) {
                            if (First[i].isEmpty() && !Second[i].isEmpty()) return 1;
                            if (!First[i].isEmpty() && Second[i].isEmpty()) return -1;
                            result = First[i].compareToIgnoreCase(Second[i]);
                        } else {
                            if (First[i].isEmpty() && !Second[i].isEmpty()) return -1;
                            if (!First[i].isEmpty() && Second[i].isEmpty()) return 1;
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
        // Store unique items in result.
        ArrayList<String> result = new ArrayList<String>();
        // Record encountered Strings in HashSet.
        HashSet<String> set = new HashSet<String>();
        // Loop over argument list.
        for (String item : list) {
            // If String is not in set, add it to the list and the set.
            if (!set.contains(item)) {
                result.add(item);
                set.add(item);
            }
        }
        return result;
    }

}
