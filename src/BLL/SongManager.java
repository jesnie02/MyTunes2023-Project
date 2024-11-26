package BLL;

import BE.Artist;
import BE.Genre;
import BE.Song;
import BLL.Utility.SongSearcher;
import DAL.ISongDataAccess;

import DAL.db.ArtistDAO_DB;
import DAL.db.GenreDAO_DB;
import DAL.db.SongDAO_DB;

import java.io.IOException;
import java.util.List;

public class SongManager {
    private SongSearcher songSearcher = new SongSearcher();

    private ISongDataAccess songDAO;

    private GenreDAO_DB genreDAO_DB;

    private ArtistDAO_DB artistDAO_db;

    public  SongManager() throws IOException {
        songDAO = new SongDAO_DB();
        genreDAO_DB = new GenreDAO_DB();
        artistDAO_db = new ArtistDAO_DB();
    }

    public List<Song> getAllSongs() throws Exception {
        return songDAO.getAllSongs();
    }

    public List<Song> searchSongs(String query) throws Exception {
        List<Song> allSongs = getAllSongs();
        List<Song> searchResult = songSearcher.search(allSongs, query);
        return searchResult;
    }

    public Song createNewSong(Song newSong) throws Exception {

        Artist artist = new Artist(-1, newSong.getArtist());
        Genre genre = new Genre(-1, newSong.getGenre());
        artist.setArtistID(artistDAO_db.getArtistID(newSong.getArtist()));
        genre.setGenreID(genreDAO_DB.getGenreId(newSong.getGenre()));
        if (artist.getArtistID()== -1) {
            artist = artistDAO_db.insertArtist(new Artist(-1,newSong.getArtist()));

        }

        return songDAO.createSongs(newSong,artist.getArtistID(),genre.getGenreID());
    }

    public void updateSongs(Song selectedSong) throws Exception {

        Artist artist = new Artist(-1, selectedSong.getArtist());
        Genre genre = new Genre(-1, selectedSong.getGenre());
        artist.setArtistID(artistDAO_db.getArtistID(selectedSong.getArtist()));
        genre.setGenreID(genreDAO_DB.getGenreId(selectedSong.getGenre()));
        if (artist.getArtistID()== -1) {
            artist = artistDAO_db.insertArtist(new Artist(-1,selectedSong.getArtist()));

        }
        songDAO.updateSongs(selectedSong, artist.getArtistID(),genre.getGenreID());
    }

    public void deleteSong(Song selectedSong) throws Exception {
        songDAO.deleteSongs(selectedSong);
    }
}
