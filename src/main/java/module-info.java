module code.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires com.zaxxer.hikari;
    // MySQL connector is used on classpath; explicit requires may not be necessary on all setups
    requires org.slf4j;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens code.demo to javafx.fxml;
    opens code.demo.controller to javafx.fxml;
    exports code.demo;
}