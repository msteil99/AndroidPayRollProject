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
    private FragmentManager fragMan; private FragmentTransaction fragTrans;
    private String dateKey;
    private int year,month,day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_main);

     calendarFragment = new CalendarFragment();
        fragMan = getSupportFragmentManager();
        fragTrans = fragMan.beginTransaction();
        fragTrans.add(R.id.flFragment,calendarFragment);
        fragTrans.commit();
         //below is a test
        //dateStart =  LocalDate.of(2020, 3, 1); //parse value from settings
        //String checker =   dateKey = dateStart.getYear() + "/" + dateStart.getMonthValue() + "/" + dateStart.getDayOfMonth();

        //Toast.makeText(this, checker , Toast.LENGTH_LONG).show();
    }


  public void onDateSelected(String dateKey){

      CurrentDateFragment dateFragment = new CurrentDateFragment();
      Bundle args = new Bundle();
      //parse date key month +1 assigning value 1 to January instead of 0
      String parseDate = dateKey;
      String delims = "/"; // so the delimiters are:  + - * / ^ space
      String[] tokens = parseDate.split(delims);

      year = Integer.parseInt(tokens[0]);
      month = Integer.parseInt(tokens[1])+1; //plus one to set January value to 1
      day = Integer.parseInt(tokens[2]);


      dateKey = year + "/" + month + "/" + day; // not sure why I have to do this
      this.dateKey = dateKey; // need this to compute value

      //sharedPreferences =  getSharedPreferences("MyPref" ,Context.MODE_PRIVATE);
      //sharedPreferences.getString("hourlyRateKey","0");

      args.putString("dateKey", dateKey);

      /* this works
      sharedPreferences =  getSharedPreferences("M y P r e f s" ,Context.MODE_PRIVATE);
      args.putString("dateKey", sharedPreferences.getString("hourlyRateKey",""));
      dateFragment.setArguments(args);
     */

      dateFragment.setArguments(args);
      fragTrans = getSupportFragmentManager().beginTransaction();
      // Replace whatever is in the fragment_container view with this fragment,
      // and add the transaction to the back stack so the user can navigate back
      fragTrans.replace(R.id.flFragment, dateFragment);
      fragTrans.addToBackStack(null);

      // Commit the transaction
      fragTrans.commit();
    }

  public void onSettingsSelected(){
      SettingsFragment settingsFragment = new SettingsFragment();
      fragTrans = getSupportFragmentManager().beginTransaction();
      fragTrans.replace(R.id.flFragment, settingsFragment);
      fragTrans.addToBackStack(null);
      fragTrans.commit();
    }

    //could switch all keys to an ENUM class for the settings function
    public void onSettingsChanged(){

        //get the first pay date and dates to iterate through
         LocalDate dateStart = LocalDate.of(2020, 7, 1); //parse value from settings
          int daysPerCycle = 14; //get value from settings
          int regHoursPDay = 7;
          String date;
          String regHoursKey;
         //dateKey needs to be seperate or it will change for the
         //this iteration applys reg hours from 1 to 14 day
         while(dateStart.isBefore(LocalDate.now())) {
             for (int i = 0; i < daysPerCycle; i++) {
                 //instantiate values with regHours from settings
                date = dateStart.getYear() + "/" + dateStart.getMonthValue() + "/" + dateStart.getDayOfMonth(); //returns 2020/07/01
                regHoursKey = date + "reg";
                SharedPreferences.Editor editor = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefKey), Context.MODE_PRIVATE).edit());
                editor.putString(regHoursKey, String.valueOf(regHoursPDay));
                editor.apply();
                dateStart = dateStart.plusDays(1);
            }
            //savePayPeriod(frm,to)
             //claucate values from to and save
         }

        //save PayperiodValue()
        //getPayperiod(date)//returns the payperiod it belongs to


       // dateStart = dateStart.plusDays(1)//iterate to next paydate

        //basically I need to have a start date to iterate through 14 days marking the values of the 14 days with
        //a value say the startDate which will be the key for the values, so basically all I Need to compute
        //each iteration is the startDates, so for payperiod at startDate until currentDate
        //if I loop unitl current date it should stop at the last payperiod then need to find a ways to add all the values form that
        //one pay period - sta
    }

    public void onHoursChanged(){

        SharedPreferences sharedPreferences = Objects.requireNonNull(getSharedPreferences(getResources().getString(R.string.prefKey), Context.MODE_PRIVATE)); // still crashes

         String regRate = sharedPreferences.getString(getResources().getString(R.string.hourlyRateKey), "0"); //change to non null
         String otRate = sharedPreferences.getString(getResources().getString(R.string.otRateKey),"0");
         String sickRate = sharedPreferences.getString(getResources().getString(R.string.sickPercentKey),"0");

         //construct below in a onSettingsChanged Listener
        PayRollTrack payRollTrack = new PayRollTrack(); //
         payRollTrack.setHourlyRate(Double.parseDouble(regRate));//get this from settins  \/
         payRollTrack.setOverTimeRate(Double.parseDouble(otRate));
         payRollTrack.setSickPay(Double.parseDouble(sickRate));//get this from settings

          //probably stops here 0 value
         String regHours = sharedPreferences.getString(dateKey + "reg", "0"); //this works String regHoursKey = curDate + "reg";
         String otHours = sharedPreferences.getString(dateKey+ "ot","0");
         String sickHours = sharedPreferences.getString(dateKey+ "sick","0");


        payRollTrack.setRegWorked(Double.parseDouble(regHours));
        payRollTrack.setOtWorked(Double.parseDouble(otHours));
        payRollTrack.setSickWorked(Double.parseDouble(sickHours));

        //use shared preferences to save day total
          payRollTrack.getDayTotal();

        Toast.makeText(this, String.valueOf(payRollTrack.getDayTotal()) , Toast.LENGTH_LONG).show();
    }

    public void onBackPressed(){
       // all other activities should return to the calendar screen
        if(!calendarFragment.isVisible()){
            fragMan.popBackStack();
            fragTrans = fragMan.beginTransaction();
            fragTrans.replace(R.id.flFragment,calendarFragment);
            fragTrans.commit();
        }
    }

    public void iteratePayPeriod(LocalDate dateStart, LocalDate dateEnd, int daysPerCycle){

         if(dateEnd.isBefore(dateStart))
             //throw an error message


            while(dateStart.isBefore(dateEnd)){
             for(int i = 0; i<daysPerCycle; i++){


                dateStart = dateStart.plusDays(1);
            }
            System.out.println("New Loop " + dateStart.toString());
            //save vlaue (key, total4PayPeriod)
        }
     }

     //iterate each pay day from the startDate specified in settings to the end date specified, this way when the user changes
    // they can change their hourly amount and start date and any point they want.

    // all this iteration should do for now is create a payroll number for each pay date
    //loop (date,key1),(date1,key1)(date2,key1)(date3,key2)
    //once that is finished the main calendar application can get the current date at start and apply the curPayPeriod
    //value on start. In the date selected fragment the user can select a date and when applied it saves the value/values
    //of that date in a new keyvalue pair, date - gets the payperiod #

    //need a get current payperiord(date) return pay period
    //maybe a a set dateTotal(date,hourly,ot,sick)
    //get payperiodvalues(curDate, nextPayperiodTotal)

    /*
    *   Settings - 14 days cycle
    *   Settings - startDate
    *   Automate - startDate to current Date Saving each payperiod
    *   Save - Key/Value Key is PayPeriod dates Key-StartDate, Value- Amount Paid
    *
    * */




}




 /*to do - have a seperate xml that pulls up all previous pay period dates
 * When changing current date pay period it has to recalculate the values for the payperiod
 * */



