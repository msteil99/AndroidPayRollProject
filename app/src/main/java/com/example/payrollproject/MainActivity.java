package com.example.payrollproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity implements CalendarFragment.OnSelectedListener, CurrentDateFragment.OnHoursChangedListener,
        SettingsFragment.OnSettingsChangedListener {

    private CalendarFragment calendarFragment;
    private FragmentManager fragMan; private FragmentTransaction fragTrans;
    private SharedPreferences sharedPrefSeti;
    private SharedPreferences sharedPrefUData;
    private SharedPreferences.Editor edMain;
    private PayRollTrack payRollTrack;
    private float payPerTotal;
    private UserData userData;
    private LocalDate firstLocalDate;
    private Set<String> dateSet;
    private PayListFragment payListFragment;


    //todo on first init of application settings should be applied first or application crashes
    //todo sum total for year in date changed and settings changed
    //todo print days in PayListFragment
    //todo change settings to have a custom hourly amount
    //todo sort paydates in paylist

    /*function gets previous user data for current date and pay period then
      passes to the calendar fragment for display
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //applies all values associated with the current date
        userData = new UserData(LocalDate.now());
        //instantiate previous settings data
        sharedPrefSeti = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefSeti), Context.MODE_PRIVATE));
        sharedPrefUData = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));

        //retrieve current pay period data and pass to fragment
        Bundle args = new Bundle();
        args.putString(getResources().getString(R.string.payPeriodTotalKey), String.valueOf(userData.getPayRollTotal()));
        args.putString(getResources().getString(R.string.payDateKey),userData.getPayDate());

        calendarFragment = new CalendarFragment();
        calendarFragment.setArguments(args);
        fragMan = getSupportFragmentManager();
        fragTrans = fragMan.beginTransaction();
        fragTrans
                .add(R.id.flFragment, calendarFragment)
                .commit();
    }

    //once user selects date in calendar pass the specific datekey to fragment
    public void onDateSelected(String dateKey) {
        CurrentDateFragment dateFragment = new CurrentDateFragment();
        Bundle args = new Bundle();
        args.putString("dateKey", dateKey);
        dateFragment.setArguments(args);

        //replace current fragment with dateFragment, adding transaction to backstack allows user to undo when pressing back button
        fragTrans = getSupportFragmentManager().beginTransaction();
        fragTrans
                .replace(R.id.flFragment, dateFragment)
                .addToBackStack(null)
                .commit();
    }

    public void onPayListSelected(){
        payListFragment = new PayListFragment();
        fragTrans = getSupportFragmentManager().beginTransaction();
        fragTrans
                .replace(R.id.flFragment, payListFragment)
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

    public void onSettingsChanged() {
        //retrieve any previous user data
        aplyPrevSeti();
        //set first Pay period *do not place in aplyPrevSeti or large loop
        String firstDate= sharedPrefSeti.getString(getResources().getString(R.string.firstPayPerKey),"2020/01/01");
        setFirstPayPer(firstDate);
        LocalDate dateEnd = LocalDate.of(2022, 12, 25); // parse value from settings
        //reset user data to start date, iterate and apply new values
        userData = new UserData(firstLocalDate);
        int payRollNum = userData.getPayRollNum();
        dateSet = new TreeSet<>();

        //sum total for payroll
        float sum = 0;
        int daysPerCycle = 14; //get value from settings
        edMain= Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit());
        while (firstLocalDate.isBefore(dateEnd)) {
            for (int i = 0; i < daysPerCycle; i++) {
                saveDayTotal(userData.getDateKey()); //saves and track hours worked for the day
                sum+=Float.parseFloat(getDayTotal(userData.getDateKey())); //sum each day in pay period
                edMain.putInt(getResources().getString(R.string.payPeriodNumKey) + userData.getDateKey(),  payRollNum);//payrollnum associated with date
                dateSet.add(userData.getDateKey()); //add and save all dates associated with pay period to  a set

                firstLocalDate = firstLocalDate.plusDays(1);
                userData.setDateKey(firstLocalDate);
            }
            edMain.putString(getResources().getString(R.string.payDateKey) + payRollNum, userData.getDateKey()); //save paydate for corresponding pay period
            edMain.putFloat(getResources().getString(R.string.payPeriodTotalKey) + payRollNum, sum); //save pay total for corresponding pay period
            edMain.putStringSet(getResources().getString(R.string.datesWorkedKey) + userData.getDateKey(), dateSet);//save the list of dates associated with pay period with paydate as key
            dateSet = new TreeSet<>();
            sum = 0;
            payRollNum++;
        }
        edMain.apply();

        //pass text and current view
        customToast("Saved", this.findViewById(android.R.id.content));
    }
    //todo Toast to show day total saved
    //todo save all values needed for each date...
    public void onHoursChanged(String dateKey) {

        userData = new UserData(dateKey);

        float prevDay = Float.parseFloat(getDayTotal(dateKey)); // previous total from the day
        saveDayTotal(userData.getDateKey());

        float payRollTotal = userData.getPayRollTotal() - prevDay + Float.parseFloat(getDayTotal(dateKey));
        savePayRollTotal(dateKey,payRollTotal);

        //reset userData to display correct amount, save new String printout of payRoll and send to PayListFrag
        userData = new UserData(dateKey);
        updatePayListFrag();

        //update cur pay period value in calendar screen
        if(userData.getPayRollNum() == new UserData(LocalDate.now()).getPayRollNum()){
            Bundle args = new Bundle();
            args.putString(getResources().getString(R.string.payPeriodTotalKey), String.valueOf(payRollTotal));
            args.putString(getResources().getString(R.string.payDateKey), userData.getPayDate());
            calendarFragment.setArguments(args);
        }
        //replace fragment
        fragMan = getSupportFragmentManager();
        fragMan.popBackStack();
        fragTrans = fragMan.beginTransaction();
        fragTrans.replace(R.id.flFragment, calendarFragment).commit();

        //pass text and current view
        customToast(getDayTotal(dateKey) + " Saved", this.findViewById(android.R.id.content));
    }

    //paylist will be printed in paylist fragment, may be better to add all in Settings to ensure sorted properly, frm pay start to pay end
    //this would avoid the reinit
    public void updatePayListFrag(){
        Set<String> dates = new TreeSet<>();
        dates= sharedPrefUData.getStringSet(getResources().getString(R.string.payDatesKey),dates);
        dates.add(userData.getPayDate());

        SharedPreferences.Editor editor = sharedPrefUData.edit();
        editor.putStringSet(getResources().getString(R.string.payDatesKey), dates);

        editor.apply();
    }


    //creates an array of date values in year/month/day and passes to LocalDate object
    public void setFirstPayPer(String firstDate){
        int count = 0;
        int[] array = new int[3];

        Scanner scan = new Scanner(firstDate);
        scan.useDelimiter("/");

        while(scan.hasNext()){
            array[count] = Integer.parseInt(scan.next());
            Log.d("firstpayperiodnums", String.valueOf(array[count]));
            count++;
        }
        scan.close();

        firstLocalDate= LocalDate.of(array[0],array[1],array[2]);
    }

    /*function to save total for the day, gets values from settings and uses PayRollTrack object to
     * compute the daily value, saves new day to Shared Pref*/
    public void saveDayTotal(String dateKey){
        SharedPreferences.Editor edSaveDay;

        aplyPrevSeti();

        String regHoursKey = getResources().getString(R.string.regHoursKey) + dateKey;
        String otHoursKey =  getResources().getString(R.string.otHoursKey) + dateKey;
        String sickHoursKey = getResources().getString(R.string.sickHoursKey) + dateKey;


        String regHours = sharedPrefUData.getString(regHoursKey, "0");
        Log.d("regHoursnores", String.valueOf(R.string.regHoursKey));
        Log.d("reghoursres",getResources().getString(R.string.regHoursKey) + dateKey);
        String otHours = sharedPrefUData.getString(otHoursKey, "0");
        String sickHours = sharedPrefUData.getString(sickHoursKey, "0");

        payRollTrack.setRegWorked(Float.parseFloat(Objects.requireNonNull(regHours)));
        payRollTrack.setOtWorked(Float.parseFloat(Objects.requireNonNull(otHours)));
        payRollTrack.setSickWorked(Float.parseFloat(Objects.requireNonNull(sickHours)));

        edSaveDay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit());
        edSaveDay.putString(getResources().getString(R.string.dayTotalKey) + dateKey, payRollTrack.getDayTotal()).commit();

    }


    public void savePayRollTotal(String dateKey, float amount){
        SharedPreferences.Editor edSavePR;
        edSavePR = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit());

        UserData savePayRoll = new UserData(dateKey);
        edSavePR.putFloat(getResources().getString(R.string.payPeriodTotalKey) + savePayRoll.getPayRollNum(), amount).commit();
    }

    public String getDayTotal(String dateKey){
        return sharedPrefUData.getString(getResources().getString(R.string.dayTotalKey)+ dateKey, ""); //todo crashes if key changed
    }

    /*get rates from settings and apply to payrolltrack for numerical functions  */
    public void aplyPrevSeti(){
        //value set by user
        String regRate = sharedPrefSeti.getString(getResources().getString(R.string.hourlyRateKey), "0"); //change to non null

        payRollTrack = new PayRollTrack();
        payRollTrack.setHourlyRate(Float.parseFloat(Objects.requireNonNull(regRate)));
        payRollTrack.setOverTimeRate(1.5f);
        payRollTrack.setPayPercent(75);
    }

    public void onBackPressed() {
        // all other activities should return to the calendar screen
        if (!calendarFragment.isVisible()) {
            fragMan.popBackStack();
            fragTrans = fragMan.beginTransaction();
            fragTrans.replace(R.id.flFragment, calendarFragment).commit();
        }
    }

    //custom toast to display near the bottom of the screen
    public void customToast(String text , View v){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_custom,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView t = layout.findViewById(R.id.toastText);
        t.setText(text);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, v.getHeight());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /*Inner class to retrieve user data for the selected date*/
    //todo check if setting data needs to be done at all
    class UserData{

        private String dateKey,payDate;
        private int payRollNum;
        private float payRollTotal;
        private  SharedPreferences sharedPrefPay;

        public UserData(LocalDate date){
            sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
            setData(date);
        }

        public UserData(String dateKey){
            sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
            setData(dateKey);
        }


        private String getDateKey(){
            return dateKey;
        }

        private void setDateKey(String dateKey){
            this.dateKey = dateKey;
        }

        private void setDateKey(LocalDate date) {
            this.dateKey = date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth(); //getDateKey(Local Date)
        }

        private int getPayRollNum(){
            return payRollNum;
        }

        private void setPayRollNum(int payRollNum){
            this.payRollNum = payRollNum;
        }

        private float getPayRollTotal(){
            return payRollTotal;
        }

        private void setPayRollTotal(float payRollTotal){
            this.payRollTotal = payRollTotal;
        }

        private String getPayDate(){
            return payDate;
        }

        private void setPayDate(String payDate){
            this.payDate = payDate;
        }
        //sets all other data related to the date
        private void setData(String dateKey){
            setDateKey(dateKey);
            payRollNum =
                    sharedPrefPay.getInt(getResources().getString(R.string.payPeriodNumKey) + dateKey, 0);
            setPayRollNum(payRollNum);
            payPerTotal=
                    sharedPrefPay.getFloat(getResources().getString(R.string.payPeriodTotalKey) + payRollNum, 0);
            setPayRollTotal(payPerTotal);
            payDate=
                    sharedPrefPay.getString(getResources().getString(R.string.payDateKey)+payRollNum,"Date");
            setPayDate(payDate);
        }

        private void setData(LocalDate date){
            setDateKey(dateKey = date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth());
            payRollNum =
                    sharedPrefPay.getInt(getResources().getString(R.string.payPeriodNumKey) + dateKey, 0);
            setPayRollNum(payRollNum);
            payPerTotal=
                    sharedPrefPay.getFloat(getResources().getString(R.string.payPeriodTotalKey) + payRollNum, 0);
            setPayRollTotal(payPerTotal);
            payDate=
                    sharedPrefPay.getString(getResources().getString(R.string.payDateKey)+ payRollNum,"");
            setPayDate(payDate);
        }
    }
}