package com.plag_in.playmusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

public class PlayMusicService extends Service {
    MediaPlayer mPlayer;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate()
    {
        Toast.makeText(this, "Служба создана",
                Toast.LENGTH_SHORT).show();
       // mPlayer = MediaPlayer.create(this, R.raw.sample);
        mPlayer.setLooping(false);
    }

    @Override
    public void onStart(Intent intent, int startid)
    {
        Toast.makeText(this, "Служба запущена",
                Toast.LENGTH_SHORT).show();
        mPlayer.start();
    }

    @Override
    public void onDestroy()
    {
        Toast.makeText(this, "Служба остановлена",
                Toast.LENGTH_SHORT).show();
        mPlayer.stop();
    }
}