/* now I need to store and add all current dates within my date range from setting
   basically create an intent in setting that the main activity can extract from

   I can the compute the daily value from the day

   add that value to an array that is 14 days long within a given range

   once that value has been
*/
        /* LocalDate class i wll take the date from the key provided by settings and iterate to the next pay date, thinking that
         having a if(LocalDate.current > LocalDateend restart iteration. This will iterate the payroll every two weeks)

         How should I get the pay period using local date I will get the first pay period from the user in
         2020/01/20 format the algorithm will then insantiate
         */
        /*

//next step is to iterate through the calendar and instantiate with a value
//iterate through dates with key/value pair
//i need to sum the values in differnet areas - when settings has changed or when currentdate is changed
//maybe have the listener from main activity if either is changed then sum the values of that payperiod that
//the change lied on
         */

/*create the class that takes the key/value pair of the calendar
  and setsValues to that date, getsValues from that date etc
  //pass the calendar object in amethod(Calendar calen, value)
   calen.getDate
   value = value
   set value

   // Java class can get the insatntiated calendars start and end
    // once it has that it knows how long to ierate the value
    // so I will have to pass the calendar object which gets date from and to
    then I can say from start date to end date insantiate a value every 14 days that is the payperiod
*

      /*date Format = 2020/01/01
        so in order to populate a value to the specific date in my calendar I would need to
        create a class that specifes values to dates, it has to be aware of the year month , day etc

      */



