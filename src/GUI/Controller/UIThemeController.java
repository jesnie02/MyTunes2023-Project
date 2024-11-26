package GUI.Controller;

import GUI.Utility.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class UIThemeController {
    private Stage primaryStage; // You need to set this stage in your main application
    public Button toggleBlackGreen;
    public Button toggleBlackGreyOrange;
    public Button toggleBlackPurpleWhite;
    public Button toggleDarkPurpleBlue;
    @FXML
    private AnchorPane rootPane;

    private boolean isBlackAndGreenTheme = false;
    private boolean isBlackGreyOrangeTheme = false;
    private boolean isBlackPurpleWhiteTheme = false;
    private boolean isDarkPurpleBlueTheme = false;
    private boolean isMintTheme = false;


    @FXML
    public void initialize() {
        // Apply the default theme when the controller is initialized
        String defaultThemePath = "/Styles/DefaultTheme.css";
        toggleTheme(defaultThemePath, false);
        ThemeManager.applyThemeToAllWindows(defaultThemePath);
    }

    public void SaveTheme(ActionEvent actionEvent) {
    }

    public void CloseThemeWindow(ActionEvent actionEvent) {
        Window window = rootPane.getScene().getWindow();
        ThemeManager.removeScene(window.getScene());
        window.hide();
    }

    @FXML
    public void SwitchToBlackGreen(ActionEvent actionEvent) {
        String cssPath = "/Styles/BlackAndGreen.css";
        toggleTheme(cssPath, isBlackAndGreenTheme);
        ThemeManager.applyThemeToAllWindows(cssPath);
        isBlackAndGreenTheme = !isBlackAndGreenTheme;
    }

    @FXML
    public void SwitchToBlackGreyOrange(ActionEvent actionEvent) {
        String cssPath = "/Styles/BlackGreyOrange.css";
        toggleTheme(cssPath, isBlackGreyOrangeTheme);
        ThemeManager.applyThemeToAllWindows(cssPath);
        isBlackGreyOrangeTheme = !isBlackGreyOrangeTheme;
    }

    @FXML
    public void SwitchToBlackPurpleWhite(ActionEvent actionEvent) {
        String cssPath = "/Styles/BlackPurpleWhite.css";
        toggleTheme(cssPath, isBlackPurpleWhiteTheme);
        ThemeManager.applyThemeToAllWindows(cssPath);
        isBlackPurpleWhiteTheme = !isBlackPurpleWhiteTheme;
    }

    @FXML
    public void SwitchToDarkPurpleBlue(ActionEvent actionEvent) {
        String cssPath = "/Styles/DarkPurpleBlue.css";
        toggleTheme(cssPath, isDarkPurpleBlueTheme);
        ThemeManager.applyThemeToAllWindows(cssPath);
        isDarkPurpleBlueTheme = !isDarkPurpleBlueTheme;
    }

    public void SwitchToMint(ActionEvent actionEvent) {
        String cssPath = "/Styles/Mint.css";
        toggleTheme(cssPath, isMintTheme);
        ThemeManager.applyThemeToAllWindows(cssPath);
        isMintTheme = !isMintTheme;
    }

    private void toggleTheme(String cssPath, boolean isThemeApplied) {
        if (isThemeApplied) {
            // Remove theme
            rootPane.getStylesheets().remove(getClass().getResource(cssPath).toExternalForm());
        } else {
            // Apply theme
            rootPane.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        }
    }
}

