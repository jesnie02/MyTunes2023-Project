package GUI.Model;

import BE.PlayList;
import BE.Song;
import BLL.PlaylistManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.List;

public class PlaylistModel {
    private ObservableList<PlayList> playlists;
    private PlaylistManager playlistManager;
    private String playlistName;
    private ObservableList<Song> songsInPlaylist = FXCollections.observableArrayList();
    private PlayList currentPlaylist;


    public PlaylistModel() throws IOException {
        playlistManager = new PlaylistManager();
        playlists = FXCollections.observableArrayList();
    }

    public ObservableList<PlayList> getPlaylists() {
        return playlists;
    }

    public void loadPlaylists() {
        List<PlayList> playlistData = null;
        try {
            playlistData = playlistManager.getAllPlaylists();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (PlayList playlist : playlistData) {
            int songCount = playlistManager.getSongCountForPlaylist(playlist.getId());
            playlist.setNumberOfSongs(songCount);
        }
        playlists.setAll(playlistData);
    }

    public void createPlaylist(String playlistName) {
        PlayList newPlaylist = null;
        try {
            newPlaylist = playlistManager.createPlaylist(playlistName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        playlists.add(newPlaylist);
    }

    public void deletePlaylist(PlayList playlist) throws Exception {
        playlistManager.deletePlaylist(playlist);
    }

    public ObservableList<Song> getSongsInPlaylist() {
        return songsInPlaylist;
    }

    public void loadSongsForPlaylist(int playlistId) {
        List<Song> songsList = null;
        try {
            songsList = playlistManager.getSongsForPlaylist(playlistId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        songsInPlaylist.clear();
        songsInPlaylist.addAll(songsList);
    }

    public void updatePlaylist(int id, String newPlaylistName) {
        try {
            playlistManager.updatePlaylist(new PlayList(id, newPlaylistName));
            loadPlaylists();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteSongFromPlaylist(Song song, PlayList playlist) throws Exception {
        playlistManager.deleteSongFromPlaylist(song, playlist.getId());
    }

    public void updateSongOrder(int playlistId, List<Song> songs) {
        // Loop through the songs to update their new order in the ObservableList
        for (int i = 0; i < songs.size(); i++) {
            songs.get(i).setSortOrder(i);
        }
        // Update the database with the new order
        try {
            playlistManager.updateSongOrderInPlaylist(playlistId, songs);
            // Optionally reload songs for the playlist to reflect the new order in the UI
            loadSongsForPlaylist(playlistId);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions (e.g., log the error, show an error alert to the user)
        }
    }
}