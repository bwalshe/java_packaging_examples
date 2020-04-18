package com.example.rationals;

public final class RationalNumber {
    private int _numerator;
    private int _denominator;

    public RationalNumber(int numerator, int denominator) {
        if ( denominator == 0){
            throw new IllegalArgumentException("Denominator must be non-zero!");
        }
        int commonFactor = gcd(numerator, denominator);
        _numerator = numerator / commonFactor;
        _denominator = denominator / commonFactor;
    }

    public RationalNumber(int numerator) {
        this(numerator, 1);
    }

    public String toString() {
        return _numerator + "/" + _denominator;
    }

    public int getNumerator() {
        return _numerator;
    }

    public int getDenominator() {
        return _denominator;
    }

    public double numericValue() {
        return _numerator/ (double) _denominator;
    }

    public RationalNumber add(RationalNumber other) {
        int numerator = this._numerator * other._denominator
            + other._numerator * this._denominator;
        int denominator = this._denominator * other._denominator;
        return new RationalNumber(numerator, denominator);
    }

     public RationalNumber multiply(RationalNumber other) {
        int numerator = this._numerator * other._numerator;
        int denominator = this._denominator * other._denominator;
        return new RationalNumber(numerator, denominator);
    }


    @Override
    public boolean equals(Object o) {
        if(!(o instanceof RationalNumber)){
            return false;
        }
        RationalNumber n = (RationalNumber) o;
        return this._numerator == n._numerator &&
            this._denominator == n._denominator;
    }

    @Override
    public int hashCode() {
        return 31 * Integer.hashCode(_numerator) + Integer.hashCode(_denominator);
    }

    private int gcd(int a, int b) {
        if (a == 0) {
            return b;
        } else if(b == 0) {
            return a;
        } else {
            int high = Math.max(a, b);
            int low = Math.min(a, b);
            int q = high / low;
            int r = high - q * low;
            return gcd(low, r);
        }
    }
}