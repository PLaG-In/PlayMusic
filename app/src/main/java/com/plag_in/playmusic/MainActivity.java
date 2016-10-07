package com.plag_in.playmusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;

import static android.R.id.list;

public class MainActivity extends Activity implements OnClickListener {

    private static final int UPDATE_FREQUENCY = 500;
    private static final int STEP_VALUE = 4000;

    private ImageButton prevButton = null;
    private ImageButton nextButton = null;
    private ImageButton buttonPlayStop = null;
    private static MediaPlayer mediaPlayer = null;
    private SeekBar seekBar = null;
    private MediaCursorAdapter mediaAdapter = null;
    private TextView selectedFile = null;

    private boolean isStarted = true;
    private String currentFile = "";
    private boolean isMovingSeekBar = false;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
     /*   super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            initViews();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedFile = (TextView) findViewById(R.id.selectedfile);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        buttonPlayStop = (ImageButton) findViewById(R.id.ButtonPlayStop);
        prevButton = (ImageButton) findViewById(R.id.RewindLeftButton);
        nextButton = (ImageButton) findViewById(R.id.RewindRightButton);
       // btNext = (ImageButton) findViewById(R.id.btNxt);
        //btPrev = (ImageButton) findViewById(R.id.btPrev);

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(onCompletion);
        mediaPlayer.setOnErrorListener(onError);
        seekBar.setOnSeekBarChangeListener(seekBarChanged);

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        if (null != cursor) {
            cursor.moveToFirst();

            mediaAdapter = new MediaCursorAdapter(this, R.layout.list_item, cursor);

            setListAdapter(mediaAdapter);

            buttonPlayStop.setOnClickListener(onButtonClick);
            nextButton.setOnClickListener(onButtonClick);
            prevButton.setOnClickListener(onButtonClick);
            nextButton.setOnClickListener(onButtonClick);
            nextButton.setOnClickListener(onButtonClick);

        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {return;}
        String txt = data.getStringExtra("final");
        //txtView.setText(txt);
    }

    private void initViews() throws IOException {
        buttonPlayStop = (ImageButton) findViewById(R.id.ButtonPlayStop);
        buttonPlayStop.setImageResource(R.drawable.bt_play);
        /*Uri myUri = Uri.parse("file:///sdcard/");
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(getApplicationContext(), myUri);
        //mediaPlayer = MediaPlayer.create(this, R..imagine_dragons_radioactive);*/
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
            buttonPlayStop.setImageResource(R.drawable.bt_play);
            try{
                mediaPlayer.start();
                startPlayProgressUpdater();
            }catch (IllegalStateException e) {
                mediaPlayer.pause();
            }
        }else {
            buttonPlayStop.setImageResource(R.drawable.bt_pause);
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
            buttonPlayStop.setImageResource(R.drawable.bt_play);
            seekBar.setProgress(0);
        }
    }
//---------------------------------------------------------------------------------
    int itemsInList = list.getAdapter().getCount();
    for(int i = 1; i < itemsInList; i++){
        list.setSelection(i);
    }
////--------------------------------------------------------------
    public static void nextSong() {
        int numOfSong = songList.size();

        if (!isShuffle) { // Shuffle mode is off
            if (currentPosition < numOfSong - 1) {
                currentPosition++;
                currentSong = songList.get(currentPosition);
                Log.d("my_log", "position = "+currentPosition);
                playBackMusic();
            } else {
                currentPosition = 0;
                currentSong = songList.get(currentPosition);
                Log.d("my_log", "position = "+currentPosition);
                playBackMusic();
            }
        } else { // Shuffle mode is on
            Random rand = new Random();
            currentPosition = rand.nextInt(numOfSong);
            currentSong = songList.get(currentPosition);
            Log.d("my_log", "position = "+currentPosition);
            playBackMusic();
        }
    }
    public static void playBackMusic() {
        try {
            mediaPlayer.release();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    endOfTheSong();
                }
            });

            isPlaying = true;
            mediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void endOfTheSong() {
        if (isRepeat == 1) { // currently repeat one song
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } else if (isRepeat == 2) { // currently repeat all songs
            nextSong();
        } else { // currently no repeat

            if (currentPosition != songList.size() - 1) nextSong();

        }
    }
    ////--------------------------------------------------------------
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        currentFile = (String) view.getTag();
        startPlay(currentFile);
    }
