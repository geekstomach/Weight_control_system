package ru.issp.weight_control_system;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import jssc.SerialPortException;
import ru.issp.weight_control_system.utils.PowerSetter;

public class SetPowerController {
    public TextArea textArea;
    public Button SetPower;
    public TextField setPowerArea;

    public void SetPowerButtonClicked(ActionEvent actionEvent) throws SerialPortException, InterruptedException {
        //TODO добавить стринг форматтер для того чтобы исключить ввод неверных данных
        try {

    int power = Integer.parseInt(setPowerArea.getText());
     if ((power>=0)&&(power<=10000)){
         PowerSetter.setPower(power);
         PowerSetter.POWER = power;
textArea.appendText("Значение мощности изменилось на " + String.valueOf(power));
}else {
         PowerSetter.setPower(PowerSetter.POWER);
         textArea.appendText("ВВеденные значения находятся вне допустимых границ");

         textArea.appendText("Значение мощности Осталось прежним" + String.valueOf(power));

     }

}catch (NumberFormatException ex) {
            textArea.appendText("ВВедите цифровое значение.");

            //System.out.println("NumberFormatException");
}


    }

}
