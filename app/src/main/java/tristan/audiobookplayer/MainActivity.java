package tristan.audiobookplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;


public class MainActivity extends ActionBarActivity {

    MediaPlayer mp;
    MusicPositionDB mpdb;
    String currentFilename;

    final static String LAST_FILE = "last_file";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mpdb = new MusicPositionDB(this);
        String filename = "";
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        SharedPreferences settings = getPreferences(MODE_PRIVATE);

        if (extras != null) {
            filename = extras.getString(FileSelector.FILENAME_INTENT);
        }


        if (filename == null || filename.equals(""))
        {
            filename = settings.getString(LAST_FILE,  "/storage/sdcard1/Music" + "/between-the-devil-and-the-deep-blue-sea2.mp3");
            Log.d("AudioBookPlayer", "No Intent filename found");
        }
        else
        {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(LAST_FILE, filename);
            editor.commit();
            Log.d("AudioBookPlayer", filename);
        }

        currentFilename = filename;
        setContentView(R.layout.activity_main);
        int pos = mpdb.getTrackPosition(filename);
        TextView textView = (TextView) findViewById(R.id.nowPlayingTextBox);
        textView.setText(filename);

        if (isExternalStorageWritable()) {
            File file = new File(filename);
            if (file.exists()) {
                //Do action

                mp = MediaPlayer.create(this, Uri.parse(filename));
                if (pos != -1) {
                    mp.seekTo(pos);
                }
            }
            else
            {
                Log.d("AudioBookPlayer", "File does not exist");
            }
        }
        else {
            Log.d("AudioBookPlayer", "External Storage Unavailable");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void onPlayButtonClick(View view)
    {
        mp.start();
    }

    public void onStopButtonClick(View view)
    {
        mp.pause();
        mpdb.writeTrackPosition(currentFilename, mp.getCurrentPosition());
    }

    public void onOpenButtonClick(View view)
    {
        mp.release();
        Intent intent = new Intent(this, FileSelector.class);
        startActivity(intent);
    }

    public void onClearButtonClick(View view) {
        mpdb.clearDatabase();
    }

    public void onResetButtonClick(View view) {
        mp.pause();
        mp.seekTo(0);
        mpdb.writeTrackPosition(currentFilename, 0);
    }
}
