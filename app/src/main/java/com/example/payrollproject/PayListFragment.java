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

//or have it as a date button, when clicked iterate backwards displaying each date
//have it all in a list sorted by dates
//need to dynamically add the dates or an update class

//what do I want? each time currentDate is updated I would like paylist to know and add the date to the list,
//create a sorted list of date objects?


//todo use localDate to compare the dates
//create a sorted list using LocalDate to compare
//todo add and sort list of dates dynamically

//create a sorted list of text objects that can not have duplicates
//initally display only the pay dates
//if paydates are clicked display the the rest of the sorted date objects up until the next paydate
//

public class PayListFragment extends Fragment  {

    private SharedPreferences pref;
    private ArrayList<String> payDate;

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
             //list.add(entry.getValue().toString());
            //could sort and add list in this loop



        }
        return str;
    }


}
