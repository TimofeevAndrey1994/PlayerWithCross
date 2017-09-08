package com.timofeev.playerwithcross;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;




public class ServiceForPlayers extends Service {

    MediaPlayer mediaPlayer1;
    MediaPlayer mediaPlayer2;

    Uri musicUri1;
    Uri musicUri2;

    CountDownTimer countDownTimer;

    int enter_count=0;


    int i;


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        musicUri1 = Uri.parse(intent.getStringExtra("selectedUri1"));
        musicUri2 = Uri.parse(intent.getStringExtra("selectedUri2"));
        i = intent.getIntExtra("crossfade",0);

        mediaPlayer1 = MediaPlayer.create(getApplicationContext(),musicUri1);
        mediaPlayer2 = MediaPlayer.create(getApplicationContext(),musicUri2);

        mediaPlayer1.start();

        startPlayers(i);

        return super.onStartCommand(intent, flags, startId);
    }

    void startPlayers(final int crossfade){

   final int max = getMaxDuration(mediaPlayer1,mediaPlayer2);
   countDownTimer = new CountDownTimer(max,crossfade*1000) {

          @Override
          public void onTick(long l) {

              enter_count += 1;

              if (enter_count == 1) {
                  return;
              }

              if(mediaPlayer1!=null) {
                  if (mediaPlayer1.isPlaying()) {
                          if((max-mediaPlayer2.getCurrentPosition())<=crossfade){
                              Toast.makeText(getApplicationContext(), "2 трек закончен, 1 доигрывает", Toast.LENGTH_SHORT).show();
                              return;
                          }
                          mediaPlayer1.pause();
                          mediaPlayer2.start();
                          upVolume(mediaPlayer2,crossfade);

                       //   Toast.makeText(getApplicationContext(), "player 1", Toast.LENGTH_SHORT).show();
                          return;


                  }
              }
              if(mediaPlayer2!=null) {
                  if (mediaPlayer2.isPlaying()) {
                      if((max-mediaPlayer1.getCurrentPosition())<=crossfade){
                          Toast.makeText(getApplicationContext(), "1 трек закончен, второй доигрывает", Toast.LENGTH_SHORT).show();
                          return;
                      }

                      mediaPlayer2.pause();
                      mediaPlayer1.start();
                      upVolume(mediaPlayer1,crossfade);

                     // Toast.makeText(getApplicationContext(), "player 2", Toast.LENGTH_SHORT).show();
                      return;

                  }
              }
          }

                @Override
                public void onFinish() {
                    Toast.makeText(getApplicationContext(),"Воспроизведение треков остановлено",Toast.LENGTH_LONG).show();
                    stopSelf();
                }
            };

        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
        if (mediaPlayer1!=null){
           if (mediaPlayer1.isPlaying()) {
               mediaPlayer1.stop();
               mediaPlayer1.release();
           }
        }
        if (mediaPlayer2!=null){
            if (mediaPlayer2.isPlaying()) {
                mediaPlayer2.stop();
                mediaPlayer2.release();
            }
        }


    }

    int getMaxDuration(MediaPlayer mp1, MediaPlayer mp2){


        if (mp1.getDuration()>mp2.getDuration()){
            return mp1.getDuration();
        }
        if (mp2.getDuration()>mp1.getDuration()){
            return mp2.getDuration();
        } else
            return mp1.getDuration();



    }

    void upVolume(MediaPlayer mp, int i){

        for(int k=1;k==i;k++){
            mp.setVolume(10+k,10+k);
        }

    }

}
