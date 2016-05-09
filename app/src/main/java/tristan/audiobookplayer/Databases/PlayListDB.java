package tristan.audiobookplayer.Databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tristan on 06/05/16.
 */
public class PlayListDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AudioBook";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "AudioBookPlaylists";
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (id INTEGER, name STRING);";

    SQLiteDatabase db;

    public PlayListDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String[] getPlaylists() {
        // Get all the playlists from the db
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        String[] playlistNames = new String[c.getCount()];
        c.moveToFirst();
        if (c.getCount() > 0) {
            int i = 0;
            playlistNames[i] = c.getString(1);
            while (c.moveToNext()) {
                playlistNames[i] = c.getString(1);
                i++;
            }

        }

        return playlistNames;
    }
}
