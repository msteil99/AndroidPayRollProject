package com.example.payrollproject;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Objects;

//todo create a minutes or hours picker to calucate

public class CurrentDateFragment extends Fragment {


    private String curDate;
    private OnHoursChangedListener mHoursChanged;

    public interface OnHoursChangedListener {
        void onHoursChanged(String dateKey);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

     curDate = getArguments().getString(getResources().getString(R.string.dateKey));
     Log.d("curdatekey", curDate); //works

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
     SharedPreferences pref = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefPayRoll),Context.MODE_PRIVATE);
      etCurDateReg.setText(pref.getString(regHoursKey, "0"));
      etCurDateOt.setText(pref.getString(otHoursKey,"0"));
      etCurDateSick.setText(pref.getString(sickHoursKey,"0"));



     Button btnAddHours = v.findViewById(R.id.btnAddHours);
      btnAddHours.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
         SharedPreferences.Editor edCurDate = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit();
         edCurDate.putString(regHoursKey, etCurDateReg.getText().toString());
         edCurDate.putString(otHoursKey,etCurDateOt.getText().toString());
         edCurDate.putString(sickHoursKey,etCurDateSick.getText().toString());
         edCurDate.apply();

             try {
                 mHoursChanged = (OnHoursChangedListener) getActivity();
                 Objects.requireNonNull(mHoursChanged).onHoursChanged(curDate);
             } catch (ClassCastException e) {
                 throw new ClassCastException(Objects.requireNonNull(mHoursChanged).toString()
                         + " must implement OnSelectedListener");
             }

         }
     });
      return v;
    }


    //shared prefs - userH


}
