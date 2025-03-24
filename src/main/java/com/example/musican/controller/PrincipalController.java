package com.example.musican.controller;

import com.example.musican.model.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.InputStream;
import java.sql.*;
import java.util.LinkedList;

public class PrincipalController {

    @FXML
    ImageView imageSong; //Ya

    @FXML
    ComboBox <String> comboArtist, comboSongs; //Ya

    @FXML
    Label artistName, songTitle; //Ya

    @FXML
    Button previousButton, playButton, nextButton; //Ya

    @FXML
    Slider sliderSong;

    String mp3Path;

    Media media;

    MediaPlayer mediaPlayer;

    boolean isArtistSelected = false;

    //Boolean to get the first ArtistName and Song when the app is launched
    boolean isFirstTime = true;
    boolean isFirstTime2 = true;

    //Initialize functions when FXML file instance the controller
    @FXML
    public void initialize() {
        insertArtistsIntoComboBoxArtist();

        insertSongsOfEveryArtist();

        comboArtist.setOnAction(event -> {
            setOnActionComboArtist();
        });

        comboSongs.setOnAction(event -> {
            setOnActionComboSong();
        });

    }

    public void setOnActionComboArtist(){
        isArtistSelected = true;

        // Call the function to load the songs related to the artist
        insertSongsOfEveryArtist();
        setLabelForArtist();

    }

    public void mediaToBePlayed(){
        String pathMp3 = getClass().getResource("/" + selectAudioToBePlayed()).toString();
        media = new Media(pathMp3);
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnEndOfMedia(() -> {
            System.out.println("La canción ha terminado000.");
            nextSong();
        });

        sliderSong.setMin(0);

        mediaPlayer.setOnReady(() -> {
            Duration totalDuration = media.getDuration();
            sliderSong.setMax(totalDuration.toSeconds());
            mediaPlayer.play();
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!sliderSong.isValueChanging()) { // Evitar conflictos al mover el Slider manualmente
                sliderSong.setValue(newValue.toSeconds());
            }


        });

