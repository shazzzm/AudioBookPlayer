package tristan.audiobookplayer;

/**
 * Created by tristan on 17/05/16.
 * Holds information about an individual song
 */
public class Song {
    int _id;
    String _path;

    public Song(int id, String path) {
        _id = id;
        _path = path;
    }

    public int get_id() {
        return _id;
    }

    public String get_path() {
        return _path;
    }
}
