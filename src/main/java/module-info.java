module com.example.rocketmissionsimulatormodule {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.rocketmissionsimulatormodule to javafx.fxml;
    exports com.example.rocketmissionsimulatormodule;
}