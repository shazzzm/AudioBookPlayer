package tristan.audiobookplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by tristan on 06/05/16.
 */
public class PlayOrderDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AudioBook";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "AudioBookPlayOrder";
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (filename STRING, skipped INTEGER, played_at DATETIME DEFAULT CURRENT_TIMESTAMP);";

    SQLiteDatabase db;

    public PlayOrderDB(Context context) {
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

    public void insertTrack(String filename, boolean skipped) {
        ContentValues values = new ContentValues();
        values.put("filename", filename);
        values.put("skipped", skipped);

        db.insert(TABLE_NAME, null, values);

    }
}
