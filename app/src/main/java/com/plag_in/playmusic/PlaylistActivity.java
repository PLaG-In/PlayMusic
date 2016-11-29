package com.plag_in.playmusic;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {

    ListView lv;
    String[] items;
    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
        }else {
            lv = (ListView) findViewById(R.id.lvPlaylist);
            final ArrayList<File> mySongs = findSongs(Environment.getExternalStorageDirectory());
            if(mySongs == null){
                return;
            }
            items = new String[mySongs.size()];
            for (int i = 0; i < mySongs.size(); i++) {
                items[i] = mySongs.get(i).getName().replace(".mp3", "").replace(".wav", "");
            }

            ArrayAdapter<String> adp = new ArrayAdapter<>(getApplicationContext(),
                    R.layout.song_layout, R.id.textView2, items);
            lv.setAdapter(adp);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startService(new Intent(PlaylistActivity.this, PlayMusicService.class)
                            .putExtra("playlist", mySongs).putExtra("pos", position));
                    Intent intent = new Intent(PlaylistActivity.this, MainActivity.class);
                    intent.putExtra("pos", position).putExtra("playlist", mySongs);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }


    private ArrayList<File> findSongs(File root) {
        ArrayList<File> al = new ArrayList<>();
        File[] files = root.listFiles();
        if(files != null) {
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
        }else{
            Log.d(LOG_TAG, "На SD-карте нет музыкальный файлов.");
            return null;
        }
    }
}
