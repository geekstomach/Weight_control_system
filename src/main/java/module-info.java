module ru.issp.weight_control_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jssc;
    requires java.logging;


    opens ru.issp.weight_control_system to javafx.fxml;
    exports ru.issp.weight_control_system;
    opens ru.issp.weight_control_system.Model to javafx.fxml;
    exports ru.issp.weight_control_system.Model;
}