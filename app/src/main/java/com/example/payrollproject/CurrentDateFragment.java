package com.example.payrollproject;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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
     //get date selected key
     curDate = getArguments().getString(getResources().getString(R.string.dateKey));

     View v = inflater.inflate(R.layout.fragment_current_date, container, false);
    //sets title to match date key
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

     final TextView tvRegNum = v.findViewById(R.id.tvRegHours);
     final TextView tvOtNum  = v.findViewById(R.id.tvOTHours);
     final TextView tvSickNum = v.findViewById(R.id.tvSickHours);

     final EditText etFrmHour = v.findViewById(R.id.etFrmHours);
     final EditText etFrmMin = v.findViewById(R.id.etFrmMin);
     final EditText etToHour = v.findViewById(R.id.etToHours);
     final EditText etToMin = v.findViewById(R.id.etToMin);

     //instantiate keys unique to this date
     final String regHoursKey = curDate + R.string.regHoursKey;
     final String otHoursKey = curDate + R.string.otHoursKey;
     final String sickHoursKey = curDate + R.string.sickHoursKey;

     //This will set the text if user has previous saved value
     SharedPreferences pref = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefPayRoll),Context.MODE_PRIVATE);
     tvRegNum.setText(pref.getString(regHoursKey, "0"));
     tvOtNum.setText(pref.getString(otHoursKey,"0"));
     tvSickNum.setText(pref.getString(sickHoursKey,"0"));

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
              String num = String.valueOf(dif.getHourDif());
              //print hours
              String wageRate = spWageRate.getSelectedItem().toString();
                if(wageRate.contains("reg")){
                 tvRegNum.setText(num);
                }
                if(wageRate.contains("ot")){
                 tvOtNum.setText(num);
                }
                else if(wageRate.contains("sick")){
                 tvSickNum.setText(num);
               }
          }
      });
      //reset all to zero
      Button btnClear = v.findViewById(R.id.btnClear);
      btnClear.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              tvRegNum.setText("0");
              tvOtNum.setText("0");
              tvSickNum.setText("0");
          }
      });
      //save results and return to main frag



      Button btnSave = v.findViewById(R.id.btnSaveDate);
      btnSave.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              SharedPreferences.Editor edCurDate = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit();
                  edCurDate.putString(regHoursKey, tvRegNum.getText().toString());
                  edCurDate.putString(otHoursKey, tvOtNum.getText().toString());
                  edCurDate.putString(sickHoursKey, tvSickNum.getText().toString());
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
}






