package com.xcode.lockcapture.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import com.xcode.lockcapture.capture.ICaptureTakenEvent;

import java.util.Date;

/**
 * Created by Administrator on 2015/4/8.
 */
public class VolumeChangedObserver extends ContentObserver {

    private AudioManager _audioManager;
    private int _cachedVolume;
    private int _maxVolume;
    private static final int VOLUME_MIN_VALUE = 0;
    private ICaptureTakenEvent _iCaptureTakenEvent;
    public static final int AUDIO_TYPE = AudioManager.STREAM_MUSIC;


    public VolumeChangedObserver(Handler handler, ICaptureTakenEvent captureTakenEvent) {
        super(handler);
        _iCaptureTakenEvent = captureTakenEvent;
        _audioManager = (AudioManager) captureTakenEvent.GetContext().getSystemService(Context.AUDIO_SERVICE);
        _maxVolume = _audioManager.getStreamMaxVolume(AUDIO_TYPE);
        _cachedVolume = _audioManager.getStreamVolume(AUDIO_TYPE);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        int currentVolume = _audioManager.getStreamVolume(AUDIO_TYPE);

        if (_cachedVolume == currentVolume)
            return;

        _iCaptureTakenEvent.TakenPicture();

        if (currentVolume == VOLUME_MIN_VALUE || currentVolume == _maxVolume)
            currentVolume = _cachedVolume;

        _cachedVolume = currentVolume;
        _audioManager.setStreamVolume(AUDIO_TYPE, currentVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }


}
