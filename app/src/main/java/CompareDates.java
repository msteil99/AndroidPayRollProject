import java.time.LocalDate;

public class CompareDates implements Comparable<CompareDates> {


    private LocalDate dateTime;

    public CompareDates(){}

    public CompareDates(LocalDate dateTime){
        this.dateTime = dateTime;
    }

    public CompareDates(int year, int month, int day){
        this.dateTime = dateTime.of(year,month,day);
    }

    public LocalDate getDateTime() {
        return dateTime;
    }

    public void setDateTime(int year, int month, int day) {
        this.dateTime = dateTime.of(year,month,day);
    }

    public void setDateTime(LocalDate dateTime){
        this.dateTime = dateTime;
    }

    @Override
    public int compareTo(CompareDates o) {
        return getDateTime().compareTo(o.getDateTime());
    }

 }


