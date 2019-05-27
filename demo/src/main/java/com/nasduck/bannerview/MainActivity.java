package com.nasduck.bannerview;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.nasduck.lib.BannerAdapter;
import com.nasduck.lib.BannerView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<String> mUrlStringList;
    private List<Drawable> mDrawableList;

    private Button mStopBtn;
    private BannerView mBannerViewNet;
    private BannerView mBannerViewLocal;
    private BannerAdapter mNetAdapter;
    private BannerAdapter mLocalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        mStopBtn = (Button) findViewById(R.id.btn_stop);
        mStopBtn.setOnClickListener(this);

        mBannerViewNet = findViewById(R.id.banner_view_net);
        mBannerViewLocal = findViewById(R.id.banner_view_local);

        mNetAdapter = new BannerAdapter(mUrlStringList.toArray(new String[mUrlStringList.size()]));
        mBannerViewNet.setAdapter(mNetAdapter, mUrlStringList.size());
        mBannerViewNet.hasIndicator(true);
        mBannerViewNet.setIntervalTime(3000);

        mLocalAdapter = new BannerAdapter(mDrawableList.toArray(new Drawable[mDrawableList.size()]));
        mBannerViewLocal.setAdapter(mLocalAdapter, mDrawableList.size());
        mBannerViewLocal.hasIndicator(true);
        mBannerViewLocal.setIntervalTime(3000);

        getLifecycle().addObserver(mBannerViewNet);
        getLifecycle().addObserver(mBannerViewLocal);
    }

    private void initData() {

        mUrlStringList = new ArrayList<>();
        mDrawableList = new ArrayList<>();

        mUrlStringList.add("https://cdn.pixabay.com/photo/2019/04/28/21/12/cosmos-4164414_1280.jpg");
        mUrlStringList.add("https://cdn.pixabay.com/photo/2018/11/19/03/27/nature-3824496_1280.jpg");
        mUrlStringList.add("https://cdn.pixabay.com/photo/2019/04/27/00/46/strawberries-4159028_1280.jpg");
        mUrlStringList.add("https://cdn.pixabay.com/photo/2017/02/17/23/15/duiker-island-2076042_1280.jpg");
        mUrlStringList.add("https://cdn.pixabay.com/photo/2019/04/22/04/32/blue-4145659_1280.jpg");
        mUrlStringList.add("https://cdn.pixabay.com/photo/2019/04/27/13/51/spaceman-4160023_1280.jpg");

        mDrawableList.add(ContextCompat.getDrawable(this, R.drawable.d1));
        mDrawableList.add(ContextCompat.getDrawable(this, R.drawable.d2));
        mDrawableList.add(ContextCompat.getDrawable(this, R.drawable.d3));
        mDrawableList.add(ContextCompat.getDrawable(this, R.drawable.d4));
        mDrawableList.add(ContextCompat.getDrawable(this, R.drawable.d5));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_stop) {
            if (mBannerViewNet.isPlaying()) {
                mBannerViewNet.stopPlay();
                mBannerViewLocal.stopPlay();
                mStopBtn.setText(getResources().getString(R.string.start));
            } else {
                mBannerViewNet.play();
                mBannerViewLocal.play();
                mStopBtn.setText(getResources().getString(R.string.stop));
            }
        }

    }
}
