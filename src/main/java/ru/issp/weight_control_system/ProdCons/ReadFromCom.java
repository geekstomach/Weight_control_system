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
    //FIXME проверить какие данные я получаю с датчика веса( и отображение их на графике)

    private final BlockingQueue<byte[]> queueFromCom;
    int weight;

    public ReadFromCom(BlockingQueue<byte[]> q){
        queueFromCom = q;
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
        //TODO очень некрасиво, надо ли переделать?
        byte[] rawData = new byte[12];
        try {
               while (Singleton.getInstance().getInputBufferBytesCount() <12){
                    Thread.sleep(48);
                }


                byte[] readBuffer = new byte[Singleton.getInstance().getInputBufferBytesCount()];
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
                    Singleton.getInstance().writeByte(s.getBytes()[i]);
                } catch (SerialPortException | InterruptedException e) {
                    System.err.println(e);
                    e.printStackTrace();
                }
            }

        }
}
