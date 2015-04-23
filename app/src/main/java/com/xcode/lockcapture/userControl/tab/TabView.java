package com.xcode.lockcapture.userControl.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xcode.lockcapture.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/10.
 */
public class TabView extends LinearLayout implements View.OnClickListener {


    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private PagerAdapter mPagerAdapter;
    private int mChildSize;
    private List<TabItem> mTabItems;
    private OnItemIconTextSelectListener mListener;

    private int mTextSize = 15;
    private int mTextColorSelect = 0xff45c01a;
    private int mTextColorNormal = 0xff777777;
    private int mPadding = 10;

    public TabView(Context context) {
        this(context, null);
    }

    public TabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.TabView);
        int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            switch (typedArray.getIndex(i)) {
                case R.styleable.TabView_text_size:
                    mTextSize = (int) typedArray.getDimension(i, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            mTextSize, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.TabView_text_normal_color:
                    mTextColorNormal = typedArray.getColor(i, mTextColorNormal);
                    break;
                case R.styleable.TabView_text_select_color:
                    mTextColorSelect = typedArray.getColor(i, mTextColorSelect);
                    break;
                case R.styleable.TabView_item_padding:
                    mPadding = (int) typedArray.getDimension(i, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            mPadding, getResources().getDisplayMetrics()));
                    break;
            }
        }
        typedArray.recycle();
        mTabItems = new ArrayList<>();
    }

    public void setViewPager(final ViewPager mViewPager) {
        if (mViewPager == null) {
            return;
        }
        this.mViewPager = mViewPager;
        this.mPagerAdapter = mViewPager.getAdapter();
        this.mChildSize = this.mPagerAdapter.getCount();
        this.mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                View leftView;
                View rightView;

                if (positionOffset > 0) {
                    leftView = mViewPager.getChildAt(position);
                    rightView = mViewPager.getChildAt(position + 1);
                    leftView.setAlpha(1 - positionOffset);
                    rightView.setAlpha(positionOffset);
                    mTabItems.get(position).setTabAlpha(1 - positionOffset);
                    mTabItems.get(position + 1).setTabAlpha(positionOffset);
                } else {
                    mViewPager.getChildAt(position).setAlpha(1);
                    mTabItems.get(position).setTabAlpha(1 - positionOffset);
                }
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
        if (mPagerAdapter instanceof OnItemIconTextSelectListener) {
            mListener = (OnItemIconTextSelectListener) mPagerAdapter;
        }else {
            throw new RuntimeException("�������pageAdapterʵ��OnItemIconTextSelectListener�ӿ�");
        }
        initItem();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener mOnPageChangeListener) {
        this.mOnPageChangeListener = mOnPageChangeListener;
    }

    private void initItem() {
        for (int i = 0; i < mChildSize; i++) {
            TabItem tabItem = new TabItem(getContext());
            LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tabItem.setPadding(mPadding, mPadding, mPadding, mPadding);
            tabItem.setIconText(mListener.onIconSelect(i), mListener.onTextSelect(i));
            tabItem.setTextSize(mTextSize);
            tabItem.setTextColorNormal(mTextColorNormal);
            tabItem.setTextColorSelect(mTextColorSelect);
            tabItem.setLayoutParams(params);
            tabItem.setTag(i);
            tabItem.setOnClickListener(this);
            mTabItems.add(tabItem);
            addView(tabItem);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        if (mViewPager.getCurrentItem() == position) {
            return;
        }
        for (TabItem tabItem : mTabItems) {
            tabItem.setTabAlpha(0);
        }
        mTabItems.get(position).setTabAlpha(1);
        mViewPager.setCurrentItem(position, false);
    }

    public interface OnItemIconTextSelectListener {

        int[] onIconSelect(int position);

        String onTextSelect(int position);
    }
}
