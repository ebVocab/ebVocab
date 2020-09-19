package de.ebuchner.vocab.model.currency;

import junit.framework.TestCase;

public class YahooFinanceCurrencyTest extends TestCase {

    // Currency Conversion from Yahoo Finance is no longer available
    public void doNotTestCurrency() {
        double rate = YahooFinanceCurrency.obtainRate("EUR", "INR");
        assertTrue(rate > 0);
        System.out.println("INR to EUR rate " + rate);
    }
}
