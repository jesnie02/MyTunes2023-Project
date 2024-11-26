import GUI.Utility.ThemeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {
    public static void main (String[] args){
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the LoginWindow.fxml using an FXMLLoader
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginWindow.fxml"));
        Parent root = loader.load(); // Load the root node
        Scene scene = new Scene(root);

        // Apply the default theme
        ThemeManager.applyTheme(scene, "/Styles/DefaultTheme.css");

        primaryStage.setScene(scene); // Set the scene to the loaded root
        primaryStage.setTitle("Login"); // Set the title of the stage
        Image image = new Image("/Images/Capture.PNG");
        primaryStage.getIcons().add(image);

        // Set the stage not resizable
        primaryStage.setResizable(false);

        primaryStage.show(); // Display the stage

    }
}
