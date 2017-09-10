package com.timofeev.playerwithcross;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private static final int SELECT_AUDIO_FILE1 = 1;
    private static final int SELECT_AUDIO_FILE2 = 2;

    private static final int PERMISSION_REQUEST_CODE = 123;

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

        //флаг, проверяющий, запущен ли сервис
        boolean serviceFlag = isMyServiceRunning(ServiceForPlayers.class);

        //Находим элементы и ставим слушатели

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        textView = (TextView) findViewById(R.id.textView);
        valueTextView = (TextView) findViewById(R.id.textViewForValue);
        valueTextView.setText("2");

        file1 = (Button) findViewById(R.id.file1);
        file1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (hasPermissions()) {
                    chooseFile(SELECT_AUDIO_FILE1);

                } else {

                    requestPermissionWithRationale();

                }

            }
        });

        file2 = (Button) findViewById(R.id.file2);
        file2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (hasPermissions()) {
                    chooseFile(SELECT_AUDIO_FILE2);

                } else {

                    requestPermissionWithRationale();

                }

            }
        });

        btnPlay = (Button) findViewById(R.id.buttonPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedUri1 == null || selectedUri2 == null) {
                    Toast.makeText(MainActivity.this, "Выберете 2 файла для воспроизведения", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Запускаем сервис и передаем в него URI выбранных песен, и значение кроссфейда
                Intent intent = new Intent(MainActivity.this, ServiceForPlayers.class);
                intent.putExtra("selectedUri1", selectedUri1.toString());
                intent.putExtra("selectedUri2", selectedUri2.toString());
                intent.putExtra("crossfade", seekBar.getProgress() + 2);
                startService(intent);
                //делаем доступной только кнопку стоп
                file1.setEnabled(false);
                file2.setEnabled(false);
                seekBar.setEnabled(false);
                btnStop.setEnabled(true);
                btnPlay.setEnabled(false);
            }
        });


        btnStop = (Button) findViewById(R.id.buttonStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //останавливаем сервис и возвращаем доступность кнопок
                stopService(new Intent(MainActivity.this, ServiceForPlayers.class));
                seekBar.setEnabled(true);
                file1.setEnabled(true);
                file2.setEnabled(true);
                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);


            }
        });

        if (serviceFlag) {
            btnStop.setEnabled(true);
        } else btnPlay.setEnabled(true);

    }


    void chooseFile(int requestCode) {

        //метод для выбора аудиофайла
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select audioFile"), requestCode);

    }

    //известный метод для обработки выбора
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_AUDIO_FILE1) {
                selectedUri1 = data.getData();
                selectedPath1 = getName(selectedUri1);
                Log.d("myName", String.valueOf(selectedPath1));
                file1.setText(selectedUri1.getPath());
            }
            if (requestCode == SELECT_AUDIO_FILE2) {
                selectedUri2 = data.getData();
                selectedPath2 = getName(selectedUri2);
                file2.setText(selectedUri2.getPath());
            }
        }

    }


    public String getName(Uri uri) {
        Cursor returnCursor = this.getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        return returnCursor.getString(nameIndex);
    }

    //переопределенные методы seekbar
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        valueTextView.setText(String.valueOf(seekBar.getProgress() + 2));
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

    //метод проверки разрещения
    private boolean hasPermissions() {
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    //пытаемся регистрировать разрешение
    private void requestPerms() {

        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }


    //обрабатываем ответ пользователя
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                for (int res : grantResults) {
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed) {
            //user granted all permissions we can perform our task.
            Toast.makeText(getApplicationContext(), "разрешения успешно получены", Toast.LENGTH_SHORT).show();
        } else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();

                } else {
                    //вызов snackBar
                }
            }
        }

    }

    public void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String message = "Storage permission is needed to show files count";
            Snackbar.make(MainActivity.this.findViewById(R.id.activity_view), message, Snackbar.LENGTH_LONG)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPerms();
                        }
                    })
                    .show();
        } else {
            requestPerms();
        }
    }

}




