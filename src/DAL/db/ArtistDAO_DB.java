package DAL.db;

import BE.Artist;

import java.io.IOException;
import java.sql.*;

public class ArtistDAO_DB {

    private DataBaseConnector SongdatabaseConnector;

    public ArtistDAO_DB()throws IOException {
        SongdatabaseConnector = new DataBaseConnector();
    }

    public Artist insertArtist(Artist artist) throws Exception {

        // SQL command
        String sql = "INSERT INTO Artist (ArtistName) VALUES (?);";

        try (Connection conn = SongdatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            // Bind parameters
            stmt.setString(1, artist.getArtistName());

            // Run the specified SQL statement
            stmt.executeUpdate();

            // Get the generated ID from the DB
            ResultSet rs = stmt.getGeneratedKeys();
            int id = 0;

            if (rs.next()) {
                id = rs.getInt(1);
            }

            return new Artist(id, artist.getArtistName() );
        }
        catch (SQLException ex)
        {
            // create entry in log file
            ex.printStackTrace();
            throw new Exception("Could not insert artist", ex);
        }
    }

    public int getArtistID(String artistName) throws Exception {
        try (Connection conn = SongdatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT ArtistID FROM Artist WHERE ArtistName = ?")) {

            // Set parameters for the PreparedStatement
            stmt.setString(1, artistName);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                return rs.getInt("ArtistID");
            }
            return -1;
        } catch (SQLException e) {
            throw new Exception("Error getting artist ID", e);
        }
    }
}
