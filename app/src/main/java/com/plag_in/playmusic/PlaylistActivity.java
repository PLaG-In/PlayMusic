package com.plag_in.playmusic;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {

    ListView lv;
    String[] items;

    final String LOG_TAG = "myLogs";
    final String DIR_SD = "media";
    final String FILENAME_SD = "fileSD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        lv = (ListView) findViewById(R.id.lvPlaylist);

        final ArrayList<File> mySongs = findSongs(Environment.getExternalStorageDirectory());
        items = new String[mySongs.size()];
        for (int i = 0; i < mySongs.size(); i++) {
            //toast(mySongs.get(i).getName().toString());
            items[i] = mySongs.get(i).getName().replace(".mp3", "").replace(".wav", "");
        }

        ArrayAdapter<String> adp = new ArrayAdapter<>(getApplicationContext(),
                R.layout.song_layout, R.id.textView2, items);
        lv.setAdapter(adp);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PlaylistActivity.this, MainActivity.class);
                intent.putExtra("pos", position).putExtra("playlist", mySongs);
                setResult(RESULT_OK, intent);
                finish();
                /*startActivity(new Intent(getApplicationContext(),
                        MainActivity.class).putExtra("pos", position).putExtra("playlist", mySongs));*/
            }
        });
    }

    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {return;}
        Intent intentF = new Intent();
        setResult(RESULT_OK,intentF);
        finish();
    }*/

   /* public void toast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }*/

    public ArrayList<File> findSongs(File root) {
        ArrayList<File> al = new ArrayList<>();
        File[] files = root.listFiles();
        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                al.addAll(findSongs(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                    al.add(singleFile);
                }
            }
        }
        return al;
    }
}
