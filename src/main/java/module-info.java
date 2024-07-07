module com.example.jenproj2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.jenproj2 to javafx.fxml;
    exports com.example.jenproj2;
}