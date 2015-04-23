package com.xcode.lockcapture.fragment;


import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.xcode.lockcapture.MainActivity;
import com.xcode.lockcapture.R;
import com.xcode.lockcapture.capture.CameraPreview;
import com.xcode.lockcapture.capture.ICaptureTakenEvent;
import com.xcode.lockcapture.capture.SavePictureTask;
import com.xcode.lockcapture.common.GlobalConfig;
import com.xcode.lockcapture.common.IFragment;
import com.xcode.lockcapture.common.Utils;
import com.xcode.lockcapture.media.BGMusicService;
import com.xcode.lockcapture.observer.VolumeChangedObserver;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class CaptureStatus extends Fragment implements ICaptureTakenEvent, IFragment {
    Camera _camera;
    boolean _isReadToGo = false;

    FrameLayout _previewContainer;
    CameraPreview _cameraPreview;
    MainActivity mMainActivity;
    int _front_camera_index = -1;
    int _back_camera_index = -1;
    int _currentCameraIndex = -1;
    int _cameraPictureRotation;
    ObjectAnimator _colorAnimation;

    TextView _tvStatus;
    Switch _shChangeStatus;
    Switch _shUseFrontCamera;
    FrameLayout _statusContainer;
    VolumeChangedObserver _volumeChanged;

    public CaptureStatus() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_capture_status, container, false);
        _volumeChanged = new VolumeChangedObserver(new Handler(), CaptureStatus.this);
        initControl(view);

        _colorAnimation = Utils.GenerateColorAnimator(getActivity(),R.animator.status_color_change,_statusContainer);
        initCamera();
        GlobalConfig.RawImageStoreUrl = mMainActivity.getExternalFilesDir(null).getAbsolutePath() + "/imgs/";
        readyToGo();
        return view;
    }


    public void GoWithAnimate() {
        _tvStatus.setText(R.string.capture_status_on);
        _shUseFrontCamera.setEnabled(true);
        _colorAnimation.reverse();
        readyToGo();
        Toast.makeText(mMainActivity, R.string.alert_capture_ready, Toast.LENGTH_SHORT).show();
    }

    public void RelaxWithAnimate() {
        _tvStatus.setText(R.string.capture_status_off);
        _shUseFrontCamera.setEnabled(false);
        _colorAnimation.start();
        relax();
        Toast.makeText(mMainActivity, R.string.alert_capture_off, Toast.LENGTH_SHORT).show();
    }

    private void readyToGo() {

        if (_isReadToGo)
            return;

        if (_camera == null) {
            try {
                _camera = Camera.open(_currentCameraIndex);
            } catch (Exception e) {
                Toast.makeText(mMainActivity, R.string.warning_camera_not_available, Toast.LENGTH_SHORT).show();
                _isReadToGo = false;
                return;
            }
        }

        Camera.Parameters parameters = _camera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        if (_currentCameraIndex == _back_camera_index) {
            _cameraPictureRotation = 90;
            //set preview to right orientation
            // _camera.setDisplayOrientation(90);
        } else {
            _cameraPictureRotation = 270;
        }
        parameters.setRotation(_cameraPictureRotation);
        _camera.setParameters(parameters);
        _cameraPreview = new CameraPreview(getActivity(), _camera);
        _previewContainer.addView(_cameraPreview);
        mMainActivity.getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, _volumeChanged);
        mMainActivity.startService(new Intent(getActivity(), BGMusicService.class));

        _isReadToGo = true;
    }

    private void relax() {

        if (_isReadToGo == false)
            return;

        if (_camera != null) {
            _camera.stopPreview();
            _camera.release();
            _camera = null;
        }

        _previewContainer.removeView(_cameraPreview);
        mMainActivity.getContentResolver().unregisterContentObserver(_volumeChanged);
        mMainActivity.stopService(new Intent(mMainActivity, BGMusicService.class));
        _isReadToGo = false;
    }

    @Override
    public void onDestroy() {
        relax();
        super.onDestroy();
    }

    @Override
    public Context GetContext() {
        return mMainActivity.getApplicationContext();
    }

    @Override
    public void TakenPicture() {
        if (_isReadToGo)
            _camera.takePicture(null, null, pictureCallback);
    }

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            new SavePictureTask().execute(data);
        }
    };


    void initCamera() {
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
                _back_camera_index = i;
            else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                _front_camera_index = i;
        }
        _currentCameraIndex = _back_camera_index;
    }

    void initControl(View view) {
        _previewContainer = (FrameLayout) view.findViewById(R.id.flCameraContainer);
        _shChangeStatus = (Switch) view.findViewById(R.id.status_change_state);
        _statusContainer = (FrameLayout) view.findViewById(R.id.status_container);
        _tvStatus = (TextView) view.findViewById(R.id.status_capture_status);
        _shUseFrontCamera = (Switch) view.findViewById(R.id.status_change_camera);

        _shChangeStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    GoWithAnimate();
                else
                    RelaxWithAnimate();
            }
        });

        _shUseFrontCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _currentCameraIndex = isChecked ? _front_camera_index : _back_camera_index;
                relax();
                readyToGo();
            }
        });
    }

    @Override
    public void OnEnter() {

    }
}
