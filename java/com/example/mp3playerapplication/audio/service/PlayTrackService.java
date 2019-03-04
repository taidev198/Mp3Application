package com.example.mp3playerapplication.audio.service;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.media.MediaSessionManager;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.MediaController;
import android.widget.Toast;

import com.example.mp3playerapplication.R;
import com.example.mp3playerapplication.audio.adapter.AudioListAdapter;
import com.example.mp3playerapplication.audio.model.Audio;
import com.example.mp3playerapplication.audio.view.PlayerActivity;
import com.example.mp3playerapplication.utils.AudioUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**https://stackoverflow.com/questions/45194576/android-create-notification-w-media-controls-on-service-start*/
public class PlayTrackService extends Service implements
        AudioListAdapter.receivedFileCallback,
         MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
                                MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener{
    private static final String CHANNEL_ID = "1";

    private static final  int NOTIFICATION_ID = 501;
    MediaPlayer mediaPlayer  = new MediaPlayer();
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaController mediaController ;
    private MediaControllerCompat.TransportControls transportControls;

    //Used to pause/resume MediaPlayer
    private int resumePosition;

    //AudioFocus
    private AudioManager audioManager;

    // Binder given to clients
    //private final IBinder iBinder = new LocalBinder();

    //List of available Audio files
    private List<Audio> audioList;
    private int audioIndex = -1;
    private boolean isPlaying= true;
    private int currentID =-1;
    private Audio activeAudio; //an object on the currently playing audio

    String filepath;
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        activeAudio = new Audio();
        activeAudio.setFilepath(intent.getStringExtra("path_file"));
        if (mediaSession != null){
            String action = intent.getAction();
            switch (action){
                case AudioUtils.PAUSE:  buildNotification(AudioUtils.PAUSE);
                                        pauseMedia();
                    System.out.println("pause");
                                        break;
                case AudioUtils.PLAY: buildNotification(AudioUtils.PLAY);
                                      resumeMedia();
                                      break;
                case AudioUtils.BACK: buildNotification(AudioUtils.PLAY);
                                        skipToPrevious();
                                        break;
                 default:
                        skipToNext();
                        buildNotification(AudioUtils.PLAY);
            }
        }
       else{
            audioIndex = Integer.parseInt(intent.getStringExtra("CURRENT_ID"));
            audioList = (List<Audio>) intent.getSerializableExtra("AUDIO_LIST");
           try {
               initMediaSession();
               initMediaPlayer();
           } catch (RemoteException e) {
               e.printStackTrace();
           }
            buildNotification(AudioUtils.PLAY);
       }

        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {

        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }

        //Disable the PhoneStateListener

        removeNotification();
    }


    private void initMediaPlayer() {
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();//new MediaPlayer instance

        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(activeAudio.getFilepath());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        if (isPlaying)
        mediaPlayer.prepareAsync();

    }

    @SuppressLint("ServiceCast")
    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
//        }
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        mediaSession.setMediaButtonReceiver(null);

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                buildNotification(AudioUtils.PLAY);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
                buildNotification(AudioUtils.PAUSE);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();

                skipToNext();
               // updateMetaData();
                buildNotification(AudioUtils.NEXT);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();

                skipToPrevious();
               // updateMetaData();
                buildNotification("PLAYING");
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }




    private void handleIntent( Intent intent ) {
        if( intent == null || intent.getAction() == null )
            return;

        String action = intent.getAction();

        if( action.equalsIgnoreCase( AudioUtils.PLAY ) ) {
            transportControls.play();
        } else if( action.equalsIgnoreCase( AudioUtils.PAUSE ) ) {
            transportControls.pause();
//        } else if( action.equalsIgnoreCase( ACTION_FAST_FORWARD ) ) {
//            transportControls.fastForward();
//        } else if( action.equalsIgnoreCase( ACTION_REWIND ) ) {
//            transportControls.rewind();
//        } else if( action.equalsIgnoreCase( ACTION_PREVIOUS ) ) {
//            transportControls.skipToPrevious();
        } else if( action.equalsIgnoreCase( AudioUtils.NEXT ) ) {
            transportControls.skipToNext();
        } else if( action.equalsIgnoreCase( AudioUtils.STOP ) ) {
            transportControls.stop();
        }
    }
    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }


    }

    private void skipToNext() {

        if (audioIndex == audioList.size() - 1) {
            //if last in playlist
            audioIndex = 0;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get next in playlist
            activeAudio = audioList.get(++audioIndex);
        }

        //Update stored index

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }

    private void skipToPrevious() {
        if (audioIndex == 0) {
            //if first in playlist
            //set index to the last of audioList
            audioIndex = audioList.size() - 1;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get previous in playlist
            activeAudio = audioList.get(--audioIndex);
        }
        //Update stored index
        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }


    private void buildNotification(String action){
        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if (action.equals(AudioUtils.PLAY)) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (action.equals(AudioUtils.PAUSE)) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.image5); //replace with your own image

        NotificationChannel channel = null;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID,
                    "Primary channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setLightColor(Color.GREEN);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.enableVibration(true);
            channel.enableLights(true);
            manager.createNotificationChannel(channel);
        }

        Intent resultIntent = new Intent(this, PlayerActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(PlayerActivity.class);
        taskStackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = taskStackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Create a new Notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        // Hide the timestamp
                        .setShowWhen(false)
                        // Set the Notification style
                        .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                                // Attach our MediaSession token
                                .setMediaSession(mediaSession.getSessionToken())
                                // Show our playback controls in the compat view
                                .setShowActionsInCompactView(0, 1, 2))
                        // Set the Notification color
                        .setColor(getResources().getColor(R.color.colorAccent))
                        // Set the large and small icons
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(android.R.drawable.stat_sys_headset)
                        // Set Notification content information
                        .setContentText(activeAudio.getArtist())
                        .setContentTitle(activeAudio.getAlbum())
                        .setContentInfo(activeAudio.getTitle())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        // Add playback actions
                        .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                        .addAction(notificationAction, "pause", play_pauseAction)
                        .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2));
        notificationBuilder.setContentIntent(resultPendingIntent);
        manager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, PlayTrackService.class);
        playbackAction.putExtra("TITLE",activeAudio.getTitle());
        playbackAction.putExtra("ARTISTS",activeAudio.getArtist());
        playbackAction.putExtra("DURATION",activeAudio.getDuration());
        playbackAction.putExtra("path_file", activeAudio.getFilepath());
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(AudioUtils.PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, PendingIntent.FLAG_UPDATE_CURRENT);
            case 1:
                // Pause
                playbackAction.setAction(AudioUtils.PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, PendingIntent.FLAG_UPDATE_CURRENT);
            case 2:
                // Next track
                playbackAction.setAction(AudioUtils.NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, PendingIntent.FLAG_UPDATE_CURRENT);
            case 3:
                // Previous track
                playbackAction.setAction(AudioUtils.BACK);
                return PendingIntent.getService(this, actionNumber, playbackAction, PendingIntent.FLAG_UPDATE_CURRENT);
            default:
                break;
        }
        return null;
    }


    @Override
    public void onAudioFocusChange(int focusChange) {

        //Invoked when the audio focus of the system is updated.
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Invoked when playback of a media source has completed.
        stopMedia();
        // Remove the notification
        removeNotification();
        //stop the service
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
    }


    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onReceived(List<Audio> audio) {
            audioList = new ArrayList<>(audio);
    }

    public  class MediaReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AudioUtils.PAUSE)){
                Toast.makeText(context, "Pause", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
