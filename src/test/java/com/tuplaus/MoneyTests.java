package com.tuplaus;

import com.tuplaus.component.Money;
import com.tuplaus.exception.MoneyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTests {
    @Test
    void moneyObjectCanBeCreated() {
        Money money = new Money("123");

        assertEquals("123.0000", money.getAmountAsString());
    }

    @Test
    void negativeMoneyObjectCanBeCreated() {
        Money money = new Money("-123.45");

        assertEquals("-123.4500", money.getAmountAsString());
    }

    @Test
    void canGetFormattedAmount() {
        Money money = new Money("123");

        assertEquals("123.00", money.getFormattedAmount());
    }

    @Test
    void moneyObjectsCanBeAdded() {
        Money money1 = new Money("10.00");
        Money money2 = new Money("20.00");
        Money money3 = money1.add(money2);

        assertEquals("10.0000", money1.getAmountAsString());
        assertEquals("20.0000", money2.getAmountAsString());
        assertEquals("30.0000", money3.getAmountAsString());
    }

    @Test
    void moneyObjectsCanBeSubtracted() {
        Money money1 = new Money("10.00");
        Money money2 = new Money("20.00");
        Money money3 = money1.subtract(money2);

        assertEquals("10.0000", money1.getAmountAsString());
        assertEquals("20.0000", money2.getAmountAsString());
        assertEquals("-10.0000", money3.getAmountAsString());
    }

    @Test
    void isNegativeWorks() {
        Money money1 = new Money("1");
        Money money2 = new Money("-1");

        assertFalse(money1.isNegative());
        assertTrue(money2.isNegative());
    }

    @Test
    void cantCreateMoneyFromStringsWithInvalidCharacters() {
        assertThrows(MoneyException.class, () -> new Money("abc"));
    }

    @Test
    void moneysEqual() {
        Money money1 = new Money("1");
        Money money2 = new Money("1");

        assertEquals(money1, money2);
    }

    @Test
    void moneysEqualWithDecimalsWithinPrecision() {
        Money money1 = new Money("1.0001");
        Money money2 = new Money("1.0001");

        assertEquals(money1, money2);
    }

    @Test
    void moneysEqualWithDifferentDecimalsNotWithinPrecision() {
        Money money1 = new Money("1");
        Money money2 = new Money("1.00001");

        assertEquals(money1, money2);
    }

    @Test
    void moneysWithDifferentAmountsAreNotEqual() {
        Money money1 = new Money("1");
        Money money2 = new Money("2");

        assertNotEquals(money1, money2);
    }
}
