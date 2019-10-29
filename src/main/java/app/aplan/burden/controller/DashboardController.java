package app.aplan.burden.controller;

import app.aplan.burden.App;
import app.aplan.burden.Utils;
import com.google.common.base.Strings;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static app.aplan.burden.App.configuration;

public class DashboardController implements Initializable {
    @FXML
    public VBox mainPanel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!Strings.isNullOrEmpty(configuration.getLastOpen())) {
            try {
                open(configuration.getLastOpen());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void openScriptsPanel(ActionEvent actionEvent) throws IOException {
        clear();
        open("/scripts.fxml");
    }

    @FXML
    public void openFirewallPanel(ActionEvent actionEvent) throws IOException {
        clear();
        open("/firewall.fxml");
    }

    private void open(String fxml) throws IOException {
        mainPanel.getChildren().add(App.loadFromFxml(fxml));
        configuration.setLastOpen(fxml);
        Utils.writeConfig(configuration);
    }

    private void clear() {
        mainPanel.getChildren().remove(1, mainPanel.getChildren().size());
    }

    public void close(ActionEvent actionEvent) {
        Platform.exit();
    }
}
