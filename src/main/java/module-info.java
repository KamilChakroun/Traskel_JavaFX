module com.pi.traskel {
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.j;
    requires java.sql;
    requires java.mail;


    opens com.pi.traskel to javafx.fxml;
    exports com.pi.traskel;
    exports Entities;
    opens Entities to javafx.fxml;
    exports Controllers;
    opens Controllers to javafx.fxml;
    exports Services;
    opens Services to javafx.fxml;
}