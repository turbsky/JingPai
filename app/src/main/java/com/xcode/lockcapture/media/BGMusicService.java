package com.xcode.lockcapture.media;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.xcode.lockcapture.R;

/**
 * Created by Administrator on 2015/4/9.
 */
public class BGMusicService extends Service {
    private MediaPlayer _mediaPlayer;


    @Override
    public void onCreate() {
        super.onCreate();
        _mediaPlayer = MediaPlayer.create(this, R.raw.bg_sound);
        _mediaPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        _mediaPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _mediaPlayer.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
