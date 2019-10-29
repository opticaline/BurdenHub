package app.aplan.burden.controller;

import app.aplan.burden.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class DashboardController {
    @FXML
    public VBox mainPanel;

    public void openScriptsPanel(ActionEvent actionEvent) throws IOException {
        clear();
        mainPanel.getChildren().add(App.loadFromFxml("/scripts.fxml"));
    }

    @FXML
    public void openFirewallPanel(ActionEvent actionEvent) throws IOException {
        clear();
        mainPanel.getChildren().add(App.loadFromFxml("/firewall.fxml"));
    }

    private void clear() {
        mainPanel.getChildren().remove(1, mainPanel.getChildren().size());
    }
}
