package com.example.payrollproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/*Class will retrieve and printed list from shared preference storage and display to screen, listener attached to Main activity
 * adjusts print if any changes*/

//put the listener in main activity as normal and send all values associated with the payDate

//todo send the infromation to paylist via listeners,

public class PayListFragment extends Fragment  {

    private SharedPreferences pref;
    private ArrayList<String> payDates = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        pref = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefPrintPay),Context.MODE_PRIVATE);

        String payList = printAll();

        View v = inflater.inflate(R.layout.fragment_pay_list, container, false);
        TextView list = v.findViewById(R.id.tvPayList);
        list.setText(payList);

        return v;
    }

    //function to retrieve all values in shared pref
    public String printAll(){
        String str = "";
        //Set<String> treeSet = new TreeSet<>();
        //treeSet = pref.getStringSet("payDateKey",treeSet);


        Map<String, ?> allEntries = pref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            str += "\n" + entry.getValue().toString() + "\n";


        }
        return str;
    }


}
