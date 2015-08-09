import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public void writeTrackPosition(String filename, String position)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("FILEBANE", filename);
        values.put("POSITION", position);
        db.insert(TABLE_NAME, null, values);
    }
}
