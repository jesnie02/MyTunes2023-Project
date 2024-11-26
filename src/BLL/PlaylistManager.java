package BLL;

import BE.PlayList;
import BE.Song;
import DAL.IPlaylistDataAccess;
import DAL.db.PlaylistDAO_DB;
import GUI.Utility.CurrentUser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PlaylistManager {
    private IPlaylistDataAccess playlistDAO;

    private PlaylistDAO_DB playlistDAO_DB;


    public PlaylistManager() throws IOException {
        playlistDAO = new PlaylistDAO_DB();
        this.playlistDAO_DB = new PlaylistDAO_DB();
    }

    public void deleteSongFromPlaylist(Song song, int playlistId) throws Exception {
        playlistDAO_DB.deleteSongFromPlaylist(song, playlistId);
    }

    public List<PlayList> getAllPlaylists() throws Exception {
        return playlistDAO.getAllUserPlaylist(CurrentUser.getInstance().getCurrentuser().getID());
    }

    public PlayList createPlaylist(String name) throws Exception {
        PlayList newPlaylist = new PlayList(-1,name,0,0);
        return playlistDAO.createPlaylist(newPlaylist, CurrentUser.getInstance().getCurrentuser().getID());
    }

    public void updatePlaylist(PlayList playlist) throws Exception {
        playlistDAO.updatePlaylist(playlist);
    }

    public void deletePlaylist(PlayList playlist) throws Exception {
        playlistDAO.deletePlaylist(playlist);
    }

    public int getSongCountForPlaylist(int playlistId) {
        return playlistDAO_DB.getSongCountForPlaylist(playlistId);
    }

    public List<Song> getSongsForPlaylist(int playlistId) throws Exception {
        return playlistDAO_DB.getSongsForPlaylist(playlistId);
    }

    public void addSongToPlaylist(Song song, PlayList playlist) throws Exception {
        playlistDAO_DB.addSongToPlaylist(song, playlist);
    }

    public void updateSongOrderInPlaylist(int playlistId, List<Song> songs) throws SQLException {
        playlistDAO_DB.updateSongOrder(playlistId, songs);
    }
}
