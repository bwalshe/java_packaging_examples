package com.example.rationals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public final class RationalNumberTest {

    private static final int NUMERATOR = 3;
    private static final int DENOMINATOR = 7;

    @Test
    public void testConstructorTest() {
        RationalNumber n = new RationalNumber(NUMERATOR, DENOMINATOR);
        testRational(n, NUMERATOR, DENOMINATOR);
    }

    @Test
    public void testConstructorWithDefaultDenominator() {
        RationalNumber n = new RationalNumber(NUMERATOR);
        testRational(n, NUMERATOR, 1);
    }

    @Test
    public void testConstructorUsesGcd() {
        int c = 2;
        RationalNumber n = new RationalNumber(NUMERATOR * c, DENOMINATOR * c);
        testRational(n, NUMERATOR, DENOMINATOR);
    }

    @Test
    public void testAdd() {
        RationalNumber n1 = new RationalNumber(1, 2);
        RationalNumber n2 = new RationalNumber(1, 4);
        testRational(n1.add(n2), 3, 4);
    }

    @Test
    public void testMultiply() {
        RationalNumber n1 = new RationalNumber(1, 2);
        RationalNumber n2 = new RationalNumber(1, 4);
        testRational(n1.multiply(n2), 1, 8);
    }

    @Test
    public void testEquality() {
        RationalNumber n1 = new RationalNumber(1, 2);
        RationalNumber n2 = new RationalNumber(1, 2);
        RationalNumber n3 = new RationalNumber(1, 3);

        assertEquals(n1, n2);
        assertTrue(!n1.equals(n3));
    }


    private static void testRational(RationalNumber n, int expectedNumerator, int expectedDenominator) {
        assertEquals(n.getNumerator(), expectedNumerator);
        assertEquals(n.getDenominator(), expectedDenominator); 
    }
}