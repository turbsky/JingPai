package com.xcode.lockcapture.album;

import android.media.Image;

import com.xcode.lockcapture.common.GlobalConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimmy on 2015/4/13.
 */
public class LocalImageManager {
    private List<ImageEntity> _imgList;

    private static LocalImageManager localImageManager;

    public static LocalImageManager GetInstance() {
        if (localImageManager == null)
            localImageManager = new LocalImageManager();

        return localImageManager;
    }

    public LocalImageManager() {
        _imgList = new ArrayList<>();
        RefreshImageList();
    }

    public void AddImage(String imageUrl) {

        boolean isFounded = false;

        for (ImageEntity entity : _imgList) {
            if (entity.ImageUrl.equals(imageUrl)) {
                isFounded = true;
                break;
            }
        }

        if (isFounded == false)
            _imgList.add(0, new ImageEntity(imageUrl));
    }

    public List<ImageEntity> getImageList() {
        return _imgList;
    }

    public ImageEntity getImage(int pos) {
        return _imgList.get(pos);
    }

    public boolean RefreshImageList() {
        String[] imgs = new File(GlobalConfig.RawImageStoreUrl).list();

        //all delete
        if (imgs == null || imgs.length == 0) {
            _imgList.clear();
            return true;
        }

        if (imgs.length == _imgList.size())
            return false;

        _imgList.clear();
        for (int i = imgs.length - 1; i >= 0; i--) {
            _imgList.add(new ImageEntity(imgs[i]));
        }
        return true;
    }

    public void SetSelected(int pos) {
        ImageEntity entity = _imgList.get(pos);

        if (entity != null)
            entity.IsSelected = !entity.IsSelected;
    }

    public void ClearImageListSelect() {
        for (ImageEntity entity : _imgList) {
            entity.IsSelected = false;
        }
    }

    public List<String> InvertImageListSelect() {
        List<String> selectedImageList = new ArrayList<>();

        for (ImageEntity entity : _imgList) {
            entity.IsSelected = !entity.IsSelected;

            if (entity.IsSelected)
                selectedImageList.add(entity.ImageUrl);
        }
        return selectedImageList;
    }
}
