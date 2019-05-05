package com.nasduck.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.nasduck.lib.indicator.RoundIndicator;

import java.lang.reflect.Field;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

/**
 * Created by yi on 2019/4/25.
 * Description: 顶部横幅轮播栏
 */
public class BannerView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private static final String TAG = "BannerView";

    // 轮播内容个数
    int mSize;
    // 是否自动播放，默认为 true
    boolean mAutoPlay;
    // 轮播时间间隔变量
    private int mIntervalTime;
    // 轮换持续时间
    private int mSmoothDuration;
    // 平滑切换
    private boolean mSmoothScroll;

    private ViewPager mViewPager;
    private RoundIndicator mIndicator;
    private PagerAdapter mAdapter;
    private Handler mHandler;

    private static final int NEXT_PAGE_MESSAGE = 1;  // 下一页事件消息
    private static final int INTERVAL_TIME = 3000;  // 轮播间隔常量


    // 上一状态是否为拖拽状态
    private boolean mIsAfterDragging;

    public BannerView(Context context) {
        super(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.banner_view, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        mAutoPlay = typedArray.getBoolean(R.styleable.BannerView_autoPlay, true);  // 默认自动轮播
        mIntervalTime = typedArray.getInt(R.styleable.BannerView_intervalTime, INTERVAL_TIME);
        mSmoothScroll = typedArray.getBoolean(R.styleable.BannerView_smoothScroll, false);
        mSmoothDuration = typedArray.getInt(R.styleable.BannerView_scrollTime, -1);
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
            mHandler.sendEmptyMessageDelayed(NEXT_PAGE_MESSAGE, mIntervalTime);
        }
    }

    public void setIntervalTime(int intervalTime) {
        mIntervalTime = intervalTime;
    }

    public void startPlay() {
        mAutoPlay = true;
        mHandler.sendEmptyMessageDelayed(NEXT_PAGE_MESSAGE, mIntervalTime);
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
        if (mSmoothDuration > 0 && mSmoothDuration < mIntervalTime) {
            setSmoothScroll(mSmoothDuration);
        }
    }

    public void initData() {
        mIsAfterDragging = false;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case NEXT_PAGE_MESSAGE:
                        if (mAutoPlay) {
                            if (mSmoothScroll) {
                                mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1), true);
                            } else {
                                mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1));
                            }
                            sendEmptyMessageDelayed(NEXT_PAGE_MESSAGE, mIntervalTime);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * @param state 新的状态.
     * viewpager 滑动监听，设置手动滑动按下时，停止自动轮播
     * 松开时继续播放
     */
    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case SCROLL_STATE_DRAGGING:
                stopPlay();
                mIsAfterDragging = true;
                break;
            case SCROLL_STATE_IDLE:
                if (mIsAfterDragging) {
                    mIsAfterDragging = false;
                    startPlay();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }


    // 设置自定义滑动 Scroller
    private void setSmoothScroll(int duration) {
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            SmoothSpeedScroller scroller = new SmoothSpeedScroller(mViewPager.getContext(), new LinearInterpolator());
            scroller.setDuration(duration);
            mScroller.set(mViewPager, scroller);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "initData: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "initData: " + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, "initData: " + e.getMessage() );
        }
    }
}
