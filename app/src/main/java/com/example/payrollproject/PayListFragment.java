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
import android.widget.Toast;

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
//todo create a dynamic list of button containing pay dates
//   PAYDATES
//    Button or better yet text that looks like clickable button
//    Button2
//    button3 etc

//create an arrayList of button objects adding infinite


public class PayListFragment extends Fragment  {

    private SharedPreferences spPayDates;
    private SharedPreferences spPayData;
    private Set<String> payDates;
    private ArrayList<View> btnList;
    private String payDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        spPayDates = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefPrintPay),Context.MODE_PRIVATE);
        spPayData = Objects.requireNonNull(getContext().getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
        String payList = printAll();

        View v = inflater.inflate(R.layout.fragment_pay_list, container, false);
        TextView list = v.findViewById(R.id.tvPayList);
        list.setText(payList);

        return v;
    }

    //function to retrieve all values in shared pref
    public String printAll(){

        String str = "";
        spPayData = Objects.requireNonNull(getContext().getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
        payDates = new TreeSet<>();
        Iterator<String> it;

        Map<String, ?> allEntries = spPayDates.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            payDate = entry.getValue().toString();

        }
        return str;
    }


    //set of dates to iterate and apply using payDate string as key
    // payDates= spPayData.getStringSet(getResources().getString(R.string.dateSetKey) + entry.getValue().toString(), payDates);
    //  it = payDates.iterator();
           /* works
            while(it.hasNext()) {
                str += it.next() + " Yep";
            }
            */

}
