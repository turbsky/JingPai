package com.xcode.lockcapture.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xcode.lockcapture.R;
import com.xcode.lockcapture.common.IFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class AppIntroduce extends Fragment implements IFragment {


    public AppIntroduce() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_introduce, container, false);
    }


    @Override
    public void OnEnter() {

    }
}
