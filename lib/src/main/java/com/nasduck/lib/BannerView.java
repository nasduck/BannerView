package com.nasduck.lib;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.nasduck.lib.indicator.RoundIndicator;

import java.lang.reflect.Field;

import static android.arch.lifecycle.Lifecycle.State.STARTED;
import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

/**
 * Created by yi on 2019/4/25.
 * Description: 顶部横幅轮播栏
 */
public class BannerView extends FrameLayout
        implements ViewPager.OnPageChangeListener,
        DefaultLifecycleObserver {

    private static final String TAG = "BannerView";


    int mSize;  // 轮播内容个数
    boolean isAutoPlay;  // 是否自动播放，默认为 true
    private int mIntervalTime;  // 轮播时间间隔变量
    private int mSmoothDuration;  // 轮换持续时间
    private boolean mSmoothScroll;  // 平滑切换
    private boolean isPlaying;  // 是否正在播放

    private ViewPager mViewPager;
    private RoundIndicator mIndicator;
    private PagerAdapter mAdapter;
    private static Handler mHandler;

    private static final int NEXT_PAGE_MESSAGE = 1;  // 下一页事件消息
    private static final int INTERVAL_TIME = 3000;  // 轮播间隔常量

    private Lifecycle mLifecycle;  // 生命周期
    private boolean mIsAfterDragging;  // 上一状态是否为拖拽状态

    public BannerView(Context context) {
        super(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.banner_view, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        isAutoPlay = typedArray.getBoolean(R.styleable.BannerView_autoPlay, true);  // 默认自动轮播
        mIntervalTime = typedArray.getInt(R.styleable.BannerView_intervalTime, INTERVAL_TIME);
        mSmoothScroll = typedArray.getBoolean(R.styleable.BannerView_smoothScroll, false);
        mSmoothDuration = typedArray.getInt(R.styleable.BannerView_scrollTime, -1);
        initData();
        initView();
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        mLifecycle = owner.getLifecycle();
        Log.i(TAG, "onCreate: ");
    }

    public void setAdapter(PagerAdapter adapter, int size) {
        mAdapter = adapter;
        mSize = size;

        // 内容个数小于等于1时，默认设置不自动轮播
        if (mSize <= 1) {
            isAutoPlay = false;
        }
        int mid = Integer.MAX_VALUE / 2 - ((Integer.MAX_VALUE / 2) % size);
        mViewPager.setCurrentItem(mid);
        mViewPager.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    /**
     * 设置轮播间隔时间
     */
    public void setIntervalTime(int intervalTime) {
        mIntervalTime = intervalTime;
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
        isPlaying = false;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == NEXT_PAGE_MESSAGE) {// 设置平滑切换
                    if (mSmoothScroll) {
                        mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1), true);
                    } else {
                        mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1));
                    }
                    sendEmptyMessageDelayed(NEXT_PAGE_MESSAGE, mIntervalTime);
                }
            }
        };
    }

    /**
     * 监听 onStart() 生命周期
     * 根据是否自动轮播的状态，控制是否轮播
     */
    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        if (isAutoPlay) {
            play();
        }
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        stopPlay();
    }

    /**
     * 开始轮播
     * 在屏幕可见时开始轮播，否则通过生命周期回调再次判断是否开始自动轮播
     */
    public void play() {
        isAutoPlay = true;
        if (mLifecycle.getCurrentState().isAtLeast(STARTED)) {
            if (!isPlaying) {
                isPlaying = true;
                mHandler.sendEmptyMessageDelayed(NEXT_PAGE_MESSAGE, mIntervalTime);
            }
            Log.i(TAG, "play: start play");
        }
    }

    /**
     * 停止轮播
     */
    public void stopPlay() {
        mHandler.removeCallbacksAndMessages(null);
        isAutoPlay = false;
        isPlaying = false;
        Log.i(TAG, "stopPlay: ");
    }

    /**
     * @return 是否需要自动轮播
     */
    public boolean isAutoPlay() {
        return isAutoPlay;
    }

    /**
     * @return 当前轮播状态
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * @param state 新的状态.
     *              viewpager 滑动监听，设置手动滑动按下时，停止自动轮播
     *              松开时继续播放
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
                    play();
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


    /*************
     * 设置自定义滑动 Scroller
     * 控制图片轮播切换过程持续时间
     * */
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
            Log.e(TAG, "initData: " + e.getMessage());
        }
    }
}
