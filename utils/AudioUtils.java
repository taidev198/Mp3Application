package com.example.mp3playerapplication.utils;

import android.os.Environment;
import android.provider.MediaStore;

public class AudioUtils {

    public static String[] PROJECTIONS = {
            MediaStore.Audio.Media._ID,     //0
            MediaStore.Audio.Media.ARTIST,  //1
            MediaStore.Audio.Media.TITLE,   //2
            MediaStore.Audio.Media.DATA,    //3
            MediaStore.Audio.Media.DISPLAY_NAME,//4
            MediaStore.Audio.Media.DURATION,//5
            MediaStore.Audio.Media.ALBUM,   //6
    };

      public static final String PLAY = "com.example.mp3playerapplication.PLAY",
        PAUSE= "com.example.mp3playerapplication.PAUSE",
        RESUME = "com.example.mp3playerapplication.RESUME",
        NEXT = "com.example.mp3playerapplication.NEXT",
        BACK = "com.example.mp3playerapplication.BACK",
        STOP = "com.example.mp3playerapplication.STOP";



   public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);

    }

}
