package com.timofeev.playerwithcross;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;



public class MyPlayer {

    Uri musicUri;
    Context context;
    MediaPlayer mediaPlayer;


    public MyPlayer(final Uri musicUri, final Context context){

        this.musicUri = musicUri;
        this.context = context;





    }

    public void go(int progress, Uri musicUri) {


           mediaPlayer = MediaPlayer.create(context, musicUri);
           mediaPlayer.seekTo(progress);
           mediaPlayer.start();

    }

            void stop(){
                mediaPlayer.stop();
                mediaPlayer.release();
            }

            int getDuration(){

            return this.mediaPlayer.getDuration();

            }


            boolean isPlaying(){

                return this.mediaPlayer.isPlaying();
            }

            int pause(){

                this.mediaPlayer.pause();
                this.mediaPlayer.release();
                return this.mediaPlayer.getCurrentPosition();
            }

        }











