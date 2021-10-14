package ru.issp.weight_control_system;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jssc.SerialPortException;
import ru.issp.weight_control_system.utils.PowerSetter;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SetPowerController implements Initializable {
    public TextArea textArea;
    public Button SetPower;
    public TextField setPowerArea;
    public Button toChart;
    public Button setPower;
    public Button startCooling;
    public Button stopCooling;
    public TextField setCoolingTimeArea;
    public Label currentPowerLabel;

    private Scene getWeightScene;

    boolean cooling;
    int timePeriod = 3;

    private ScheduledExecutorService scheduledExecutorService;
    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public void SetPowerButtonClicked(ActionEvent actionEvent) throws SerialPortException, InterruptedException {
        //TODO добавить стринг форматтер для того чтобы исключить ввод неверных данных
        try {

    int power = Integer.parseInt(setPowerArea.getText());
     if ((power>=0)&&(power<=10000)){
         PowerSetter.setPower(power);
         textArea.appendText("Значение мощности изменилось на " + String.valueOf(power) +"\n");
         currentPowerLabel.setText(Integer.toString(PowerSetter.getPOWER()));
}else {
         PowerSetter.setPower(PowerSetter.getPOWER());
         textArea.appendText("ВВеденные значения находятся вне допустимых границ" +"\n");

         textArea.appendText("Значение мощности Осталось прежним" + String.valueOf(power) +"\n");


     }
currentPowerLabel.setText(Integer.toString(PowerSetter.getPOWER()));
}catch (NumberFormatException ex) {
            textArea.appendText("ВВедите цифровое значение." +"\n");

            //System.out.println("NumberFormatException");
}


    }

    public void StartCoolingButtonClicked(ActionEvent actionEvent) {
        cooling = true;
        int coolingTimeInMin = Integer.parseInt(setCoolingTimeArea.getText());
        int currentPower = PowerSetter.getPOWER();
        int cyclesDecrease = (coolingTimeInMin*60)/timePeriod;
        int powerDecrease = currentPower/cyclesDecrease;
        AtomicInteger cycleCount = new AtomicInteger();

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {


    Platform.runLater(() -> {
        System.out.println("Должно срабатывать раз в 3 секунды");
        if (cycleCount.get() <cyclesDecrease) {

            try {
                PowerSetter.setPower(PowerSetter.getPOWER() - powerDecrease);

            } catch (SerialPortException | InterruptedException e) {
                e.printStackTrace();
            }
        currentPowerLabel.setText(Integer.toString(PowerSetter.getPOWER()));
        cycleCount.getAndIncrement();}else{
            try {
                PowerSetter.setPower(0);
                currentPowerLabel.setText(Integer.toString(PowerSetter.getPOWER()));
            } catch (SerialPortException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

            } catch (Throwable e) {
                e.printStackTrace();
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE,"Caught exception in ScheduledExecutorService.",e);
            }}, 0, timePeriod, TimeUnit.SECONDS);
    }
    public void StopCoolingButtonClicked(ActionEvent actionEvent) {
      cooling = false;
      scheduledExecutorService.shutdown();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cooling = false;
        currentPowerLabel.setText("0");
    }

    public void setGetWeightScene(Scene scene) {
        getWeightScene = scene;
    }
    public void switchSceneButtonClicked(ActionEvent actionEvent) throws IOException {
        Stage primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(getWeightScene);

    }
}
