package com.lz.util;

import java.text.DecimalFormat;
import java.util.Random;

public class RandomUtil {

    private static final Random random = new Random();
    private static final DecimalFormat fourdf = new DecimalFormat("0000");
    private static final DecimalFormat sixdf = new DecimalFormat("000000");

    private RandomUtil(){}

    public static String getFourBitRandom(){
        return fourdf.format(random.nextInt(10000));
    }
    public static String getSixBirRandom(){
        return fourdf.format(random.nextInt(10000));
    }

}
