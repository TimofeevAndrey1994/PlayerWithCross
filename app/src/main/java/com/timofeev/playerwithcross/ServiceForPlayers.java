package com.timofeev.playerwithcross;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


public class ServiceForPlayers extends Service {

    MediaPlayer mediaPlayer1;
    MediaPlayer mediaPlayer2;



    Uri musicUri1;
    Uri musicUri2;

    CountDownTimer countDownTimer;

    //для громкости
    final float x1 = 0.1f;
    final float x2 = 0.2f;
    final float x3 = 0.3f;
    final float x4 = 0.4f;
    final float x5 = 0.5f;
    final float x6 = 0.6f;
    final float x7 = 0.7f;
    final float x8 = 0.8f;
    final float x9 = 0.9f;
    final float x10 = 1.0f;


    int i;
    //счетчики
    int enter_count=1;
    int enter_count2=1;
    int enter_count_for2track=1;
    int enter_count2_for2track=1;


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

        //получем данные из интента

        musicUri1 = Uri.parse(intent.getStringExtra("selectedUri1"));
        musicUri2 = Uri.parse(intent.getStringExtra("selectedUri2"));
        //получаем значение кроссфейда
        i = intent.getIntExtra("crossfade",0);

        //создаем плееры и поехали.....

        mediaPlayer1 = MediaPlayer.create(getApplicationContext(),musicUri1);

        mediaPlayer2 = MediaPlayer.create(getApplicationContext(),musicUri2);


        mediaPlayer1.start();

        startPlayers(i);

        return super.onStartCommand(intent, flags, startId);
    }

    void startPlayers(final int crossfade){

        final int max = getMaxDuration(mediaPlayer1,mediaPlayer2);


   //таймер.....метод onTick будет вызвваться по величине кроссфейда, и это будет происходить очень долго...пока самы не остановим сервис(практически)
   countDownTimer = new CountDownTimer(max*100, 1000) {

          //в этом методе меняем треки
          @Override
          public void onTick(long l) {

              //повышаем громкость в первые 10 секунд трека, если трек играет
              if(mediaPlayer1.isPlaying()) {
                  if (mediaPlayer1.getCurrentPosition() <= 10000) {
                      upVolume(mediaPlayer1, enter_count);
                      enter_count = enter_count + 1;
                      Log.d("enter_count", String.valueOf(enter_count));
                      enter_count2 = 1;
                  }
              }

              //повышаем громкость в первые 10 секунд трека, если трек играет
              if(mediaPlayer2.isPlaying()) {
                  if (mediaPlayer2.getCurrentPosition() <= 10000) {
                      upVolume(mediaPlayer2, enter_count_for2track);
                      enter_count_for2track = enter_count_for2track + 1;
                      Log.d("enter_countfor2track", String.valueOf(enter_count_for2track));
                      enter_count2_for2track = 1;
                  }
              }
              //если остается меньше 10 сек до конца, трек начинает затихать
              if (mediaPlayer1.getDuration()-mediaPlayer1.getCurrentPosition()<=10000){
                  downVolume(mediaPlayer1, enter_count2);
                  enter_count2=enter_count2+1;
                  enter_count=1;
                  Log.d("enter_count2", String.valueOf(enter_count2));
              }

              //если остается меньше 10 сек до конца, трек начинает затихать
              if(mediaPlayer2.getDuration()-mediaPlayer2.getCurrentPosition()<=10000){
                  downVolume(mediaPlayer2, enter_count2_for2track);
                  enter_count2_for2track=enter_count2_for2track+1;
                  Log.d("enter_count2_for2track", String.valueOf(enter_count2_for2track));
                  enter_count_for2track=1;
              }



               //если играет 1 плеер и подходит кроссфейд, то стартуем
              if(mediaPlayer1!=null) {
                  if (mediaPlayer1.isPlaying()) {
                          if(mediaPlayer1.getDuration()-mediaPlayer1.getCurrentPosition()<=crossfade*1000){

                              if(!mediaPlayer2.isPlaying()){
                               mediaPlayer2.start();
                               mediaPlayer2.setVolume(0.1f,0.1f);

                              }
                          }
                          return;
                  }
              }

              //если играет 2 плеер и подходит кроссфейд, то стартуем
              if(mediaPlayer2!=null) {
                  if (mediaPlayer2.isPlaying()) {
                      if(mediaPlayer2.getDuration()-mediaPlayer2.getCurrentPosition()<=crossfade*1000){

                          if(!mediaPlayer1.isPlaying()){
                              mediaPlayer1.start();
                              mediaPlayer1.setVolume(0.1f, 0.1f);
                          }
                      }

                      return;

                  }
              }
          }

                @Override
                public void onFinish() {
                    stopSelf();
                }
            };

        //стартуем таймер
        countDownTimer.start();
    }

    //освобождаем плеер и останавливаем таймер при уничтожении сервиса
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

    //получем максимальную длину из двух треков
    int getMaxDuration(MediaPlayer mp1, MediaPlayer mp2){


        if (mp1.getDuration()>mp2.getDuration()){
            return mp1.getDuration();
        }
        if (mp2.getDuration()>mp1.getDuration()){
            return mp2.getDuration();
        } else
            return mp1.getDuration();



    }

    //метод для повышения громкости
    void upVolume(MediaPlayer mp, int k){

        if(k==1){
            mp.setVolume(x1,x1);
        }

        if(k==2){
            mp.setVolume(x2,x2);
        }

        if(k==3){
            mp.setVolume(x3,x3);
        }

        if(k==4){
            mp.setVolume(x4,x4);
        }

        if(k==5){
            mp.setVolume(x5,x5);
        }
        if(k==6){
            mp.setVolume(x6,x6);
        }
        if(k==7){

            mp.setVolume(x7,x7);
        }
        if(k==8){
            mp.setVolume(x8,x8);
        }
        if(k==9){
            mp.setVolume(x9,x9);
        }
        if(k==10){
            mp.setVolume(x10,x10);
        }


    }

    void downVolume(MediaPlayer mp, int k){


        if(k==1){
            mp.setVolume(x10,x10);
        }

        if(k==2){
            mp.setVolume(x9,x9);
        }

        if(k==3){
            mp.setVolume(x8,x8);
        }

        if(k==4){
            mp.setVolume(x7,x7);
        }

        if(k==5){
            mp.setVolume(x6,x6);
        }
        if(k==6){
            mp.setVolume(x5,x5);
        }
        if(k==7){
            mp.setVolume(x4,x4);
        }
        if(k==8){
            mp.setVolume(x3,x3);
        }
        if(k==9){
            mp.setVolume(x2,x2);
        }
        if(k==10){
            mp.setVolume(x1,x1);
        }

    }

}
