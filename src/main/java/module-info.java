module com.farmfrenzy {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.farmfrenzy to javafx.fxml;
    opens com.farmfrenzy.controller to javafx.fxml;

    exports com.farmfrenzy;
    exports com.farmfrenzy.controller;
    exports com.farmfrenzy.model;
    exports com.farmfrenzy.db;
    exports com.farmfrenzy.util;
}
