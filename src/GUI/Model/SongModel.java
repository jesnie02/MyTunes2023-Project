package GUI.Model;

import BE.Song;
import BLL.SongManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class SongModel {

    private ObservableList<Song> songsToBeViewed;
    private SongManager songManager;


    public SongModel() throws Exception {
        songManager = new SongManager();
        songsToBeViewed = FXCollections.observableArrayList();
        songsToBeViewed.addAll(songManager.getAllSongs());
    }

    public ObservableList<Song> getObservableSongs() {
        return songsToBeViewed;
    }

    public void searchSongs(String query) throws Exception {
        List<Song> searchResults = songManager.searchSongs(query);
        songsToBeViewed.clear();
        songsToBeViewed.addAll(searchResults);
    }

    public void createNewSongs(Song newSongs) throws Exception {
        Song s = songManager.createNewSong(newSongs);
        songsToBeViewed.add(s);

    }

    public void updateSong(Song updatedSong) throws Exception {
        // update song in DAL layer (through the layers)
        songManager.updateSongs(updatedSong);

        for (Song song : songsToBeViewed) {
            if (song.getId() == updatedSong.getId()) {
                song.setTitle(updatedSong.getTitle());
                song.setLength(updatedSong.getLength());
                song.setArtist(updatedSong.getArtist());
                song.setGenre(updatedSong.getGenre());
                song.setFilePath(updatedSong.getFilePath());
                break; // Exit the loop after finding the matching song
            }
        }
    }

    public void deleteSong(Song selectedSongs) throws Exception {
        // delete song in DAL layer (through the layers)
        songManager.deleteSong(selectedSongs);

        // remove from observable list (and UI)
        songsToBeViewed.remove(selectedSongs);
    }
}
