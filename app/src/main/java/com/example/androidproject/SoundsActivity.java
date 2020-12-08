package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class SoundsActivity extends AppCompatActivity {

    ListView soundListView;
    ArrayList<String> arrayList;
    ArrayAdapter soundAdapter;
    MediaPlayer soundPlayer;
    Button resumeSound;
    Button pauseSound;
    int pause;
    /**
     * arrayList created and sounds added from raw file to the arrayList.
     * items from arrayList are displayed in arrayList layout.
     * when soundName is clicked on arrayList sound starts playing and it finds the matching sound for the soundName.
     * pauseButton saves the current position of mediaPlayer and when resumeButton is pressed, sound continues from that position.
     * stopButton stops the sound and releases mediaPlayer for battery saving purposes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sounds);

        soundListView = findViewById(R.id.soundListView);
        //arraylist created and sounds are added to the arraylist.
        arrayList = new ArrayList<String>();
        Field[] sounds = R.raw.class.getFields();
        for (int i = 0; i < sounds.length; i++) {
            arrayList.add(sounds[i].getName());
        }
        //sets arraylist sound names into an array layout
        soundAdapter = new ArrayAdapter(this, R.layout.listview_style2, arrayList);
        soundListView.setAdapter(soundAdapter);
        //when something is clicked in an array, voice is being played
        soundListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                if (soundPlayer != null) {
                    soundPlayer.release();
                }
                //finds the correct sound for specific name and sound is being looped so it can be listened whilst trying to sleep.
                int soundNumber = getResources().getIdentifier(arrayList.get(i), "raw", getPackageName());
                soundPlayer = MediaPlayer.create(SoundsActivity.this, soundNumber);
                soundPlayer.setLooping(true);
                soundPlayer.start();
                pauseSound.setVisibility(View.VISIBLE);
                resumeSound.setVisibility(View.INVISIBLE);
            }
        });
        //finds the timeframe when soundfile was paused at and starts playing from there.
        resumeSound = (Button) findViewById(R.id.resumeButton);
        resumeSound.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                soundPlayer.seekTo(pause);
                soundPlayer.start();
                resumeSound.setVisibility(View.INVISIBLE);
                pauseSound.setVisibility(View.VISIBLE);
            }
        });
        //pauses current sound and saves the current position of mediaplayer.
        pauseSound = (Button) findViewById(R.id.pauseButton);
        pauseSound.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                soundPlayer.pause();
                pause = soundPlayer.getCurrentPosition();
                resumeSound.setVisibility(View.VISIBLE);
                pauseSound.setVisibility(View.INVISIBLE);
            }
        });
    }

     /**sound stops when back button is pressed and mediaplayer is released decreasing battery usage.
     back button has release feature because if user plays sounds on it and presses pause and closes the app without pressing stop, mediaplayer is never released and uses battery.
     */
    public void onDestroy(){
        super.onDestroy();
        if (soundPlayer != null) {
            soundPlayer.stop();
            soundPlayer.release();
        }
    }
}