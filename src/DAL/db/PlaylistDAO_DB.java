package DAL.db;

import BE.PlayList;
import BE.Song;
import DAL.IPlaylistDataAccess;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO_DB implements IPlaylistDataAccess {
    private DataBaseConnector playlistdatabaseConnector;

    public PlaylistDAO_DB()throws IOException {
        playlistdatabaseConnector = new DataBaseConnector();
    }



    @Override
    public List<PlayList> getAllPlaylists() throws Exception {
        List<PlayList> allPlaylists = new ArrayList<>();
        String sql = "SELECT playlist.PlaylistID, playlist.PlaylistName, COUNT(ps.SongID) as TotalSongs, coalesce(SUM(Songs.SongDuration),0) as TotalDuration\n" +
                "From Playlist playlist\n" +
                "Left JOIN PlaylistSongs ps ON playlist.PlaylistID = ps.PlaylistID\n" +
                "Left JOIN Songs songs ON ps.SongID = songs.SongID\n" +
                "GROUP BY playlist.PlaylistID, playlist.PlaylistName";


        try (Connection conn = playlistdatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {



            while (rs.next()) {
                int id = rs.getInt("PlaylistID");
                String name = rs.getString("PlaylistName");
                int playlistDuration = rs.getInt("TotalDuration");
                int TotalSongs = rs.getInt("TotalSongs");
                PlayList playlist = new PlayList(id, name, TotalSongs,playlistDuration );
                allPlaylists.add(playlist);


            }
        } catch (SQLException ex) {
            throw new Exception("Error retrieving all playlists: " + ex.getMessage(), ex);
        }
        return allPlaylists;
    }

    @Override
    public List<PlayList> getAllUserPlaylist(int UserID) throws Exception {
        List<PlayList> allUserPlaylist = new ArrayList<>();
        String sql = "SELECT p.PlaylistID, p.PlaylistName, COUNT(ps.SongID) as TotalSongs,\n" +
                "COALESCE(SUM(s.SongDuration), 0) as TotalDuration\n" +
                "FROM Playlist p\n" +
                "INNER JOIN userPlaylist up ON p.PlaylistID = up.playlistID\n" +
                "LEFT JOIN PlaylistSongs ps ON p.PlaylistID = ps.PlaylistID\n" +
                "LEFT JOIN Songs s ON ps.SongID = s.SongID\n" +
                "WHERE up.userID = ?\n" +
                "GROUP BY p.PlaylistID, p.PlaylistName";


        try (Connection conn = playlistdatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

              stmt.setInt(1, UserID);


            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("PlaylistID");
                    String name = rs.getString("PlaylistName");
                    int playlistDuration = rs.getInt("TotalDuration");
                    int TotalSongs = rs.getInt("TotalSongs");
                    PlayList playlist = new PlayList(id, name, TotalSongs, playlistDuration);
                    allUserPlaylist.add(playlist);


                }
            } catch (SQLException ex) {
                throw new Exception("Error retrieving all playlists: " + ex.getMessage(), ex);
            }
            return allUserPlaylist;

        }
    }



    @Override
    public PlayList createPlaylist(PlayList playlist, int userid) throws Exception {
        // The SQL command
        String sqlP = "INSERT INTO Playlist (PlaylistName) VALUES (?);";

        String sqlU = "INSERT INTO userPlaylist (playlistID, userId) VALUES (?,?);";

        try (Connection conn = playlistdatabaseConnector.getConnection();
             PreparedStatement pstmtP = conn.prepareStatement(sqlP, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pstmtU = conn.prepareStatement(sqlU)) {
                conn.setAutoCommit(false);

            // Bind the name parameter

            pstmtP.setString(1, playlist.getName());

            // Execute the insert operation
            int affectedRows = pstmtP.executeUpdate();

            // Check if the insert was successful
            if (affectedRows == 0) {
                throw new SQLException("Creating playlist failed, no rows affected.");
            }
            int newPlaylistID = -1;
            try (ResultSet generatedKeys = pstmtP.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Assuming your Playlist table has an auto-incremented ID
                    newPlaylistID  = generatedKeys.getInt(1);
                    //return new PlayList(playlist.getId(),playlist.getName() ); // Return the new Playlist object with the generated id
                } else {
                    throw new SQLException("Creating playlist failed, no ID obtained.");
                }
            }
            pstmtU.setInt(1,newPlaylistID);
            pstmtU.setInt(2, userid);
            pstmtU.executeUpdate();
            conn.commit();
            playlist.setId(newPlaylistID);
            return playlist;
        } catch (SQLException ex) {
            try(Connection conn = playlistdatabaseConnector.getConnection()) {
                conn.rollback();
            }catch (SQLException e){
                ex.addSuppressed(e);
            }
            // Handle any SQL exceptions here
            throw new Exception("Error creating playlist: " + ex.getMessage(), ex);
        }

    }

    @Override
    public void updatePlaylist(PlayList playlist) throws Exception {
        String sql = "UPDATE Playlist SET PlaylistName = ? WHERE PlaylistID = ?";
        try (Connection conn = playlistdatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playlist.getName());
            pstmt.setInt(2, playlist.getId());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            throw new Exception("Error updating playlist: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void deletePlaylist(PlayList playlist) throws SQLException {
        Connection conn = null;
        PreparedStatement deletePlaylistSongsStmt = null;
        PreparedStatement deletePlaylistStmt = null;
        PreparedStatement userdeletePlaylistStmt = null;

        try {
            conn = playlistdatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // First, delete references in PlaylistSongs
            String deletePlaylistSongsSql = "DELETE FROM PlaylistSongs WHERE PlaylistID = ?";
            deletePlaylistSongsStmt = conn.prepareStatement(deletePlaylistSongsSql);
            deletePlaylistSongsStmt.setInt(1, playlist.getId());
            deletePlaylistSongsStmt.executeUpdate();

            String userdeletePlaylistSql = "DELETE FROM userPlaylist WHERE playlistID = ?";
            userdeletePlaylistStmt = conn.prepareStatement(userdeletePlaylistSql);
            userdeletePlaylistStmt.setInt(1, playlist.getId());
            userdeletePlaylistStmt.executeUpdate();

            // Then, delete the playlist
            String deletePlaylistSql = "DELETE FROM Playlist WHERE PlaylistID = ?";
            deletePlaylistStmt = conn.prepareStatement(deletePlaylistSql);
            deletePlaylistStmt.setInt(1, playlist.getId());
            int affectedRows = deletePlaylistStmt.executeUpdate();

            if (affectedRows == 0) {
                conn.rollback(); // Rollback as no playlist was deleted
                throw new SQLException("Deleting playlist failed, no rows affected.");
            }

            conn.commit(); // Commit transaction

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback in case of error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Error deleting playlist: " + playlist.getName(), e);
        } finally {
            if (deletePlaylistSongsStmt != null) deletePlaylistSongsStmt.close();
            if (deletePlaylistStmt != null) deletePlaylistStmt.close();
            if (conn != null) conn.close();
        }
    }


    public int getSongCountForPlaylist(int playlistId) {
        // SQL query to count songs in a specific playlist
        String sql = "SELECT COUNT(*) FROM dbo.PlaylistSongs WHERE PlaylistId = ?";

        try (Connection conn = playlistdatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the playlistId parameter in the query
            pstmt.setInt(1, playlistId);

            try (ResultSet rs = pstmt.executeQuery()) {
                // If the result set is valid, return the count
                if (rs.next()) {
                    return rs.getInt(1); // The count is in the first column of the result set
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle this exception more appropriately in a real application
        }
        // Return 0 or throw an exception if there was an error or the playlist does not exist
        return 0;
    }

    public List<Song> getSongsForPlaylist(int playlistId) {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT s.SongID, s.SongTitle, s.SongDuration, s.FilePath, a.ArtistName, g.GenreType " +
                "FROM Songs s " +
                "INNER JOIN Artist a ON s.ArtistID = a.ArtistID " +
                "INNER JOIN Genre g ON s.GenreID = g.GenreID " +
                "INNER JOIN PlaylistSongs ps ON s.SongID = ps.SongID " +
                "WHERE ps.PlaylistID = ?" +
                "ORDER BY ps.SortID asc ";

        try (Connection conn = playlistdatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playlistId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("SongID");
                    String title = rs.getString("SongTitle");
                    int duration = rs.getInt("SongDuration");
                    String filePath = rs.getString("FilePath");
                    String artistName = rs.getString("ArtistName");
                    String genreType = rs.getString("GenreType");

                    Song song = new Song(id, artistName, title, genreType, duration, filePath);
                    songs.add(song);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Håndter eventuelle undtagelser her
        }

        return songs;
    }

    public void addSongToPlaylist(Song song, PlayList playlist) throws SQLException {
        String sql = "INSERT INTO PlaylistSongs (PlaylistID, SongID) VALUES (?, ?);";

        try (Connection conn = playlistdatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playlist.getId());
            pstmt.setInt(2, song.getId());


            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Adding song to playlist failed, no rows affected.");
            }
        } catch (SQLException ex) {
            throw new SQLException("Error adding song to playlist: " + ex.getMessage(), ex);
        }
    }

    public void deleteSongFromPlaylist(Song song, int playlistId) throws SQLException {
        String sql = "DELETE FROM PlaylistSongs WHERE PlaylistID = ? AND SongID = ?";

        try (Connection conn = playlistdatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playlistId);
            pstmt.setInt(2, song.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting song from playlist failed, no rows affected.");
            }
        } catch (SQLException ex) {
            throw new SQLException("Error deleting song from playlist: " + ex.getMessage(), ex);
        }
    }

    public void updateSongOrder(int playlistId, List<Song> songs) throws SQLException {
        String sql = "UPDATE PlaylistSongs SET SortID = ? WHERE PlaylistID = ? AND SongID = ?";

        try (Connection conn = playlistdatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // Disable auto-commit for batch execution

            for (Song song : songs) {
                pstmt.setInt(1, song.getSortOrder());
                pstmt.setInt(2, playlistId);
                pstmt.setInt(3, song.getId());
                pstmt.addBatch(); // Add to batch
            }

            pstmt.executeBatch(); // Execute batch
            conn.commit(); // Commit the transaction
        } catch (SQLException e) {
            // Rollback or handle the exception as needed
            throw e;
        }
    }


}


