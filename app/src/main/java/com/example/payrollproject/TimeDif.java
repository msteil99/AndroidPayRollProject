package com.example.payrollproject;

/*
*   this is a helper class that calculates the difference between times in 24 hour format producing
*   decimal where 1 = 1 hour. All range checks are done in CurrentDateFragment.java
*
* */


public class TimeDif {

    private int frmHour, toHour;
    private int frmMin, toMin;
    private double hourFinal = 0;
    private double minFinal = 0;

    public TimeDif(int frmHour, int frmMin, int toHour, int toMin){
     this.frmHour = frmHour; this.toHour = toHour;
     this.frmMin = frmMin; this.toMin = toMin;
     hourDif();
     minDif();
    }

    public double getHourDif(){
        return hourFinal + minFinal;
    }

    private void hourDif(){
     while(frmHour != toHour){
      hourFinal++;
      frmHour++;
      if(frmHour >= 24)
       frmHour = 0;
      }
    }

    private void minDif(){
     while(frmMin != toMin){
      if(toMin > frmMin){
       toMin --;
       minFinal++;
      }
      if(frmMin > toMin){
       frmMin--;
       minFinal++;
      }
     }
     minFinal /= 60;
    }

}
