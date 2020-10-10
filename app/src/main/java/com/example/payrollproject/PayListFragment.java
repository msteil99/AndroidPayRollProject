package com.example.payrollproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class PayListFragment extends Fragment  {

    private SharedPreferences spPayData;
    private Set<String> payDateSet;
    private Set<String> datesWorkedSet;
    private ArrayList<View> payBtnList;
    private String strPayDate = "";
    private String dateData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v= inflater.inflate(R.layout.fragment_pay_list, container, false);
        //list of buttons to display individual payroll date
        payBtnList = new ArrayList<>();

        spPayData = Objects.requireNonNull(getContext().getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
        payDateSet = spPayData.getStringSet(getResources().getString(R.string.payDatesKey),new TreeSet<String>());

        //Container for  payroll buttons
        LinearLayout linearLayout =  v.findViewById(R.id.liPayDateList);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5,50,5,0);

        //iterate and apply payroll buttons to screen
        Iterator<String> it = payDateSet.iterator();
        while(it.hasNext()) {

            final Button btnPayRoll = new Button(getContext());
            btnPayRoll.setBackground(getResources().getDrawable(R.drawable.btn_custom, getActivity().getTheme()));

            payBtnList.add(btnPayRoll);
            strPayDate = it.next();

            btnPayRoll.setText(strPayDate);
            final Float total = spPayData.getFloat(getResources().getString(R.string.payPeriodTotalKey) + btnPayRoll.getText(), 0);

            //remove any payroll btns with zero value
            if (total > 0) {
                btnPayRoll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        TextView tvPay = getActivity().findViewById(R.id.tvPDate);
                        tvPay.setText(String.valueOf(total));

                        dateData = "";
                        datesWorkedSet = spPayData.getStringSet(getResources().getString(R.string.datesWorkedKey) + btnPayRoll.getText(), new TreeSet<String>());

                        Iterator<String> it2 = datesWorkedSet.iterator();

                        while (it2.hasNext()) {
                            String printKey = it2.next();
                            dateData += printDateData(printKey);
                        }
                        TextView tv = getActivity().findViewById(R.id.tvPrintDates);
                        tv.setText(dateData);
                        tv.setTextIsSelectable(true); //user can copy text
                    }
                });
                linearLayout.addView(btnPayRoll, layoutParams);
            }
         }
         return v;
    }

    public String printDateData(String key){
        String print = "";

        String regHoursKey =  getResources().getString(R.string.regHoursKey) + key;
        String otHoursKey =   getResources().getString(R.string.otHoursKey) + key;
        String sickHoursKey = getResources().getString(R.string.sickHoursKey) + key;
        String dayTotalKey = getResources().getString(R.string.dayTotalKey) + key;

        String dayTotal = spPayData.getString(dayTotalKey,"0");

        if(Float.valueOf(dayTotal) > 0) {
            print += key + "\nReg Hours=" + spPayData.getString(regHoursKey, "0") + "\nOT Hours(1.5)= "
                    + spPayData.getString(otHoursKey, "0") + "\nSickHours= " + spPayData.getString(sickHoursKey, "0") + "\n"
                    + "Day Total = " + dayTotal + "\n\n";
        }
        return print;
    }
}



