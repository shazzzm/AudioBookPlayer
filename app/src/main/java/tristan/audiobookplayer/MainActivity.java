package tristan.audiobookplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;


public class MainActivity extends ActionBarActivity {

    MediaPlayer mp;
    MusicPositionDB mpdb;
    String currentFilename;

    // Updates the seek bar
    Handler seekBarHandler = new Handler();

    // Saves the current time in the db so it isn't lost if something bad happens
    Handler updateHandler;

    // Key for the last file being played from the config file
    final static String LAST_FILE = "last_file";

    // Interval which we run a task to update the seek bar at
    final static int SEEK_INTERVAL = 1000;

    // Interval to update the db to where we are in the file
    final static int UPDATE_INTERVAL = 5000;

    Runnable seekBarRunnable = new Runnable() {
        @Override
        public void run() {
            SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
            seekBar.setProgress(getProgress());
            seekBarHandler.postDelayed(seekBarRunnable, SEEK_INTERVAL);
        }
    };

    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            mpdb.writeTrackPosition(currentFilename, mp.getCurrentPosition());
            updateHandler.postDelayed(updateRunnable, UPDATE_INTERVAL);
        }
    };


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


        if (filename == null || filename.equals("")) {
            filename = settings.getString(LAST_FILE, "/storage/sdcard1/Music" + "/between-the-devil-and-the-deep-blue-sea2.mp3");
            Log.d("AudioBookPlayer", "No Intent filename found");
        } else {
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
            } else {
                Log.d("AudioBookPlayer", "File does not exist");
            }
        } else {
            Log.d("AudioBookPlayer", "External Storage Unavailable");
        }

        // Set up the seekbar
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                int seekTo = getSeek(seekBar.getProgress());
                mp.seekTo(seekTo);
            }
        });

        updateHandler = new Handler();
        updateRunnable.run();
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

    private int getProgress()
    {
        float progressValue = (float)mp.getCurrentPosition()/(float)mp.getDuration();
        return (int)(progressValue*100);
    }

    private int getSeek(int progress)
    {
        float progressValue = (float)progress/100;
        return (int)((float)mp.getDuration()*progressValue);
    }


    private void startSeekUpdater() {
        seekBarRunnable.run();
    }

    private void stopSeekUpdater() {
        seekBarHandler.removeCallbacks(seekBarRunnable);
    }


    public void onPlayButtonClick(View view) {
        mp.start();
        startSeekUpdater();
    }

    public void onStopButtonClick(View view) {
        mp.pause();
        mpdb.writeTrackPosition(currentFilename, mp.getCurrentPosition());
        stopSeekUpdater();
    }

    public void onOpenButtonClick(View view) {
        mp.release();
        stopSeekUpdater();
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
