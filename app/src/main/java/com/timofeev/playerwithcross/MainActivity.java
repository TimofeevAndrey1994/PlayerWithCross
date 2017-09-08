package com.timofeev.playerwithcross;

import android.app.ActivityManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private static final int SELECT_AUDIO_FILE1 = 1;
    private static final int SELECT_AUDIO_FILE2 = 2;

    String selectedPath1;
    String selectedPath2;

    SeekBar seekBar;

    TextView textView;
    TextView valueTextView;

    Button file1;
    Button file2;
    Button btnPlay;
    Button btnStop;

    Uri selectedUri1;
    Uri selectedUri2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        boolean serviceFlag = isMyServiceRunning(ServiceForPlayers.class);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        textView = (TextView) findViewById(R.id.textView);
        valueTextView = (TextView) findViewById(R.id.textViewForValue);
        valueTextView.setText("2");

        file1 = (Button) findViewById(R.id.file1);
        file1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               chooseFile(SELECT_AUDIO_FILE1);
            }
        });

        file2 = (Button) findViewById(R.id.file2);
        file2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile(SELECT_AUDIO_FILE2);
            }
        });

        btnPlay = (Button) findViewById(R.id.buttonPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedPath1==null || selectedPath2 == null){
                    Toast.makeText(MainActivity.this,"Выберете 2 файла для воспроизведения",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(MainActivity.this,ServiceForPlayers.class);
                intent.putExtra("selectedUri1",selectedUri1.toString());
                intent.putExtra("selectedUri2",selectedUri2.toString());
                intent.putExtra("crossfade",seekBar.getProgress()+2);
                startService(intent);
                btnStop.setVisibility(Button.VISIBLE);
                btnPlay.setVisibility(Button.INVISIBLE);
            }
        });


        btnStop = (Button) findViewById(R.id.buttonStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopService(new Intent (MainActivity.this, ServiceForPlayers.class));
                btnPlay.setVisibility(Button.VISIBLE);
                btnStop.setVisibility(Button.INVISIBLE);


            }
        });

        if (serviceFlag){
            btnStop.setVisibility(Button.VISIBLE);
        } else btnPlay.setVisibility(Button.VISIBLE);

    }


    void chooseFile(int requestCode){

        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select audioFile"), requestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_AUDIO_FILE1) {
                selectedUri1 = data.getData();
                selectedPath1 = getPath(selectedUri1);
                file1.setText(selectedPath1);
            }
            if (requestCode == SELECT_AUDIO_FILE2) {
                selectedUri2 = data.getData();
                selectedPath2 = getPath(selectedUri2);
                file2.setText(selectedPath2);
            }
        }

    }

    public String getPath(Uri uri) {
        String[] proj = { MediaStore.Audio.Media.DATA };
        CursorLoader loader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        valueTextView.setText(String.valueOf(seekBar.getProgress()+2));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private boolean isMyServiceRunning(Class<ServiceForPlayers> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}


