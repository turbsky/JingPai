package com.xcode.lockcapture.fragment;


import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xcode.lockcapture.R;
import com.xcode.lockcapture.album.ImageAdapter;
import com.xcode.lockcapture.album.ImageEntity;
import com.xcode.lockcapture.album.LocalImageManager;
import com.xcode.lockcapture.common.GlobalConfig;
import com.xcode.lockcapture.common.IFragment;
import com.xcode.lockcapture.common.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumExplore extends Fragment implements IFragment, View.OnClickListener {

    public static final int REQUEST_CODE_VIEW_ALBUM = 0;
    ImageAdapter _imageAdapter;
    ObjectAnimator _colorAnimator;
    Button _btnChangeMode;
    Button _btnInvertSelect;
    Button _btnExport;
    Button _btnDelete;
    List<String> _selectedImageList;

    ProgressBar _progressBar;
    String _tempStr;

    public AlbumExplore() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_explore, container, false);
        final GridView gridview = (GridView) view.findViewById(R.id.album_container);

        gridview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.gridview_image_space);
                final int numCount = gridview.getNumColumns();
                int columnWidth = (gridview.getWidth() - columnSpace * (numCount - 1)) / numCount;
                GlobalConfig.GridviewColumnWidth = columnWidth;

                _imageAdapter = new ImageAdapter(getActivity(), R.layout.image_list_item, LocalImageManager.GetInstance().getImageList());
                gridview.setAdapter(_imageAdapter);

                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (GlobalConfig.IsImageEditMode) {
                            ImageEntity selectedImage = LocalImageManager.GetInstance().getImage(position);

                            if (selectedImage.IsSelected)
                                _selectedImageList.remove(selectedImage.ImageUrl);
                            else
                                _selectedImageList.add(selectedImage.ImageUrl);

                            LocalImageManager.GetInstance().SetSelected(position);
                            OnEnter();
                        } else {
                            String imageUrl = Utils.wrapUrlWithPicassoPrefix(_imageAdapter.getItem(position).ImageUrl);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(imageUrl), "image/*");
                            getActivity().startActivityForResult(intent, REQUEST_CODE_VIEW_ALBUM);
                        }

                    }
                });

                gridview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        _colorAnimator = Utils.GenerateColorAnimator(getActivity(), R.animator.album_title_color_change, view.findViewById(R.id.album_title_container));
        initButton(view);
        switchBtnEnable(false);
        _selectedImageList = new ArrayList<>();
        _progressBar = (ProgressBar) view.findViewById(R.id.album_progress_bar);
        GlobalConfig.ImageExportUrl = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/jingPai/";
        return view;
    }

    void refreshBtnText() {
        int selectedImageCount = _selectedImageList.size();
        String countStr = selectedImageCount == 0 ? "" : String.format(" (%d)", selectedImageCount);
        _btnExport.setText(String.format("导出%s", countStr));
        _btnDelete.setText(String.format("删除%s", countStr));
    }

    void initButton(View view) {
        _btnChangeMode = (Button) view.findViewById(R.id.album_btn_change_mode);
        _btnChangeMode.setOnClickListener(this);

        _btnInvertSelect = (Button) view.findViewById(R.id.album_btn_invert_select);
        _btnInvertSelect.setOnClickListener(this);

        _btnExport = (Button) view.findViewById(R.id.album_btn_export);
        _btnExport.setOnClickListener(this);

        _btnDelete = (Button) view.findViewById(R.id.album_btn_delete);
        _btnDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.album_btn_change_mode:
                onBtnChangeMode();
                break;
            case R.id.album_btn_invert_select:
                onBtnInvertSelect();
                break;
            case R.id.album_btn_export:
                onBtnExport();
                break;
            case R.id.album_btn_delete:
                onBtnDelete();
                break;
        }
    }

    void onBtnInvertSelect() {
        _selectedImageList = LocalImageManager.GetInstance().InvertImageListSelect();
        OnEnter();
    }

    void onBtnChangeMode() {
        GlobalConfig.IsImageEditMode = !GlobalConfig.IsImageEditMode;

        if (GlobalConfig.IsImageEditMode) {
            _colorAnimator.start();
            _btnChangeMode.setText("取消");
            switchBtnEnable(true);
        } else {
            _colorAnimator.reverse();
            LocalImageManager.GetInstance().ClearImageListSelect();
            _selectedImageList.clear();
            _btnChangeMode.setText("编辑");
            switchBtnEnable(false);
        }
        OnEnter();
    }

    void refreshBtnExportText(int currentCount) {
        _btnExport.setText(String.format("导出中%d/%d", currentCount, _selectedImageList.size()));
    }

    void refreshBtnDeleteText(int currentCount) {
        _btnDelete.setText(String.format("删除中%d/%d", currentCount, _selectedImageList.size()));
    }

    void onBtnExport() {

        if (_selectedImageList.size() == 0) {
            Toast.makeText(getActivity(), R.string.warning_image_not_selected, Toast.LENGTH_SHORT).show();
            return;
        }

        new CopyFileTask().execute(_selectedImageList);
    }

    void onBtnDelete() {
        if (_selectedImageList.size() == 0) {
            Toast.makeText(getActivity(), R.string.warning_image_not_selected, Toast.LENGTH_SHORT).show();
            return;
        }

        new DeleteFileTask().execute(_selectedImageList);
    }

    void switchBtnEnable(boolean isEnable) {
        _btnInvertSelect.setEnabled(isEnable);
        _btnExport.setEnabled(isEnable);
        _btnDelete.setEnabled(isEnable);
    }

    @Override
    public void OnEnter() {
        if (_imageAdapter != null) {
            _imageAdapter.notifyDataSetChanged();
            refreshBtnText();
        }
    }

    class DeleteFileTask extends AsyncTask<List<String>, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            _tempStr = _btnDelete.getText().toString();
            switchBtnEnable(false);
            refreshBtnDeleteText(0);
            _progressBar.setProgress(0);
            _progressBar.setMax(_selectedImageList.size());
            _progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(List<String>... params) {
            String exportUrl = GlobalConfig.ImageExportUrl;
            Utils.MkDir(exportUrl);

            for (int i = 0; i < params[0].size(); i++) {
                String fileName = GlobalConfig.RawImageStoreUrl + params[0].get(i);
                File file = new File(fileName);

                if (file.exists())
                    file.delete();

                publishProgress(i + 1);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            _progressBar.setProgress(values[0]);
            refreshBtnDeleteText(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            switchBtnEnable(true);
            _progressBar.setVisibility(View.GONE);
            _btnDelete.setText(_tempStr);
            Toast.makeText(getActivity(), String.format("操作成功,共计删除%d张图片", _selectedImageList.size()), Toast.LENGTH_LONG).show();
            LocalImageManager.GetInstance().RefreshImageList();
            _selectedImageList.clear();
            OnEnter();
            super.onPostExecute(aBoolean);
        }
    }

    class CopyFileTask extends AsyncTask<List<String>, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            _tempStr = _btnExport.getText().toString();
            switchBtnEnable(false);
            refreshBtnExportText(0);
            _progressBar.setProgress(0);
            _progressBar.setMax(_selectedImageList.size());
            _progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(List<String>... params) {
            String exportUrl = GlobalConfig.ImageExportUrl;
            Utils.MkDir(exportUrl);

            for (int i = 0; i < params[0].size(); i++) {
                try {
                    String fileName = params[0].get(i);
                    Utils.CopyFile(GlobalConfig.RawImageStoreUrl + fileName, exportUrl + fileName);
                    publishProgress(i + 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            _progressBar.setProgress(values[0]);
            refreshBtnExportText(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            switchBtnEnable(true);
            _progressBar.setVisibility(View.GONE);
            _btnExport.setText(_tempStr);
            Toast.makeText(getActivity(), String.format("图片导出成功！共计%d张。前往手机图片文件夹-JingPai目录查看成果", _selectedImageList.size()), Toast.LENGTH_LONG).show();
            super.onPostExecute(aBoolean);
        }
    }
}
