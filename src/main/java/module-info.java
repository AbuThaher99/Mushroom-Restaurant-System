module com.example.mushroom {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires kernel;
    requires layout;
    requires io;
    requires itextpdf;


    opens com.example.mushroom to javafx.fxml;
    exports com.example.mushroom;
}