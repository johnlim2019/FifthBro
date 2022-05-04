package com.example.testbro;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimePeriod {
    public long start, end;
    public long duration;

    TimePeriod(){}

    TimePeriod(Date st, Date en){
        this.start =st.getTime();
        this.end = en.getTime();
        this.duration = en.getTime() - st.getTime();
    }

    public long getStart() {
        return this.start;
    }

    public long getEnd() {
        return this.end;
    }

    public Date retStart(){
        return new Date(start);
    }

    public void setStart(long st) {
        this.start = st;
    }

    public void setEnd(long en) {
        this.end = en;
    }
    public Date retEnd(){
        return new Date(end);
    }
//    public int gHours(){
//        return (int)Math.round(TimeUnit.HOURS.convert(this.duration, TimeUnit.MILLISECONDS));
//    }
    public boolean overlap(TimePeriod that){
        return ((this.retEnd().getTime() > that.retStart().getTime()) && (that.retEnd().getTime() > this.retStart().getTime())
                || (this.retStart().getTime() < that.retEnd().getTime() && this.retEnd().getTime() > that.retEnd().getTime())
                || (this.retStart().getTime() > that.retStart().getTime() && this.retEnd().getTime() < that.retEnd().getTime())
        );
    }
}
