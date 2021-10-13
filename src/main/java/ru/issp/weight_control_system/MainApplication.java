package ru.issp.weight_control_system;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.issp.weight_control_system.utils.Singleton;


import java.io.IOException;

public class MainApplication extends Application {
    //TODO Научиться писать правильные Javadoc
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("getWeight.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("setPower.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 750);
        stage.setTitle("Get Weight");
        stage.setScene(scene);
        stage.show();
        }

    @Override
    public void stop() throws Exception {
        super.stop();
        Singleton.getInstance().closePort();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
   launch();
    }

}