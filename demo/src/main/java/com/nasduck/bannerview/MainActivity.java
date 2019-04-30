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

    private boolean mNeedPlayNet;
    private boolean mNeedPlayLocal;

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
        mBannerViewNet.hasIndicator(false);

        mLocalAdapter = new BannerAdapter(mDrawableList.toArray(new Drawable[mDrawableList.size()]));
        mBannerViewLocal.setAdapter(mLocalAdapter, mDrawableList.size());
        mBannerViewLocal.hasIndicator(true);
        mBannerViewLocal.setIntervalTime(4000);
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
        switch (v.getId()) {
            case R.id.btn_stop:
                if (mBannerViewNet.isAutoPlay()) {
                    mBannerViewNet.stopPlay();
                    mBannerViewLocal.stopPlay();
                    mStopBtn.setText(getResources().getString(R.string.start));
                } else {
                    mBannerViewNet.startPlay();
                    mBannerViewLocal.startPlay();
                    mStopBtn.setText(getResources().getString(R.string.stop));
                }
                break;
            default:
                break;
        }

    }


    /**
     * 视图不可见时，停止自动轮播
     * 并保存是否自动轮播状态，以便恢复
     * */
    @Override
    protected void onStop() {
        super.onStop();
        mNeedPlayNet = mBannerViewNet.isAutoPlay();
        if (mNeedPlayNet) {
            mBannerViewNet.stopPlay();
        }
        mNeedPlayLocal = mBannerViewLocal.isAutoPlay();
        if (mNeedPlayLocal) {
            mBannerViewLocal.stopPlay();
        }
    }

    /**
     * 恢复轮播状态
     * */
    @Override
    protected void onRestart() {
        super.onRestart();

        if (mNeedPlayNet) {
            mBannerViewNet.startPlay();
        }

        if (mNeedPlayLocal) {
            mBannerViewLocal.startPlay();
        }
    }
}
