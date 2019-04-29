package com.nasduck.bannerview;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import com.nasduck.lib.BannerAdapter;
import com.nasduck.lib.BannerView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<String> mUrlStringList;
    private List<Bitmap> mBitmapList;

    private Button mStopBtn;
    private BannerView mBannerView;
    private BannerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        mStopBtn = (Button) findViewById(R.id.btn_stop);
        mStopBtn.setOnClickListener(this);

        mBannerView = findViewById(R.id.banner_view);
        mAdapter = new BannerAdapter(mUrlStringList.toArray(new String[mUrlStringList.size()]));
        mBannerView.setAdapter(mAdapter, mUrlStringList.size());
        mBannerView.hasIndicator(true);
    }

    private void initData() {

        mUrlStringList = new ArrayList<>();
        mBitmapList = new ArrayList<>();

        mUrlStringList.add("https://raw.githubusercontent.com/sohnyi/Rescources/master/images/tiny_cesar-couto-1539936-unsplash.jpg");
        mUrlStringList.add("https://api.hzshzj.com/attachment/images/8/2019/01/TnqjHFFg69EjgGV8e0gG09QYtJhU69.png");
        mUrlStringList.add("https://raw.githubusercontent.com/sohnyi/Rescources/master/images/tiny_homas-aeschleman-1538081-unsplash.jpg");
        mUrlStringList.add("https://api.hzshzj.com/attachment/images/8/2019/01/xdEBNGZlQ24W2BDAdZzzliP41T39Ne.png");
        mUrlStringList.add("https://raw.githubusercontent.com/sohnyi/Rescources/master/images/tiny_mohamad-zaheri-1539533-unsplash.jpg");
        mUrlStringList.add("https://api.hzshzj.com/attachment/images/8/20if (mBannerView.isAutoPlay()) {\n" +
                "            mStopBtn.setText(getResources().getString(R.string.stop));\n" +
                "        } else {\n" +
                "            mStopBtn.setText(getResources().getString(R.string.start));\n" +
                "        }5z7xvpp5KWF6aPk56Lpxg6yf6rw.png");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_stop:
                if (mBannerView.isAutoPlay()) {
                    mBannerView.stopPlay();
                    mStopBtn.setText(getResources().getString(R.string.start));
                } else {
                    mBannerView.startPlay();
                    mStopBtn.setText(getResources().getString(R.string.stop));
                }
                break;
            default:
                break;
        }

    }
}
