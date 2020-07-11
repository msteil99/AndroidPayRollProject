package com.example.payrollproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class CurrentDateFragment extends Fragment {

    //private String MyDatePREF = "MyPrefs";
    private String curDate;
    OnHoursChangedListener mHoursChanged;

    public interface OnHoursChangedListener {
        public void onHoursChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


     curDate = getArguments().getString("dateKey");

     //the inflater object inflates a view that has not yet been created where is the findViewByID has already been inflated
     View v = inflater.inflate(R.layout.fragment_current_date, container, false);
     TextView tvCurDate = v.findViewById(R.id.tvCurrentDate);
     tvCurDate.setText(curDate);

     final EditText etCurDateReg = v.findViewById(R.id.etRegHours);
     final EditText etCurDateOt = v.findViewById(R.id.etOtHours);
     final EditText etCurDateSick = v.findViewById(R.id.etSickHours);
     //instantiate keys unique to this date
     final String regHoursKey = curDate + "reg"; //changed from +=
     final String otHoursKey = curDate + "ot";
     final String sickHoursKey = curDate + "sick";

     //This will set the text if user has already placed a value, placeing the zero ensures a non null value
     SharedPreferences pref = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefKey),Context.MODE_PRIVATE);
      etCurDateReg.setText(pref.getString(regHoursKey, "0"));
      etCurDateOt.setText(pref.getString(otHoursKey,"0"));
      etCurDateSick.setText(pref.getString(sickHoursKey,"0"));


     Button btnAddHours = v.findViewById(R.id.btnAddHours);
      btnAddHours.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
         SharedPreferences.Editor editor = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefKey), Context.MODE_PRIVATE).edit();
         editor.putString(regHoursKey, etCurDateReg.getText().toString());
         editor.putString(otHoursKey,etCurDateOt.getText().toString());
         editor.putString(sickHoursKey,etCurDateSick.getText().toString());
         editor.apply();

             try {
                 mHoursChanged = (OnHoursChangedListener) getActivity();
                 mHoursChanged.onHoursChanged();
             } catch (ClassCastException e) {
                 throw new ClassCastException(mHoursChanged.toString()
                         + " must implement OnSelectedListener");
             }
             //Toast.makeText(getContext(),"saved",Toast.LENGTH_SHORT).show();
         }
     });
      return v;
    }

    //create a function to save totalvalue in a sharedPreference

    /*datekey = 2020/01/06
      cdRegHours * stHourlyRate
      cdSickHours * getsick
      ot * getOt

      regHOurs onClick - getRegHours + getSickRate + getOtRate * all the settings vlaues then pass that anwser to the
    *
    */

    /*\
    * thinking of rewriting the whole application
    *  CurrentDate - saves all shared preference data and adds the total to a key value pair
    *  It may still be more beneficial to create a sharedViewModel
    *
    *  which values in my application need to be shared?
    *  what does each activity accomplish?
    *  where are the values being stored
    *
    *
    *
    *
    * */

}
