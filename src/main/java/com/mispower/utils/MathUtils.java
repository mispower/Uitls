package com.mispower.utils;

/**
 * The class {@code MathUtils} contains some methods which {@code Math} is not contains.
 *
 * @author wuguolin
 */
public class MathUtils {

    public static double log(double antilogarithm, double base) {
        return Math.log(antilogarithm) / Math.log(base);
    }

    public static double log2(double antilogarithm) {
        return log(antilogarithm, 2);
    }
}
