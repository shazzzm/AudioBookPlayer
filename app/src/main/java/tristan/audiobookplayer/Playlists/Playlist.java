package tristan.audiobookplayer.Playlists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tristan.audiobookplayer.Song;

/**
 * Created by tristan on 11/05/16.
 */
public class Playlist {
    private String _name;
    private int _id;
    private List<Song> _songs;


    public Playlist(int id, String name, Song[] songs) {
        _name = name;
        _id = id;
        _songs = Arrays.asList(songs);
    }

    public Playlist(int id, String name) {
        _name = name;
        _id = id;
        _songs = new ArrayList<>();
    }

    public String getName() {
        return _name;
    }

    public int getId() {
        return _id;
    }
}
