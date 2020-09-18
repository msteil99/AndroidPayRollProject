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

        //retrive previous user values
        SharedPreferences pref = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefSeti),Context.MODE_PRIVATE);
        etHourlyRate.setText(pref.getString(getResources().getString(R.string.hourlyRateKey), "0"));
        etDaysPerCycle.setText((pref.getString(getResources().getString(R.string.daysPerCycleKey),"0")));

        Button btnSetAll = v.findViewById(R.id.btnSetAll);
        //save user entered text
        btnSetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo place if != null
                SharedPreferences.Editor edSet = Objects.requireNonNull(getContext()).getSharedPreferences(getResources().getString(R.string.prefSeti), Context.MODE_PRIVATE).edit();
                edSet.putString(getString(R.string.hourlyRateKey), etHourlyRate.getText().toString());
                edSet.putString(getString(R.string.daysPerCycleKey),etDaysPerCycle.getText().toString());
                edSet.apply();

                try {
                    onSettingsChangedListener = (OnSettingsChangedListener)getActivity();
                    Objects.requireNonNull(onSettingsChangedListener).onSettingsChanged();
                } catch (ClassCastException e) {
                    throw new ClassCastException(Objects.requireNonNull(onSettingsChangedListener).toString()
                            + " must implement OnSelectedListener");
                }

            }
        });
        return v;
    }
}


