package tristan.audiobookplayer.Playlists;

/**
 * Created by tristan on 11/05/16.
 */
public class Playlist {
    private String _name;
    private int _id;

    public Playlist(int id, String name) {
        _name = name;
        _id = id;
    }

    public String getName() {
        return _name;
    }

    public int getId() {
        return _id;
    }
}
