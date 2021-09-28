package ru.issp.weight_control_system;

import java.util.Arrays;

public class Converter {
    public static void main0(String[] args) {
        String rawData = "00003DB3871B";

        String data = rawData.substring(2, rawData.length() - 2);

        System.out.println(data);

        System.out.println(Long.parseLong(data, 16));
    }

    public static void main(String[] args) {
        byte[] data = new byte[12];

        String rawData = "00003DB3871B";

        for (int i = 0; i < rawData.length(); i++) {
            data[i] = (byte) rawData.charAt(i);
//            data[i] = (byte) Integer.parseInt(String.valueOf(rawData.charAt(i)), 16);
        }

        System.out.println(4043655);
        System.out.println(getLongFromBytes(data));
    }

    public static long getLongFromBytes(byte[] rawData) {
        StringBuilder hexString = new StringBuilder();

        for (int i = 2; i < 10; i++) {
            hexString.append((char)rawData[i]);
        }

        return Long.parseLong(hexString.toString(), 16);
    }

}
