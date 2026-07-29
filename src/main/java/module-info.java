module com.farmfrenzy {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;

    opens com.farmfrenzy to javafx.fxml;
    opens com.farmfrenzy.controller to javafx.fxml;

    exports com.farmfrenzy;
    exports com.farmfrenzy.controller;
    exports com.farmfrenzy.engine;
    exports com.farmfrenzy.exception;
    exports com.farmfrenzy.model;
    exports com.farmfrenzy.model.base;
    exports com.farmfrenzy.model.enums;
    exports com.farmfrenzy.repository;
    exports com.farmfrenzy.util;
}
