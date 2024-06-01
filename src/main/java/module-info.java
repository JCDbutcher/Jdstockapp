module test {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires barcode4j;
    requires java.desktop;
    requires java.mail;
    requires jbcrypt;

    opens test to javafx.fxml;
    opens mainapp to javafx.fxml;
    exports test;
    exports controllers;
    opens controllers to javafx.fxml;
    opens entity to javafx.base, org.hibernate.orm.core, jakarta.persistence;
    exports mainapp;
    opens utils to javafx.base, org.hibernate.orm.core;
}