        // Mover la reproducción cuando el usuario mueve el Slider
        sliderSong.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (sliderSong.isValueChanging()) { // Solo si el usuario está moviendo el Slider
                mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
            }
        });
    }

    public void setOnActionComboSong(){
        if (!isArtistSelected){
            setLabelForSong();
            selectAudioToBePlayed();
            changeImageView();
            mediaToBePlayed();
        }
        isArtistSelected = false;
    }

    public void insertArtistsIntoComboBoxArtist(){

        try (Connection conn = DatabaseConnection.connect()){

            if (conn != null){

                Statement statement = conn.createStatement();

                String selectAllArtists = """
                        SELECT name FROM artist;
                        """;

                ResultSet resultSet = statement.executeQuery(selectAllArtists);

                LinkedList<String> artists = new LinkedList<>();

                while (resultSet.next()){
                    artists.add(resultSet.getString("name"));
                }

                comboArtist.getItems().addAll(artists);

                if (isFirstTime && !artists.isEmpty()) {
                    comboArtist.setValue(artists.getFirst());
                    artistName.setText(artists.getFirst());
                    isFirstTime = false;
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getValueFromComboBoxArtist(){

        isArtistSelected = false;
        String selectedArtist = comboArtist.getValue();

        String artistToReturn = "";

        if (selectedArtist != null){
            artistToReturn = selectedArtist;
        }

        return artistToReturn;
    }



    public void insertSongsOfEveryArtist(){

        comboSongs.getItems().clear();

        try (Connection conn = DatabaseConnection.connect()){

            if (conn != null){

                String selectSongsOfTheSpecificArtist = """
                        SELECT song.title FROM song
                        JOIN artist_song ON song.song_id = artist_song.song_id
                        JOIN artist ON artist_song.artist_id = artist.artist_id
                        WHERE artist.name = ?;
                        """;

                String nameArtist = getValueFromComboBoxArtist();
                if (nameArtist == null || nameArtist.isEmpty()) {
                    // Si no hay artista seleccionado, selecciona un artista predeterminado
                    nameArtist = "NSQK"; // Sustituye con el nombre de un artista por defecto

                }

                PreparedStatement preparedStatement = conn.prepareStatement(selectSongsOfTheSpecificArtist);
                preparedStatement.setString(1, nameArtist);

                ResultSet resultSet = preparedStatement.executeQuery();

                LinkedList<String> songs = new LinkedList<>();

                while (resultSet.next()){
                    songs.add(resultSet.getString("title"));
                }

                comboSongs.getItems().addAll(songs);


                if (isFirstTime2 && !songs.isEmpty()){
                    //isArtistSelected = true;
                    comboSongs.setValue(songs.getLast());
                    songTitle.setText(songs.getLast());
                    isFirstTime2 = false;
                }else{
                    comboSongs.setValue(comboSongs.getItems().getFirst());
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getValueComboBoxSongs(){

        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }

        String selectedArtist = comboSongs.getValue();

        String songToReturn = "";

        if (selectedArtist != null){
            songToReturn = selectedArtist;
        }
        return songToReturn;
    }

    public void setLabelForArtist(){
        artistName.setText("");
        artistName.setText(getValueFromComboBoxArtist());
    }

    public void setLabelForSong(){
        songTitle.setText("");
        songTitle.setText(getValueComboBoxSongs());
    }


    public String selectAudioToBePlayed(){
        try(Connection conn = DatabaseConnection.connect()){

            if (conn != null){

                String selectMp3 = """
                        SELECT song.mp3_path, song.image FROM song
                        JOIN artist_song ON song.song_id = artist_song.song_id
                        JOIN artist ON artist_song.artist_id = artist.artist_id
                        WHERE song.title = ? AND artist.name = ?;
                        """;

                String title = getValueComboBoxSongs() ;
                String name = getValueFromComboBoxArtist() ;

                PreparedStatement preparedStatement = conn.prepareStatement(selectMp3);
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, name);

                ResultSet resultSet = preparedStatement.executeQuery();

                mp3Path = resultSet.getString("mp3_path");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return mp3Path;
    }

    public void changeImageView() {
        String selectImage = """
                            SELECT song.image FROM song
                            JOIN artist_song ON song.song_id = artist_song.song_id
                            JOIN artist ON artist_song.artist_id = artist.artist_id
                            WHERE song.title = ? AND artist.name = ?;
                        """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement statement = conn.prepareStatement(selectImage)) {

            String title = getValueComboBoxSongs();
            String name = getValueFromComboBoxArtist();

            statement.setString(1, title);
            statement.setString(2, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Obtener el BLOB como InputStream
                    InputStream inputStream = resultSet.getBinaryStream("image");

                    if (inputStream != null) {
                        // Convertir InputStream a Image
                        Image image = new Image(inputStream);

                        // Verificar si la imagen tiene error
                        if (image.isError()) {
                            System.out.println("Error al cargar la imagen: " + image.getException().getMessage());
                        } else {
                            // Establecer la imagen en el ImageView
                            imageSong.setImage(image);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Functions to control media/audio
    public void playPause(){

        if (mediaPlayer == null){

            try {
                mediaToBePlayed();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if (mediaPlayer != null){
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.READY || status == MediaPlayer.Status.PAUSED) {
                mediaPlayer.play(); // Si está listo o pausado, reproduce
            }else if (status == MediaPlayer.Status.PLAYING){
                mediaPlayer.pause();
            }
        }
    }

    public void nextSong (){

        String currentSong = comboSongs.getValue();
        int currentIndex = comboSongs.getItems().indexOf(currentSong);

        String currentArtist = comboArtist.getValue();
        int currentArtistIndex = comboArtist.getItems().indexOf(currentArtist);

        if (currentIndex < comboSongs.getItems().size() - 1) {
            // Hay una siguiente canción
            comboSongs.setValue(comboSongs.getItems().get(currentIndex + 1));
            setOnActionComboSong(); // Reproducir la siguiente canción
        } else {
            //Es el último artista, ir al primer artista y reproducir su primer canción
            if (currentArtistIndex == comboArtist.getItems().size() - 1 ){
                comboArtist.setValue(comboArtist.getItems().getFirst());
            }else {
                comboArtist.setValue(comboArtist.getItems().get(currentArtistIndex + 1));
            }
            comboSongs.setValue(comboSongs.getItems().getFirst());
            setOnActionComboSong();
        }

    }

    public void previousSong(){
        String currentSong = comboSongs.getValue();
        int currentIndex = comboSongs.getItems().indexOf(currentSong);

        String currentArtist = comboArtist.getValue();
        int currentArtistIndex = comboArtist.getItems().indexOf(currentArtist);

        if (currentIndex > 0) {
            // Hay una canción anterior
            comboSongs.setValue(comboSongs.getItems().get(currentIndex - 1));
            setOnActionComboSong(); // Reproducir la canción anterior
        } else {
            //El artista no es el primero, ve al anterior
            if (currentArtistIndex > 0) {
                comboArtist.setValue(comboArtist.getItems().get(currentArtistIndex - 1));
            } else {
                //Estás en el primer artista, ve al último artista
                comboArtist.setValue(comboArtist.getItems().getLast());
            }
            // Seleccionar la última canción del nuevo artista
            comboSongs.setValue(comboSongs.getItems().getLast());
            setOnActionComboSong(); // Reproducir la última canción del nuevo artista
        }
    }

}
