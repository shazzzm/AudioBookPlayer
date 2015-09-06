package tristan.audiobookplayer;

import android.content.Intent;
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
    public static final String FILENAME_INTENT = "filename";
    private final String SELECTOR_START = "/storage";
    final String[] extensions = {"mp3", "m4b", "m4a"};

    private File currentLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selector);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        ListView listView = (ListView)findViewById(R.id.fileListView);
        mAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1);
        File storage_folder = new File(SELECTOR_START);
        currentLocation = storage_folder;

        File[] storage_folders = storage_folder.listFiles();
        foldersInListView = storage_folders;
        String[] dirs = getFilenames(storage_folders);

        mAdapter.addAll(dirs);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Log.d("AudioBookFileSelector", "Running OnclickListener");
                                                // Open the selected folder
                                                if (foldersInListView[position].isDirectory()) {
                                                    foldersInListView[position].getAbsolutePath();
                                                    File[] newfiles = foldersInListView[position].listFiles();
                                                    foldersInListView = newfiles;
                                                    mAdapter.clear();
                                                    mAdapter.addAll(getFilenames(newfiles));
                                                }
                                                else if (isMediaFile(foldersInListView[position].getName()))
                                                {
                                                    Bundle extras = new Bundle();
                                                    extras.putString(FILENAME_INTENT, foldersInListView[position].getAbsolutePath());
                                                    Log.d("AudioBookFileSelector", "Sending intent");
                                                    Log.d("AudioBookFileSelector", foldersInListView[position].getAbsolutePath());
                                                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                                                    intent.putExtras(extras);
                                                    view.getContext().startActivity(intent);
                                                }
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

    public void onUpButtonClick(View view)
    {
        File[] newfiles = currentLocation.listFiles();
        foldersInListView = newfiles;
        mAdapter.clear();
        mAdapter.addAll(getFilenames(newfiles));
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

    private boolean isMediaFile(String filename) {
        String[] filenameArray = filename.split("\\.");
        String extension = filenameArray[filenameArray.length - 1];
        for (int i = 0; i < extensions.length; i++)
        {
            if (extensions[i].equals(extension))
            {
                return true;
            }
        }

        return false;
    }

}
