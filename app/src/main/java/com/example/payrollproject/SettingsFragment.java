package com.example.payrollproject;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Objects;

public class SettingsFragment extends Fragment {

    private String firstDay, lastDay;

    OnSettingsChangedListener onSettingsChangedListener;

    public interface OnSettingsChangedListener {
         void onSettingsChanged();
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
        final EditText etDaysPerCycle = v.findViewById(R.id.etDaysPerCycle);

        final FrameLayout flDatePick = v.findViewById(R.id.flDatePick);
        final CalendarView calendar = v.findViewById(R.id.cvDateChoose);
        final Button btnSetAll = v.findViewById(R.id.btnSetAll);

        //retrieve previous user values
        SharedPreferences pref = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefSeti),Context.MODE_PRIVATE);
        etHourlyRate.setText(pref.getString(getResources().getString(R.string.hourlyRateKey), ""));
        etDaysPerCycle.setText((pref.getString(getResources().getString(R.string.daysPerCycleKey),"")));
        firstDay = pref.getString(getResources().getString(R.string.firstPayPerKey),"2020/01/01");
        lastDay=pref.getString(getResources().getString(R.string.lastPayPerKey), "2020/12/28");

        //places calendar in foreground, once date selected by user calendar no longer visible
        Button btnFirstPay = v.findViewById(R.id.btnFirstPay);
        btnFirstPay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                flDatePick.setVisibility(View.VISIBLE);
                btnSetAll.setVisibility(View.GONE);

                calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                        //month+1 so Jan = 1
                        String dateKey = year + "/" + (month+1) + "/" + day;
                        firstDay = dateKey;
                        customToast(dateKey + " added");
                        flDatePick.setVisibility(View.GONE);
                        btnSetAll.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        //places calendar in foreground, once date selected by user calendar no longer visible
        Button btnLastPay = v.findViewById(R.id.btnLastPay);
        btnLastPay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                flDatePick.setVisibility(View.VISIBLE);
                btnSetAll.setVisibility(View.GONE);

                calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                        //month+1 so Jan = 1
                        String dateKey = year + "/" + (month+1) + "/" + day;
                        lastDay = dateKey;
                        customToast(dateKey + " added");
                        flDatePick.setVisibility(View.GONE);
                        btnSetAll.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        //save user entered text
        btnSetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor edSet = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefSeti), Context.MODE_PRIVATE).edit();
                edSet.putString(getResources().getString(R.string.hourlyRateKey), etHourlyRate.getText().toString());
                edSet.putString(getResources().getString(R.string.daysPerCycleKey),etDaysPerCycle.getText().toString());
                edSet.putString(getResources().getString(R.string.firstPayPerKey),firstDay);
                edSet.putString(getResources().getString(R.string.lastPayPerKey),lastDay);
                edSet.apply();

                try {
                    onSettingsChangedListener = (OnSettingsChangedListener)getActivity();
                    Objects.requireNonNull(onSettingsChangedListener).onSettingsChanged();
                } catch (ClassCastException e) {
                    throw new ClassCastException(Objects.requireNonNull(onSettingsChangedListener).toString()
                            + " must implement OnSettingsChangedListener");
                }
            }
        });
        return v;
    }

    public void customToast(String str){
        //Custom text
        final View layout = getActivity().getLayoutInflater().inflate(R.layout.toast_custom,
                (ViewGroup)getActivity().findViewById(R.id.custom_toast_container));

        final TextView toastText = layout.findViewById(R.id.toastText);
        toastText.setText(str);
        final Toast toast = new Toast(getContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 2000);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}


