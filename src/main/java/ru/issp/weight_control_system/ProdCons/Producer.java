package ru.issp.weight_control_system.ProdCons;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Producer implements Runnable {
    private final BlockingQueue queue;
    private final SerialPort serialPort;
    int weight;

    public Producer(BlockingQueue q){
        queue = q;
        this.serialPort = initSerialPort();
  weight = 0;
    }

    @Override
    public void run() {
        try {
            while (true){
                queue.put(produce());
            }
        } catch (InterruptedException ex){
            //обробатываем исключение
            System.err.println("Исключение " + ex);
            Logger.getLogger(Producer.class.getName()).log(Level.SEVERE,null,ex);
        }

    }
    Object produce(){
        System.out.println("Читаем из COM порта." );
        writeByteToCom();
        return readBytesFromCom();}

    private byte[] readBytesFromCom() {
        byte[] weightData = new byte[16];//очень некрасиво, переделать
        try {

                while (serialPort.getInputBufferBytesCount() <16){
                    Thread.sleep(48);
                }

                byte[] readBuffer = new byte[serialPort.getInputBufferBytesCount()];
                readBuffer = serialPort.readBytes(readBuffer.length);
                weightData = readBuffer;

        } catch (Exception e) { e.printStackTrace(); }


        return  weightData;}

    private void writeByteToCom() {
            char StartMarker = '<';
            char EndMarker = '>';
            String s = StartMarker+"0000000080"+EndMarker; //это типо команда на старт?
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
