package com.example.payrollproject;

import java.text.DecimalFormat;


public class PayRollTrack {

    private float hourlyRate,otRate,otDoubleRate;
    private float sickPay, dayTotal;
    private float regWorked, otWorked, otDoubleWorked, sickWorked;

    public PayRollTrack(){ }

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

    public void setDoubleTimeRate(){ otDoubleRate = 2.0f * hourlyRate;}
    public float getOtDoubleRate(){return 2.0f * hourlyRate; }

    //this will be reg worked per day
    public void setRegWorked(float regWorked){ this.regWorked = regWorked; }
    public float getRegWorked(){ return regWorked; }

    public void setOtWorked(float otWorked){ this.otWorked = otWorked; }
    public float getOtWorked(){ return otWorked; }
    //return the hours of OT worked in a day

    public void setOtDouble(float otDoubleWorked){this.otDoubleWorked = otDoubleWorked; }
    public float getOtDouble(){ return otDoubleWorked; }

    public float getSickWorked(){ return sickWorked; }

    public void setSickWorked(float sickWorked){ this.sickWorked = sickWorked; }

    public String getDayTotal(){
        DecimalFormat df = new DecimalFormat(".##");

        dayTotal =  (regWorked * hourlyRate)+(otWorked * otRate)+(sickPay * sickWorked) + (otDoubleWorked * otDoubleRate);
        return df.format(dayTotal);
    }
}





