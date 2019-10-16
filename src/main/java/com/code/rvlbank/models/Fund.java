package com.code.rvlbank.models;


import com.code.rvlbank.utils.RiskValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fund {

    public static final int DEFAULT_ROUNDING_MODE = BigDecimal.ROUND_HALF_EVEN;

    private BigDecimal amount;
    private String currency;

    public static Fund add(Fund before, Fund after) {
        if (RiskValidator.isFundQualified(before, after)) {
            Fund fund = new Fund(before.getAmount().add(after.getAmount()), before.getCurrency());
            fund.setAmount(fund.getAmount().setScale(4, DEFAULT_ROUNDING_MODE));

            return fund;
        }

        return null;
    }

    public static Fund subtract(Fund before, Fund after) {
        if (RiskValidator.isSubtractFundQualified(before, after)) {
            Fund fund = new Fund(before.getAmount().subtract(after.getAmount()), before.getCurrency());
            fund.setAmount(fund.getAmount().setScale(4, DEFAULT_ROUNDING_MODE));

            return fund;
        }

        return  null;
    }

    public static BigDecimal multiply(BigDecimal before, BigDecimal after) {
        BigDecimal newAmount = before.multiply(after);
        newAmount = newAmount.setScale(4, DEFAULT_ROUNDING_MODE);
        return newAmount;
    }

    public static BigDecimal divide(BigDecimal before, BigDecimal after) {
        BigDecimal newAmount = before.divide(after);
        newAmount = newAmount.setScale(4, DEFAULT_ROUNDING_MODE);
        return newAmount;
    }


}
