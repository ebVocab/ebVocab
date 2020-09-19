package de.ebuchner.vocab.model.currency;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Currency;
import java.util.List;

public class RupeeCurrencyFormatterTest extends TestCase {

    List<Double> input = Arrays.asList(
            0.0,
            0.99,
            1.0,
            10.01,
            150.55,
            1813.34,
            34541.129,
            100000.0,
            934544.3,
            4543321.01,
            10000000.00,
            99999999.99,
            99999999.999
    );
    List<String> result = Arrays.asList(
            "0,00,00,000.00 (0 rupee)",
            "0,00,00,000.99 (99 paise)",
            "0,00,00,001.00 (1 rupee)",
            "0,00,00,010.01 (10 rupee and 1 paisa)",
            "0,00,00,150.55 (150 rupee and 55 paise)",
            "0,00,01,813.34 (1813 rupee and 34 paise)",
            "0,00,34,541.13 (34541 rupee and 13 paise)",
            "0,01,00,000.00 (1 lakh)",
            "0,09,34,544.30 (9 lakhs 34544 rupee and 30 paise)",
            "0,45,43,321.01 (45 lakhs 43321 rupee and 1 paisa)",
            "1,00,00,000.00 (1 crore)",
            "9,99,99,999.99 (9 crores 99 lakhs 99999 rupee and 99 paise)",
            "10,00,00,000.00 (10 crores)"

    );

    public void testFormatter() {
        RupeeCurrencyFormatter formatter = new RupeeCurrencyFormatter();
        assertNull(formatter.format(null, Double.NaN));
        assertNull(formatter.format(Currency.getInstance("EUR"), Double.NaN));
        assertNull(formatter.format(Currency.getInstance("EUR"), 4711));
        assertNull(formatter.format(Currency.getInstance("USD"), Double.NaN));
        assertNull(formatter.format(Currency.getInstance("USD"), 4711));
        assertNull(formatter.format(Currency.getInstance("INR"), Double.NaN));

        assertEquals(input.size(), result.size());

        for (int i = 0; i < input.size(); i++) {
            assertEquals(
                    result.get(i),
                    formatter.format(
                            Currency.getInstance("INR"),
                            input.get(i)
                    )
            );
        }


    }
}
