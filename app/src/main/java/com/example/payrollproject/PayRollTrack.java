package com.example.payrollproject;

import android.icu.util.LocaleData;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.time.LocalDate;

public class PayRollTrack {

    private double hourlyRate,otRate;
    private double regHours,otHours;
    private double sickPay, dayTotal;
    private double regWorked, otWorked, sickWorked;
    private LocalDate frmDate, toDate;


    public PayRollTrack(){}

    //set user income per hour
    public void setHourlyRate(double hourlyRate){
        this.hourlyRate = hourlyRate;
    }

    //return user income per hour
    public double getHourlyRate(){
        return hourlyRate;
    }

    //this method sets the percentage of pay if user has sick rate
    public void setSickPay(double percentage){
        this.sickPay = percentage/100 * hourlyRate;
    }

    public double getSickPay(){
        return sickPay/100 * hourlyRate;
    }

    //take a number to mulitple reg hour rate with
    public void setOverTimeRate(double multiple){
        otRate = multiple * hourlyRate;
    }

    public double getOverTime(){
        return otRate;
    }

    //this will be reg worked per day
    public void setRegWorked(double regWorked){
        this.regWorked = regWorked;
    }

    public double getRegWorked(){
        return regWorked;
    }

    public double getOtWorked(){
        return otWorked;
    }
    //return the hours of OT worked in a day
    public void setOtWorked(double otWorked){
        this.otWorked = otWorked;
    }

    public double getSickWorked(){
        return sickWorked;
    }

    public void setSickWorked(double sickWorked){
        this.sickWorked = sickWorked;
    }

    public double getDayTotal(){
        return (regWorked * hourlyRate)+(otWorked * otRate)+(sickPay * sickWorked);
    }

    //maybe can parse the key
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void iterateDates(int frmYear, int frmMonth, int frmDay, int numDays, int toDay, int toMonth, int toYear){

         frmDate = LocalDate.of(frmYear,frmMonth,frmDay);
         toDate = LocalDate.of(toYear,toMonth,toDay);

        while(frmDate.isBefore(toDate)) {
            for (int i = 0; i < numDays; i++) {
                frmDate = frmDate.plusDays(1);
            }
        }



        }


}


