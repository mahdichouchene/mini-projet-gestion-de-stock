module com.example.demogestionstockisimm {
    requires javafx.controls;
    requires javafx.fxml;
    opens com.example.demogestionstockisimm to javafx.fxml;
    opens com.example.demogestionstockisimm.controller to javafx.fxml;
    opens com.example.demogestionstockisimm.model to javafx.base;
    opens com.example.demogestionstockisimm.datastore to javafx.fxml;
    exports com.example.demogestionstockisimm;requires java.sql;
    opens com.example.demogestionstockisimm.dao to javafx.fxml;

}