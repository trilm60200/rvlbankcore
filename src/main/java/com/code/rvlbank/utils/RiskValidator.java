package com.code.rvlbank.utils;

import com.code.rvlbank.models.Fund;

import java.math.BigDecimal;

public class RiskValidator {

    public static boolean isFundQualified(Fund before, final Fund after) {
        if (!before.getCurrency().equals(after.getCurrency())) {
            throw new IllegalArgumentException("Invalid Currency.");
        }

        if (after.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid Amount.");
        }
        return true;
    }

    public static boolean isSubtractFundQualified(Fund before, final Fund after) {
        if (isFundQualified(before, after) && after.getAmount().compareTo(before.getAmount()) <= 0)
            return true;
        return false;
    }
}
