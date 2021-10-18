package ru.issp.weight_control_system;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.issp.weight_control_system.Model.ModelProperty;
import ru.issp.weight_control_system.utils.Singleton;


import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    //TODO Научиться писать правильные Javadoc
    @Override
    public void start(Stage stage) throws IOException {

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

        ObservableList<ModelProperty> list = FXCollections.observableArrayList();
        DataTransfer.transferData(list);
        tableController.setDataList(list);
        getWeightController.setDataList(list);
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