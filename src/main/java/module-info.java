module ru.issp.weight_control_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens ru.issp.weight_control_system to javafx.fxml;
    exports ru.issp.weight_control_system;
}