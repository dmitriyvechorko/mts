module mts {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens mts to javafx.fxml;
    exports mts;
    exports mts.controllers to javafx.fxml;
    requires java.sql;
    requires static lombok;
    opens mts.controllers to javafx.fxml;
    requires javafx.base;
    opens mts.models to javafx.base;
}