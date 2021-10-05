module com.maitrog.weatherstats {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires jackson.core;
    requires com.jfoenix;
    requires java.sql;
    requires jackson.databind;
    requires com.microsoft.sqlserver.jdbc;

    opens com.maitrog.weatherstats to javafx.fxml, javafx.controls;
    opens Controllers to javafx.fxml, com.jfoenix, java.sql, javafx.controls, jackson.core, jackson.databind;
    opens Models to jackson.core, jackson.databind, java.sql, com.microsoft.sqlserver.jdbc;

    exports com.maitrog.weatherstats;
}