package ru.issp.weight_control_system.ProdCons;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Consumer implements Runnable{

    Long zeroValue;

    private final BlockingQueue inputQueue;

    public BlockingQueue<Long> getOutputQueue() {
        return outputQueue;
    }

    private final LinkedBlockingQueue<Long> outputQueue;


    public Consumer(BlockingQueue q){
        inputQueue = q;
        outputQueue= new LinkedBlockingQueue<>();
        zeroValue=0L;
    }

    @Override
    public void run() {
        try{
            zeroValue = getWeightDataLong((byte[]) inputQueue.take());
            while (true) {
                consume(inputQueue.take());
            }
            }catch (InterruptedException ex){
            //обробатываем исключение
            System.err.println("Исключение " + ex);
            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
    void consume(Object x) {
        System.out.printf("[%s] Потреблено  : %s %n", Thread .currentThread().getName(),getWeightDataLong((byte[]) x));
        try {
            outputQueue.put(getWeightDataLong((byte[]) x)-zeroValue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private BigInteger getWeightDataInt(byte[] numRead){

        byte[] weightData = new byte[8];
        System.arraycopy(numRead, 2, weightData, 0, 8);
        return new BigInteger(weightData);}


    private long getWeightDataLong(byte[] numRead) {
        byte[] weightData = new byte[8];
        System.arraycopy(numRead, 2, weightData, 0, 8);
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(weightData);
        buffer.flip();//need flip
        return buffer.getLong();
    }
}