package app.aplan.burden;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Dashboard");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);

/*
        InputStream iconStream = getClass().getResourceAsStream("/icon.png");
        Image image = new Image(iconStream);
        primaryStage.getIcons().add(image);
*/

        primaryStage.setScene(new Scene(loadFromFxml("/dashboard.fxml")));
        primaryStage.show();
    }

    public static Parent loadFromFxml(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = App.class.getResource(fxml);
        loader.setLocation(xmlUrl);
        return loader.load();
    }
}
