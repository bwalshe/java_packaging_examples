package com.example.progress;

import java.util.Calendar;
import com.example.rationals.RationalNumber;


public final class YearProgress {

    public static void main(String[] args) {
        final Calendar cal = Calendar.getInstance();

        final int year = cal.get(Calendar.YEAR);
        final RationalNumber progress = fractionComplete(cal);
        System.out.println("Congratulaitons - you are " + progress
                + " way through " + year + ".");
    }

    public static RationalNumber fractionComplete(Calendar currentDate) {
        final int daysInYear = currentDate.getActualMaximum(Calendar.DAY_OF_YEAR);
        final int today = currentDate.get(Calendar.DAY_OF_YEAR);
        return new RationalNumber(today, daysInYear);
    }
}