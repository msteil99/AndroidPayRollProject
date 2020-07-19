package com.example.payrollproject;




import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements CalendarFragment.OnSelectedListener, CurrentDateFragment.OnHoursChangedListener,
        SettingsFragment.OnSettingsChangedListener {

    private CalendarFragment calendarFragment;
    private FragmentManager fragMan; private FragmentTransaction fragTrans;
    private String dateKey;
    private int year, month, day;
    private SharedPreferences sharedPrefSeti, sharedPrefPay;
    private SharedPreferences.Editor editor;
    private PayRollTrack payRollTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarFragment = new CalendarFragment();
        fragMan = getSupportFragmentManager();
        fragTrans = fragMan.beginTransaction();
        fragTrans
                .add(R.id.flFragment, calendarFragment)
                .commit();
    }

    public void onDateSelected(String dateKey) {
        CurrentDateFragment dateFragment = new CurrentDateFragment();
        Bundle args = new Bundle();
        //function to parse month, so Jan will be 1 not 0 when selecting date from Calendar
        String parseDate = dateKey;
        String delims = "/";
        String[] tokens = parseDate.split(delims);
        year = Integer.parseInt(tokens[0]);
        month = Integer.parseInt(tokens[1]) + 1;
        day = Integer.parseInt(tokens[2]);

        dateKey = year + "/" + month + "/" + day;
        this.dateKey = dateKey; // need this to compute value

        args.putString("dateKey", dateKey);
        dateFragment.setArguments(args);

        //replace current fragment with dateFragment, adding transaction to backstack allows user to undo when pressing back button
        fragTrans = getSupportFragmentManager().beginTransaction();
        fragTrans
                .replace(R.id.flFragment, dateFragment)
                .addToBackStack(null)
                .commit();
    }

    public void onSettingsSelected() {
        SettingsFragment settingsFragment = new SettingsFragment();

        fragTrans = getSupportFragmentManager().beginTransaction();
        fragTrans
                .replace(R.id.flFragment, settingsFragment)
                .addToBackStack(null)
                .commit();
    }
     //todo having toruble saving the payrolltrack number and saving and returng full payroll amount
    public void onSettingsChanged() {

        sharedPrefSeti = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefSeti), Context.MODE_PRIVATE));
        //values set by user
        String regRate = sharedPrefSeti.getString(getResources().getString(R.string.hourlyRateKey), "0"); //change to non null
        String otRate = sharedPrefSeti.getString(getResources().getString(R.string.otRateKey), "0");
        String sickRate = sharedPrefSeti.getString(getResources().getString(R.string.sickPercentKey), "0");

        //payRollTrack for most calculations
        payRollTrack = new PayRollTrack(); //
        payRollTrack.setHourlyRate(Double.parseDouble(regRate));//get this from settins  \/
        payRollTrack.setOverTimeRate(Double.parseDouble(otRate));
        payRollTrack.setSickPay(Double.parseDouble(sickRate));//get this from settings

        //get first and last day of the pay period
        LocalDate dateStart = LocalDate.of(2020, 7, 1); //parse value from settings
        LocalDate dateEnd = LocalDate.of(2020, 7, 31); // parse value from settings


         dateKey = dateStart.getYear() + "/" + dateStart.getMonthValue() + "/" + dateStart.getDayOfMonth(); //returns 2020/07/01;

        //returning 39 at this point
        //int payRollNum = sharedPrefPay.getInt(dateKey,0); // crashed the application a couple times? should be non null value
          int payRollNum = 0;
          double sum = 0;

         int daysPerCycle = 14; //get value from settings
         editor= Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit());
         while (dateStart.isBefore(dateEnd)) {
           for (int i = 0; i < daysPerCycle; i++) {
                editor.putInt( dateKey,payRollNum);
                 saveDayTotal(dateKey); //appears to work
                 sum+=Double.parseDouble(getDayTotal(dateKey));
                 //savePayPeriodTotal();
                dateStart = dateStart.plusDays(1);
           }
            //todo save pay sum + track payroll num
           Toast.makeText(this,"payRollNum " + payRollNum,Toast.LENGTH_LONG).show();


            payRollNum++;
         }
            editor.apply();
       // Toast.makeText(this, payRollNum,Toast.LENGTH_LONG).show();


        sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
        float check= sharedPrefPay.getFloat(getResources().getString(R.string.payPeriodTotalKey) + 0, 0);
       // Toast.makeText(this, getDayTotal("2020/07/02"),Toast.LENGTH_LONG).show(); //returns 39

    }

    public void onHoursChanged(String dateKey) {
        saveDayTotal(dateKey);
        Toast.makeText(this, String.valueOf(payRollTrack.getDayTotal()), Toast.LENGTH_LONG).show();
    }

    //todo this one is not returning properly, going another route :)
    public void sumPayPeriod(int payPeriod){
        String date;
        float sum = 0;

        Map<String, ?> allEntries = sharedPrefPay.getAll();
        editor = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit());

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

         if(entry.getValue() == String.valueOf(payPeriod)){
             date = entry.getKey();
             sum += Double.parseDouble(getDayTotal(date));
         }
         editor.putFloat(getResources().getString(R.string.payPeriodTotalKey) + payPeriod ,sum);
        }
         editor.commit();

        sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
       Float check= sharedPrefPay.getFloat(getResources().getString(R.string.payPeriodTotalKey) + payPeriod,0);
       Toast.makeText(this,"hmmm- " + sum,Toast.LENGTH_LONG).show();

    }


    public int getPayPeriod(String date){
        sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
        return 0;
    }

    //works
    public void saveDayTotal(String dateKey){
        sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));

        String regHours = sharedPrefPay.getString(dateKey + "reg", "0"); //this works String regHoursKey = curDate + "reg";
        String otHours = sharedPrefPay.getString(dateKey + "ot", "0");
        String sickHours = sharedPrefPay.getString(dateKey + "sick", "0");

        payRollTrack.setRegWorked(Double.parseDouble(regHours));
        payRollTrack.setOtWorked(Double.parseDouble(otHours));
        payRollTrack.setSickWorked(Double.parseDouble(sickHours));

        editor = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit());
        editor.putString("DayTotal" + dateKey, String.valueOf(payRollTrack.getDayTotal())).commit();

    }
    //works -- could change to save a float since its numeric //todo change name since payrolltrack has same method
    public String getDayTotal(String dateKey){
        sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
        String total =  sharedPrefPay.getString("DayTotal"+ dateKey, "");


        return total;
    }

    public void getPayPeriodTotal(String Payperiod){
    }

    public void onBackPressed() {
        // all other activities should return to the calendar screen
        if (!calendarFragment.isVisible()) {
            fragMan.popBackStack();
            fragTrans = fragMan.beginTransaction();
            fragTrans.replace(R.id.flFragment, calendarFragment).commit();
        }
    }



}

