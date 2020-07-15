package com.example.payrollproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Objects;


public class SettingsFragment extends Fragment {


    OnSettingsChangedListener onSettingsChangedListener;

    public interface OnSettingsChangedListener {
        public void onSettingsChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_settings, container, false);
        final EditText etHourlyRate = v.findViewById(R.id.etHourlyRate);
        final EditText etOtRate = v.findViewById(R.id.etOTRate);
        final EditText etSickPercentage = v.findViewById(R.id.etSickPercent);
        final EditText etDaysPerCycle = v.findViewById(R.id.etDaysPerCycle);

        //lets create an on datechanged picker
        SharedPreferences pref = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefSeti),Context.MODE_PRIVATE);
        etHourlyRate.setText(pref.getString(getResources().getString(R.string.hourlyRateKey), "0"));
        etOtRate.setText(pref.getString(getResources().getString(R.string.otRateKey),"0"));
        etSickPercentage.setText(pref.getString(getResources().getString(R.string.sickPercentKey),"0"));
        etDaysPerCycle.setText((pref.getString(getResources().getString(R.string.daysPerCycleKey),"0")));

        //set all to string or to much work
        Button btnSetAll = v.findViewById(R.id.btnSetAll);
        //saves user entered text
        btnSetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefSeti), Context.MODE_PRIVATE).edit();
                editor.putString(getString(R.string.hourlyRateKey), etHourlyRate.getText().toString());
                editor.putString(getString(R.string.otRateKey), etOtRate.getText().toString());
                editor.putString(getString(R.string.sickPercentKey), etSickPercentage.getText().toString());
                editor.putString(getString(R.string.daysPerCycleKey),etDaysPerCycle.getText().toString());
                editor.commit();

                try {
                    onSettingsChangedListener = (OnSettingsChangedListener)getActivity();
                    onSettingsChangedListener.onSettingsChanged();
                } catch (ClassCastException e) {
                    throw new ClassCastException(onSettingsChangedListener.toString()
                            + " must implement OnSelectedListener");
                }
                Toast.makeText(getContext(),"Saved",Toast.LENGTH_LONG).show();
            }
        });
        return v;
    }

    /****
     * Application is crashing if settings is not instantiate to a number prior to start

     * **/

    // I need a frm and to date in the settings application. this will iterate and apply key value pairs
    //(datekey,payperiodval)
    //usePayPeriodVal now in the curDateFrag when I enter the daily value it finds the corresponding payperiodval and adds the date
    //first is iterate and apply values




    //return the next pay date given the current date and cycle
    //var - payperiod length, payperiod startdate

    //onclick - iterate through the calendar given the start date - this could iterate all the way to the current date
    //populate the values and save until the algorith
    //mainActivity will have to get the currentDate and calculate the next payperiod
    // add a listener to communicate with main activity when the value of days per cycle and start date are assigned
    //the application could sum the values from the current dates - lets try and sum values form currentdate
    //could I just use the same key formula and iterate

    /*
    *
    *   set all to 0 button/ reset button
    *
    *
    * */

    //once the user hits enter in the setting application the program goes to work - basically it will take the start date and the amount of days
    //to iterate through applying all values it has so far, the user can change the values in the calendar object as needed and the application
    //will update





}


