package com.example.mp3playerapplication.data.source;

import android.support.annotation.NonNull;

import com.example.mp3playerapplication.audio.model.Audio;

import java.util.List;

public interface AudioDataSource {

    interface LoadAudioCallback{
        void onAudioLoaded(List<Audio> audio);

        void onAudioNotAvailable();
    }

    interface GetAudioCallback{
        void onTaskLoaded(Audio audio);

        void onDataNotAvailable();
    }
    void getAudioList(@NonNull LoadAudioCallback callback);

    void getAudio(@NonNull String audioId, @NonNull GetAudioCallback callback);

    void saveAudio(@NonNull Audio audio);

    void completeAudio(@NonNull Audio audio);

    void completeAudio(@NonNull String audioId);

    void activateAudio(@NonNull Audio audio);

    void activateAudio(@NonNull String audioId);

    void refreshAudios();

    void deleteAudio(@NonNull String audioId);

}
