package ru.issp.weight_control_system;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestData implements Runnable {

    public BlockingQueue<Double> getOutputQueue() {
        return outputQueue;
    }

    private final LinkedBlockingQueue<Double> outputQueue = new LinkedBlockingQueue<>();
    double currentMass = 0L;
    double deviationMass = 9.933186656786307d;
    Random random = new Random();
    //double deviationMass = 10d;
    @Override
    public void run() {
        try {

            while (true){
                Thread.sleep(100);
                currentMass = currentMass + deviationMass+ random.nextGaussian()-0.5;
                //currentMass = currentMass + (long)deviationMass +(long)(deviationMass*Math.random())-(long)(deviationMass*Math.random());
                //System.out.println("Генерируем тестовые данные в TestData "+ currentMass);
                outputQueue.put(currentMass);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
