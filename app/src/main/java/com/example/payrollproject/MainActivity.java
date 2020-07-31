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
    private String dateKey, payDate;
    private SharedPreferences sharedPrefSeti;
    private SharedPreferences sharedPrefPay;
    private SharedPreferences.Editor edMain;
    private PayRollTrack payRollTrack;
   // private int payRollNum;
    private float payPerTotal;
    private UserData userData;
   // private LocalDate curDate = LocalDate.now(); //2020-07-19

    //todo change tvNextPayNum in calendarFrag to represent the next Pay
    //todo create get/set for most fuctions
    //todo get key object to take a LocalDate object as a parameter and return dateKey string
    //todo set user data constructor with datekey string in header and set all values

    /*on init function finds the current date to retrieve pay period number and amount passing args to
      a calendar fragment to display next pay period amount
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userData = new UserData(LocalDate.now());
        //todo get the next paydate from
        sharedPrefSeti = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefSeti), Context.MODE_PRIVATE));

        Log.d("innerclass", userData.getDateKey());//works
        Log.d("prnumoncreate", String.valueOf(userData.getPayRollNum())); //0
        Log.d("paypertotaloncreate", String.valueOf(userData.getPayRollTotal()));// 0.0

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

    public void onDateSelected(String dateKey) {
        CurrentDateFragment dateFragment = new CurrentDateFragment();
        Bundle args = new Bundle();

        // parse month+1 so January = 1
        String parseDate = dateKey;
        String delims = "-";
        String[] tokens = parseDate.split(delims);

        int year = Integer.parseInt(tokens[0]);
        int month = Integer.parseInt(tokens[1]) + 1;
        int day = Integer.parseInt(tokens[2]);

        dateKey = year + "-" + month + "-" + day; //2020-7-1
        this.dateKey = dateKey; //change current dateKey to selected date

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
        //function retrieves previous user data
        getPayRollSeti();
        //first and last day of the pay period
        LocalDate dateStart = LocalDate.of(2020, 7, 10); //parse value from settings
        LocalDate dateEnd = LocalDate.of(2020, 12, 25); // parse value from settings

        userData = new UserData(dateStart);
        int payRollNum = userData.getPayRollNum();
       // setDateKey(dateStart); //returns 2020/7/01;
       // setPayRollNum(getDateKey());//
        Log.d("payrollnumstart", String.valueOf(payRollNum));

        //sum total for payroll //todo sum total for year
        float sum = 0;
        int daysPerCycle = 14; //get value from settings
        //todo get and set paydate functions
        edMain= Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit());
        while (dateStart.isBefore(dateEnd)) {
            for (int i = 0; i < daysPerCycle; i++) {
                saveDayTotal(userData.getDateKey()); //saves and track hours worked for the day
                sum+=Double.parseDouble(getDayTotal(userData.getDateKey())); //sum each day in pay period
                edMain.putInt(getResources().getString(R.string.payPeriodNumKey) + userData.getDateKey(),  payRollNum); //payrollnum associated with date
                Log.d("payrollnumloop", String.valueOf(payRollNum)); //correct
                dateStart = dateStart.plusDays(1);
                userData.setDateKey(dateStart); //what happens when dateKey is set
                Log.d("checkdate2", userData.getDateKey());
            }
            edMain.putString(getResources().getString(R.string.payDateKey) + payRollNum, userData.getDateKey()); //set paydate for corresponding pay period
            edMain.putFloat(getResources().getString(R.string.payPeriodTotalKey) + payRollNum, sum); //set pay total for corresponding pay period
            Log.d("setSum", String.valueOf(sum));
            sum = 0;
            payRollNum++;
        }
        edMain.apply();
    }


    //how could I iterate and update a pay period when the user changes a specific date.
    //i need to find the first day of the payperiod and the last day

    public void onHoursChanged(String dateKey) {
        userData = new UserData(dateKey);

        float prevDay = Float.parseFloat(getDayTotal(dateKey)); // previous total from the day
        Log.d("hcprevDay", String.valueOf(prevDay));
        saveDayTotal(userData.getDateKey());
        Log.d("hcnewDay",getDayTotal(dateKey));

        float payRollTotal = userData.getPayRollTotal() - prevDay + Float.parseFloat(getDayTotal(dateKey)); //todo payPerTotal in helper class
        savePayRollTotal(dateKey,payRollTotal);

        //reset user data here, this should all be done in userdata class
        userData = new UserData(dateKey);

        //todo save new payperiod total

        Log.d("hcNewTotal ", String.valueOf(payRollTotal));

        //todo fix me
        //update next pay value if current pay roll changed, reCalcualate pay roll
        if(userData.getPayRollNum() == new UserData(LocalDate.now()).getPayRollNum()){ //change
            Bundle args = new Bundle();
            args.putString(getResources().getString(R.string.payPeriodTotalKey), String.valueOf(payRollTotal));
            args.putString(getResources().getString(R.string.payDateKey), userData.getPayDate());
            calendarFragment.setArguments(args);
            fragMan = getSupportFragmentManager();
            fragTrans = fragMan.beginTransaction();
            fragTrans
                    .add(R.id.flFragment, calendarFragment)
                    .commit();
        }
        Toast.makeText(this,userData.getPayRollTotal() + " New PayPer Total " ,Toast.LENGTH_LONG).show();
    }

    /*function to save total for the day, gets all previous values from settings and uses PayRollTrack object to
     * compute the daily value*/
    //todo create seperate save day function for settings
    public void saveDayTotal(String dateKey){
        SharedPreferences.Editor edSaveDay;

        getPayRollSeti();

        String regHours = sharedPrefPay.getString(dateKey + "reg", "0"); //this works String regHoursKey = curDate + "reg";
        String otHours = sharedPrefPay.getString(dateKey + "ot", "0");
        String sickHours = sharedPrefPay.getString(dateKey + "sick", "0");

        payRollTrack.setRegWorked(Double.parseDouble(Objects.requireNonNull(regHours)));
        payRollTrack.setOtWorked(Double.parseDouble(Objects.requireNonNull(otHours)));
        payRollTrack.setSickWorked(Double.parseDouble(Objects.requireNonNull(sickHours)));

        edSaveDay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit());
        edSaveDay.putString("DayTotal" + dateKey, String.valueOf(payRollTrack.getDayTotal())).commit();
    }

    public void savePayRollTotal(String dateKey, float amount){
      SharedPreferences.Editor edSavePR;
      edSavePR = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE).edit());

      UserData savePayRoll = new UserData(dateKey);
      edSavePR.putFloat(getResources().getString(R.string.payPeriodTotalKey) + savePayRoll.getPayRollNum(), amount).commit();
    }

    //works -- could change to save a float since its numeric //todo change name since payrolltrack has same method
    public String getDayTotal(String dateKey){
        return sharedPrefPay.getString("DayTotal"+ dateKey, "");
    }

    /*Function to get previous payroll setting or set new ones, adding values to PayRollTrack class to
     * compute daily value, pay period totals etc  */
    public void getPayRollSeti(){
        //values set by user
        String regRate = sharedPrefSeti.getString(getResources().getString(R.string.hourlyRateKey), "0"); //change to non null
        Log.d("regRate", Objects.requireNonNull(regRate));
        String otRate = sharedPrefSeti.getString(getResources().getString(R.string.otRateKey), "0");
        Log.d("otRate", Objects.requireNonNull(otRate));
        String sickRate = sharedPrefSeti.getString(getResources().getString(R.string.sickPercentKey), "0");
        Log.d("sickrate", Objects.requireNonNull(sickRate));

        payRollTrack = new PayRollTrack(); //
        payRollTrack.setHourlyRate(Double.parseDouble(Objects.requireNonNull(regRate)));//get this from settins  \/
        payRollTrack.setOverTimeRate(Double.parseDouble(Objects.requireNonNull(otRate)));
        payRollTrack.setPayPercent(Double.parseDouble(Objects.requireNonNull(sickRate)));//get this from settings
    }

    public void onBackPressed() {
        // all other activities should return to the calendar screen
        if (!calendarFragment.isVisible()) {
            fragMan.popBackStack();
            fragTrans = fragMan.beginTransaction();
            fragTrans.replace(R.id.flFragment, calendarFragment).commit();
        }
    }

    /*Inner class to set all data*/
 //todo for this program to work, once one of these is set all of them should be
  class UserData{

    private String dateKey,payDate;
    private int payRollNum;
    private float payRollTotal;


    public UserData(LocalDate date){
      sharedPrefPay = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefPayRoll), Context.MODE_PRIVATE));
      setDateKey(dateKey = date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth()); //getDateKey(Local Date)
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

    public UserData(String dateKey){
        setDateKey(dateKey); //todo format check
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
  }

}
