package GUI.Model;

import BE.User;
import BLL.AuthHandler;

import java.io.IOException;

public class RegisterModel {

    private AuthHandler authHandler;
    private User user;
    private String statusMsg;

    public RegisterModel() throws IOException {
        authHandler = new AuthHandler();
    }

    public boolean usernameEmpty(String username) {
        return username == null || username.isEmpty();
    }

    public boolean passwordEmpty(String password, String repeated) {
        return password.isEmpty() || repeated.isEmpty();
    }

    public boolean passwordMatch(String password, String repeated) {
        return password.equals(repeated);
    }

    public boolean userExists(String username) throws Exception {
        return authHandler.userExists(username);
    }

    public boolean isValidName(String username) {
        if (username == null || username.isEmpty())
            return false;

        String regex = "^[a-zA-Z0-9]+$";
        if (username.matches(regex))
            return false;

        return true;
    }

    public String statusMessage() {
        return statusMsg;
    }

    public boolean createUser(String username, String password, String repeated) throws Exception {
        if (usernameEmpty(username)) {
            statusMsg = "Username is empty";
            return false;
        }

        if (passwordEmpty(password, repeated)) {
            statusMsg = "One of the passwords is empty";
            return false;
        }

        if (isValidName(username)) {
            statusMsg = "Username is not valid";
            return false;
        }

        if (userExists(username)) {
            statusMsg = "Username exists";
            return false;
        }

        if (!passwordMatch(password, repeated)) {
            statusMsg = "Passwords does not match";
            return false;
        }

        authHandler.createUser(new User(username, password));
        statusMsg = "Succesfully created user!";
        return true;
    }

}
