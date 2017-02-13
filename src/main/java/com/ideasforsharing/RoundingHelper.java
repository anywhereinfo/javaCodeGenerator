package com.ideasforsharing;

import java.io.IOException;
import java.math.BigDecimal;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class RoundingHelper implements Helper<Object> {
    @Override
    public Object apply(Object value, Options options) throws IOException {
        Integer scale = (Integer) options.hash("scale");
  //      System.out.println("Value: " + value + ", scale: " + scale);
        if (value == null || scale == null) {
            throw new IllegalArgumentException("Cannot perform operations on null values");
        }
        BigDecimal bigDecimal = new BigDecimal(value.toString());
        bigDecimal = bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
//        System.out.println("About to return: " + bigDecimal.toString());
        return bigDecimal.toString();
    }
}