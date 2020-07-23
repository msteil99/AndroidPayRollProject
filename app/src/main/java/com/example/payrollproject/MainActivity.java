package com.example.payrollproject;




import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;



public class MainActivity extends AppCompatActivity implements CalendarFragment.OnSelectedListener, CurrentDateFragment.OnHoursChangedListener,
        SettingsFragment.OnSettingsChangedListener {

    private CalendarFragment calendarFragment;
    private FragmentManager fragMan; private FragmentTransaction fragTrans;
    private String dateKey;
    private int year, month, day;
    private SharedPreferences sharedPrefSeti, sharedPrefPay;
    private SharedPreferences.Editor editor;
    private PayRollTrack payRollTrack;
    private LocalDate curDate = LocalDate.now(); //2020-07-19

    //todo change tvNextPayNum in calendarFrag to represent the next Pay
    //todo getCurrentPayPeriod then get pay amount for that pay period
    //todo sharedpref payroll num not returning properly
    //todo crashing if settings not set each time

    /*on init function finds the current date to retrieve pay period number and amount passing args to
      a calendar fragment to display next pay period amount
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //access shared preferences
        sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
        dateKey= curDate.getYear() + "-" + curDate.getMonthValue() + "-" + curDate.getDayOfMonth();

        int payRNum = sharedPrefPay.getInt(R.string.payPeriodNumber + dateKey, 0);
       // Log.d("prnumoncreate", String.valueOf(payRNum)); //0
        float payPeriodTotal=
        sharedPrefPay.getFloat(getResources().getString(R.string.payPeriodTotalKey) + payRNum, 0);
        //Log.d("paypertotaloncreate", String.valueOf(payPeriodTotal));// 0.0

        Bundle args = new Bundle();
        args.putString("PayDate", String.valueOf(payPeriodTotal));

        calendarFragment = new CalendarFragment();
        calendarFragment.setArguments(args);
        fragMan = getSupportFragmentManager();
        fragTrans = fragMan.beginTransaction();
        fragTrans
                .add(R.id.flFragment, calendarFragment)
                .commit();
       // Log.d("dateonstart",dateKey); //2020-7-22

    }



    public void onDateSelected(String dateKey) {
        CurrentDateFragment dateFragment = new CurrentDateFragment();
        Bundle args = new Bundle();
        //function to parse month, so Jan will be 1 not 0 when selecting date from Calendar
        String parseDate = dateKey;
        String delims = "-";
        String[] tokens = parseDate.split(delims);
        year = Integer.parseInt(tokens[0]);
        month = Integer.parseInt(tokens[1]) + 1;
        day = Integer.parseInt(tokens[2]);

        //todo what does this key do compared to the other dateKey
        dateKey = year + "-" + month + "-" + day; //2020-7-1
        this.dateKey = dateKey; // need this to compute value

        Toast.makeText(this,dateKey,Toast.LENGTH_LONG).show(); //ok

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

        sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
        sharedPrefSeti = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefSeti), Context.MODE_PRIVATE));

        //values set by user
        String regRate = sharedPrefSeti.getString(getResources().getString(R.string.hourlyRateKey), "0"); //change to non null
       // Log.d("regRate", regRate);
        String otRate = sharedPrefSeti.getString(getResources().getString(R.string.otRateKey), "0");
       // Log.d("otRate",otRate);
        String sickRate = sharedPrefSeti.getString(getResources().getString(R.string.sickPercentKey), "0");
       // Log.d("sickrate",sickRate);
        //payRollTrack for most calculations
        payRollTrack = new PayRollTrack(); //
        payRollTrack.setHourlyRate(Double.parseDouble(regRate));//get this from settins  \/
        payRollTrack.setOverTimeRate(Double.parseDouble(otRate));
        payRollTrack.setSickPay(Double.parseDouble(sickRate));//get this from settings

        //get first and last day of the pay period
        LocalDate dateStart = LocalDate.of(2020, 7, 1); //parse value from settings
        LocalDate dateEnd = LocalDate.of(2020, 7, 31); // parse value from settings

        dateKey = dateStart.getYear() + "-" + dateStart.getMonthValue() + "-" + dateStart.getDayOfMonth(); //returns 2020/7/01;
        //Log.d("checkDate1", dateKey);
        //this appears to work

        //payroll num return 0,0,2
        //will it start at 0 if i change the date
        int payRollNum = sharedPrefPay.getInt(R.string.payPeriodNumber + dateKey,0); // crashed the application a couple times? should be non null value
        //Log.d("payrollnumstart", String.valueOf(payRollNum));
        float sum = 0;
        int daysPerCycle = 14; //get value from settings

         //todo get and set paydate functions
         editor= Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit());
          while (dateStart.isBefore(dateEnd)) {
           for (int i = 0; i < daysPerCycle; i++) {
               saveDayTotal(dateKey); //appears to work
               //sum payperiod total
               sum+=Double.parseDouble(getDayTotal(dateKey));
                //todo fix me
               editor.putInt(R.string.payPeriodNumber + dateKey, payRollNum);
               //Log.d("payrollnumloop", String.valueOf(payRollNum)); //correct
               dateStart = dateStart.plusDays(1);
               dateKey = dateStart.getYear() + "-" + dateStart.getMonthValue() + "-" + dateStart.getDayOfMonth();
              // Log.d("checkdate2", dateKey);
           }
             editor.putFloat(getResources().getString(R.string.payPeriodTotalKey) + payRollNum, sum);
             sum = 0;
             payRollNum++;
          }
            editor.apply();

    }
    //todo check saveDayTotal seems to be crashing because settings not saving to sharepreferences?
    public void onHoursChanged(String dateKey) {
        saveDayTotal(dateKey);
        Toast.makeText(this,String.valueOf(payRollTrack.getDayTotal()),Toast.LENGTH_LONG).show();
    }

    //todo redo this loop if needed
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

      // sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
      // Float check= sharedPrefPay.getFloat(getResources().getString(R.string.payPeriodTotalKey) + payPeriod,0);
      // Toast.makeText(this,"hmmm- " + sum,Toast.LENGTH_LONG).show();

    }


    public int getPayPeriod(String date){
        sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
        return 0;
    }

    //works
    public void saveDayTotal(String dateKey){
        //todo check sharedPref duplicates
        //todo check delete
      // sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));

        String regHours = sharedPrefPay.getString(dateKey + "reg", "0"); //this works String regHoursKey = curDate + "reg";
        String otHours = sharedPrefPay.getString(dateKey + "ot", "0");
        String sickHours = sharedPrefPay.getString(dateKey + "sick", "0");

        payRollTrack.setRegWorked(Double.parseDouble(regHours));
        payRollTrack.setOtWorked(Double.parseDouble(otHours));
        payRollTrack.setSickWorked(Double.parseDouble(sickHours));
        //todo check for editor duplicates
        editor = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit());
        editor.putString("DayTotal" + dateKey, String.valueOf(payRollTrack.getDayTotal())).commit();

    }
    //works -- could change to save a float since its numeric //todo change name since payrolltrack has same method
    public String getDayTotal(String dateKey){
        sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
        String dayTotal =  sharedPrefPay.getString("DayTotal"+ dateKey, "");
        return dayTotal;
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

