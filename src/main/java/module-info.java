module com.control {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.control to javafx.fxml;
    opens com.datastruct to javafx.fxml;
    opens com.logic to javafx.fxml;

    exports com.control;
    exports com.datastruct;
    exports com.logic;
}
