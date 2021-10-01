package ru.issp.weight_control_system;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.issp.weight_control_system.utils.Singleton;


import java.io.IOException;

public class MainApplication extends Application {
    //TODO Добавить возможность управления мощностью!!!
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("getWeight.fxml"));
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
        //TODO возможно стоит перенести запуск старой программы в класс Serial port Singleton
        executeNika();//Т.к. не удалось решить проблемы инициализации параметров COM порта, запускаем старую программу для настройки параметров соединения.
        launch();
    }

    //TODO научиться завершать работу с закрытием программы на крестик
   static void executeNika(){
        Process initComByDelhi = null;
        try {
            //TODO Find out how to use path from resources src/main/resources/Nika/ADC_Demo.exe
            initComByDelhi =   new ProcessBuilder("src/main/resources/Nika/ADC_Demo.exe").start();
            Thread.sleep(50);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            assert initComByDelhi != null;
            initComByDelhi.destroy();
        }
    }
}