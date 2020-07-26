package com.example.payrollproject;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

public class CalendarFragment extends Fragment {

  //  private String dateKey;
    OnSelectedListener mdateSelected;
    private CalendarView calendar;

    //create an interface that the main activity must implement called on date sleected that handles date selecected
     //The container Activity must implement this interface so the frag can deliver messages
    public interface OnSelectedListener {

        public void onDateSelected(String key);
        public void onSettingsSelected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);


        //todo check check
        final TextView tvPayTotal = v.findViewById(R.id.tvNextPayNum);
        tvPayTotal.setText(getArguments().getString(getResources().getString(R.string.payPeriodTotalKey)));
        final TextView tvNextPayDate = v.findViewById(R.id.tvNextPayDate);
        tvNextPayDate.setText(getArguments().getString(getResources().getString(R.string.payDateKey)));

        ImageView imageView = v.findViewById(R.id.imSetting);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mdateSelected = (OnSelectedListener)getActivity();
                    mdateSelected.onSettingsSelected();
                } catch (ClassCastException e) {
                    throw new ClassCastException(mdateSelected.toString()
                            + " must implement OnSelectedListener");
                }
            }
        });

        calendar = v.findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String dateKey = year + "-" + month + "-" + day;
                // This makes sure that the container activity has implemented
                // the callback interface. If not, it throws an exception.
                try {
                    mdateSelected = (OnSelectedListener)getActivity();
                    mdateSelected.onDateSelected(dateKey);
                } catch (ClassCastException e) {
                    throw new ClassCastException(mdateSelected.toString()
                            + " must implement OnSelectedListener");
                }
            }
        });
        return v;
    }




}

/*Next I would like the Next Pay date to populate the next pay period using the settings specifications
* using the settings specifications I need to add the total for the next pay
* I can then save each one separately*/