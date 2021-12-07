package ru.issp.weight_control_system;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jssc.SerialPortException;
import ru.issp.weight_control_system.Model.ModelProperty;
import ru.issp.weight_control_system.utils.PowerSetter;
import ru.issp.weight_control_system.utils.Singleton;

import java.io.IOException;


public class MainApplication extends Application {
    ObservableList<ModelProperty> sourceList = FXCollections.observableArrayList();
    ObservableList<Double> realMassList = FXCollections.observableArrayList();
    ObservableList<Double> modelRadiusList = FXCollections.observableArrayList();

    //TODO Научиться писать правильные Javadoc
    @Override
    public void start(Stage stage) throws IOException {

        // getting loader and a pane for the first getWeightScene.
        // loader will then give a possibility to get related controller
        //FXMLLoader getWeightLoader = new FXMLLoader(MainApplication.class.getResource("getWeight.fxml"));
        FXMLLoader getWeightLoader = new FXMLLoader(MainApplication.class.getResource("mainWindow.fxml"));
        Parent getWeightPane = getWeightLoader.load();
        Scene getWeightScene = new Scene(getWeightPane);

        // getting loader and a pane for the second setPowerScene
        FXMLLoader setPowerLoader = new FXMLLoader(MainApplication.class.getResource("setPower.fxml"));
        Parent setPowerPane = setPowerLoader.load();
        Scene setPowerScene = new Scene(setPowerPane);


        // injecting second scene into the controller of the first scene
        // MainController getWeightController = getWeightLoader.getController();
        MainWindowController getWeightController = getWeightLoader.getController();
        getWeightController.setSetPowerScene(setPowerScene);


        // injecting first scene into the controller of the second scene
        SetPowerController setPowerPaneController = setPowerLoader.getController();
        setPowerPaneController.setGetWeightScene(getWeightScene);


        stage.setTitle("Get Weight");
        stage.setScene(getWeightScene);
        stage.show();


        DataTransfer.transferData(sourceList, realMassList, modelRadiusList);
        getWeightController.setDataList(sourceList, realMassList, modelRadiusList);


        // set listener to provide control power by keyboard
        /*getWeightScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W -> {
                    System.out.println("Кнопка W нажата, увеличиваем мощность на 10 единиц");
                    try {
                        PowerSetter.setPower(PowerSetter.getPOWER() + 10);
                    } catch (SerialPortException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                case Q -> {
                    System.out.println("Кнопка Q нажата, уменьшаем мощность на 10 единиц");
                    try {
                        PowerSetter.setPower(PowerSetter.getPOWER() - 10);
                    } catch (SerialPortException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                case A-> {
                    System.out.println("Кнопка A нажата,включаем Auto Ranging для M_fast ");
                    getWeightController.yAxisLineChartWeight.setAutoRanging(true);                }
                case S -> {
                    getWeightController.yAxisLineChartWeight.setAutoRanging(false);
                    int LowerBound = (int) getWeightController.yAxisLineChartWeight.getLowerBound();
                    getWeightController.yAxisLineChartWeight.setLowerBound(LowerBound);
                    getWeightController.yAxisLineChartWeight.setUpperBound(LowerBound+1000);
                }
                case Z -> {
                    if (!getWeightController.yAxisLineChartWeight.isAutoRanging()){
                        int LowerBound = (int) getWeightController.yAxisLineChartWeight.getLowerBound();
                        getWeightController.yAxisLineChartWeight.setLowerBound(LowerBound-500);
                        getWeightController.yAxisLineChartWeight.setUpperBound(LowerBound+500);
                    }

                }
                case X -> {
                    if (!getWeightController.yAxisLineChartWeight.isAutoRanging()){
                        int LowerBound = (int) getWeightController.yAxisLineChartWeight.getLowerBound();
                        getWeightController.yAxisLineChartWeight.setLowerBound(LowerBound+500);
                        getWeightController.yAxisLineChartWeight.setUpperBound(LowerBound+1500);
                    }
                }
            }
        });*/

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Singleton.getInstance().closePort();
        //TODO Организовать выход из цикла поучения данных по смене флага отсюда
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
   launch();
    }


}