package com.example.mp3playerapplication.audio.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mp3playerapplication.R;
import com.example.mp3playerapplication.audio.model.Audio;
import com.example.mp3playerapplication.audio.service.PlayTrackService;
import com.example.mp3playerapplication.utils.AudioUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.MyHolder> {
    List<Audio> mAudio;
    final boolean[] isRunning = {false};
    Context mContext;
    RecyclerView recyclerView;
    public interface receivedFileCallback{
        void onReceived(List<Audio> audio);
    }

    receivedFileCallback mCallback;

    public AudioListAdapter(Activity context){
        mContext = context;
        recyclerView = ((Activity) mContext).findViewById(R.id.recycler_view);
    }
    public void setAudioList(List<Audio> audio){
        if (mAudio != null){
            mAudio.clear();
        }
        mAudio = new ArrayList<>(audio);
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.card_view,viewGroup, false);

        return new MyHolder(v);
    }

    /**This method is called for each items on recycler view*/
    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, final int i) {
        final Audio audio = mAudio.get(i);
        final Context mContext = myHolder.textView.getContext();

        myHolder.textView.setText(audio.getTitle());
        final Intent intent = new Intent(mContext, PlayTrackService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        myHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback = new PlayTrackService();
                mCallback.onReceived(mAudio);
                if (!isRunning[0]){
                    intent.putExtra("CURRENT_ID", Integer.toString(i));
                    intent.putExtra("AUDIO_LIST", (Serializable) mAudio);
                    intent.putExtra("TITLE",audio.getTitle());
                    intent.putExtra("ARTISTS",audio.getArtist());
                    intent.putExtra("DURATION",audio.getDuration());
                    intent.putExtra("path_file", audio.getFilepath());
                    intent.setAction(AudioUtils.PLAY);
                    System.out.println(audio.getFilepath());
                    mContext.startService(intent);
                    isRunning[0] = true;
                    Toast.makeText(myHolder.textView.getContext(), audio.getTitle() + " " +i, Toast.LENGTH_SHORT).show();
                }else {
                    mContext.stopService(intent);
                    Intent i1 = new Intent(mContext, PlayTrackService.class);
                    i1.putExtra("path_file", audio.getFilepath());
                    mContext.startService(i1);
                    isRunning[0] = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAudio.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{
        TextView textView;
        MyHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.content);

        }
    }


}
