package ru.issp.weight_control_system;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.issp.weight_control_system.Model.Model;
import ru.issp.weight_control_system.Model.ModelProperty;
import ru.issp.weight_control_system.ProdCons.FromByteToWeight;
import ru.issp.weight_control_system.ProdCons.ReadFromFile;
import ru.issp.weight_control_system.data.DataAll;
import ru.issp.weight_control_system.data.DataParam;
import ru.issp.weight_control_system.utils.Singleton;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.*;

public class MainApplication extends Application {
    ObservableList<ModelProperty> sourceList = FXCollections.observableArrayList();;
    //TODO Научиться писать правильные Javadoc
    @Override
    public void start(Stage stage) throws IOException, InterruptedException {

        // getting loader and a pane for the first getWeightScene.


        // loader will then give a possibility to get related controller
        FXMLLoader getWeightLoader = new FXMLLoader(MainApplication.class.getResource("getWeight.fxml"));
        Parent getWeightPane = getWeightLoader.load();
        Scene getWeightScene = new Scene(getWeightPane, 650, 750);

        // getting loader and a pane for the second setPowerScene
        FXMLLoader setPowerLoader = new FXMLLoader(MainApplication.class.getResource("setPower.fxml"));
        Parent setPowerPane = setPowerLoader.load();
        Scene setPowerScene = new Scene(setPowerPane, 650, 750);


        // getting loader and a pane for the second TableScene
        FXMLLoader tableLoader = new FXMLLoader(MainApplication.class.getResource("Table.fxml"));
        Parent tablePane = tableLoader.load();
        Scene setTableScene = new Scene(tablePane, 650, 750);

       // injecting second scene into the controller of the first scene
        MainController getWeightController = (MainController) getWeightLoader.getController();
        getWeightController.setSetPowerSceneScene(setPowerScene);
        getWeightController.setTableScene(setTableScene);

        // injecting first scene into the controller of the second scene
        SetPowerController setPowerPaneController = (SetPowerController) setPowerLoader.getController();
        setPowerPaneController.setGetWeightScene(getWeightScene);


        // injecting second scene into the controller of the first scene
        TableController tableController = (TableController) tableLoader.getController();
        tableController.setGetWeightScene(getWeightScene);
        stage.setTitle("Get Weight");
        stage.setScene(getWeightScene);
        stage.show();

       Platform.runLater(new Runnable() {
            public void run() {
                try {
                    DataTransfer.transferData(sourceList);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        tableController.setDataList(sourceList);
        getWeightController.setDataList(sourceList);
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