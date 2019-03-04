package com.example.mp3playerapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.mp3playerapplication.R;

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "com.example.mp3playerapplication.action.main";
        public static String INIT_ACTION = "com.example.mp3playerapplication.action.init";
        public static String PREV_ACTION = "com.example.mp3playerapplication.action.prev";
        public static String PLAY_ACTION = "com.example.mp3playerapplication.action.play";
        public static String NEXT_ACTION = "com.example.mp3playerapplication.action.next";
        public static String STARTFOREGROUND_ACTION = "com.example.mp3playerapplication.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.example.mp3playerapplication.action.stopforeground";

    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_launcher_foreground, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }

}