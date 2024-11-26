package BE;

public class Song {

    private String genre;
    private int length;
    private int id;
    private String title;
    private String artist;
    private String FilePath;
    private int sortOrder;

    public Song(String artist, String title, String genre, int length, String filePath) {
        this.title = title;
        this.genre = genre;
        this.length = length;
        this.artist = artist;
        this.FilePath = filePath;
    }

    public Song(int id, String artist, String title, String genre, int length, String filePath) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.length = length;
        this.artist = artist;
        this.FilePath = filePath;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre){
        this.genre = genre;
    }

    public int getLength() {
        return length;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        this.FilePath = filePath;
    }

    public void setLength(int length){
        this.length = length;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString()
    {
        return id + ": " + title + genre + length + artist;
    }
}

