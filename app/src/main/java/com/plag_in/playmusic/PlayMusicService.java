package com.plag_in.playmusic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class PlayMusicService extends Service {

    private static ArrayList<File> mySongs;
    private static int position;
    private static Context contxt;
    public static MediaPlayer mPlayer;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Служба создана",
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Служба запущена",
                Toast.LENGTH_SHORT).show();
        mySongs = (ArrayList) intent.getParcelableArrayListExtra("playlist");
        setPlayer(intent.getIntExtra("pos", 0));

        return super.onStartCommand(intent, flags, startId);
    }

    public static void seekToLeft(int pos) {
        mPlayer.seekTo(mPlayer.getCurrentPosition() - pos);
    }

    public static void seekToRight(int pos) {
        mPlayer.seekTo(mPlayer.getCurrentPosition() + pos);
    }

    public static void seekTo(int pos) {
        mPlayer.seekTo(pos);
    }

    @NonNull
    public static Integer getProgress() {
        return mPlayer.getCurrentPosition();
    }

    @NonNull
    public static Integer getDuration() {
        return mPlayer.getDuration();
    }

    @NonNull
    public static Boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public static void Pause() {
        mPlayer.pause();
    }

    public static void Play() {
        mPlayer.start();
    }

    public void setPlayer(int pos) {
        position = pos;
        String song = mySongs.get(position).toString();
        Uri uri = Uri.parse(song);
        contxt = getApplicationContext();

        mPlayer = MediaPlayer.create(contxt, uri);
        mPlayer.start();
    }

    public static void nextSong(int pos){
        mPlayer.stop();
        mPlayer.release();

        position = pos;
        String song = mySongs.get(position).toString();
        Uri uri = Uri.parse(song);
        mPlayer = MediaPlayer.create(contxt, uri);
        mPlayer.start();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Служба остановлена",
                Toast.LENGTH_SHORT).show();
        mPlayer.stop();
        mPlayer.reset();
        mPlayer.release();

        mPlayer = null;
    }
}

