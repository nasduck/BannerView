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

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;


/**
 * Created by yi on 2019/4/25.
 * Description: 顶部横幅轮播栏
 */
public class BannerView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private RoundIndicator mIndicator;
    private PagerAdapter mAdapter;
    private Handler mHandler;

    private static final int NEXT_PAGE_MESSAGE = 1;  // 下一页事件消息
    private static final int STOP_PLAY = 2; // 停止自动轮播事件消息
    private static final int INTERVAL_TIME = 3000;  // 轮播间隔常量

    // 是否自动播放，默认为 true
    private boolean mAutoPlay;
    // 轮播内容个数
    private int mSize;
    // 轮播时间间隔变量
    private int mIntervalTime;


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

        // 内容个数小于等于1时，默认设置不自动轮播
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
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * @param has 是否自带 Indicator
     */
    public void hasIndicator(boolean has) {
        if (has) {
            mIndicator.setViewPager(mViewPager, mSize);
        }
    }


    public void initView() {
        mViewPager = findViewById(R.id.view_pager);
        mIndicator = findViewById(R.id.round_indicator);

        mViewPager.addOnPageChangeListener(this);
    }

    public void initData() {

        mAutoPlay = true;
        mIntervalTime = INTERVAL_TIME;

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case NEXT_PAGE_MESSAGE:
                        if (mAutoPlay) {
                            mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1));
                            sendEmptyMessageDelayed(NEXT_PAGE_MESSAGE, mIntervalTime);
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

    /**
     * @param state 新的状态
     * viewpager 滑动监听，设置手动滑动按下时，停止自动轮播
     * 松开时继续播放
     * */
    @Override
    public void onPageScrollStateChanged(int state) {
        if (SCROLL_STATE_DRAGGING == state) {
            stopPlay();
        }

        if (SCROLL_STATE_SETTLING == state) {
            startPlay();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }


}
