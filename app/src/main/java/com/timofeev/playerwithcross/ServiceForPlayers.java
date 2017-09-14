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

    final float MAX_VOLUME = 100;

    Uri musicUri1;
    Uri musicUri2;

    CountDownTimer countDownTimer;

    int i;

    //переменная для текущей громкости
    private int iVolume_for1=0;
    private int iVolume_for2=0;

    private final static int INT_VOLUME_MAX = 100;
    private final static int INT_VOLUME_MIN = 0;
    private final static float FLOAT_VOLUME_MAX = 1;
    private final static float FLOAT_VOLUME_MIN = 0;


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


   //таймер.....метод onTick будет вызвваться по величине кроссфейда, и это будет происходить очень долго...пока самы не остановим сервис(практически)
   countDownTimer = new CountDownTimer(5000, 100) {

          //в этом методе меняем треки
          @Override
          public void onTick(long l) {



              //повышаем громкость в первые 10 секунд трека, если трек играет
              if(mediaPlayer1.isPlaying()) {
                  if (mediaPlayer1.getCurrentPosition() <= 10000 && iVolume_for1<=101) {

                      updateVolume(+1, iVolume_for1, mediaPlayer1);

                  }
              }

              //повышаем громкость в первые 10 секунд трека, если трек играет
              if(mediaPlayer2.isPlaying()) {
                  if (mediaPlayer2.getCurrentPosition() <= 10000 && iVolume_for2<=101 ) {

                      updateVolume(+1, iVolume_for2,mediaPlayer2);

                  }
              }
              //если остается меньше 10 сек до конца, трек начинает затихать
              if (mediaPlayer1.getDuration()-mediaPlayer1.getCurrentPosition()<=10000 && !(iVolume_for1==0)){

                  updateVolume(-1,iVolume_for1,mediaPlayer1);

              }

              //если остается меньше 10 сек до конца, трек начинает затихать
              if(mediaPlayer2.getDuration()-mediaPlayer2.getCurrentPosition()<=10000 && !(iVolume_for2==0)){

                  updateVolume(-1, iVolume_for2,mediaPlayer2);

              }



               //если играет 1 плеер и подходит кроссфейд, то стартуем
              if(mediaPlayer1!=null) {
                  if (mediaPlayer1.isPlaying()) {
                          if(mediaPlayer1.getDuration()-mediaPlayer1.getCurrentPosition()<=crossfade*1000){

                              if(!mediaPlayer2.isPlaying()){
                               mediaPlayer2.start();

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
                          }
                      }

                      return;

                  }
              }
          }

                @Override
                public void onFinish() {

                    //теперь каждый раз, когда попадаем в этот метод,
                    // будет заново стартовать таймер, и таймер получится бесконечным,
                    // пока пользователь сам его не остановит

                    if (countDownTimer!=null){
                        countDownTimer.start();
                    }

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

    //метод для линейного изменения громкости
    private void updateVolume(int change, int iVolume, MediaPlayer mp) {
        // уменьшаем или увеличиваем в зависимости от знака перед change

        iVolume = iVolume + change;



        if (iVolume < INT_VOLUME_MIN)
            iVolume = INT_VOLUME_MIN;
        else if (iVolume > INT_VOLUME_MAX)
            iVolume = INT_VOLUME_MAX;
        // переводим во float
        float fVolume = 1 - ((float) Math.log(INT_VOLUME_MAX - iVolume) / (float) Math.log(INT_VOLUME_MAX));
        Log.d("fVolume", String.valueOf(fVolume));


        if (fVolume < FLOAT_VOLUME_MIN)
            fVolume = FLOAT_VOLUME_MIN;
        else if (fVolume > FLOAT_VOLUME_MAX)
            fVolume = FLOAT_VOLUME_MAX;

            mp.setVolume(fVolume, fVolume);
            if (mp==mediaPlayer1) {
                iVolume_for1 = iVolume;
                Log.d("iVolume_for1", String.valueOf(iVolume_for1));
            }
            if(mp==mediaPlayer2){
                iVolume_for2 = iVolume;
                Log.d("iVolume_for2", String.valueOf(iVolume_for2));
            }
    }

}
