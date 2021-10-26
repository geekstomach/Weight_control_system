package ru.issp.weight_control_system;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import jssc.SerialPortException;
import ru.issp.weight_control_system.Model.ModelProperty;
import ru.issp.weight_control_system.utils.PowerSetter;
import ru.issp.weight_control_system.utils.Singleton;

import java.io.IOException;


public class MainApplication extends Application {
    ObservableList<ModelProperty> sourceList = FXCollections.observableArrayList();
    ObservableList<Double> realMassList = FXCollections.observableArrayList();
    //TODO Научиться писать правильные Javadoc
    @Override
    public void start(Stage stage) throws IOException{

        // getting loader and a pane for the first getWeightScene.
        // loader will then give a possibility to get related controller
        FXMLLoader getWeightLoader = new FXMLLoader(MainApplication.class.getResource("getWeight.fxml"));
        Parent getWeightPane = getWeightLoader.load();
        Scene getWeightScene = new Scene(getWeightPane, 650, 750);
        // set listener to provide control power by keyboard
        getWeightScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case F1: {
                    System.out.println("Кнопка F1 нажата, увеличиваем мощность на 10 единиц");
                    try {
                        PowerSetter.setPower(PowerSetter.getPOWER()+10);
                    } catch (SerialPortException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                case F2:  System.out.println("Кнопка F2 нажата, уменьшаем мощность на 10 единиц");
                    try {
                        PowerSetter.setPower(PowerSetter.getPOWER()-10);
                    } catch (SerialPortException | InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        });
        // getting loader and a pane for the second setPowerScene
        FXMLLoader setPowerLoader = new FXMLLoader(MainApplication.class.getResource("setPower.fxml"));
        Parent setPowerPane = setPowerLoader.load();
        Scene setPowerScene = new Scene(setPowerPane, 650, 750);


        // getting loader and a pane for the second TableScene
        FXMLLoader tableLoader = new FXMLLoader(MainApplication.class.getResource("Table.fxml"));
        Parent tablePane = tableLoader.load();
        Scene setTableScene = new Scene(tablePane, 750, 750);

       // injecting second scene into the controller of the first scene
        MainController getWeightController = getWeightLoader.getController();
        getWeightController.setSetPowerSceneScene(setPowerScene);
        getWeightController.setTableScene(setTableScene);

        // injecting first scene into the controller of the second scene
        SetPowerController setPowerPaneController = setPowerLoader.getController();
        setPowerPaneController.setGetWeightScene(getWeightScene);


        // injecting second scene into the controller of the first scene
        TableController tableController = tableLoader.getController();
        tableController.setGetWeightScene(getWeightScene);
        stage.setTitle("Get Weight");
        stage.setScene(getWeightScene);
        stage.show();

        DataTransfer.transferData(sourceList,realMassList);
        tableController.setDataList(sourceList);
        getWeightController.setDataList(sourceList,realMassList);

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