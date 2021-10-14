package ru.issp.weight_control_system.ProdCons;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import ru.issp.weight_control_system.utils.Singleton;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadFromCom implements Runnable {
    //FIXME Выделить  SerialPort в отдельный клас Singletone
    // - чтобы была возможность в него записывать данные по управлению мощностью
    // - проверить какие данные я получаю с датчика веса( и отображение их на графике)

    private final BlockingQueue<byte[]> queueFromCom;
    //public  SerialPort serialPort;

    int weight;

    public ReadFromCom(BlockingQueue<byte[]> q){
        queueFromCom = q;
        //this.serialPort = initSerialPort();
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
        System.out.println("Читаем из COM порта.");
        writeByteToCom();
        return readBytesFromCom();}

    private byte[] readBytesFromCom() {
        byte[] rawData = new byte[12];//TODO очень некрасиво, переделать
        try {

                //while (serialPort.getInputBufferBytesCount() <12){
                while (Singleton.getInstance().getInputBufferBytesCount() <12){
                    Thread.sleep(48);
                }

                ///byte[] readBuffer = new byte[serialPort.getInputBufferBytesCount()];
                byte[] readBuffer = new byte[Singleton.getInstance().getInputBufferBytesCount()];
                //readBuffer = serialPort.readBytes(readBuffer.length);
                readBuffer = Singleton.getInstance().readBytes(readBuffer.length);
                rawData = readBuffer;

        } catch (Exception e) { e.printStackTrace(); }
        System.out.println(Arrays.toString(rawData));

        return  rawData;}

    private void writeByteToCom() {
            char StartMarker = '<';
            char EndMarker = '>';
            String s = StartMarker+"0000000080"+EndMarker; //Это команда на старт. Нужна ли каждый раз?
            for (int i = 0; i < s.length(); i++) {
                try {
                    Thread.sleep(48);
                    //serialPort.writeByte(s.getBytes()[i]);
                    Singleton.getInstance().writeByte(s.getBytes()[i]);
                } catch (SerialPortException | InterruptedException e) {
                    System.err.println(e);
                    e.printStackTrace();
                }
            }

        }

    /*private SerialPort initSerialPort(){
        //TODO Добавить установку порта через интерфейс
        // - В будущем переработать на MODBUS
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
    return serialPort;}*/




}
