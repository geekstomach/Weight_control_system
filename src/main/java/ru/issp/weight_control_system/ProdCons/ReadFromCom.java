package ru.issp.weight_control_system.ProdCons;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadFromCom implements Runnable {
    private final BlockingQueue<byte[]> queueFromCom;
    private final SerialPort serialPort;
    int weight;

    public ReadFromCom(BlockingQueue<byte[]> q){
        queueFromCom = q;
        this.serialPort = initSerialPort();
  weight = 0;
    }

    @Override
    public void run() {
        try {
            while (true){
                queueFromCom.put(produce());
            }
        } catch (InterruptedException ex){
            //обрабатываем исключение
            System.err.println("Исключение " + ex);
            Logger.getLogger(ReadFromCom.class.getName()).log(Level.SEVERE,null,ex);
        }

    }
    byte[] produce(){
        System.out.println("Читаем из COM порта." );
        writeByteToCom();
        return readBytesFromCom();}

    private byte[] readBytesFromCom() {
        byte[] rawData = new byte[12];//TODO очень некрасиво, переделать
        try {

                while (serialPort.getInputBufferBytesCount() <12){
                    Thread.sleep(48);
                }

                byte[] readBuffer = new byte[serialPort.getInputBufferBytesCount()];
                readBuffer = serialPort.readBytes(readBuffer.length);
                rawData = readBuffer;

        } catch (Exception e) { e.printStackTrace(); }


        return  rawData;}

    private void writeByteToCom() {
            char StartMarker = '<';
            char EndMarker = '>';
            String s = StartMarker+"0000000080"+EndMarker; //Это команда на старт. Нужна ли каждый раз?
            for (int i = 0; i < s.length(); i++) {
                try {
                    Thread.sleep(48);
                    serialPort.writeByte(s.getBytes()[i]);
                } catch (SerialPortException | InterruptedException e) {
                    System.err.println(e);
                    e.printStackTrace();
                }
            }
        }

    private SerialPort initSerialPort(){
        //TODO Добавить установку порта через интерфейс
        //- В будующем переработать на MODBUS
        System.out.println("Listening all com ports available on device");
        //Метод getPortNames() возвращает массив строк. Элементы массива уже отсортированы.
        //Getting all serial port names available on device

        String[] portNames = SerialPortList.getPortNames();
        for (String portName : portNames) {
            System.out.println(portName);
        }
        System.out.println("We work with " + portNames[0]);

        //В конструктор класса передаём имя порта с которым мы будем работать
        SerialPort serialPort = new SerialPort(portNames[0]);
        try {
            serialPort.openPort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    return serialPort;}




}
