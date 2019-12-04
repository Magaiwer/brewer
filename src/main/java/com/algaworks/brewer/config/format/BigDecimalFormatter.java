package com.algaworks.brewer.config.format;

import org.springframework.format.Formatter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class BigDecimalFormatter implements Formatter<BigDecimal> {

    private DecimalFormat decimalFormat;


    public BigDecimalFormatter(String patter) {
        NumberFormat format = NumberFormat.getInstance(new Locale("pt","BR"));
        decimalFormat = (DecimalFormat) format;
        decimalFormat.setParseBigDecimal(true);
        decimalFormat.applyPattern(patter);
    }

    @Override
    public BigDecimal parse(String text, Locale locale) throws ParseException {
        return (BigDecimal) decimalFormat.parse(text);
    }

    @Override
    public String print(BigDecimal bigDecimal, Locale locale) {
        return decimalFormat.format(bigDecimal);
    }
}
