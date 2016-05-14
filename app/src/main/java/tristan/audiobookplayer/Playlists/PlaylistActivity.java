package tristan.audiobookplayer.Playlists;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import tristan.audiobookplayer.Databases.PlayListDB;
import tristan.audiobookplayer.R;

public class PlaylistActivity extends Activity {

    PlayListDB playListDB;
    ArrayAdapter<String> mAdapter;


    public PlaylistActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        playListDB = new PlayListDB(this);
        setupPlaylists();
    }

    public void onCreatePlaylistButtonClick(View view) {
        Dialog d = onCreateDialog(null);
        d.show();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        final LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.playlist_dialog, null))
                // Add action buttons
                .setPositiveButton(R.string.playlist_dialog_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog f = (Dialog) dialog;
                        EditText playlistNameField = (EditText)f.findViewById(R.id.playlist_name);
                        Log.d("AudioBookPlayer", "Set playlist name is: " + playlistNameField.getText().toString());
                        playListDB.createPlaylist(playlistNameField.getText().toString());
                        setupPlaylists();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //LoginDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();

    }

    private void setupPlaylists() {
        Playlist[] playlists = playListDB.getPlaylists();
        ListView listView = (ListView)findViewById(R.id.playlistListView);
        mAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1);

        String[] playlistNames = new String[playlists.length];

        for (int i = 0; i < playlists.length; i++) {
            playlistNames[i] = playlists[i].getName();
            Log.d("AudioBookPlayer", "Playlist Name: " + playlistNames[i]);
        }

        mAdapter.addAll(playlistNames);
        listView.setAdapter(mAdapter);
    }
}
