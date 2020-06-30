package com.nasduck.bannerview;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nasduck.bannerview.loader.GlideImageLoader;
import com.nasduck.bannerview.loader.ResourceImageLoader;
import com.nasduck.lib.BannerScaleType;
import com.nasduck.lib.BannerView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<String> mUrlStrings;
    private List<Drawable> mDrawables;

    private Button mStopBtn;
    private BannerView mBannerViewGlide;
    private BannerView mBannerViewResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        mStopBtn = (Button) findViewById(R.id.btn_stop);
        mStopBtn.setOnClickListener(this);

        mBannerViewGlide = findViewById(R.id.banner_view_glide);
        mBannerViewResource = findViewById(R.id.banner_view_resource);
        getLifecycle().addObserver(mBannerViewGlide);
        getLifecycle().addObserver(mBannerViewResource);

        mBannerViewGlide.setImageLoader(new GlideImageLoader())
                .hasIndicator(true)
                .setImageUrls(mUrlStrings)
                .setScaleType(BannerScaleType.CENTER_CROP)
                .setLoop(false)
                .init();

        mBannerViewResource.setImageLoader(new ResourceImageLoader())
                .setImageUrls(mDrawables)
                .setAutoPlay(true)
                .hasIndicator(true)
                .setScaleType(BannerScaleType.CENTER_CROP)
                .setIntervalTime(5000)
                .init();

        mBannerViewResource.setClickListener(position -> Toast.makeText(MainActivity.this,
                " clicked at " + position, Toast.LENGTH_SHORT).show());
        mBannerViewResource.setScrolledListener( position -> Toast.makeText(MainActivity.this, "current position: " + position, Toast.LENGTH_SHORT).show());

    }

    private void initData() {

        mUrlStrings = new ArrayList<>();
        mUrlStrings.add("https://cdn.pixabay.com/photo/2019/04/28/21/12/cosmos-4164414_1280.jpg");
        mUrlStrings.add("https://cdn.pixabay.com/photo/2018/11/19/03/27/nature-3824496_1280.jpg");
        mUrlStrings.add("https://cdn.pixabay.com/photo/2019/04/27/00/46/strawberries-4159028_1280.jpg");
        mUrlStrings.add("https://cdn.pixabay.com/photo/2017/02/17/23/15/duiker-island-2076042_1280.jpg");
        mUrlStrings.add("https://cdn.pixabay.com/photo/2019/04/22/04/32/blue-4145659_1280.jpg");
        mUrlStrings.add("https://cdn.pixabay.com/photo/2019/04/27/13/51/spaceman-4160023_1280.jpg");
        mUrlStrings.add("https://api.hzshzj.com/attachment/images/8/2019/01/TnqjHFFg69EjgGV8e0gG09QYtJhU69.png");
        mUrlStrings.add("https://api.hzshzj.com/attachment/images/8/2019/01/xdEBNGZlQ24W2BDAdZzzliP41T39Ne.png");
        mUrlStrings.add("https://api.hzshzj.com/attachment/images/8/2019/01/b6z5z7xvpp5KWF6aPk56Lpxg6yf6rw.png");

        mDrawables = new ArrayList<>();
        mDrawables.add(ContextCompat.getDrawable(this, R.drawable.d1));
        mDrawables.add(ContextCompat.getDrawable(this, R.drawable.d2));
        mDrawables.add(ContextCompat.getDrawable(this, R.drawable.d3));
        mDrawables.add(ContextCompat.getDrawable(this, R.drawable.d4));
        mDrawables.add(ContextCompat.getDrawable(this, R.drawable.d5));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_stop) {
            if (mBannerViewGlide.isPlaying()) {
                mBannerViewGlide.stop();
                mBannerViewResource.stop();
                mStopBtn.setText(getResources().getString(R.string.start));
            } else {
                mBannerViewGlide.play();
                mBannerViewResource.play();
                mStopBtn.setText(getResources().getString(R.string.stop));
            }
        }
    }
}
