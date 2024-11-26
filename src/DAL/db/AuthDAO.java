package DAL.db;

import BE.User;
import BLL.utils.Encryption;

import java.io.IOException;
import java.sql.*;

public class AuthDAO {

    private DataBaseConnector databaseConnector;

    public AuthDAO() throws IOException {
        databaseConnector = new DataBaseConnector();
    }

    public User loginUser(String username, String password) throws Exception {
        String sql = "SELECT id, username, password FROM users WHERE username = ?";
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && Encryption.decryptString(rs.getString("password")).equals(password)) {
                return new User(rs.getInt("id"), rs.getString("username"), password);
            }
        }
        return null;


    }

    public boolean userExists(String username) throws Exception {
        String sql = "SELECT username FROM dbo.users WHERE username = ?";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String usr = rs.getString("username");
                return usr != null;
            }
        }
        return false;


    }

    public User createUser(User user) throws Exception {
        // SQL command
        String sql = "INSERT INTO dbo.users (username,password) VALUES (?,?);";

        //
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            // Bind parameters
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());

            System.out.println(user.getPassword());

            // Run the specified SQL statement
            stmt.executeUpdate();

            // Get the generated ID from the DB
            ResultSet rs = stmt.getGeneratedKeys();
            int id = 0;

            if (rs.next()) {
                id = rs.getInt(1);
            }

            // Create user object and send up the layers
            User createdUser = new User(id, user.getUsername(), user.getPassword());

            return createdUser;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new Exception("Could not create song", ex);
        }
    }


}
