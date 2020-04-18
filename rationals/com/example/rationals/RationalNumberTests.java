package com.example.rationals;


public final class RationalNumberTests {

    private static final int NUMERATOR = 3;
    private static final int DENOMINATOR = 7;

    public static void main(String[] arg) {
        testConstructor();
        testConstructorWithDefaultDenominator();
        testConstructorUsesGcd();
        testAdd();
        testEquality();
    }

    public static void testConstructor() {
        RationalNumber n = new RationalNumber(NUMERATOR, DENOMINATOR);
        testRational(n, NUMERATOR, DENOMINATOR, "Constructor");
    }

    public static void testConstructorWithDefaultDenominator() {
        RationalNumber n = new RationalNumber(NUMERATOR);
        testRational(n, NUMERATOR, 1, "Constructor with default value");
    }

    public static void testConstructorUsesGcd() {
        int c = 2;
        RationalNumber n = new RationalNumber(NUMERATOR * c, DENOMINATOR * c);
        testRational(n, NUMERATOR, DENOMINATOR, "Constructor GCD");
    }

    public static void testAdd() {
        RationalNumber n1 = new RationalNumber(1, 2);
        RationalNumber n2 = new RationalNumber(1, 4);
        testRational(n1.add(n2), 3, 4, "Addition");
    }

    public static void testMultiply() {
        RationalNumber n1 = new RationalNumber(1, 2);
        RationalNumber n2 = new RationalNumber(1, 4);
        testRational(n1.multiply(n2), 1, 8, "Multiplication");
    }

    public static void testEquality() {
        RationalNumber n1 = new RationalNumber(1, 2);
        RationalNumber n2 = new RationalNumber(1, 2);
        RationalNumber n3 = new RationalNumber(1, 3);

        if(!n1.equals(n2) || n1.equals(n3)) {
            System.err.println("FAIL: Equality");
            System.exit(1);
        }

        System.err.println("PASS: Equality");

    }

    private static void testRational(RationalNumber n, int expectedNumerator, int expectedDenominator, 
                              String testName) {
        if (n.getNumerator() == expectedNumerator && n.getDenominator() == expectedDenominator) 
            System.err.println("PASS: " + testName);
        else {
            System.err.println("FAIL: " + testName);
            System.err.println("Expected " + expectedNumerator + "/" + expectedDenominator);
            System.err.println("Found " + n.getNumerator() + "/" + n.getDenominator());
            System.exit(1);
        }
    }
}