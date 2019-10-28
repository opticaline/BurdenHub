package app.aplan.burden.controller;

import app.aplan.burden.entity.PortForward;
import app.aplan.burden.service.PortForwardService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class FirewallController implements Initializable {
    private PortForwardService pfService = new PortForwardService();
    public ComboBox<String> listenAddress;
    public TextField listenPort;
    public ComboBox<String> connectAddress;
    public TextField connectPort;
    @FXML
    public Button create;
    @FXML
    public TableView<PortForward> table;
    public ComboBox<PortForward> disabled;
    public Button enable;

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(pfService.getPortForwards());
        table.refresh();
        initMenu();
        ObservableList<String> address = FXCollections.observableArrayList(pfService.getAddress());
        listenAddress.setItems(address);
        connectAddress.setItems(address);
        create.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Create this proxy?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                PortForward pf = new PortForward();
                pf.setListenAddress(listenAddress.getValue());
                pf.setListenPort(Integer.parseInt(listenPort.getText()));
                pf.setConnectAddress(connectAddress.getValue());
                pf.setConnectPort(Integer.parseInt(connectPort.getText()));
                try {
                    pfService.create(pf);
                    table.getItems().add(pf);
                    table.refresh();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        disabled.setItems(FXCollections.observableArrayList(pfService.loadDisabledProxy()));
        enable.setOnAction(event -> {
            PortForward pf = disabled.getSelectionModel().getSelectedItem();
            pfService.enable(pf);
            disabled.getItems().remove(pf);
            try {
                pfService.create(pf);
                table.getItems().add(pf);
                table.refresh();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void initMenu() {
        ContextMenu cm = new ContextMenu();
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> {
            PortForward pf = table.getSelectionModel().getSelectedItem();
            try {
                pfService.remove(pf);
                table.getItems().remove(pf);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        cm.getItems().add(delete);
        MenuItem disable = new MenuItem("Disable");
        disable.setOnAction(event -> {
            PortForward pf = table.getSelectionModel().getSelectedItem();
            pfService.disable(pf);
            disabled.getItems().add(pf);
            delete.getOnAction().handle(event);
        });
        cm.getItems().add(disable);

        table.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY && table.getSelectionModel().getSelectedItem() != null) {
                cm.show(table, event.getScreenX(), event.getScreenY());
            }
        });
    }
}
