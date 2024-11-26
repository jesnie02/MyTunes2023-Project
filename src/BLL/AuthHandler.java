package BLL;

import BE.User;
import DAL.db.AuthDAO;

import java.io.IOException;

public class AuthHandler {

    private AuthDAO authDAO;

    public AuthHandler() throws IOException {
        authDAO = new AuthDAO();
    }

    public User loginUser(String username, String password) throws Exception {
        return authDAO.loginUser(username, password);
    }

    public User createUser(User user) throws Exception {
        return authDAO.createUser(user);
    }

    public boolean userExists(String username) throws Exception {
        return authDAO.userExists(username);
    }
}
