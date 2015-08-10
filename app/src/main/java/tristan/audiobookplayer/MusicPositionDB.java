package tristan.audiobookplayer;

import android.content.ContentValues;
import android.content.Context;
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
            "CREATE TABLE " + TABLE_NAME + " (FILENAME TEXT, POSITION TEXT);";

    MusicPositionDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public String getTrackPosition(String filename)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {filename};
        Cursor c = db.query(TABLE_NAME, null, "FILENAME = ?", args, null, null, null);
        if (c.getCount() > 0) {
            return c.getString(0);
        }
        else {
            Log.d("MusicPositionDB", "Filename " + filename + " not found");
            return "0";
        }
    }

    public void writeTrackPosition(String filename, String position)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("FILENAME", DatabaseUtils.sqlEscapeString(filename));
        values.put("POSITION", position);
        // Check if the track is already in the db
        if (getTrackPosition(filename) == "0") {
            db.insert(TABLE_NAME, null, values);
            Log.d("MusicPositionDB", "Inserted filename into db");
        } else {
            String[] args = {filename};
            db.update(TABLE_NAME, values, "filename = ?", args);
            Log.d("MusicPositionDB", "Updated filename");
        }
     }
}
