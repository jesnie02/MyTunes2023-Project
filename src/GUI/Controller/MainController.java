package GUI.Controller;

import BE.PlayList;
import BE.Song;
import BLL.PlaylistManager;
import GUI.Utility.ThemeManager;
import GUI.Model.PlaylistModel;
import GUI.Model.SongModel;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    @FXML
    private TextField txtSongSearch;
    @FXML
    private TableView<Song> tblSongsInPlaylist, tblSongsViewed;
    @FXML
    private TableColumn<Song,String> colArtistPlaylist, colTitlePlaylist, ColTime;
    @FXML
    private TableColumn<PlayList, String> colPlaylistTime, colPlaylistName;
    @FXML
    private TableColumn colTitle, colArtist, colCategory;
    @FXML
    private TableView<PlayList> tblPlaylist;
    @FXML
    private TableColumn<PlayList, Integer> colSongsInPlaylist;
    @FXML
    public Button btnUpdatePlaylist , btnDeleteSongOnPlaylist;
    @FXML
    private Button btnMoveUp , btnMoveDown;
    @FXML
    private Button btnPlayPause;
    @FXML
    public Button btnAddNewSong , btnUpdateSong , btnNextSong , btnPreviousSong;
    @FXML
    private Slider songProgressSlider, volumeSlider;
    @FXML
    private Label lblSongPlaying;
    @FXML
    private Label lblTimeInfo;
    @FXML
    private StringProperty currentSongDetails = new SimpleStringProperty();
    @FXML
    private int currentSongIndex = 0;
    @FXML
    private Song currentPlayingSong;
    @FXML
    private boolean isCurrentSongFromPlaylist = false;
    @FXML
    private BorderPane rootPane;
    private CreateUpdateDialogBox dialogboxes = new CreateUpdateDialogBox();
    @FXML
    private PlaylistModel playlistModel;
    @FXML
    private PlaylistManager playlistManager;
    @FXML
    private MediaPlayer mediaPlayer;
    @FXML
    private SongModel songModel;
    @FXML
    private AnimationTimer timer;


    public MainController() throws Exception {
        songModel = new SongModel();
        this.playlistModel = new PlaylistModel();
        playlistManager = new PlaylistManager();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.mediaPlayer = new MediaPlayer();

        setupTableViews();
        setupEventListeners();
        loadPlaylists();
        doubleClickTableViewSongs();
        doubleClickTableViewPlaylist();
        setTimeInfo();
        setupMediaControls();

        btnMoveUp.setOnAction(this::onMoveUpClicked);
        btnMoveDown.setOnAction(this::onMoveDownClicked);

        lblSongPlaying.textProperty().bind(currentSongDetails);
    }

    // Handles the action of closing the main window.
    public void btnCloseMainWindow(ActionEvent actionEvent) throws IOException {
        Window window = rootPane.getScene().getWindow();
        ThemeManager.removeScene(window.getScene());
        window.hide();
    }

    // Opens the theme window.
    public void btnGoToTheme(ActionEvent actionEvent) throws IOException {
        // Creates an FXMLLoader to load the ToDoListWindow
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UIThemeWindow.fxml"));
        // Load the FXML file and get the root node
        Parent root = loader.load();

        // Create a new stage for the ToDoListWindow
        Stage stage = new Stage();
        stage.setScene(new Scene(root)); // Set the scene to the loaded root

        // Apply the default theme
        ThemeManager.applyTheme(stage.getScene(), "/Styles/DefaultTheme.css");

        stage.setTitle("Themes"); // Set the title of the stage
        stage.initModality(Modality.APPLICATION_MODAL); // Set the modality of the stage to application modal
        Image image = new Image("/Images/Capture.PNG");
        stage.getIcons().add(image);
        stage.show(); // Display the stage
    }

    private void setupMediaControls() {
        volumeSlider.setValue(50);
        mediaPlayer.setVolume(volumeSlider.getValue() / 100);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100; // Convert to a scale of 0.0 to 1.0
            mediaPlayer.setVolume(volume);
        });

        songProgressSlider.valueProperty().bindBidirectional(mediaPlayer.currentSongPositionProperty());
        songProgressSlider.setOnMousePressed(event -> mediaPlayer.pauseMedia());

        songProgressSlider.setOnMouseReleased(event -> {
            double newPosition = songProgressSlider.getValue();
            double totalDuration = mediaPlayer.getTotalDurationInSeconds();
            mediaPlayer.seekTo((newPosition/100.0)*totalDuration);
        });

        //plays next or previous song in the tableview
        btnPreviousSong.setOnAction(event -> {
            if (isCurrentSongFromPlaylist) {
                playPreviousSongInPlaylist();
            } else {
                playPreviousSongInView();
            }
        });

        btnNextSong.setOnAction(event -> {
            if (isCurrentSongFromPlaylist) {
                playNextSongInPlaylist();
            } else {
                playNextSongInView();
            }
        });
    }

    private void setupEventListeners() {
        tblPlaylist.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                playlistModel.loadSongsForPlaylist(newSelection.getId());
            }
        });

        txtSongSearch.textProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                songModel.searchSongs(newValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        playlistModel.getSongsInPlaylist().addListener((ListChangeListener<Song>) c -> {
            tblSongsInPlaylist.setItems(playlistModel.getSongsInPlaylist());
        });

        mediaPlayer.setOnSongEndListener(() -> {
            if (isCurrentSongFromPlaylist) {
                playNextSongInPlaylist();
            } else {
                playNextSongInView();
            }
        });
    }

    private void setupTableViews() {
        tblSongsViewed.setItems(songModel.getObservableSongs());
        colTitle.setCellValueFactory(new PropertyValueFactory<>("Title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("Artist"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("genre"));
        ColTime.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(getDurationFormatted(cellData.getValue().getLength())));

        tblPlaylist.setItems(playlistModel.getPlaylists());
        colPlaylistName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        colSongsInPlaylist.setCellValueFactory(new PropertyValueFactory<>("totalSongs"));
        colPlaylistTime.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(getDurationFormatted(cellData.getValue().getPlaylistDuration())));

        colArtistPlaylist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colTitlePlaylist.setCellValueFactory(new PropertyValueFactory<>("title"));
        tblSongsInPlaylist.setItems(playlistModel.getSongsInPlaylist());

    }

    // Sets up double click action for songs in the table view.
    private void doubleClickTableViewSongs() {
        tblSongsViewed.setRowFactory(tv -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Song rowData = row.getItem();
                    playSong(rowData, false); // Playing song from general view
                }
            });
            return row;
        });
    }

    // Sets up double click action for playlist in the table view.
    private void doubleClickTableViewPlaylist(){
        tblSongsInPlaylist.setRowFactory(tv -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Song rowData = row.getItem();
                    playSong(rowData, true); // Playing song from playlist
                }
            });
            return row;
        });
    }

    // Sets up a timer to update time info labels.
    public void setTimeInfo(){
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (mediaPlayer.isMediaPlaying() || mediaPlayer.isPaused()) {
                    String currentTime = mediaPlayer.getCurrentTimeFormatted();
                    String totalDuration = mediaPlayer.getTotalDurationFormatted();
                    lblTimeInfo.setText(currentTime + " / " + totalDuration);
                }
            }
        };
        timer.start();
    }

    // Starts the timer for updating time labels.
    private void startTimer() {
        timer.start();
    }

    // Toggles play/pause for the selected song.
    public void btnPlayPause(ActionEvent actionEvent) {
        if (mediaPlayer.isPlaying() && currentPlayingSong != null) {
            // Pauses if a song is already playing
            mediaPlayer.pauseMedia();
            updatePlayPauseButtonGraphic(false);
        } else {
            // Find which song should be played
            Song selectedSong = isCurrentSongFromPlaylist
                    ? tblSongsInPlaylist.getSelectionModel().getSelectedItem()
                    : tblSongsViewed.getSelectionModel().getSelectedItem();
            if (selectedSong != null) {
                playSong(selectedSong, isCurrentSongFromPlaylist);
                updatePlayPauseButtonGraphic(true);
            }
        }
    }

    // Formats duration from seconds to a mm:ss string format.
    public String getDurationFormatted(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    // Plays the selected song.
    private void playSong(Song song, boolean fromPlaylist) {
        isCurrentSongFromPlaylist = fromPlaylist;
        currentPlayingSong = song;

        if (song != null && mediaPlayer != null) {
            isCurrentSongFromPlaylist = fromPlaylist;
            currentPlayingSong = song;

            if (song != null && mediaPlayer != null) {
                if (!song.getFilePath().equals(mediaPlayer.getCurrentSongFilePath()) || !mediaPlayer.isPlaying()) {
                    mediaPlayer.playMedia(song.getFilePath());
                }

                startTimer();
                currentSongDetails.set("Playing: " + song.getArtist() + " - " + song.getTitle());
                updatePlayPauseButtonGraphic(true); //Updates the image on the button from play to pause
            }
        }
    }

    private void updatePlayPauseButtonGraphic(boolean isPlaying) {
        Image image = new Image(isPlaying ? "/Images/pause-solid.png" : "/Images/play-solid.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(25);
        imageView.setFitWidth(30);
        imageView.setPreserveRatio(true);
        btnPlayPause.setGraphic(imageView);
    }

    private void playPreviousSongInPlaylist() {
        int currentIndex = tblSongsInPlaylist.getSelectionModel().getSelectedIndex();
        if (currentIndex > 0) {
            currentIndex--;
            tblSongsInPlaylist.getSelectionModel().select(currentIndex);
            Song previousSong = tblSongsInPlaylist.getItems().get(currentIndex);
            playSong(previousSong, true);
        }
        // Optionally, handle wrap-around or stopping at the first song
    }
    // Plays the previous song in the list.

    private void playPreviousSongInView() {
        int currentIndex = tblSongsViewed.getSelectionModel().getSelectedIndex();
        if (currentIndex > 0) {
            currentIndex--;
            tblSongsViewed.getSelectionModel().select(currentIndex);
            Song previousSong = tblSongsViewed.getItems().get(currentIndex);
            playSong(previousSong, false);
        }
        // Optionally, handle wrap-around or stopping at the first song
    }

    // Opens a dialog to create new songs.
    public void createNewSongs(ActionEvent actionEvent) throws Exception {
        Optional<Song> result = dialogboxes.addUpdateSongDialog(false, null);

        result.ifPresent(song -> {
            try {
                songModel.createNewSongs(song);
            } catch (Exception e) {
                e.printStackTrace(); // Or handle the exception in another way
            }
        });
    }

    // Opens a dialog to update selected song.
    public void updateSongs(ActionEvent actionEvent) throws Exception {
        Song selectedSong = tblSongsViewed.getSelectionModel().getSelectedItem();
        tblSongsInPlaylist.setItems(songModel.getObservableSongs());
        if (selectedSong != null) {
            // If update is pressed the boolean "isUpdating" returns true in order to differentiate between update and create.
            Optional<Song> result = dialogboxes.addUpdateSongDialog(true, selectedSong);
            result.ifPresent(song -> {
                try {
                    songModel.updateSong(song);
                    tblSongsViewed.refresh();
                    tblSongsInPlaylist.refresh();

                } catch (Exception e) {
                    throw new RuntimeException();
                }
            });
        }else{ //Displays message when no song is selected
            if (selectedSong == null) {
                showAlert("No song selected", "Please select a song to update.");
            }
        }
    }

    // Deletes the selected song.
    public void deleteSong(ActionEvent actionEvent) {
        Song selectedSong = tblSongsViewed.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {

            String alertMessage = "Are you sure you want to delete '" +
                    selectedSong.getTitle() + "' by '" + selectedSong.getArtist() + "'?";
            Alert confirmationAlert = showDeleteAlert(false, alertMessage);

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                try {
                    songModel.deleteSong(selectedSong);
                    songModel.getObservableSongs().remove(selectedSong);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (selectedSong == null) {
                showAlert("No song selected", "Please select a song for deletion");
            }
        }
    }

    //Helper method for showing alert when deleting either song or playlist
    private static Alert showDeleteAlert(boolean isPlaylist, String message) {
        Alert confirmationAlert = new Alert(Alert.AlertType.NONE, //Use NONE inorder to get rid of the standard icon
                message,
                ButtonType.YES, ButtonType.NO);

        confirmationAlert.setAlertType(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setHeaderText(null); //inorder to make it more simple
        confirmationAlert.setGraphic(null); // Removes the questionmark
        return confirmationAlert;
    }

    // Opens a dialog to create a new playlist.
    public void createNewPlaylist(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Playlist");
        dialog.setHeaderText("Enter the name of the new playlist:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(playlistName -> {
            try {
                playlistModel.createPlaylist(playlistName);
                loadPlaylists(); // Reload or refresh the list
            } catch (Exception e) {
                e.printStackTrace(); // Or handle this more gracefully
            }
        });
    }

    // Loads playlists into the table view.
    private void loadPlaylists() {
        playlistModel.loadPlaylists();
        tblPlaylist.refresh();
    }

    // Deletes the selected playlist.
    public void deletePlaylist(ActionEvent actionEvent) {
        PlayList selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            String alertMessage = "Are you sure you want to delete the playlist '" +
                    selectedPlaylist.getName() + "'" + "?";
            Alert confirmationAlert = showDeleteAlert(true, alertMessage);

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                try {
                    playlistModel.deletePlaylist(selectedPlaylist);
                    loadPlaylists();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle the error properly (show a dialog, etc.)
                }
            }

        } else {
            if (selectedPlaylist == null) {
                showAlert("No playlist selected", "Please select a playlist for deletion");
            }
        }
    }

    // Adds the selected song to the selected playlist.
    public void addSongToPlaylist(ActionEvent actionEvent) {
        Song selectedSong = tblSongsViewed.getSelectionModel().getSelectedItem();
        PlayList selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();

        if (selectedSong == null) {
            showAlert("No song selected", "Please select a song to add to the playlist.");
            return;
        }

        if (selectedPlaylist == null) {
            showAlert("No Playlist Selected", "Please select a playlist.");
            return;
        }

        try {
            playlistManager.addSongToPlaylist(selectedSong, selectedPlaylist);
            playlistModel.loadSongsForPlaylist(selectedPlaylist.getId());
            playlistModel.loadPlaylists();
            tblSongsInPlaylist.setItems(playlistModel.getSongsInPlaylist());
            tblSongsInPlaylist.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not add the song to the playlist.");
        }
    }

    // Displays an alert dialog with specified title and content.
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Updates the selected playlist's name.
    public void updatePlaylist(ActionEvent actionEvent) {
        PlayList selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            TextInputDialog dialog = new TextInputDialog(selectedPlaylist.getName());
            dialog.setTitle("Update Playlist");
            dialog.setHeaderText("Enter the new name for the playlist:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newPlaylistName -> {
                try {
                    playlistModel.updatePlaylist(selectedPlaylist.getId(), newPlaylistName);
                    loadPlaylists(); // Reload or refresh the list
                } catch (Exception e) {
                    e.printStackTrace(); // Or handle this more gracefully
                }
            });
        } else {
            if (selectedPlaylist == null) {
                showAlert("No Playlist selected", "Please select a playlist to update");
            }
        }
    }

    // Deletes the selected song from the selected playlist.
    public void deleteSongOnPlaylist(ActionEvent actionEvent) {
        PlayList selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();
        Song selectedSong = tblSongsInPlaylist.getSelectionModel().getSelectedItem();

        if (selectedPlaylist == null) {
            showAlert("No Playlist Selected", "Please select a playlist.");
            return;
        }

        if (selectedSong == null) {
            showAlert("No Song Selected", "Please select a song from the playlist.");
            return;
        }

        String alertMessage = "Are you sure you want to delete '" +
                selectedSong.getTitle() + "' by '" + selectedSong.getArtist() + "' from playlist '" +
                selectedPlaylist.getName() + "'?" ;
        Alert confirmationAlert = showDeleteAlert(false, alertMessage);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                playlistModel.deleteSongFromPlaylist(selectedSong, selectedPlaylist);
                refreshSongsInPlaylist(selectedPlaylist);

            } catch (Exception e) {
                e.printStackTrace(); // Log the error
                showAlert("Error", "Failed to delete the song from the playlist: " + e.getMessage());
            }
        }
    }

    // Refreshes the song list for a playlist.
    private void refreshSongsInPlaylist(PlayList playlist) {
        try {
            playlistModel.loadSongsForPlaylist(playlist.getId());
            tblSongsInPlaylist.setItems(playlistModel.getSongsInPlaylist());
            tblSongsInPlaylist.refresh();
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            showAlert("Error", "Failed to refresh the song list: " + e.getMessage());
        }
    }

    @FXML
    public void onSongSliderReleased(MouseEvent mouseEvent) {
    // Handles the release of the song progress slider.
    }

    // Moves the selected song up in the playlist.
    @FXML
    private void onMoveUpClicked(ActionEvent event) {
        int selectedIndex = tblSongsInPlaylist.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0) {

            // Get the ObservableList
            ObservableList<Song> songs = playlistModel.getSongsInPlaylist();

            // Swap elements
            Song songToMoveUp = songs.get(selectedIndex);
            songs.set(selectedIndex, songs.get(selectedIndex - 1));
            songs.set(selectedIndex - 1, songToMoveUp);

            updateSongOrderInDatabase();

            // Update selection and refresh TableView
            tblSongsInPlaylist.getSelectionModel().select(selectedIndex - 1);
            tblSongsInPlaylist.refresh();
        }
    }

    // Moves the selected song down in the playlist.
    @FXML
    private void onMoveDownClicked(ActionEvent event) {
        int selectedIndex = tblSongsInPlaylist.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < playlistModel.getSongsInPlaylist().size() - 1) {
            // Get the ObservableList
            ObservableList<Song> songs = playlistModel.getSongsInPlaylist();

            // Swap elements
            Song songToMoveDown = songs.get(selectedIndex);
            songs.set(selectedIndex, songs.get(selectedIndex + 1));
            songs.set(selectedIndex + 1, songToMoveDown);

           updateSongOrderInDatabase();

            // Update selection and refresh TableView
           tblSongsInPlaylist.getSelectionModel().select(selectedIndex + 1);
           tblSongsInPlaylist.refresh();
        }
    }

    // Updates the order of songs in the playlist and database
    private void updateSongOrderInDatabase() {
        PlayList selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            List<Song> songs = new ArrayList<>(tblSongsInPlaylist.getItems());
            playlistModel.updateSongOrder(selectedPlaylist.getId(), songs);
            tblSongsInPlaylist.refresh();
        }
    }

    private void playNextSongInView() {
        // Get the index of the currently playing song in the song view
        int currentIndex = tblSongsViewed.getSelectionModel().getSelectedIndex();
        // Check if there is a next song in the view
        if (currentIndex < tblSongsViewed.getItems().size() - 1) {
            // Move to the next song
            currentIndex++;
            tblSongsViewed.getSelectionModel().select(currentIndex);
            Song nextSong = tblSongsViewed.getItems().get(currentIndex);
            playSong(nextSong, false);
        } else {

            // loop to the first song
             if (!tblSongsViewed.getItems().isEmpty()) {
                 tblSongsViewed.getSelectionModel().select(0);
                 playSong(tblSongsViewed.getItems().get(0), false);
             }
        }
    }

    private void playNextSongInPlaylist() {
        // Get the index of the currently playing song in the playlist
        int currentIndex = tblSongsInPlaylist.getSelectionModel().getSelectedIndex();
        // Check if there is a next song in the playlist
        if (currentIndex < tblSongsInPlaylist.getItems().size() - 1) {
            // Move to the next song
            currentIndex++;
            tblSongsInPlaylist.getSelectionModel().select(currentIndex);
            Song nextSong = tblSongsInPlaylist.getItems().get(currentIndex);
            playSong(nextSong, true);
        } else {

            // Or loop to the first song
             if (!tblSongsInPlaylist.getItems().isEmpty()) {
                tblSongsInPlaylist.getSelectionModel().select(0);
                playSong(tblSongsInPlaylist.getItems().get(0), true);
           }
        }
    }
}
