package com.nasduck.lib;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import android.widget.FrameLayout;

import com.nasduck.lib.indicator.RoundIndicator;


/**
 * Created by yi on 2019/4/25.
 * Description: 顶部横幅轮播栏
 */
public class BannerView extends FrameLayout {

    private ViewPager mViewPager;
    private RoundIndicator mIndicator;
    private PagerAdapter mAdapter;

    private Handler mHandler;
    private static final int NEXT_PAGE_MESSAGE = 1;
    private static final int STOP_PLAY = 2;
    private static final int INTERVAL_TIME = 3000;

    private boolean mAutoPlay;
    private int mSize;

    public BannerView(Context context) {
        super(context);
        mAutoPlay = true;
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.banner_view, this);

        initData();
        initView();
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(PagerAdapter adapter, int size) {
        mAdapter = adapter;
        mSize = size;
        if (mSize <= 1) {
            mAutoPlay = false;
        }
        int mid = Integer.MAX_VALUE / 2 - ((Integer.MAX_VALUE / 2) % size);
        mViewPager.setCurrentItem(mid);
        mViewPager.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        if (mAutoPlay) {
            mHandler.sendEmptyMessageDelayed(NEXT_PAGE_MESSAGE, INTERVAL_TIME);
        }
    }

    public void setAutoPlay(boolean autoPlay) {
        mAutoPlay = autoPlay;
    }

    public void startPlay() {
        mAutoPlay = true;
        mHandler.sendEmptyMessageDelayed(NEXT_PAGE_MESSAGE, INTERVAL_TIME);
    }

    public boolean isAutoPlay() {
        return mAutoPlay;
    }

    public void stopPlay() {
        mAutoPlay = false;
    }

    public void hasIndicator(boolean has) {
        if (has) {
            mIndicator.setViewPager(mViewPager, mSize);
        }
    }


    public void initView() {
        mViewPager = findViewById(R.id.view_pager);
        mIndicator = findViewById(R.id.round_indicator);
    }

    public void initData() {

        mAutoPlay = true;

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case NEXT_PAGE_MESSAGE:
                        if (mAutoPlay) {
                            mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1));
                            sendEmptyMessageDelayed(NEXT_PAGE_MESSAGE, INTERVAL_TIME);
                        }
                        break;
                    case STOP_PLAY:
                        stopPlay();
                        break;
                    default:
                        break;
                }
            }
        };
    }

}
