package ru.issp.weight_control_system.ProdCons;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FromByteToWeight implements Runnable{

    private final BlockingQueue <byte[]> inputQueue;

    public BlockingQueue<Long> getOutputQueue() {
        return outputQueue;
    }

    private final LinkedBlockingQueue<Long> outputQueue;

    //TODO добавить возможность калибровки датчика веса (может отдельной подпрограммой)
    double k = 0.23;
    long zeroValue = 4044534;

    public FromByteToWeight(BlockingQueue<byte[]>  q){
        inputQueue = q;
        outputQueue= new LinkedBlockingQueue<>();

    }

    @Override
    public void run() {
        try{
            while(true) {
                consume(inputQueue.take());
            }
        }catch (InterruptedException ex){
            //обрабатываем исключение
            System.err.println("Исключение " + ex);
            Logger.getLogger(FromByteToWeight.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
    void consume(byte[] x) {
        System.out.printf("[%s] Потреблено  : %s %n", Thread .currentThread().getName(),getWeightDataLong2(x)*k);

        try {

            outputQueue.put((long) (getWeightDataLong2(x)*k));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //TODO проверить правильность обнуления начального значения
    //TODO проверить правильность получаемых данных
    private Long getWeightDataLong2(byte[] rawData) {
       return getLongFromBytes(rawData)-zeroValue;
    }


    public static long getLongFromBytes(byte[] rawData) {
        StringBuilder hexString = new StringBuilder();

        for (int i = 2; i < 10; i++) {
            hexString.append((char)rawData[i]);
        }

        return Long.parseLong(hexString.toString(), 16);
    }

}