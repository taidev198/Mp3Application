package com.example.mp3playerapplication.data.source.local;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.example.mp3playerapplication.data.source.AudioDataSource;
import com.example.mp3playerapplication.audio.asynctask.LoadingAudioAsyncTask;
import com.example.mp3playerapplication.audio.model.Audio;

import java.util.List;

public class AudioLocalDataSource implements AudioDataSource {

    private static AudioLocalDataSource INSTANCE = null;
    ContentResolver contentResolver;
    Resources resources;

    private AudioLocalDataSource(ContentResolver contentResolver, Resources resources){
        this.contentResolver = contentResolver;
        this.resources = resources;
    }

    public static AudioLocalDataSource getInstance(ContentResolver contentResolver, Resources resources){
        if (INSTANCE == null){
            return new AudioLocalDataSource(contentResolver, resources);
        }
        return INSTANCE;
    }


    @Override
    public void getAudioList(@NonNull final LoadAudioCallback callback) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        switch (msg.what){
                            case LoadingAudioAsyncTask.LOAD_AUDIO_DONE:
                                List<Audio> list = (List<Audio>) msg.obj;
                                callback.onAudioLoaded(list);
                                break;
                            default:
                                callback.onAudioNotAvailable();
                        }
                        return false;
                    }
                });

                LoadingAudioAsyncTask audioAsyncTask =
                        new LoadingAudioAsyncTask(contentResolver, resources, handler);
                audioAsyncTask.execute();
            }
        };

        runnable.run();

    }

    @Override
    public void getAudio(@NonNull String audioId, @NonNull GetAudioCallback callback) {

    }

    @Override
    public void saveAudio(@NonNull Audio audio) {

    }

    @Override
    public void completeAudio(@NonNull Audio audio) {

    }

    @Override
    public void completeAudio(@NonNull String audioId) {

    }

    @Override
    public void activateAudio(@NonNull Audio audio) {

    }

    @Override
    public void activateAudio(@NonNull String audioId) {

    }

    @Override
    public void refreshAudios() {

    }

    @Override
    public void deleteAudio(@NonNull String audioId) {

    }
}
