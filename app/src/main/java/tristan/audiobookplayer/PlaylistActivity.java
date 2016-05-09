package tristan.audiobookplayer;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import tristan.audiobookplayer.Databases.PlayListDB;

public class PlaylistActivity extends Activity {

    PlayListDB playListDB;
    ArrayAdapter<String> mAdapter;


    public PlaylistActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        String[] playlists = playListDB.getPlaylists();
        ListView listView = (ListView)findViewById(R.id.fileListView);
        mAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1);
        mAdapter.addAll(playlists);
        listView.setAdapter(mAdapter);
    }

    public void onUpButtonClick(Context context) {

    }

}
