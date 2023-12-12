module org.application.ship_fx {
    requires javafx.controls;
    requires javafx.fxml;

    exports org.application.ship_fx.gui;
    opens org.application.ship_fx.gui to javafx.fxml;
    exports org.application.ship_fx.controller;
    opens org.application.ship_fx.controller to javafx.fxml;

}