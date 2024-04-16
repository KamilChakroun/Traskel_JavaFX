module com.pi.traskel {
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.j;
    requires java.sql;


    opens com.pi.traskel to javafx.fxml;
    exports com.pi.traskel;
}