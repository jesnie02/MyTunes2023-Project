package BE;

import BLL.utils.Encryption;

public class User {

    private int id;
    private String username, password;

    public User(int id, String username, String password) throws Exception {
        this.id = id;
        this.username = username;
        setPassword(password);
    }

    public User(String username, String password) throws Exception {
        this.username = username;
        setPassword(password);
    }

    private void setPassword(String str) throws Exception {
        this.password = Encryption.encryptString(str);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getID() {
        return id;
    }
}
