package com.xcode.lockcapture.album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xcode.lockcapture.R;
import com.xcode.lockcapture.common.GlobalConfig;
import com.xcode.lockcapture.common.Utils;

import java.util.List;

/**
 * Created by kimmy on 2015/4/12.
 */
public class ImageAdapter extends ArrayAdapter<ImageEntity> {

    private List<ImageEntity> _imgList;
    private Context _context;
    private int _resourceID;
    private int _itemSize;
    private GridView.LayoutParams _itemLayoutParams;
    private int INDICATOR_SELECTED = R.drawable.indicator_selected;
    private int INDICATOR_NOT_SELECTED = R.drawable.indicator_unselected;

    public ImageAdapter(Context context, int resource, List<ImageEntity> objects) {
        super(context, resource, objects);
        _context = context;
        _resourceID = resource;
        _imgList = objects;
        _itemSize = GlobalConfig.GridviewColumnWidth;
        _itemLayoutParams = new GridView.LayoutParams(_itemSize, _itemSize);
    }


    @Override
    public int getCount() {
        return _imgList.size();
    }

    @Override
    public ImageEntity getItem(int position) {
        return _imgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(_context).inflate(_resourceID, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageList_Item);
            viewHolder.imageIndicator = (ImageView) view.findViewById(R.id.imageList_indicator);
            view.setTag(viewHolder);
            view.setLayoutParams(_itemLayoutParams);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.InitView(_imgList.get(position));

        return view;
    }

    class ViewHolder {
        public ImageView imageView;
        public ImageView imageIndicator;

        public void InitView(ImageEntity entity) {
            Picasso.with(_context).load(Utils.wrapUrlWithPicassoPrefix(entity.ImageUrl))
                    .placeholder(R.drawable.image_place_holder)
                    .resize(_itemSize, _itemSize).centerCrop()
                    .into(imageView);

            if (GlobalConfig.IsImageEditMode == false) {
                imageIndicator.setVisibility(View.GONE);
                return;
            }

            imageIndicator.setVisibility(View.VISIBLE);
            int indicatorSource = entity.IsSelected ? INDICATOR_SELECTED : INDICATOR_NOT_SELECTED;
            Picasso.with(_context).load(indicatorSource).into(imageIndicator);
        }
    }
}
