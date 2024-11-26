package BE;

public class Artist {

    private String artistName;

    private int artistID;

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getArtistID() {
        return artistID;
    }

    public void setArtistID(int artistID) {
        this.artistID = artistID;
    }

    public Artist(int artistID, String artistName){
        this.artistID = artistID;
        this.artistName = artistName;
    }

}
