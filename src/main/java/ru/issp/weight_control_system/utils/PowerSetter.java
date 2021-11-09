package ru.issp.weight_control_system.utils;

import javafx.beans.property.IntegerProperty;
import jssc.SerialPortException;
import ru.issp.weight_control_system.MainWindowController;

//Мощность посылается раз в 3 секунды без автомата
//Мощность с автоматом посылается раз в 8*3=24 секунды
public final class PowerSetter {


    private static int POWER = 0;

    /**
     * Class constructor.
     */
    public PowerSetter() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    //TODO управление мощностью идет раз в 3 секунды

    public static void setPower(int power) throws SerialPortException, InterruptedException {
        // Посылает на генератор задание мощности (0..10000) <=> 0..100% мощности
        char startMarker = '<';
        char endMarker = '>';
        StringBuilder hexString = new StringBuilder();
        if ((power>=0)&&(power<=10000)){
            // адрес $55, команда $01
            hexString.append(startMarker).append("5501");
            hexString.append(String.format("%04x", power).toUpperCase());//должно быть 4 значащих символа
            //важно использовать toUpperCase, иначе неверно считается контрольная сумма
            int cs = 0;
            for (int i = 1; i < hexString.length(); i++) {
                cs =(cs + hexString.charAt(i));
            }
            //System.out.println(Integer.toHexString(cs%256));
            hexString.append(Integer.toHexString(cs%256).toUpperCase());
            hexString.append(endMarker);
            //System.out.println(hexString);
            //System.out.println();

        }
Singleton.getInstance().writeString(hexString.toString());
POWER = power;
MainWindowController.POWER_PROPERTY.set(POWER);
        System.out.println("Мощность установлена "+POWER);
    }

    public static int getPOWER() {
        return POWER;
    }

//https://delphisources.ru/pages/faq/faq_delphi_basics/Ord.php.html
    //TODO добавить возможность разогрева и охлаждения за определенное время
}
