package GUI.Controller;

import BE.Genre;
import BE.Song;
import DAL.db.GenreDAO_DB;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

public class CreateUpdateDialogBox {

    // Uses boolean to check if you are updating or adding song. if true it updates if false it adds new song.
    public Optional<Song> addUpdateSongDialog(boolean isUpdating, Song selectedSong) throws Exception {

        // Creating dialog box
        Dialog<Song> dialog = new Dialog<>();
        dialog.setTitle(isUpdating ? "Update Song" : "Add New Song");

        ButtonType addUpdateButtonType = new ButtonType(isUpdating ? "Update" : "Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addUpdateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Adding input fields
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField artistField = new TextField();
        artistField.setPromptText("Artist Name");
        ComboBox<String> genreComboBox = new ComboBox<>();
        genreComboBox.setPromptText("Select Genre");
        TextField timeField = new TextField();
        timeField.setPromptText("Time in Seconds");
        TextField filePathField = new TextField();
        filePathField.setEditable(false);

        GenreDAO_DB genreDAO = new GenreDAO_DB();
        List<Genre> allGenres = genreDAO.getAllGenre();

        //makes the dropdown empty
        genreComboBox.getItems().clear();

        for (Genre genre : allGenres){
            genreComboBox.getItems().add(genre.getGenreType());
        }

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Artist:"), 0, 1);
        grid.add(artistField, 1, 1);
        grid.add(new Label("Genre:"), 0, 2);
        grid.add(genreComboBox, 1, 2);
        grid.add(new Label("Time:"), 0, 3);
        grid.add(timeField, 1, 3);
        grid.add(new Label("File:"), 0, 4);
        grid.add(filePathField, 1, 4);

        // If updating the dialog box will include all the items of the selected song
        if(isUpdating && selectedSong != null) {
            titleField.setText(selectedSong.getTitle());
            artistField.setText(selectedSong.getArtist());
            genreComboBox.setValue(selectedSong.getGenre());
            timeField.setText(String.valueOf(selectedSong.getLength()));
            filePathField.setText(selectedSong.getFilePath());
        }

        // Chooses a MP3 file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
        Button chooseFileButton = new Button("Choose file");
        grid.add(chooseFileButton, 2, 4);
        // Extracts the metadata from the file
        chooseFileButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(dialog.getOwner());
            if (selectedFile != null) {
                copyFileToDataFolder(selectedFile);
                filePathField.setText(selectedFile.getName()); // Changed to use getName() instead

                Media media = new Media(selectedFile.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);

                mediaPlayer.setOnReady(() -> {
                    String artist = (String) media.getMetadata().get("artist");
                    String title = (String) media.getMetadata().get("title");
                    Duration duration = media.getDuration();

                    titleField.setText(title != null ? title : "");
                    artistField.setText(artist != null ? artist : "");
                    genreComboBox.setValue(""); // Genre is rarely part of the metadata
                    timeField.setText(String.valueOf((int) duration.toSeconds()));
                });
            }
        });

        dialog.getDialogPane().setContent(grid);

        // Checks if the input fields are empty or not
        BooleanBinding anyFieldEmpty = titleField.textProperty().isEmpty()
                .or(artistField.textProperty().isEmpty())
                .or(genreComboBox.valueProperty().isNull()).or(genreComboBox.valueProperty().asString().isEmpty())
                .or(timeField.textProperty().isEmpty())
                .or(filePathField.textProperty().isEmpty());

        // Makes the add/update button invisible if any of the input fields are empty
        Node addButton = dialog.getDialogPane().lookupButton(addUpdateButtonType);
        addButton.disableProperty().bind(anyFieldEmpty);

        // Checks if a new song is being created or a song is being updated using boolean
        // And inputs the new values based on that
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addUpdateButtonType) {
                if(isUpdating) {
                    return new Song(selectedSong.getId(),
                            artistField.getText(),
                            titleField.getText(),
                            genreComboBox.getValue(),
                            Integer.parseInt(timeField.getText()),
                            filePathField.getText());
                } else {
                    return new Song(
                            artistField.getText(),
                            titleField.getText(),
                            genreComboBox.getValue(),
                            Integer.parseInt(timeField.getText()),
                            filePathField.getText());
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    //Puts the new files added into our data folder in our project
    private void copyFileToDataFolder(File sourceFile) {
        try {
            Path destinationPath = Paths.get("Data", sourceFile.getName());
            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
