package com.plag_in.playmusic;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import java.io.IOException;

public class MainActivity extends Activity {

    private ImageButton buttonPlayStop;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            initViews();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initViews() throws IOException {
        buttonPlayStop = (ImageButton) findViewById(R.id.ButtonPlayStop);
        buttonPlayStop.setImageResource(R.drawable.ic_play);
        Uri myUri = Uri.parse("file:///sdcard/");
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(getApplicationContext(), myUri);
        //mediaPlayer = MediaPlayer.create(this, R..imagine_dragons_radioactive);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                seekChange(v);
                return false;
            }
        });
    }

    private void seekChange(View v){
        if(mediaPlayer.isPlaying()){
            SeekBar sb = (SeekBar)v;
            mediaPlayer.seekTo(sb.getProgress());
        }
    }

    public void playAndStop(View v){
        if (!mediaPlayer.isPlaying()) {
            buttonPlayStop.setImageResource(R.drawable.ic_play);
            try{
                mediaPlayer.start();
                startPlayProgressUpdater();
            }catch (IllegalStateException e) {
                mediaPlayer.pause();
            }
        }else {
            buttonPlayStop.setImageResource(R.drawable.ic_pause);
            mediaPlayer.pause();
        }
    }

    public void startPlayProgressUpdater() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification,1000);
        }else{
            mediaPlayer.pause();
            buttonPlayStop.setImageResource(R.drawable.ic_play);
            seekBar.setProgress(0);
        }
    }
}