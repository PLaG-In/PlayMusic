package com.plag_in.playmusic;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends Activity {

    private static final int UPDATE_FREQUENCY = 500;
    private static final int STEP_VALUE = 5000;

    private ImageButton prevButton;
    private ImageButton nextButton;
    private ImageButton buttonPlayStop;
    private ImageButton buttonPlaylist ;
    private ImageButton buttonStop;
    private boolean firstOpen = true;
    private boolean isMovingSeekBar = false;
    //private static MediaPlayer mediaPlayer = null;
    private TextView textView = null;
    private SeekBar seekBar = null;

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
        buttonStop = (ImageButton) findViewById(R.id.StopButton);

        buttonPlayStop.setImageResource(R.drawable.bt_play);
        nextButton.setImageResource(R.drawable.bt_right_rewind);
        prevButton.setImageResource(R.drawable.bt_left_rewind);
        buttonPlaylist.setImageResource(R.drawable.bt_list);
        buttonStop.setImageResource(R.drawable.bt_stop_song);

        buttonPlaylist.setOnClickListener(onButtonClick);

        buttonStop.setOnClickListener(onButtonClick);
        nextButton.setLongClickable(true);
        prevButton.setLongClickable(true);
        //prevButton.setOnLongClickListener((View.OnLongClickListener) this);
        seekBar.setOnSeekBarChangeListener(seekBarChanged);
    }

    private void chooseSong(){
        seekBar.setProgress(0);

        String songName = mySongs.get(position).getName().replace(".mp3", "").replace(".wav", "");
        textView.setText(songName);

        seekBar.setMax(PlayMusicService.getDuration());
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
            buttonStop.setOnClickListener(onButtonClick);
            firstOpen = false;
        }

        position = data.getIntExtra("pos", 0);
        chooseSong();
        updatePosition();
    }


    private void updatePosition() {
        handler.removeCallbacks(updatePositionRunnable);

        seekBar.setProgress(PlayMusicService.getProgress());

        handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
    }


    private View.OnClickListener onButtonClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ButtonPlayStop:
                    if (PlayMusicService.isPlaying()) {
                        PlayMusicService.Pause();
                        buttonPlayStop.setImageResource(R.drawable.bt_play);
                    } else {
                        PlayMusicService.Play();
                        buttonPlayStop.setImageResource(R.drawable.bt_pause);
                    }
                    break;
                case R.id.RewindRightButton:
                    PlayMusicService.seekToRight(STEP_VALUE);
                    break;
                case R.id.RewindLeftButton:
                    PlayMusicService.seekToLeft(STEP_VALUE);
                    break;
                case R.id.StopButton:
                    stopService(
                            new Intent(MainActivity.this, PlayMusicService.class));
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
                PlayMusicService.seekTo(progress);
            }
            if (seekBar.getMax() == PlayMusicService.getProgress()){
                if (position != mySongs.size()){
                    position += 1;
                    seekBar.setProgress(0);
                    PlayMusicService.nextSong(position);

                }else{
                    position = 0;
                }
                chooseSong();
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(updatePositionRunnable);
    }
}
