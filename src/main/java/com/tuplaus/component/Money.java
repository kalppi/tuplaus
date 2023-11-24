package com.tuplaus.component;

import com.tuplaus.exception.MoneyException;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Getter
public class Money {
    private final BigDecimal amount;

    public Money(String amount) {
        if (amount == null) {
            throw new MoneyException("invalid amount");
        }

        try {
            this.amount = (new BigDecimal(amount)).setScale(4, RoundingMode.DOWN);
        } catch (NumberFormatException e) {
            throw new MoneyException("invalid amount");
        }
    }

    public Money(BigDecimal amount) {
        this.amount = amount;
    }

    public Money negate() {
        return new Money(amount.negate());
    }

    public Money add(Money money) {
        BigDecimal amount = new BigDecimal(money.getAmountAsString());

        return new Money(this.amount.add(amount).toString());
    }

    public Money subtract(Money money) {
        BigDecimal amount = new BigDecimal(money.getAmountAsString());

        return new Money(this.amount.subtract(amount).toString());
    }

    public Money multiply(int mult) {
        BigDecimal amount = new BigDecimal(mult);

        return new Money(this.amount.multiply(amount).toString());
    }

    public String getAmountAsString() {
        return this.amount.toString();
    }

    public boolean isNegative() {
        return this.amount.signum() == -1;
    }

    public String getFormattedAmount() {
        DecimalFormat df = new DecimalFormat("#,###.00");
        return df.format(this.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Money m)) {
            return false;
        }

        return this.amount.compareTo(m.amount) == 0;
    }
}
