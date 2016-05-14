package tristan.audiobookplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

import tristan.audiobookplayer.Databases.MusicPositionDB;
import tristan.audiobookplayer.Playlists.PlaylistActivity;


public class MainActivity extends ActionBarActivity {

    MediaPlayer mp;
    MusicPositionDB mpdb;
    String currentFilename;
    boolean currentlyPlaying = false;

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
        setContentView(R.layout.activity_main);
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

        File file = new File(filename);
        if (file.exists()) {
            currentFilename = filename;
            setContentView(R.layout.activity_main);
            int pos = mpdb.getTrackPosition(filename);
            TextView textView = (TextView) findViewById(R.id.nowPlayingTextBox);
            textView.setText(filename);
            mp = MediaPlayer.create(this, Uri.parse(filename));
            if (pos != -1) {
                mp.seekTo(pos);
            }
        } else {
            // Disable the play and stop buttons
            SetMusicButtonsState(false);
        }

        // Set up the seekbar
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(getProgress());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // If we're playing music, seek to the selected spot
                // else don't seek
                if (mp != null) {
                    int seekTo = getSeek(seekBar.getProgress());
                    mp.seekTo(seekTo);
                } else {
                    seekBar.setProgress(0);
                }
            }
        });

        updateHandler = new Handler();
        setNotification();
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
        if (mp != null) {
            float progressValue = (float) mp.getCurrentPosition() / (float) mp.getDuration();
            return (int) (progressValue * 100);
        } else {
            return 0;
        }
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

    private void SetMusicButtonsState(boolean enable) {
        ImageButton playButton = (ImageButton) findViewById(R.id.playButton);
        ImageButton resetButton = (ImageButton) findViewById(R.id.resetButton);
        playButton.setEnabled(enable);
        resetButton.setEnabled(enable);
    }


    public void onPlayButtonClick(View view) {
        ImageButton button = (ImageButton)findViewById(R.id.playButton);

        if (currentlyPlaying) {
            mp.pause();
            mpdb.writeTrackPosition(currentFilename, mp.getCurrentPosition());
            stopSeekUpdater();
            button.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            updateHandler.removeCallbacks(updateRunnable);

        }
        else {
            mp.start();
            startSeekUpdater();
            button.setImageResource(R.drawable.ic_stop_black_24dp);
            updateRunnable.run();
        }
        currentlyPlaying = !currentlyPlaying;
    }

    public void onOpenButtonClick(View view) {
        if (mp != null) {
            mp.release();
            stopSeekUpdater();
        }
        Intent intent = new Intent(this, FileSelector.class);
        startActivity(intent);
    }

    public void onPlaylistButtonClick(View view) {
        Intent intent = new Intent(this, PlaylistActivity.class);
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

    private void handleIntent( Intent intent ) {
        if (intent == null || intent.getAction() == null)
            return;
    }

    private void setNotification() {
        Intent intent = new Intent( getApplicationContext(), NotificationReceiverActivity.class );
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.MediaStyle style = new Notification.MediaStyle();

        Notification.Builder notification = new Notification.Builder(this)
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_library_music_black_24dp)
                        // Add media control buttons that invoke intents in your media service
                .addAction(R.drawable.ic_play_arrow_black_24dp, "Play", pendingIntent)// #1
                .setPriority(Notification.PRIORITY_MAX)
                .setStyle(style);

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, notification.build());
    }

}
