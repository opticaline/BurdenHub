package app.aplan.burden.controller;

import app.aplan.burden.entity.PortForward;
import app.aplan.burden.service.PortForwardService;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

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
        initCreate();
        initDisable();
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

        table.setRowFactory(param -> {
            TableRow<PortForward> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                PortForward pf = row.getItem();
                if (event.getButton() == MouseButton.SECONDARY && pf != null) {
                    cm.show(table, event.getScreenX(), event.getScreenY());
                } else {
                    cm.hide();
                }
            });
            return row;
        });
    }

    private void initCreate() {
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
    }

    private void initDisable() {
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
        disabled.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> enable.setDisable(newValue == null));
        disabled.getItems().addListener((ListChangeListener<PortForward>) c -> disabled.setDisable(c.getList().size() == 0));
    }
}
