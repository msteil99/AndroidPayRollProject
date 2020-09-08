package com.example.payrollproject;

import android.icu.util.LocaleData;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.text.DecimalFormat;
import java.time.LocalDate;

public class PayRollTrack {

    private float hourlyRate,otRate;
    private float sickPay, dayTotal;
    private float regWorked, otWorked, sickWorked;
    //todo function to say if setPayPeriod, getPayPeriod, isCurrentPayPeriod(LocalDate date).


    public PayRollTrack(){}

    //set/get user income per hour
    public void setHourlyRate(float hourlyRate){
        this.hourlyRate = hourlyRate;
    }
    public float getHourlyRate(){
        return hourlyRate;
    }

    //percentage of pay
    public void setPayPercent(float percentage){
        this.sickPay = percentage/100 * hourlyRate;
    }
    public float getPayPercent(){
        return sickPay/100 * hourlyRate;
    }

    //take a number to mulitple reg hour rate with
    public void setOverTimeRate(float multiple){
        otRate = multiple * hourlyRate;
    }
    public float getOverTime(){
        return otRate;
    }

    //this will be reg worked per day
    public void setRegWorked(float regWorked){
        this.regWorked = regWorked;
    }
    public float getRegWorked(){
        return regWorked;
    }

    public void setOtWorked(float otWorked){
        this.otWorked = otWorked;
    }
    public float getOtWorked(){
        return otWorked;
    }
    //return the hours of OT worked in a day

    public float getSickWorked(){
        return sickWorked;
    }
    public void setSickWorked(float sickWorked){
        this.sickWorked = sickWorked;
    }

    public float getDayTotal(){
        dayTotal =  Math.round((regWorked * hourlyRate)+(otWorked * otRate)+(sickPay * sickWorked));
        return dayTotal;
    }
}





