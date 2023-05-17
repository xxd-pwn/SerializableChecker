module com.example.mychecker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jedis;


    opens com.example.mychecker to javafx.fxml;
    exports com.example.mychecker;
}