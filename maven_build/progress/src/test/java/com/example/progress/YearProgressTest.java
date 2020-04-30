package com.example.progress;


import org.junit.*;

import java.util.Calendar;
import com.example.rationals.RationalNumber;

import static org.junit.Assert.assertEquals;


public class YearProgressTest {

    @Test
    public void testProgressRegular() {
        RationalNumber expected = new RationalNumber(1, 365);
        Calendar cal = Calendar.getInstance();
        cal.set(2019, 0, 1);
        System.out.println(cal);
        assertEquals(expected, YearProgress.fractionComplete(cal));
    }

    @Test
    public void testProgressLeap() {
        RationalNumber expected = new RationalNumber(1, 366);
        Calendar cal = Calendar.getInstance();
        cal.set(2020, 0, 1);
        assertEquals(expected, YearProgress.fractionComplete(cal));
    }
    
}