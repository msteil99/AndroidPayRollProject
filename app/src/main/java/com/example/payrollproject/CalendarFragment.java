package com.example.payrollproject;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;

public class CalendarFragment extends Fragment {


  private OnSelectedListener onSelected;

    public interface OnSelectedListener {
        void onDateSelected(String key);
        void onSettingsSelected();
        void onPayListSelected();
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

        //applies next pay date and pay total for that date
        final TextView tvPayTotal = v.findViewById(R.id.tvNextPayNum);
        tvPayTotal.setText(getArguments().getString(getResources().getString(R.string.payPeriodTotalKey)));
        final TextView tvNextPayDate = v.findViewById(R.id.tvNextPayDate);
        tvNextPayDate.setText(getArguments().getString(getResources().getString(R.string.payDateKey)));

        //open settings fragment
        ImageView imageView = v.findViewById(R.id.imSetting);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onSelected = (OnSelectedListener)getActivity();
                    onSelected.onSettingsSelected();
                } catch (ClassCastException e) {
                    throw new ClassCastException(onSelected.toString()
                            + " must implement OnSelectedListener");
                }
            }
        });
        //open and pass datekey to the CurrentDate fragment
        CalendarView calendar = v.findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                //month+1 so Jan = 1
                String dateKey = year + "-" + (month+1) + "-" + day;
                try {
                    onSelected = (OnSelectedListener)getActivity();
                    onSelected.onDateSelected(dateKey);
                } catch (ClassCastException e) {
                    throw new ClassCastException(onSelected.toString()
                            + " must implement OnSelectedListener");
                }
            }
        });

        Button btn = v.findViewById(R.id.btnPayList);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onSelected = (OnSelectedListener)getActivity();
                    onSelected.onPayListSelected();
                } catch (ClassCastException e) {
                    throw new ClassCastException(onSelected.toString()
                            + " must implement OnSelectedListener");
                }
            }
        });



        return v;
    }
}

