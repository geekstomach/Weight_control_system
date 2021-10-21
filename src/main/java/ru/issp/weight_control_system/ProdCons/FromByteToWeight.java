package ru.issp.weight_control_system.ProdCons;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FromByteToWeight implements Runnable{
    //FIXME - сделать так чтобы
    // - все данные получаемые за секунду суммировались о отправлялись средним значением далее (на график и в расчеты)
    // - либо просто данные читались раз в секунду
    // - добавить обработку получаемого сигнала (производные и расчеты)
    // - Возможно необходимо создать объект с полями и расчетами


    private final BlockingQueue <byte[]> inputQueue;

    public BlockingQueue<Long> getOutputQueue() {
        return outputQueue;
    }

    private final LinkedBlockingQueue<Long> outputQueue;

    //TODO добавить возможность калибровки датчика веса (может отдельной подпрограммой)
    //Используем если читаем из файла
    double k = 0.23;
    long zeroValue = 4044534;
    //Используем если читаем из COM
    /*double k = 3.08;
    long zeroValue = 249557;*/

    public FromByteToWeight(BlockingQueue<byte[]>  q){
        inputQueue = q;
        outputQueue= new LinkedBlockingQueue<>();

    }

    @Override
    public void run() {

        try{

/*            //TODO разобраться откуда  20000000 при старте(вроде пропало при Singleton)
            Thread.sleep(48);
            zeroValue = getLongFromBytes(inputQueue.take());
            if (zeroValue == 20000000)zeroValue = getLongFromBytes(inputQueue.take());*/

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
        //.out.printf("[%s] Потреблено  : %s %n", Thread .currentThread().getName(),getWeightDataLong2(x)*k);

        try {

            outputQueue.put((long) (getWeightDataLong2(x)*k));
            //System.out.println((long) (getWeightDataLong2(x)*k));
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
            //System.out.print((char)rawData[i]);
        }

        return Long.parseLong(hexString.toString(), 16);
    }

}

/*Иногда, но не всегда(проследить взаимосвязь не удалось)
получаю в методе getLongFromBytes на выходе 20000000 при старте
думаю что связано с незакрытым COM портом
        */