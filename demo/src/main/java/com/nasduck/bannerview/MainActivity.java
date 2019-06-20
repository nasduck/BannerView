package com.nasduck.bannerview;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nasduck.lib.BannerAdapter;
import com.nasduck.lib.BannerView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String[] mUrlStrings;
    private Drawable[] mDrawables;

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

        mNetAdapter = new BannerAdapter(mUrlStrings);
        mBannerViewNet.setAdapter(mNetAdapter, mUrlStrings.length);
        mBannerViewNet.hasIndicator(true);
        mBannerViewNet.setIntervalTime(3000);
        mBannerViewNet.setPlaceHolder(ContextCompat.getDrawable(this, R.drawable.ic_phaceholder));

        mLocalAdapter = new BannerAdapter(mDrawables);
        mBannerViewLocal.setAdapter(mLocalAdapter, mDrawables.length);
        mBannerViewLocal.hasIndicator(true);
        mBannerViewLocal.setIntervalTime(3000);

        mBannerViewLocal.setClickListener(position -> Toast.makeText(MainActivity.this,
                " clicked at " + position, Toast.LENGTH_SHORT).show());


        getLifecycle().addObserver(mBannerViewNet);
        getLifecycle().addObserver(mBannerViewLocal);
    }

    private void initData() {

        mUrlStrings = new String[] {
                "https://cdn.pixabay.com/photo/2019/04/28/21/12/cosmos-4164414_1280.jpg",
                "https://cdn.pixabay.com/photo/2018/11/19/03/27/nature-3824496_1280.jpg",
                "https://cdn.pixabay.com/photo/2019/04/27/00/46/strawberries-4159028_1280.jpg",
                "https://cdn.pixabay.com/photo/2017/02/17/23/15/duiker-island-2076042_1280.jpg",
                "https://cdn.pixabay.com/photo/2019/04/22/04/32/blue-4145659_1280.jpg",
                "https://cdn.pixabay.com/photo/2019/04/27/13/51/spaceman-4160023_1280.jpg"
        };

        mDrawables= new Drawable[] {
                ContextCompat.getDrawable(this, R.drawable.d1),
                ContextCompat.getDrawable(this, R.drawable.d2),
                ContextCompat.getDrawable(this, R.drawable.d3),
                ContextCompat.getDrawable(this, R.drawable.d4),
                ContextCompat.getDrawable(this, R.drawable.d5)
        };
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_stop) {
            if (mBannerViewNet.isPlaying()) {
                mBannerViewNet.stop();
                mBannerViewLocal.stop();
                mStopBtn.setText(getResources().getString(R.string.start));
            } else {
                mBannerViewNet.play();
                mBannerViewLocal.play();
                mStopBtn.setText(getResources().getString(R.string.stop));
            }
        }
    }
}
