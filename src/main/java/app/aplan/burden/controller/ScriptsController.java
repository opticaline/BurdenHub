package app.aplan.burden.controller;

import app.aplan.burden.Utils;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ScriptsController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(ScriptsController.class);
    public ScrollPane consoleScroll;
    public TextFlow console;
    public GridPane custom;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Path> list = Files.list(Utils.scriptsDirectory())
                    .filter(path -> !path.endsWith(Utils.scriptSuffix()))
                    .collect(Collectors.toList());
            List<List<Path>> temp = Lists.partition(list, 5);
            for (int i = 0; i < temp.size(); i++) {
                for (int j = 0; j < temp.get(i).size(); j++) {
                    Path path = temp.get(i).get(j);
                    Button button = new Button(path.getFileName().toString());
                    button.setOnAction(event -> {
                        info("Run script " + path.toString());
                        try {
                            Utils.callInThread("\"" + path.toString() + "\"", this::info, this::error);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    custom.add(button, j, i);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        consoleScroll.vvalueProperty().bind(console.heightProperty());
    }

    public void connect(ActionEvent actionEvent) {
        error("connect");
    }

    private void line(Text text) {
        Platform.runLater(() -> console.getChildren().add(text));
    }

    private void info(String text) {
        Text line = new Text(text + "\n");
        line.setFill(Paint.valueOf("#000000"));
        line(line);
        logger.info(text);
    }

    private void error(String text) {
        Text line = new Text(text + "\n");
        line.setFill(Paint.valueOf("#DC322F"));
        line(line);
        logger.error(text);
    }
}
