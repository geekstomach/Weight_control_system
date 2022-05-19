package ru.issp.weight_control_system.ProdCons;


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

    //TODO добавить возможность и проверить калибровку датчика веса
    //Используем если читаем из файла
    double k = 0.23;
    long zeroValue = 4044534;
    //Используем если читаем из COM
    //коэффициент k устанавливая так, чтобы получать значение в килограммах
    /*double k = 3.08;
    long zeroValue = 249557;*/

    public FromByteToWeight(BlockingQueue<byte[]>  q){
        inputQueue = q;
        outputQueue= new LinkedBlockingQueue<>();

    }

    @Override
    public void run() {

        try{

            Thread.sleep(48);
            zeroValue = getLongFromBytes(inputQueue.take());

            //System.out.println("ZeroValue "+ zeroValue);
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
        //System.out.println(getLongFromBytes(x));
        //System.out.printf("[%s] Потреблено  : %s %n", Thread .currentThread().getName(),getWeightDataLong2(x)*k);

        try {

            outputQueue.put((long) (getWeightDataLong2(x)*k));
            //System.out.println((long) (getWeightDataLong2(x)*k));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Long getWeightDataLong2(byte[] rawData) {
       return getLongFromBytes(rawData)-zeroValue;
    }


    public static long getLongFromBytes(byte[] rawData) {
        StringBuilder hexString = new StringBuilder();

        for (int i = 3; i < 11; i++) {//TODO Важно, не посчитал открывающуюся скобку!!!и от того сдвинулось на регистр влево....
            hexString.append((char)rawData[i]);
            //System.out.print((char)rawData[i]);
        }

        return Long.parseLong(hexString.toString(), 16);
    }

}
