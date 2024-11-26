package GUI.Controller;

import BE.User;
import GUI.Model.LoginModel;
import GUI.Utility.CurrentUser;
import GUI.Utility.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginWindowController {

    @FXML
    private TextField txtName, txtPass;

    @FXML
    private Label welcomeText;

    private LoginModel loginModel;

    public LoginWindowController() {
        try {
            loginModel = new LoginModel();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void btnLogin(ActionEvent actionEvent) throws Exception {
        try {
            User user = loginModel.loginUser(txtName.getText(), txtPass.getText());
            if (user != null) {
                CurrentUser.getInstance().setCurrentuser(user);
                loadMyTunesWindow();
            } else {
                welcomeText.setText("Incorrect username or password");
            }
        } catch (Exception e) {
            welcomeText.setText("Login failed due to an error.");
            e.printStackTrace();
        }

    }

    private void loadMyTunesWindow() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MyTunes.fxml"));
        Parent root = loader.load();
        MainController myTunesController = loader.getController();

        //create a new stage for the MyTunes window
        Stage stage = new Stage();
        stage.setScene(new Scene(root));

        //apply default theme to window
        ThemeManager.applyTheme(stage.getScene(), "/Styles/DefaultTheme.css");

        //Set title of stage and show
        stage.setTitle("MyTunes");
        stage.show();

        ((Stage) txtName.getScene().getWindow()).close();
    }


    public void updateFields(String username, String password) {
        txtName.setText(username);
        txtPass.setText(password);
    }

    public void btnRegister(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegisterWindow.fxml"));
            Parent root = loader.load();
            RegisterWindowController registerWindowController = loader.getController();
            Stage stage = new Stage();

            registerWindowController.setStage(stage);
            registerWindowController.setParentController(this);

            stage.setTitle("Register");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (Exception e){
            System.out.println("Cant load register");
        }

    }
}