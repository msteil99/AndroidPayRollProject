package com.example.payrollproject;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NextPayFragment extends Fragment {

   private PayRollTrack trackPay;
    public static final String MySetiPREF = "MyPrefss" ;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_next_pay, container, false);

        TextView totalPay = v.findViewById(R.id.tvNextPayNum);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(MySetiPREF, getContext().MODE_PRIVATE);

        trackPay = new PayRollTrack();
        //hourly Rate needs to be set first // maybe add that to the constructor
         //trackPay.setHourlyRate(sharedPreferences.getFloat("hourlyRateKey",0)); //getHourly from Settings
       //  trackPay.setSickPay(sharedPreferences.getFloat("sickPercentKey",0)); //get sick percent from settings
        // trackPay.setOverTimeRate(sharedPreferences.getFloat("otRateKey",0)); //get ot from settings
     //   trackPay.setRegWorked((float)7); //get from curDate


       double dayTotal = trackPay.getDayTotal();
       totalPay.setText(String.valueOf(dayTotal));

       return v;
    }
    // getCurDate Fragment et Objects
    // add total using PayRollTracking.java
    //this will receive an intent from the
    //next pay will get the total keep track of the total stored values

}
// once that is totaled I could create a List to of pay periods to save