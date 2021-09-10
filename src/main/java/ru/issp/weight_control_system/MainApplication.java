package ru.issp.weight_control_system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

    public static void main(String[] args) {
      executeNika();//т.к. не удалось решить проблемы инициализации параметров COM порта,запускаем старую программу для настройки параметров соединения.
        launch();
    }

    //TODO научиться завершать работу с закрытием программы на крестик
   static void executeNika(){
        Process initComByDelhi = null;
        try {
            initComByDelhi =   new ProcessBuilder("C:\\Users\\donne\\IdeaProjects\\Weight_control_system\\src\\main\\resources\\Nika\\ADC_Demo.exe").start();
            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            assert initComByDelhi != null;
            initComByDelhi.destroy();
        }
    }
}