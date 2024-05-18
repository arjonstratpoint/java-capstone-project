package com.example.arjon.util;

import java.util.Random;

public class OTPForgotPassword {
    public static String generateOTP() {
        int length = 4;
        String numbers = "0123456789";
        Random rndm_method = new Random();
        char[] otp = new char[length];
        for (int i = 0; i < length; i++) {
            otp[i] = numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
        return new String(otp);
    }
}
