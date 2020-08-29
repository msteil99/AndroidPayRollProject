package com.example.payrollproject;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Objects;


//todo add hours and minutes together
//todo toast error if time not in correct range after pressing add button
//todo if edit text equals null application crashes


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
     //spinner for am or pm
     final Spinner spFrmTime = v.findViewById(R.id.spFrmTime);
     final Spinner spToTime = v.findViewById(R.id.spToTime);
     final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(),
           R.array.am_pm, android.R.layout.simple_spinner_dropdown_item);
      spFrmTime.setAdapter(adapter1);
      spToTime.setAdapter(adapter1);
     //spinner for hourly rate type
     final Spinner spWageRate = v.findViewById(R.id.spWageChooser);
     final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
           R.array.wage_chooser, android.R.layout.simple_spinner_dropdown_item);
        spWageRate.setAdapter(adapter);

      final EditText etFrmHour = v.findViewById(R.id.etFrmHours);
      final EditText etFrmMin = v.findViewById(R.id.etFrmMin);
      final EditText etToHour = v.findViewById(R.id.etToHours);
      final EditText etToMin = v.findViewById(R.id.etToMin);



     // final TextView etCurDateReg = v.findViewById(R.id.tvRegHours);
    // final TextView etCurDateOt = v.findViewById(R.id.tvOtHours);
    // final TextView etCurDateSick = v.findViewById(R.id.tvSickHours);
     //instantiate keys unique to this date
    // final String regHoursKey = curDate + "reg";
     //final String otHoursKey = curDate + "ot";
     //final String sickHoursKey = curDate + "sick";

     //This will set the text if user has already placed a value, placeing the zero ensures a non null value
     SharedPreferences pref = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefPayRoll),Context.MODE_PRIVATE);
    //  tvCurDateReg.setText(pref.getString(regHoursKey, "0"));
    //  tvCurDateOt.setText(pref.getString(otHoursKey,"0"));
     // tvCurDateSick.setText(pref.getString(sickHoursKey,"0"));


     //below will be a different Fragment
     Button btnAddHours = v.findViewById(R.id.btnAddHours);
      btnAddHours.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
             //convert user text to int
              int frmHour = Integer.parseInt(etFrmHour.getText().toString());
              int frmMin = Integer.parseInt(etFrmMin.getText().toString());
              int toHour = Integer.parseInt(etToHour.getText().toString());
              int toMin = Integer.parseInt(etToMin.getText().toString());

              //check range for 12 hour format
              if (frmHour <= 0 || frmHour > 12 || frmMin > 59) {
                  Toast.makeText(getActivity(), "Out of Range", Toast.LENGTH_SHORT).show();
                  etFrmHour.setText("12");
                  return;
              }
              if (toHour <= 0 || toHour > 12 || toMin > 59) {
                  Toast.makeText(getActivity(), "Out of Range", Toast.LENGTH_SHORT).show();
                  etToHour.setText("12");
                  return;
              }
              //convert clock to 24 hour
              if (spFrmTime.getSelectedItem().toString().contains("pm") && frmHour != 12)
                  frmHour += 12;
              if (spToTime.getSelectedItem().toString().contains("pm") && toHour != 12)
                  toHour += 12;
              if (spFrmTime.getSelectedItem().toString().contains("am")) {
                  if (frmHour == 12)
                      frmHour = 0;
              }
              if (spToTime.getSelectedItem().toString().contains("am")) {
                  if (toHour == 12)
                      toHour = 0;
              }

              //helper class to calculate difference in hours
              TimeDif dif = new TimeDif(frmHour, frmMin, toHour, toMin);
              //Toast.makeText(getActivity(), dif.getDif() + " hours", Toast.LENGTH_LONG).show();

              //print hours
              String wageRate = spWageRate.getSelectedItem().toString();
                if(wageRate.contains("reg")){
                  Toast.makeText(getActivity(),dif.getDif() + " reg hours",Toast.LENGTH_LONG).show();
                }
                if(wageRate.contains("ot")){
                Toast.makeText(getActivity(),dif.getDif() + " ot hours",Toast.LENGTH_LONG).show();
                }
                else if(wageRate.contains("sick")){
                 Toast.makeText(getActivity(),dif.getDif() + " sick hours",Toast.LENGTH_LONG).show();
               }
          }
      });

      return v;
    }






}



// String wageRate = spWageRate.getSelectedItem().toString();
                  //if(wageRate.contains("reg")){
                   //   Toast.makeText(getActivity(),endTime + "reg hours",Toast.LENGTH_LONG).show();
                 // }
                 // if(wageRate.contains("ot")){
                   //   Toast.makeText(getActivity(),endTime + " ot hours",Toast.LENGTH_LONG).show();
                 // }
                 // else if(wageRate.contains("sick")){
                   //   Toast.makeText(getActivity(),endTime + "sick hours",Toast.LENGTH_LONG).show();
                  //}







 //this will be the OnDone button
 /* btnAddHours.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
         SharedPreferences.Editor edCurDate = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit();
       //  edCurDate.putString(regHoursKey, etCurDateReg.getText().toString());
        // edCurDate.putString(otHoursKey,etCurDateOt.getText().toString());
        // edCurDate.putString(sickHoursKey,etCurDateSick.getText().toString());
         edCurDate.apply();

             try {
                 mHoursChanged = (OnHoursChangedListener) getActivity();
                 Objects.requireNonNull(mHoursChanged).onHoursChanged(curDate);
             } catch (ClassCastException e) {
                 throw new ClassCastException(Objects.requireNonNull(mHoursChanged).toString()
                         + " must implement OnSelectedListener");
             }

         }
     }); */