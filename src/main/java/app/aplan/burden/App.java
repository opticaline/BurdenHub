package app.aplan.burden;

import app.aplan.burden.config.Configuration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {
    public static Configuration configuration = Utils.readConfig();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Dashboard");
        primaryStage.setWidth(1366);
        primaryStage.setHeight(768);

/*
        InputStream iconStream = getClass().getResourceAsStream("/icon.png");
        Image image = new Image(iconStream);
        primaryStage.getIcons().add(image);
*/

        primaryStage.setScene(new Scene(loadFromFxml("/dashboard.fxml")));
        new JMetro(primaryStage.getScene(), Style.LIGHT);
        primaryStage.show();
    }

    public static Parent loadFromFxml(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("bundles.language", new Locale("cn", "CN")));
        URL xmlUrl = App.class.getResource(fxml);
        loader.setLocation(xmlUrl);
        return loader.load();
    }
}
