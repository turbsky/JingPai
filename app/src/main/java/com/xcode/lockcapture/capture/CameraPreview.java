package com.xcode.lockcapture.capture;

import android.content.Context;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Administrator on 2015/4/8.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Camera _camera;
    private SurfaceHolder _holder;
    private Surface _surface;

    public Surface getSurface() {
        return _surface;
    }

    public CameraPreview(Context context, Camera camera) {
        super(context);
        _camera = camera;
        _holder = getHolder();
        _holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initCameraPreview();
        _surface = holder.getSurface();
    }

    void initCameraPreview() {
        try {
            _camera.setPreviewDisplay(_holder);
            _camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

}
