package com.timofeev.playerwithcross;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;





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

        //получем данные из интента

        musicUri1 = Uri.parse(intent.getStringExtra("selectedUri1"));
        musicUri2 = Uri.parse(intent.getStringExtra("selectedUri2"));
        i = intent.getIntExtra("crossfade",0);

        //создаем плееры и поехали.....

        mediaPlayer1 = MediaPlayer.create(getApplicationContext(),musicUri1);
        mediaPlayer1.setLooping(true);

        mediaPlayer2 = MediaPlayer.create(getApplicationContext(),musicUri2);
        mediaPlayer2.setLooping(true);

        mediaPlayer1.start();

        startPlayers(i);

        return super.onStartCommand(intent, flags, startId);
    }

    void startPlayers(final int crossfade){

   final int max = getMaxDuration(mediaPlayer1,mediaPlayer2);


   //таймер.....метод onTick будет вызвваться по величине кроссфейда, и это будет происходить очень долго...пока самы не остановим сервис(практически)
   countDownTimer = new CountDownTimer(max*100,crossfade*1000) {

          //в этом методе меняем треки
          @Override
          public void onTick(long l) {

              enter_count += 1;

              if (enter_count == 1) {
                  return;
              }

              if(mediaPlayer1!=null) {
                  if (mediaPlayer1.isPlaying()) {

                          mediaPlayer1.pause();
                          mediaPlayer2.start();
                          upVolume(mediaPlayer2,crossfade);
                          return;


                  }
              }
              if(mediaPlayer2!=null) {
                  if (mediaPlayer2.isPlaying()) {

                      mediaPlayer2.pause();
                      mediaPlayer1.start();
                      upVolume(mediaPlayer1,crossfade);
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
    void upVolume(MediaPlayer mp, int i){

        for(int k=1;k==i;k++){
            mp.setVolume(10+k,10+k);
        }

    }

}
