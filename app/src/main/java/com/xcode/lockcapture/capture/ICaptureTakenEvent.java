package com.xcode.lockcapture.capture;

import android.content.Context;

/**
 * Created by Administrator on 2015/4/8.
 */
public interface ICaptureTakenEvent {
    Context GetContext();
    void TakenPicture();
}
