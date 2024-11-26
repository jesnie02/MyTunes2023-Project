package DAL;

import BE.PlayList;

import java.util.List;

public interface IPlaylistDataAccess {
    List<PlayList> getAllPlaylists() throws Exception;
    PlayList createPlaylist(PlayList playlist, int userid) throws Exception;
    void updatePlaylist(PlayList playlist) throws Exception;
    void deletePlaylist(PlayList playlistId) throws Exception;
    public List<PlayList> getAllUserPlaylist(int UserID) throws Exception;

}
