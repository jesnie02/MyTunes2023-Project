package BLL.Utility;

import BE.Song;

import java.util.ArrayList;
import java.util.List;

public class SongSearcher {
    public List<Song> search(List<Song> searchBase, String query) {
        List<Song> searchResult = new ArrayList<>();

        for (Song songs : searchBase) {
            if(compareToSongTitle(query, songs) || compareToSongArtist(query, songs))
            {
                searchResult.add(songs);
            }
        }
        return searchResult;
    }

    private boolean compareToSongArtist(String query, Song songs) {
        return songs.getArtist().toLowerCase().contains(query.toLowerCase());
    }

    private boolean compareToSongTitle(String query, Song songs) {
        return songs.getTitle().toLowerCase().contains(query.toLowerCase());
    }
}
