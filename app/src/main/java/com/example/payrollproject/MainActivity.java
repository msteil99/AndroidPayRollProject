package com.example.payrollproject;




import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import java.time.LocalDate;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements CalendarFragment.OnSelectedListener, CurrentDateFragment.OnHoursChangedListener,
SettingsFragment.OnSettingsChangedListener {

    private CalendarFragment calendarFragment;
    private FragmentManager fragMan;
    private FragmentTransaction fragTrans;
    private String dateKey;
    private int year, month, day;
    private int payRollNum = 0;
    private SharedPreferences sharedPrefSeti, sharedPrefPay;
    private PayRollTrack payRollTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        calendarFragment = new CalendarFragment();
        fragMan = getSupportFragmentManager();
        fragTrans = fragMan.beginTransaction();
        fragTrans.add(R.id.flFragment, calendarFragment);
        fragTrans.commit();
        //below is a test
        //dateStart =  LocalDate.of(2020, 3, 1); //parse value from settings
        //String checker =   dateKey = dateStart.getYear() + "/" + dateStart.getMonthValue() + "/" + dateStart.getDayOfMonth();

        //Toast.makeText(this, checker , Toast.LENGTH_LONG).show();
    }


    public void onDateSelected(String dateKey) {

        CurrentDateFragment dateFragment = new CurrentDateFragment();
        Bundle args = new Bundle();
        String parseDate = dateKey;
        String delims = "/"; // so the delimiters are:  + - * / ^ space
        String[] tokens = parseDate.split(delims);

        year = Integer.parseInt(tokens[0]);
        month = Integer.parseInt(tokens[1]) + 1; //plus one to set January value to 1
        day = Integer.parseInt(tokens[2]);

        dateKey = year + "/" + month + "/" + day;
        this.dateKey = dateKey; // need this to compute value

        args.putString("dateKey", dateKey);
        dateFragment.setArguments(args);

        //replace current fragment with dateFragment, adding transaction to backstack allows user to undo when pressing back
        fragTrans = getSupportFragmentManager().beginTransaction();
        fragTrans.replace(R.id.flFragment, dateFragment);
        fragTrans.addToBackStack(null);
        fragTrans.commit();
    }

    public void onSettingsSelected() {
        SettingsFragment settingsFragment = new SettingsFragment();
        fragTrans = getSupportFragmentManager().beginTransaction();
        fragTrans.replace(R.id.flFragment, settingsFragment);
        fragTrans.addToBackStack(null);
        fragTrans.commit();
    }

    /*
    * this function does a lot of heavy lifting - one if the hour values are changed in settings
    *  needs to iterate and chang all previous values in the application
    *   -setDayTotal(); setPayPeriodTotal()
    *
    *
    * */
    public void onSettingsChanged() {

        sharedPrefSeti = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefSeti), Context.MODE_PRIVATE)); // still crashes
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
        LocalDate dateEnd = LocalDate.of(2020, 12, 28); // parse value from settings


        String dateKey = dateStart.getYear() + "/" + dateStart.getMonthValue() + "/" + dateStart.getDayOfMonth(); //returns 2020/07/01;

        //each pay period will be assigned a number
        sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
        //check to see if the dateKey has a payRollNum associated? if not start at 0
        payRollNum = sharedPrefPay.getInt(dateKey, 0);

        int daysPerCycle = 14; //get value from settings
        double payPeriodTotal = 0;
        SharedPreferences.Editor editor;

        while (dateStart.isBefore(dateEnd)) {
          editor = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit());
          for (int i = 0; i < daysPerCycle; i++) {
            editor.putInt(dateKey, payRollNum);
             saveDayTotal(dateKey);
             // payPeriodTotal += Double.parseDouble(getDayTotal(date)); crashes application
             dateStart = dateStart.plusDays(1);
            }
            //now all I need to extract this key is have the curDate Value get
            //editor.putString("PayRollKey"+ payRollNum, String.valueOf(payPeriodTotal));
            editor.apply();
            payRollNum++;
        }

        //save PayperiodValue()
        //getPayperiod(date)//returns the payperiod it belongs to
    }

    public void onHoursChanged(String dateKey) {
        saveDayTotal(dateKey);
        Toast.makeText(this, String.valueOf(payRollTrack.getDayTotal()), Toast.LENGTH_LONG).show();
    }

    public void setRate(){
        sharedPrefSeti = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefSeti), Context.MODE_PRIVATE)); // still crashes

        String regRate = sharedPrefSeti.getString(getResources().getString(R.string.hourlyRateKey), "0"); //change to non null
        String otRate = sharedPrefSeti.getString(getResources().getString(R.string.otRateKey), "0");
        String sickRate = sharedPrefSeti.getString(getResources().getString(R.string.sickPercentKey), "0");

        //construct below in a onSettingsChanged Listener
        payRollTrack = new PayRollTrack(); //
        payRollTrack.setHourlyRate(Double.parseDouble(regRate));//get this from settins  \/
        payRollTrack.setOverTimeRate(Double.parseDouble(otRate));
        payRollTrack.setSickPay(Double.parseDouble(sickRate));//get this from settings
    }

    public void setPayPeriodTotal(int payperiod){
         //get the payPeriod value
    }

    public void saveDayTotal(String dateKey){
        sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));

        String regHours = sharedPrefPay.getString(dateKey + "reg", "0"); //this works String regHoursKey = curDate + "reg";
        String otHours = sharedPrefPay.getString(dateKey + "ot", "0");
        String sickHours = sharedPrefPay.getString(dateKey + "sick", "0");

        payRollTrack.setRegWorked(Double.parseDouble(regHours));
        payRollTrack.setOtWorked(Double.parseDouble(otHours));
        payRollTrack.setSickWorked(Double.parseDouble(sickHours));

         SharedPreferences.Editor editor = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit());
         editor.putString("DayTotal" + dateKey, String.valueOf(payRollTrack.getDayTotal()));
         editor.commit();
    }

    public String getDayTotal(String dateKey){
     sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));

     return sharedPrefPay.getString("DayTotal", dateKey);
    }

    public void getPayPeriodTotal(){

    }

    public void onBackPressed() {
        // all other activities should return to the calendar screen
        if (!calendarFragment.isVisible()) {
            fragMan.popBackStack();
            fragTrans = fragMan.beginTransaction();
            fragTrans.replace(R.id.flFragment, calendarFragment);
            fragTrans.commit();
        }
    }



}




