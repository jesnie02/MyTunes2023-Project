package BE;

public class Genre {

    private String genreType;
    private int genreID;

    public Genre(int genreID, String genreType) {
        this.genreType = genreType;
        this.genreID = genreID;
    }

    public Genre(String genreType) {
        this.genreType = genreType;
    }

    public int getGenreID() {
        return genreID;
    }

    public void setGenreID(int genreID) {
        this.genreID = genreID;
    }

    public String getGenreType() {
        return genreType;
    }

    public void setGenreType(String genreType) {
        this.genreType = genreType;
    }

}
