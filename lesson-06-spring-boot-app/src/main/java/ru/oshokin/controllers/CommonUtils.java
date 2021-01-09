package ru.oshokin.controllers;

import java.math.BigDecimal;

public class CommonUtils {

    public static int getIntegerOrDefault(String value, int defaultValue) {
        int funcResult;
        try {
            funcResult = Integer.parseInt(value);
        } catch(NumberFormatException e) {
            funcResult = defaultValue;
        }
        return funcResult;
    }

    public static BigDecimal castBigDecimal(String value) {
        BigDecimal funcResult = null;
        if ((value != null) && !value.isEmpty()) {
            try {
                funcResult = new BigDecimal(value);
            } catch (NumberFormatException e) {
                funcResult = null;
            }
        }
        return funcResult;
    }

}
