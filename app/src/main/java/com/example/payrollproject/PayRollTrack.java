package com.example.payrollproject;

import android.icu.util.LocaleData;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.text.DecimalFormat;
import java.time.LocalDate;

public class PayRollTrack {

    private double hourlyRate,otRate;
    private double regHours,otHours;
    private double sickPay, dayTotal;
    private double regWorked, otWorked, sickWorked;
    private LocalDate frmDate, toDate;
    //todo function to say if setPayPeriod, getPayPeriod, isCurrentPayPeriod(LocalDate date).


    public PayRollTrack(){}

    //set/get user income per hour
    public void setHourlyRate(double hourlyRate){
        this.hourlyRate = hourlyRate;
    }
    public double getHourlyRate(){
        return hourlyRate;
    }

    //percentage of pay
    public void setPayPercent(double percentage){
        this.sickPay = percentage/100 * hourlyRate;
    }
    public double getPayPercent(){
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

    public void setOtWorked(double otWorked){
        this.otWorked = otWorked;
    }
    public double getOtWorked(){
        return otWorked;
    }
    //return the hours of OT worked in a day

    public double getSickWorked(){
        return sickWorked;
    }
    public void setSickWorked(double sickWorked){
        this.sickWorked = sickWorked;
    }

    public double getDayTotal(){
        return (regWorked * hourlyRate)+(otWorked * otRate)+(sickPay * sickWorked);
    }
}





