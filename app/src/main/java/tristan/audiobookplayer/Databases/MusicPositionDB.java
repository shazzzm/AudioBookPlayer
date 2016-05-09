package tristan.audiobookplayer.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by tristan on 09/08/15.
 */
public class MusicPositionDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AudioBook";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "AudioBookPositions";
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (filename_hash INTEGER, position INTEGER);";

    SQLiteDatabase db;

    public MusicPositionDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int getTrackPosition(String filename) {
        String[] args = {String.valueOf(filename.hashCode())};
        Cursor c = db.query(TABLE_NAME, null, "filename_hash = ?", args, null, null, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            CharArrayBuffer s = new CharArrayBuffer(100);
            c.copyStringToBuffer(0, s);
            Log.d("MusicPositionDB", s.toString());
            c.copyStringToBuffer(1, s);
            Log.d("MusicPositionDB", s.toString());
            return c.getInt(1);
        }
        else {
            Log.d("MusicPositionDB", "Filename " + filename + " not found");
            return -1;
        }
    }

    public void writeTrackPosition(String filename, int position)
    {
        ContentValues values = new ContentValues();
        values.put("filename_hash", filename.hashCode());
        values.put("position", position);
        // Check if the track is already in the db
        if (getTrackPosition(filename) == -1) {
            db.insert(TABLE_NAME, null, values);
            Log.d("MusicPositionDB", "Inserted filename into db");
        } else {
            String[] args = {String.valueOf(filename.hashCode())};
            db.update(TABLE_NAME, values, "filename_hash = ?", args);
            Log.d("MusicPositionDB", "Updated filename " + filename);
        }
     }

    public void clearDatabase()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}
