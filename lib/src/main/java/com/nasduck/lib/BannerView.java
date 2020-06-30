package com.nasduck.lib;

import android.arch.lifecycle.DefaultLifecycleObserver;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nasduck.lib.indicator.RoundIndicator;
import com.nasduck.lib.loader.ImageLoaderInterface;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.arch.lifecycle.Lifecycle.State.STARTED;
import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

/**
 * Created by yi on 2019/4/25.
 * Description: 横幅轮播栏
 */
public class BannerView extends FrameLayout
        implements ViewPager.OnPageChangeListener,
        DefaultLifecycleObserver {

    private static final String TAG = "BannerView";
    private static final int NEXT_PAGE_MESSAGE = 1;  // 下一页事件消息
    private static final int INTERVAL_TIME = 3000;  // 默认切换间隔时间 3000ms



    // 属性参数
    boolean isAutoPlay;  // 是否自动播放，默认为 true
    private int mIntervalTime;  // 轮播时间间隔变量
    private int mSmoothDuration;  // 轮换持续时间
    private boolean isSmooth;  // 平滑切换
    private boolean hasIndicator;
    private int mScaleType; // 裁剪方式,默认"FIT_CENTER"
    private boolean isLoop = true; // 是否循环

    private Context mContext;

    private ViewPager mViewPager;
    private RoundIndicator mIndicator;
    private BannerPagerAdapter mAdapter;

    private Handler mHandler;
    private List mImageUrls;
    private List<View> mImageViews;
    private BannerViewClickListener mClickListener;
    private ImageLoaderInterface mImageLoader;

    private boolean hasSetIndicator;
    private boolean isPlaying;  // 是否正在播放
    private boolean mIsAfterDragging;  // 上一状态是否为拖拽状态

    private OnBannerScrolledListener mScrolledListener;

    public BannerView(Context context) {
        super(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.banner_view, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        initAttrs(context, typedArray);
        initData();
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化 BannerView 属性值
     */
    private void initAttrs(Context context, TypedArray typedArray) {


        // 是否自动轮播 默认为 true
        isAutoPlay = typedArray.getBoolean(R.styleable.BannerView_autoPlay, true);

        // 轮播切换间隔时间 默认 3000ms
        mIntervalTime = typedArray.getInt(R.styleable.BannerView_intervalTime, INTERVAL_TIME);

        // 是否开启轮播平滑切换图片，
        isSmooth = typedArray.getBoolean(R.styleable.BannerView_is_smooth_scroll, false);

        // 图片平滑切换时间，默认切换持续时间 500ms
        mSmoothDuration = typedArray.getInt(R.styleable.BannerView_scrollTime, 500);

        // 是否带指示器， 默认 false
        hasIndicator = typedArray.getBoolean(R.styleable.BannerView_has_indicator, false);

        // 图片裁剪方式， 默认 FIT_CENTER
        mScaleType = typedArray.getInt(R.styleable.BannerView_banner_scale_type, BannerScaleType.FIT_CENTER.getValue());
    }

    /**
     * 数据初始化
     */
    private void initData() {
        mViewPager = findViewById(R.id.view_pager);
        mIndicator = findViewById(R.id.round_indicator);
        mViewPager.addOnPageChangeListener(this);

        mIsAfterDragging = false;
        isPlaying = false;
        hasSetIndicator = false;
        mImageUrls = new ArrayList<>();
        mImageViews = new ArrayList<>();
        mHandler = new NextPagerHandle(mViewPager, isSmooth, mIntervalTime);
        setToSmooth();
    }

    // * 对外提供属性设置方法 ************************************************************************/
    /**
     * 设置是否自动开启轮播
     */
    public BannerView setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
        return this;
    }

    /**
     * 设置轮播间隔时间
     */
    public BannerView setIntervalTime(int intervalTime) {
        mIntervalTime = intervalTime;
        return this;
    }

    /**
     * @param has 是否自带 Indicator
     */
    public BannerView hasIndicator(boolean has) {
       this.hasIndicator = has;

       return this;
    }


    /**
     * 设置图片裁剪方式
     */
    public BannerView setScaleType(BannerScaleType scaleType) {
        mScaleType = scaleType.getValue();
        return this;
    }

    /**
     * 设置图片加载框架
     */
    public BannerView setImageLoader(ImageLoaderInterface imageLoader) {
        mImageLoader = imageLoader;
        return this;
    }


    /**
     * 设置图片地址
     */
    public BannerView setImageUrls(List<?> imageUrls) {
        mImageUrls = imageUrls;
        initImageList(mImageUrls);


        return this;
    }

    /**
     * 设置平滑切换
     * @param smooth 是否平滑切换
     */
    public BannerView setSmooth(boolean smooth) {
       isSmooth  = smooth;
       return this;
    }

    public BannerView setLoop(boolean loop) {
        isLoop = loop;
        return this;
    }

    public void init() {
        setAdapter();
        if (hasIndicator && mImageUrls.size() >  0 && !hasSetIndicator && mAdapter != null) {
            mIndicator.setViewPager(mViewPager, mImageUrls.size());
            hasSetIndicator = true;
        }

        if (mImageUrls.size() <= 1) {
            isAutoPlay = false;
        }
    }


    /**
     * 设置平滑切换持续时间
     * @param smoothDuration 平滑切换持续时间
     */
    public BannerView setSmoothDuration(int smoothDuration) {
        mSmoothDuration = smoothDuration;
        return this;
    }

    /**
     * 设置点击监听
     * @param clickListener 点击监听
     */
    public void setClickListener(BannerViewClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setScrolledListener(OnBannerScrolledListener scrolledListener) {
        mScrolledListener = scrolledListener;
    }

    // * 公共方法 **********************************************************************************/
    public BannerView start() {
        initImageList(mImageUrls);
        return this;
    }

    /**
     * 开始轮播
     * 在屏幕可见时开始轮播，否则通过生命周期回调再次判断是否开始自动轮播
     */
    public void play() {
        if (isLoop) {
            isPlaying = true;
            isAutoPlay = true;
            mHandler.sendEmptyMessageDelayed(NEXT_PAGE_MESSAGE, mIntervalTime);
        }
    }

    /**
     * 停止轮播
     */
    public void stop() {
        isPlaying = false;
        isAutoPlay = false;
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * @return 是否正在轮播
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    public int getCurrentIndex() {
        return mViewPager.getCurrentItem()  % mImageUrls.size();
    }

    // * 私有方法 **********************************************************************************/

    /**
     * 完成图片路径设置
     */
    private void initImageList(List<?> imageUrls) {
        if (imageUrls == null || imageUrls.size() <= 0) {
            Log.e(TAG, "This banner view data set is empty.");
            return;
        }
        mImageViews.clear();
        for (int i = 0; i < imageUrls.size(); i += 1) {
            View imageView = null;
            if (mImageLoader != null) {
                imageView = mImageLoader.createImageView(mContext);
            }

            if (imageView == null) {
                imageView = new ImageView(mContext);
            }
            setImageScaleType(imageView);
            mImageViews.add(imageView);
            if (mImageLoader != null) {
                mImageLoader.displayImage(mContext, imageUrls.get(i), imageView);
            } else {
                Log.e(TAG, "Please set image loader");
            }
        }
    }

    /**
     * 设置图片裁剪方式
     * @param view 需要裁剪的 view
     */
    private void setImageScaleType(View view) {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            BannerScaleType scaleType = BannerScaleType.getType(mScaleType);
            if (scaleType == null) {
                return;
            }
            switch (scaleType) {
                case CENTER:
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    break;
                case CENTER_CROP:
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                case CENTER_INSIDE:
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    break;
                case FIT_CENTER:
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    break;
                case FIT_END:
                    imageView.setScaleType(ImageView.ScaleType.FIT_END);
                    break;
                case FIT_START:
                    imageView.setScaleType(ImageView.ScaleType.FIT_START);
                    break;
                case FIT_XY:
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case MATRIX:
                    imageView.setScaleType(ImageView.ScaleType.MATRIX);
                    break;
            }
        }
    }

    /**
     * 设置数据
     */
    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new BannerPagerAdapter();
        }
        if (isLoop) {
            int mid = Integer.MAX_VALUE / 2 - ((Integer.MAX_VALUE / 2) % mImageUrls.size());
            mViewPager.setCurrentItem(mid);
        }
        mViewPager.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 设置平滑切换
     */
    private void setToSmooth() {
        if (mSmoothDuration > 0 && mSmoothDuration < mIntervalTime && isSmooth) {
            setSmoothScroll(mSmoothDuration);
        }
    }

    /**
     * 设置自定义滑动 Scroller
     * 控制图片轮播切换过程持续时间
     * @param duration 轮播切换持续时间
     * */
    private void setSmoothScroll(int duration) {
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            SmoothSpeedScroller scroller = new SmoothSpeedScroller(mViewPager.getContext(), new LinearInterpolator());
            scroller.setDuration(duration);
            mScroller.set(mViewPager, scroller);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            Log.e(TAG, "initData: " + e.getMessage());
        }
    }

    // * 内部类 ************************************************************************************/

    /**
     * 自定义切换下一页 Handle
     */
    static class NextPagerHandle extends Handler {
        private ViewPager viewPager;
        private boolean isSmooth;
        private int intervalTime;

        NextPagerHandle(ViewPager viewPager, boolean isSmooth, int intervalTime) {
            this.viewPager = viewPager;
            this.isSmooth = isSmooth;
            this.intervalTime = intervalTime;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == NEXT_PAGE_MESSAGE ) {
                // setCurrentItem(position) 与 setCurrent(position, false) 效果不同
                if (isSmooth) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
                sendEmptyMessageDelayed(NEXT_PAGE_MESSAGE, intervalTime);
            }
        }
    }

    /**
     * BannerView 适配器
     */
    class BannerPagerAdapter extends PagerAdapter {

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            int realPosition = position % mImageUrls.size();
            View view = mImageViews.get(realPosition);

            if (container.equals(view.getParent())) {
                container.removeView(view);
            }
            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            // 图片点击事件
            if (mClickListener != null) {
                view.setOnClickListener(v -> mClickListener.onImageClick(realPosition));
            }

            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public int getCount() {
            return isLoop? Integer.MAX_VALUE : mImageUrls.size();
        }
    }

    /**
     * 点击接口
     */
    public interface BannerViewClickListener {
        void onImageClick(int position);
    }

    // * 生命周期事件 *******************************************************************************/
    /**
     * 监听 onStart() 生命周期
     * 根据是否自动轮播的状态，控制是否轮播
     */
    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        if (isAutoPlay && owner.getLifecycle().getCurrentState().isAtLeast(STARTED)) {
            play();
        }
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        stop();
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
                stop();
                mIsAfterDragging = true;
                break;
            case SCROLL_STATE_IDLE:
                if (mIsAfterDragging && isAutoPlay) {
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
        if (mScrolledListener != null) {
            mScrolledListener.onBannerScrolled(getCurrentIndex());
        }
    }

    /**
     * 当前位置位置监听接口
     */
    public interface OnBannerScrolledListener {
        void onBannerScrolled(int position);
    }
}
