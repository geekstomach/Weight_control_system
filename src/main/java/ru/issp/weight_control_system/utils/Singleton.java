package ru.issp.weight_control_system.utils;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.io.IOException;

//https://github.com/YashithaNadiranga/Student-Management-System/blob/bb63b64569acbfbf5f7c9b132bd163096818054e/src/lk/ijse/gdse53/stm/sc/SConnection.java#L40
//https://github.com/Nashoba-Robotics/CoffinLED2017/blob/1684dcb66f8a2087869a0135a28835491b1f475d/Java/ArduinoExample.java
//The singleton pattern is used to design the application that needs to work with the serial port
//Обратить внимание на classloader
public class Singleton {

    private volatile static Singleton instance;
    //private static SerialPort serialPort;
    private final SerialPort serialPort;


    private Singleton() {
        serialPort = initSerialPort();
        System.out.println("Создаем экземпляр Singleton ");
    }

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                    //serialPort = initSerialPort();
                }
            }
        }
        return instance;
    }


    public int getInputBufferBytesCount() throws SerialPortException {
        return this.serialPort.getInputBufferBytesCount();
    }
    public boolean writeByte(byte singleByte) throws SerialPortException {
        return serialPort.writeByte(singleByte);
    }
    public boolean writeString(String s) throws SerialPortException {
        return serialPort.writeString(s);
    }
    public byte[] readBytes(int byteCount) throws SerialPortException {
        return serialPort.readBytes(byteCount);
    }
    public boolean closePort() throws SerialPortException {
        return serialPort.closePort();
    }
    //private static SerialPort initSerialPort() {
        private  SerialPort initSerialPort() {
            executeNika(); //TODO возможно стоит перенести запуск старой программы в класс Serial port Singleton
            //Т.к. не удалось решить проблемы инициализации параметров COM порта, запускаем старую программу для настройки параметров соединения.

            //TODO Добавить установку порта через интерфейс
        // - В будущем переработать на MODBUS
       // System.out.println("Listening all com ports available on device");
        //Метод getPortNames() возвращает массив строк. Элементы массива уже отсортированы.
        //Getting all serial port names available on device

        String[] portNames = SerialPortList.getPortNames();
/*        for (String portName : portNames) {
            System.out.println(portName);
        }*/
        System.out.println("We work with " + portNames[0]);

        //В конструктор класса передаём имя порта с которым мы будем работать
        //SerialPort serialPort = new SerialPort(portNames[0]);
        SerialPort serialPort = new SerialPort("COM6");
        try {

            serialPort.openPort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        return serialPort;}

    static void executeNika(){
        Process initComByDelhi = null;
        try {
            //TODO Find out how to use path from resources src/main/resources/Nika/ADC_Demo.exe
            initComByDelhi =   new ProcessBuilder("src/main/resources/Nika/ADC_Demo.exe").start();
            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            assert initComByDelhi != null;
            initComByDelhi.destroy();
        }
    }
}
