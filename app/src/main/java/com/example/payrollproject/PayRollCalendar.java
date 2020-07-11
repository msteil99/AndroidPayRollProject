package com.example.payrollproject;

import android.widget.CalendarView;

import java.util.Calendar;


public class PayRollCalendar {

    private CalendarView calendarView;
  //  private Calendar calendar;
    private String date;


    //add a key -- if key not used -- class instantiate all variables to 0, if the key has been used
    //get the values already applied to that key.

    //CurrentDate frag -> CalenderTrack - add hours, add reg hours, add ot
    //

    public PayRollCalendar(CalendarView calendarView){
     this.calendarView = calendarView;
    // calendar = Calendar.getInstance();

    }

    //this method will take the current milliseconds and convert that into a String in year/month/day
    public String getDate(){
        //calendar.setTimeInMillis(calendarView.getDate());
        //calendar.get(Calendar.DAY_OF_MONTH);
        //calendar.get(Calendar.DAY_OF_WEEK);
        //calendar.get(Calendar.YEAR);
        return date;
    }

    public void iterateCalendar(String dateKey){

       //maybe instead of iterating I could see what happens when I just add each one manulally
        //with expected pay in end to be calculated
        //once thats done Ill try and iterate autmatically the regular hours etc.

    }




}