// --------------------------------------------------------------------------------

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(updatePositionRunnable);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();

        mediaPlayer = null;
    }

    private void startPlay(String file) {
        Log.i("Selected: ", file);

        selectedFile.setText(file);
        seekBar.setProgress(0);

        mediaPlayer.stop();
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(file);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        seekBar.setMax(mediaPlayer.getDuration());
        buttonPlayStop.setImageResource(R.drawable.bt_pause);

        updatePosition();

        isStarted = true;
    }

    private void stopPlay() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        buttonPlayStop.setImageResource(R.drawable.bt_play);
        handler.removeCallbacks(updatePositionRunnable);
        seekBar.setProgress(0);

        isStarted = false;
    }

    private void updatePosition() {
        handler.removeCallbacks(updatePositionRunnable);

        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
    }

    private class MediaCursorAdapter extends SimpleCursorAdapter {

        public MediaCursorAdapter(Context context, int layout, Cursor c) {
            super(context, layout, c,
                    new String[]{MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.TITLE, MediaStore.Audio.AudioColumns.DURATION},
                    new int[]{R.id.displayname, R.id.title, R.id.duration});
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView name = (TextView) view.findViewById(R.id.displayname);
            TextView duration = (TextView) view.findViewById(R.id.duration);

            name.setText(cursor.getString(
                    cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)));

            title.setText(cursor.getString(
                    cursor.getColumnIndex(MediaStore.MediaColumns.TITLE)));

            long durationInMs = Long.parseLong(cursor.getString(
                    cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)));

            double durationInMin = ((double) durationInMs / 1000.0) / 60.0;

            durationInMin = new BigDecimal(Double.toString(durationInMin)).setScale(2, BigDecimal.ROUND_UP).doubleValue();

            duration.setText("" + durationInMin);

            view.setTag(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.list_item, parent, false);

            bindView(v, context, cursor);

            return v;
        }
    }

    private View.OnClickListener onButtonClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ButtonPlayStop: {
                    if (mediaPlayer.isPlaying()) {
                        handler.removeCallbacks(updatePositionRunnable);
                        mediaPlayer.pause();
                        buttonPlayStop.setImageResource(R.drawable.bt_play);
                    } else {
                        if (isStarted) {
                            mediaPlayer.start();
                            buttonPlayStop.setImageResource(R.drawable.bt_pause);

                            updatePosition();
                        } else {
                            startPlay(currentFile);
                        }
                    }

                    break;
                }
                case R.id.RewindRightButton: {
                    int seekto = mediaPlayer.getCurrentPosition() + STEP_VALUE;

                    if (seekto > mediaPlayer.getDuration())
                        seekto = mediaPlayer.getDuration();

                    mediaPlayer.pause();
                    mediaPlayer.seekTo(seekto);
                    mediaPlayer.start();

                    break;
                }
                case R.id.RewindLeftButton: {
                    int seekto = mediaPlayer.getCurrentPosition() - STEP_VALUE;

                    if (seekto < 0)
                        seekto = 0;

                    mediaPlayer.pause();
                    mediaPlayer.seekTo(seekto);
                    mediaPlayer.start();

                    break;
                }
                /*case R.id.btNxt: {
                    //TO DO
                }
                case R.id.btPrev: {
                    //TO DO
                }*/
            }
        }
    };

    private MediaPlayer.OnCompletionListener onCompletion = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            stopPlay();
        }
    };

    private MediaPlayer.OnErrorListener onError = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            return false;
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

                Log.i("OnSeekBarChangeListener", "onProgressChanged");
            }
        }
    };
}