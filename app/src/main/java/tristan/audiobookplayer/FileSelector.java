package tristan.audiobookplayer;

import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.File;


public class FileSelector extends ActionBarActivity {

    File[] foldersInListView;
    ArrayAdapter<String> mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selector);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        ListView listView = (ListView)findViewById(R.id.fileListView);
        mAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1);
        File storage_folder = new File("/storage");
        File[] storage_folders = storage_folder.listFiles();
        foldersInListView = storage_folders;
        String[] dirs = getFilenames(storage_folders);

        mAdapter.addAll(dirs);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                // Open the selected folder
                                                File[] newfiles = foldersInListView[position].listFiles();
                                                foldersInListView = newfiles;
                                                mAdapter.clear();
                                                mAdapter.addAll(getFilenames(newfiles));
                                            }
                                        }
        );


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_selector, menu);
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

    private String[] getFilenames(File[] files)
    {
        String[] strings = new String[files.length];
        for (int i = 0; i < files.length; i++)
        {
            strings[i] = files[i].getAbsolutePath();
        }
        return strings;
    }



}
