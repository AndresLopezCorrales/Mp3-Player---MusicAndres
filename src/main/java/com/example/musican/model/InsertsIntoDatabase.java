package com.example.musican.model;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class InsertsIntoDatabase {

    public void createTables() throws SQLException{
        try(Connection conn = DatabaseConnection.connect()){
            if (conn != null){
                Statement statement = conn.createStatement();

                String createTableArtists = """
                    CREATE TABLE IF NOT EXISTS artist (
                        artist_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT UNIQUE NOT NULL
                    );
                """;

                statement.execute(createTableArtists);

                // Create songs table with foreign key
                String createSongsTable = """
                    CREATE TABLE IF NOT EXISTS song (
                        song_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        image BLOB NOT NULL,
                        mp3_path TEXT NOT NULL
                    );
                """;
                statement.execute(createSongsTable);

                String createArtistSongTable = """
                    CREATE TABLE IF NOT EXISTS artist_song (
                        artist_id INTEGER,
                        song_id INTEGER,
                        PRIMARY KEY (artist_id, song_id),
                        FOREIGN KEY (artist_id) REFERENCES artist (artist_id) 
                            ON DELETE CASCADE 
                            ON UPDATE CASCADE,
                        FOREIGN KEY (song_id) REFERENCES song (song_id)
                            ON DELETE CASCADE 
                            ON UPDATE CASCADE
                    );
                """;
                statement.execute(createArtistSongTable);

                /*
                String drop = "DROP TABLE artist";
                String drop2 = "DROP TABLE song";
                String drop3 = "DROP TABLE artist_song";

                statement.execute(drop2);
                statement.execute(drop);
                statement.execute(drop3);

                 */

                System.out.println("Table created successfully!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insertIntoTables() throws SQLException {

        try (Connection conn = DatabaseConnection.connect()){
            if (conn != null){
                //Statement statement = conn.createStatement();

                //Insert into song
                /*
                String insertSong = """
                    INSERT INTO song (title, image, mp3_path) 
                    VALUES (?, ?, ?);
                """;

                String songTitle = "Siento que merezco más";
                byte[] imageBytes = Files.readAllBytes(Paths.get("src/main/resources/images/latin_mafia/TodosLosDias.jpg"));
                String mp3Path = "music/latin_mafia/siento_que_merezco_mas.mp3";

                // Preparar la sentencia para insertar la canción
                PreparedStatement preparedStatement = conn.prepareStatement(insertSong);
                preparedStatement.setString(1, songTitle);
                preparedStatement.setBytes(2, imageBytes);
                preparedStatement.setString(3, mp3Path);

                // Ejecutar el insert
                preparedStatement.executeUpdate();

                 */

                //Insert into artist
                /*
                String insertArtists = """
                        INSERT INTO artist (name) VALUES ('NSQK'),
                                                         ('Humbe'),
                                                         ('LATIN MAFIA'),
                                                         ('Alvaro Díaz')
                         ;
                """;

                statement.execute(insertArtists);
                */

                //Insert into artist_song
                /*String insertArtistSong = """
                    INSERT INTO artist_song (artist_id, song_id) 
                    VALUES (?, ?);
                """;

                int artist_id = 4;
                int song_id = 2;

                PreparedStatement preparedStatement = conn.prepareStatement(insertArtistSong);
                preparedStatement.setInt(1, artist_id);
                preparedStatement.setInt(2, song_id);
                preparedStatement.executeUpdate();
                System.exit(0);

                 */


            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void ver (){
        try (Connection conn = DatabaseConnection.connect()){
            if (conn != null){
                Statement statement = conn.createStatement();

                String ver = "SELECT * FROM artists";

                ResultSet resultSet = statement.executeQuery(ver);

                // Recorrer los resultados e imprimir cada fila
                while (resultSet.next()) {

                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");

                    System.out.println("ID: " + id + ", Name: " + name);
                }

                // Cerrar el ResultSet
                resultSet.close();

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
