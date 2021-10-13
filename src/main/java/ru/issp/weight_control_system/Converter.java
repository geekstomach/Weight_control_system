package ru.issp.weight_control_system;


import java.nio.charset.StandardCharsets;

public class Converter {
    public static void main0(String[] args) {
        String rawData = "000003A9DE1B";

        String data = rawData.substring(2, rawData.length() - 2);

        System.out.println(data);

        System.out.println(Long.parseLong(data, 16));
    }

    public static void main(String[] args) {
        byte[] data = new byte[12];

        String rawData = "00003DB6F62B";

        for (int i = 0; i < rawData.length(); i++) {
            data[i] = (byte) rawData.charAt(i);
//            data[i] = (byte) Integer.parseInt(String.valueOf(rawData.charAt(i)), 16);
        }

        System.out.println(4044534);
        System.out.println(getLongFromBytes(data));
    }

    public static void main2(String[] args) {

        System.out.println("DEC to char : " + (char) 53);//DEC to char
        System.out.println("DEC to HEX : " + Integer.toHexString(53)); //DEC to HEX
        System.out.println("HEX to DEC : " + Integer.parseInt(String.valueOf(35),16));//HEX to DEC
        //HEX to char
        //char to DEC
        //char to HEX
        String s = "<550100C8A6>";
        int cs = 0;
        for (int i = 1; i <s.length()-3 ; i++) {
            System.out.print(s.charAt(i));
            cs =(cs + s.charAt(i));
        }
        System.out.println();
        System.out.println(Integer.toHexString(cs%256));
    }
    public static long getLongFromBytes(byte[] rawData) {
        StringBuilder hexString = new StringBuilder();

        for (int i = 2; i < 10; i++) {
            hexString.append((char)rawData[i]);
        }

        return Long.parseLong(hexString.toString(), 16);
    }
public static String getHexFromDec(int power){

//return Integer.toHexString(power);}
return  String.format("%04x", power);}
}
