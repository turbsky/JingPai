package com.xcode.lockcapture.album;

/**
 * Created by kimmy on 2015/4/21.
 */
public class ImageEntity {
    public String ImageUrl;
    public boolean IsSelected;

    public ImageEntity(String imageUrl, boolean isSelected) {
        ImageUrl = imageUrl;
        IsSelected = isSelected;
    }

    public ImageEntity(String imageUrl) {
        ImageUrl = imageUrl;
        IsSelected = false;
    }

}
