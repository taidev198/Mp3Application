package com.example.mp3playerapplication.audio.presenter;


import android.content.Context;
import android.provider.MediaStore;


import com.example.mp3playerapplication.audio.AudioContract;
import com.example.mp3playerapplication.audio.model.Audio;
import com.example.mp3playerapplication.data.source.AudioDataSource;
import com.example.mp3playerapplication.data.source.AudioRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AudioPresenter implements AudioContract.Presenter {

    AudioContract.View mPlayerView;
    AudioRepository audioRepository;

    public AudioPresenter(AudioRepository audioRepository, AudioContract.View mPlayerView){
        this.mPlayerView = mPlayerView;
        this.mPlayerView.setPresenter(this);
        this.audioRepository = audioRepository;
    }



    @Override
    public void load() {
        this.mPlayerView.showProcessing();
        this.mPlayerView.hideProcessing();
        List<Audio> list = new ArrayList<>();
        this.mPlayerView.showListSongs();
    }

    @Override
    public void loadAudioList() {
        audioRepository.getAudioList(new AudioDataSource.LoadAudioCallback() {
            @Override
            public void onAudioLoaded(List<Audio> audio) {
                showAudioList(audio);
            }

            @Override
            public void onAudioNotAvailable() {

            }
        });
    }

    private void showListFiles(Context context){
        File directory = context.getExternalFilesDir(MediaStore.Audio.Media.DATA);
        if (directory != null)
            System.out.println(Arrays.toString(directory.listFiles())+"HELLO");

    }


//    void getAllSongsFromExternalStorage() {
//        String[] STAR = { "*" };
//        Cursor cursor;
//        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
//
//        cursor = mContext.getContentResolver().query(allsongsuri, STAR,
//                null, null, null);
//
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                do {
//                    String song_name = cursor
//                            .getString(cursor
//                                    .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
//                    int song_id = cursor.getInt(cursor
//                            .getColumnIndex(MediaStore.Audio.Media._ID));
//
//                    String fullpath = cursor.getString(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.DATA));
//
//                    String album_name = cursor.getString(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.ALBUM));
//                    int album_id = cursor.getInt(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
//
//                    String artist_name = cursor.getString(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.ARTIST));
//                    int artist_id = cursor.getInt(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
//                    long duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
//                    System.out.println("sonng name"+duration);
//                } while (cursor.moveToNext());
//
//            }
//            cursor.close();
//        }
//    }


    private void showAudioList(List<Audio> audio){
        mPlayerView.showListAudio(audio);

    }
    @Override
    public void filter() {

    }

    @Override
    public void play() {

    }

    @Override
    public void open() {

    }

    @Override
    public void start() {

    }
}
