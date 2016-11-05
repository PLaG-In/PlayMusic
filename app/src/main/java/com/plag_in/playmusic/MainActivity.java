package com.plag_in.playmusic;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int UPDATE_FREQUENCY = 500;
    private static final int STEP_VALUE = 5000;

    private ImageButton prevButton;
    private ImageButton nextButton;
    private boolean firstOpen = true;
    private boolean isMovingSeekBar = false;
    private ImageButton buttonPlayStop;
    private ImageButton buttonPlaylist ;
    private static MediaPlayer mediaPlayer = null;
    private TextView textView = null;
    private SeekBar seekBar = null;

   // private Thread updateSeekBar;

    private final Handler handler = new Handler();

    private final Runnable updatePositionRunnable = new Runnable() {
        public void run() {
            updatePosition();
        }
    };

    private ArrayList<File> mySongs;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        textView = (TextView) findViewById(R.id.textView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        buttonPlayStop = (ImageButton) findViewById(R.id.ButtonPlayStop);
        prevButton = (ImageButton) findViewById(R.id.RewindLeftButton);
        nextButton = (ImageButton) findViewById(R.id.RewindRightButton);
        buttonPlaylist = (ImageButton) findViewById(R.id.PlaylistButton);

        buttonPlayStop.setImageResource(R.drawable.bt_play);
        nextButton.setImageResource(R.drawable.bt_right_rewind);
        prevButton.setImageResource(R.drawable.bt_left_rewind);
        buttonPlaylist.setImageResource(R.drawable.bt_list);

        /*updateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                seekBar.setMax(totalDuration);
                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };*/
        buttonPlaylist.setOnClickListener(onButtonClick);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        nextButton.setLongClickable(true);
        prevButton.setLongClickable(true);
        //prevButton.setOnLongClickListener((View.OnLongClickListener) this);
        seekBar.setOnSeekBarChangeListener(seekBarChanged);
        /*seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });*/
    }

    private void chooseSong(){
        seekBar.setProgress(0);
        assert mySongs != null;
        String song = mySongs.get(position).toString();
        Uri uri = Uri.parse(song);

        String songName = mySongs.get(position).getName().replace(".mp3", "").replace(".wav", "");
        textView.setText(songName);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        seekBar.setMax(mediaPlayer.getDuration());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {return;}

        if (firstOpen){
            mySongs = (ArrayList) data.getParcelableArrayListExtra("playlist");
            buttonPlayStop.setOnClickListener(onButtonClick);
            nextButton.setOnClickListener(onButtonClick);
            prevButton.setOnClickListener(onButtonClick);
            firstOpen = false;
        }else{
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        position = data.getIntExtra("pos", 0);
        chooseSong();
        updatePosition();
        mediaPlayer.start();
    }


    private void updatePosition() {
        handler.removeCallbacks(updatePositionRunnable);

        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
    }


    private View.OnClickListener onButtonClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ButtonPlayStop:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        buttonPlayStop.setImageResource(R.drawable.bt_play);
                    } else {
                        mediaPlayer.start();
                        //updateSeekBar.start();
                        buttonPlayStop.setImageResource(R.drawable.bt_pause);
                    }
                    break;
                case R.id.RewindRightButton:
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + STEP_VALUE);
                    break;
                case R.id.RewindLeftButton:
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - STEP_VALUE);
                    break;
                case R.id.PlaylistButton:
                    Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
                    buttonPlayStop.setImageResource(R.drawable.bt_pause);
                    startActivityForResult(intent, 1);
                    break;
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isMovingSeekBar = false;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isMovingSeekBar = true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (isMovingSeekBar) {
                mediaPlayer.seekTo(progress);
            }
            if (seekBar.getMax() == mediaPlayer.getCurrentPosition()){
                if (position != mySongs.size()){
                    position += 1;
                    seekBar.setProgress(0);
                }else{
                    position = 0;
                }
                mediaPlayer.stop();
                mediaPlayer.reset();

                chooseSong();
                mediaPlayer.start();
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(updatePositionRunnable);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();

        mediaPlayer = null;
    }
}
