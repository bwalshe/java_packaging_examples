package com.example.progress;

import java.util.Calendar;
import com.example.rationals.RationalNumber;


public final class YearProgress {

    public static void main(String[] args) {
        final Calendar cal = Calendar.getInstance();
        final int daysInYear = cal.getMaximum(Calendar.DAY_OF_YEAR);
        final int today = cal.get(Calendar.DAY_OF_YEAR);
        final int year = cal.get(Calendar.YEAR);
        final RationalNumber progress = new RationalNumber(today, daysInYear);
        System.out.println("Congratulaitons - you are " + progress
                + " way through " + year + ".");
    }
}