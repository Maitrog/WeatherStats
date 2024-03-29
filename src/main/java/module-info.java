module com.maitrog.weatherstats {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires jackson.core;
    requires com.jfoenix;
    requires java.sql;
    requires jackson.databind;
    requires com.microsoft.sqlserver.jdbc;
    requires org.apache.commons.codec;

    requires jfreechart;
    requires MaterialFX;

    opens com.maitrog.weatherstats to javafx.fxml, javafx.controls;
    opens com.maitrog.controllers to javafx.fxml, com.jfoenix, java.sql, javafx.controls, jackson.core, jackson.databind;
    opens com.maitrog.models to jackson.core, jackson.databind, java.sql, com.microsoft.sqlserver.jdbc;

    exports com.maitrog.weatherstats;
}