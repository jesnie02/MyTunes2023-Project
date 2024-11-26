package DAL.db;

import BE.Genre;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO_DB {

    private DataBaseConnector SongdatabaseConnector;

    public GenreDAO_DB()throws IOException {
        SongdatabaseConnector = new DataBaseConnector();
    }

    public List<Genre> getAllGenre() throws Exception {
        ArrayList<Genre> allGenre = new ArrayList<>();

        try (Connection conn = SongdatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT GenreType FROM Genre";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String genreType = rs.getString("GenreType");
                Genre genre = new Genre(genreType);
                allGenre.add(genre);
            }

        } catch (SQLServerException e) {
            throw new RuntimeException(e);
        }
        return allGenre;
    }

    public int getGenreId(String genreType) throws Exception {
        try (Connection conn = SongdatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT GenreID FROM Genre WHERE GenreType = ?")) {

            // Set parameters for the PreparedStatement
            stmt.setString(1, genreType);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                return rs.getInt("GenreID");

            }
            return -1;
        } catch (SQLException e) {
            throw new Exception("Error getting genre ID", e);
        }
    }

    public Genre insertGenre(Genre genre) throws Exception {

        // SQL command
        String sql = "INSERT INTO Genre (GenreType) VALUES (?);";

        try (Connection conn = SongdatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Bind parameters
            stmt.setString(1, genre.getGenreType());

            // Run the specified SQL statement
            stmt.executeUpdate();

            // Get the generated ID from the DB
            ResultSet rs = stmt.getGeneratedKeys();
            int id = 0;

            if (rs.next()) {
                id = rs.getInt(1);
            }

            return new Genre(id,genre.getGenreType());
        } catch (SQLException ex) {
            // create entry in log file
            ex.printStackTrace();
            throw new Exception("Could not insert genre", ex);
        }
    }
}
