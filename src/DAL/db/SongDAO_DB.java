package DAL.db;

import BE.Song;
import DAL.ISongDataAccess;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDAO_DB implements ISongDataAccess {

    private DataBaseConnector SongdatabaseConnector;

    public SongDAO_DB()throws IOException{
        SongdatabaseConnector = new DataBaseConnector();
    }

    @Override
    public List<Song> getAllSongs() throws Exception {
        ArrayList<Song> allSongs = new ArrayList<>();

        try (Connection conn = SongdatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * " +
                    "FROM Songs " +
                    "JOIN Genre ON Songs.GenreID = Genre.GenreID " +
                    "JOIN Artist ON Songs.ArtistID = Artist.ArtistID;";

            ResultSet rs = stmt.executeQuery(sql);

            // Loop through rows from the database result set
            while (rs.next()) {

                //Map DB row to Movie object
                int id = rs.getInt("SongID");
                String artist = rs.getString("ArtistName");
                String title = rs.getString("SongTitle");
                String genre = rs.getString("GenreType");
                int length = rs.getInt("SongDuration");
                String filePath = rs.getString("FilePath");

                Song song = new Song(id,artist, title, genre, length, filePath);
                allSongs.add(song);
            }
            return allSongs;

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new Exception("Could not get movies from database", ex);
        }
    }

    @Override
    public Song createSongs(Song song, int artistId, int genreId) throws Exception {

        // SQL command
        String sql = "INSERT INTO Songs (SongTitle, SongDuration, ArtistID, GenreID, FilePath) VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = SongdatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Bind parameters
            stmt.setString(1, song.getTitle());
            stmt.setLong(2, song.getLength());
            stmt.setInt(3, artistId);
            stmt.setInt(4, genreId);
            stmt.setString(5, song.getFilePath());
            // Run the specified SQL statement
            stmt.executeUpdate();

            // Get the generated ID from the DB
            ResultSet rs = stmt.getGeneratedKeys();

            int id = 0;
            if (rs.next()) {
                id = rs.getInt(1);
            }

            Song createdSong = new Song(id, song.getArtist(),song.getTitle(),song.getGenre() , song.getLength() , song.getFilePath());

            return createdSong;
        } catch (SQLException ex) {
            // create entry in log file
            ex.printStackTrace();
            throw new Exception("Could not insert song", ex);
        }
    }

    @Override
    public void updateSongs(Song song, int artistId, int genreId) throws Exception {
        // SQL command
        String sql = "UPDATE dbo.Songs SET SongTitle = ?, SongDuration = ?, ArtistID = ?, GenreID = ?, FilePath = ? WHERE SongID = ?";

        try (Connection conn = SongdatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Bind parameters
            stmt.setString(1, song.getTitle());
            stmt.setInt(2, song.getLength());
            stmt.setInt(3, artistId);
            stmt.setInt(4, genreId);
            stmt.setString(5, song.getFilePath());
            stmt.setInt(6, song.getId());

            stmt.executeUpdate();
            // Run the specified SQL statement
        } catch (SQLException ex) {
            // create entry in log file
            ex.printStackTrace();
            throw new Exception("Could not update song", ex);
        }
    }

    @Override
    public void deleteSongs(Song songs) throws Exception {
        Connection conn = null;
        PreparedStatement deletePlaylistStmt = null;
        PreparedStatement deleteSongStmt = null;

        String deletePlaylistSql = "DELETE FROM PlaylistSongs WHERE SongID = ?";
        String deleteSongSql = "DELETE FROM Songs WHERE SongID = ?"; // Ensure 'SongID' is your primary key column

        try {
            conn = SongdatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // First, delete references in PlaylistSongs
            deletePlaylistStmt = conn.prepareStatement(deletePlaylistSql);
            deletePlaylistStmt.setInt(1, songs.getId()); // Ensure this gets the correct primary key
            deletePlaylistStmt.executeUpdate();

            // Then, delete the song
            deleteSongStmt = conn.prepareStatement(deleteSongSql);
            deleteSongStmt.setInt(1, songs.getId()); // Ensure this gets the correct primary key
            int rowsAffected = deleteSongStmt.executeUpdate();

            if (rowsAffected == 0) {
                conn.rollback(); // Rollback as no song was deleted
                throw new Exception("No song found with ID: " + songs.getId());
            }

            conn.commit(); // Commit transaction

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback in case of error
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            throw new Exception("Could not delete song: " + ex.getMessage(), ex);
        } finally {
            if (deletePlaylistStmt != null) deletePlaylistStmt.close();
            if (deleteSongStmt != null) deleteSongStmt.close();
            if (conn != null) conn.close();
        }
    }
}
