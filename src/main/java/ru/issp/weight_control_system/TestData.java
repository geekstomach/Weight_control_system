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
    double deviationMass = 0.00032d;
    Random random = new Random();
    //double deviationMass = 10d;
    @Override
    public void run() {
        try {

            while (true){
                Thread.sleep(10);
                //currentMass = currentMass + deviationMass + 0.01*random.nextGaussian();
                currentMass = 0;
                //currentMass = currentMass + deviationMass;
                //currentMass = currentMass + (long)deviationMass +(long)(deviationMass*Math.random())-(long)(deviationMass*Math.random());
                //System.out.println("Генерируем тестовые данные в TestData "+ currentMass);
                outputQueue.put(currentMass);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
