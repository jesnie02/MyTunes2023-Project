package BE;

public class PlayList {

    private int id;
    private String Name;
    private int playlistDuration;
    private int totalSongs;
    private Integer numberOfSongs;


    public PlayList( int id,String Name,int totalSongs ,int playlistDuration ) {
        this.id=id;
        this.Name = Name;
        this.totalSongs= totalSongs;
        this.playlistDuration= playlistDuration;
    }

    public Integer getNumberOfSongs() {
        return numberOfSongs;
    }

    public int getPlaylistDuration(){
        return playlistDuration;
    }

    public int getTotalSongs(){
        return totalSongs;
    }

    public void setNumberOfSongs(Integer numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    public PlayList(int id, String Name) {
        this.id = id;
        this.Name = Name;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    // You may override toString() if you want to display the Playlist name in a simple ListView or ComboBox
    @Override
    public String toString() {
        return Name;
    }
}
