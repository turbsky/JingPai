package com.xcode.lockcapture;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.xcode.lockcapture.album.LocalImageManager;
import com.xcode.lockcapture.common.IFragment;
import com.xcode.lockcapture.userControl.tab.TabView;
import com.xcode.lockcapture.fragment.AboutMe;
import com.xcode.lockcapture.fragment.AlbumExplore;
import com.xcode.lockcapture.fragment.CaptureStatus;
import com.xcode.lockcapture.fragment.AppIntroduce;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity {
    private String[] mTabText = {"功能", "相册",  "说明书","关于我"};
    private int[] mTabSelect = { R.mipmap.msg_select, R.mipmap.img_select, R.mipmap.setting_select,R.mipmap.about_select};
    private int[] mTabNormal = {R.mipmap.msg_normal, R.mipmap.img_normal, R.mipmap.setting_normal, R.mipmap.about_normal};
    private int mTabItemCount;
    private ViewPager mViewPager;
    private TabView mTabView;
    private Map<Integer, Fragment> mFragmentMap;
    private IFragment mIFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTabItemCount = mTabText.length;
        mFragmentMap = new HashMap(mTabItemCount);
        mViewPager = (ViewPager) findViewById(R.id.main_fragment_container);
        mViewPager.setOffscreenPageLimit(mTabItemCount);
        mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
        mTabView = (TabView) findViewById(R.id.main_tabView);
        mTabView.setViewPager(mViewPager);
        mTabView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIFragment = ((IFragment) mFragmentMap.get(position));
                mIFragment.OnEnter();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AlbumExplore.REQUEST_CODE_VIEW_ALBUM: {
                if (LocalImageManager.GetInstance().RefreshImageList())
                    mIFragment.OnEnter();
            }
            break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    class PageAdapter extends FragmentPagerAdapter implements TabView.OnItemIconTextSelectListener {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = mFragmentMap.get(position);

            if (fragment == null) {
                switch (position) {
                    case 0:
                        fragment = new CaptureStatus();
                        break;
                    case 1:
                        fragment = new AlbumExplore();
                        break;
                    case 2:

                        fragment = new AppIntroduce();
                        break;
                    case 3:
                        fragment = new AboutMe();
                        break;
                }
                mFragmentMap.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int[] onIconSelect(int position) {
            int[] icons = new int[2];
            icons[0] = mTabSelect[position];
            icons[1] = mTabNormal[position];
            return icons;
        }

        @Override
        public String onTextSelect(int position) {
            return mTabText[position];
        }

        @Override
        public int getCount() {
            return mTabItemCount;
        }
    }
}
