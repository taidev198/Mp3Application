package com.example.mp3playerapplication.audio;

import com.example.mp3playerapplication.BasePresenter;
import com.example.mp3playerapplication.BaseView;
import com.example.mp3playerapplication.audio.model.Audio;

import java.util.List;

public interface AudioContract {
    interface View extends BaseView<Presenter>{

        void showProcessing();
        void hideProcessing();
        void showListSongs();
        void showNotification();
        void showPlayTrack();

        void showListAudio(List<Audio> listAudio);
        void showNoDataLoaded();
        void showLoadingAudioError();


    }

    interface Presenter extends BasePresenter{
        void load();
        void loadAudioList();
        void filter();
        void play();
        void open();
    }
}
