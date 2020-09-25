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
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/*Class will retrieve and printed list from shared preference storage and display to screen, listener attached to Main activity
* adjusts print if any changes*/

//I will redo this to display a arrayList of buttons or text objects
//if settings changed in date range change all
//if cur date change dynamically add new values
//what should I use as a key?


public class PayListFragment extends Fragment  {

    private SharedPreferences pref;

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
        Map<String, ?> allEntries = pref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            str += "\n" + entry.getValue().toString() + "\n";
        }
        return str;
    }


}
