package com.example.mp3playerapplication.audio.view;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.mp3playerapplication.R;
import com.example.mp3playerapplication.audio.AudioContract;
import com.example.mp3playerapplication.audio.adapter.AudioListAdapter;
import com.example.mp3playerapplication.audio.presenter.AudioPresenter;
import com.example.mp3playerapplication.audio.model.Audio;
import com.example.mp3playerapplication.data.source.AudioRepository;
import com.example.mp3playerapplication.data.source.local.AudioLocalDataSource;
import com.example.mp3playerapplication.utils.PermissionInfo;

import java.util.List;

public class PlayerActivity extends AppCompatActivity implements AudioContract.View {

    RecyclerView playerListView;
    AudioPresenter audioPresenter;
    AudioListAdapter playerListViewAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();

    }

    @Override
    public void showProcessing() {

    }

    @Override
    public void hideProcessing() {

    }

    @Override
    public void showListSongs() {


    }

    @Override
    public void showNotification() {

    }

    @Override
    public void showPlayTrack() {

    }

    @Override
    public void showListAudio(List<Audio> listAudio) {
        playerListView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        playerListView.setHasFixedSize(true);
        playerListView.setLayoutManager(layoutManager);
        playerListViewAdapter = new AudioListAdapter(this);
        playerListView.setAdapter(playerListViewAdapter);

        playerListViewAdapter.setAudioList(listAudio);
        playerListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoDataLoaded() {

    }

    @Override
    public void showLoadingAudioError() {

    }

    @Override
    public void setPresenter(AudioContract.Presenter presenter) {

    }



    private void requestPermission(){
        PermissionInfo.PERMISSION_COUNT = 0;
        for (int i = 0; i < PermissionInfo.permissions.length; i++) {
            if(ContextCompat.checkSelfPermission(this, PermissionInfo.permissions[i])
            != PackageManager.PERMISSION_GRANTED){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PermissionInfo.permissions,
                            PermissionInfo.PERMISSION_REQUESCODE[i]);
                }
            } else{
                PermissionInfo.PERMISSION_COUNT++;
            }
        }
        loadAudio();
        PermissionInfo.PERMISSION_COUNT = 0;
    }

    private void loadAudio(){
        AudioLocalDataSource audioLocalDataSource =  AudioLocalDataSource.getInstance(getContentResolver(), getResources());
        AudioRepository audioRepository = AudioRepository.getInstance(audioLocalDataSource);
        audioPresenter = new AudioPresenter(audioRepository, this);
        audioPresenter.loadAudioList();
    }


}
