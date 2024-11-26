package GUI.Model;

import BE.User;
import BLL.AuthHandler;

import java.io.IOException;

public class LoginModel {

    private AuthHandler authHandler;

    public LoginModel() throws IOException {
        authHandler = new AuthHandler();
    }

    public User loginUser(String username, String password) throws Exception {
        return authHandler.loginUser(username, password);

    }
}
