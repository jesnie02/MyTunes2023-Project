package GUI.Controller;

import GUI.Model.RegisterModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterWindowController {

    @FXML
    private Label lblStatus;

    @FXML
    private TextField txtUsername, txtPass, txtPassRepeat;

    private RegisterModel registerModel;
    private Stage stage;
    private LoginWindowController controller;

    public RegisterWindowController() {
        try {
            registerModel = new RegisterModel();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setParentController(LoginWindowController controller) {
        this.controller = controller;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void updateLabel(String text) {
        lblStatus.setText(text);
    }

    public void btnRegister(ActionEvent actionEvent) throws Exception {
        String username = txtUsername.getText();
        String password = txtPass.getText();

        boolean created = registerModel.createUser(username, password, txtPassRepeat.getText());
        updateLabel(registerModel.statusMessage());

        if (created) {
            Thread.sleep(500);
            controller.updateFields(username, password);
            stage.close();
        }
    }
}
