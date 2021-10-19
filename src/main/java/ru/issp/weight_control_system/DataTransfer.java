package ru.issp.weight_control_system;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import ru.issp.weight_control_system.Model.Model;
import ru.issp.weight_control_system.Model.ModelProperty;
import ru.issp.weight_control_system.ProdCons.FromByteToWeight;
import ru.issp.weight_control_system.ProdCons.ReadFromFile;
import ru.issp.weight_control_system.data.DataAll;
import ru.issp.weight_control_system.data.DataParam;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.*;

public class DataTransfer {

    public static void transferData(ObservableList<ModelProperty> list) throws FileNotFoundException {

        BlockingQueue<byte[]> q = new LinkedBlockingQueue<>();
        ReadFromFile p = new ReadFromFile(q);//Читаем из файла
        //ReadFromCom p = new ReadFromCom(q);//Читаем из COM-порта
        FromByteToWeight c1 = new FromByteToWeight(q);
        ScheduledExecutorService scheduledExecutorService;
        DataAll dataAll = new DataAll();
        DataParam dataParam = new DataParam();


        System.out.println("Запускаем потоки producer/consumer ");
        new Thread(p).start();
        new Thread(c1).start();

        long start = System.currentTimeMillis();
/*
        Platform.runLater(new Runnable() {
            public void run() {
                textField.requestFocus();
            }
        });*/
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int size = c1.getOutputQueue().size();
            System.out.println(size);
            double currentMass = 0d;
            double sum = 0d;
            int count = 0;
            try {
                //currentMass = c1.getOutputQueue().take().doubleValue();
                for (int i = 0; i < size; i++) {
                    sum = sum+c1.getOutputQueue().take();
                    System.out.print(" "+count++);
                }
                currentMass = sum/size;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("В main получаем вес" + currentMass);
            System.out.println("Текущее время :" + createDateFormat().format(System.currentTimeMillis() - start));


            //здесь у нас создается два объекта model и ModelProperty.
            Model model = new Model(1, dataParam, dataAll, currentMass);


            Platform.runLater(new Runnable() {
                public void run() {
                    list.add(new ModelProperty(
                            model.realMass,
                            model.modelMass,
                            model.modelMassDeviation,
                            model.modelFirstDerivativeDeviation,
                            model.modelSecondDerivativeDeviation));

                }
            });


        }, 0, 1, TimeUnit.SECONDS);

    }
    private static SimpleDateFormat createDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

}
