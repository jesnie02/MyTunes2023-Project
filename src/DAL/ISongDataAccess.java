package DAL;

import BE.Song;

import java.util.List;

public interface ISongDataAccess {
    public List<Song> getAllSongs() throws Exception;

    public Song createSongs(Song song, int artistId, int genreId) throws Exception;

    public void updateSongs(Song song, int artistId, int genreId) throws Exception;

    public void deleteSongs(Song songs) throws Exception;

}

