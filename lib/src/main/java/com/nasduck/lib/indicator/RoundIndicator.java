package com.nasduck.lib.indicator;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.nasduck.lib.indicator.BaseIndicator;

import java.util.Objects;


public class RoundIndicator extends BaseIndicator implements ViewPager.OnPageChangeListener {

    private ViewPager mViewpager;
    private int mCount;

    public RoundIndicator(Context context) {
        super(context);
    }

    public RoundIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RoundIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setViewPager(ViewPager viewPager) {
        setViewPager(viewPager, -1);
    }

    public void setViewPager(ViewPager viewpager, int count) {
        mViewpager = viewpager;
        mLastPosition = -1;
        initIndicators(count);
        mViewpager.removeOnPageChangeListener(this);
        mViewpager.addOnPageChangeListener(this);
        this.onPageSelected(mViewpager.getCurrentItem());
    }

    public void initIndicators(int count) {
        removeAllViews();
        if (isViewPagerInit(mViewpager)) {
            if (count <= 0) {
                mCount = Objects.requireNonNull(mViewpager.getAdapter()).getCount();
            } else {
                mCount = count;
            }
            createIndicators(mCount, mViewpager.getCurrentItem());
        }
    }

    //* Private **********************************************************************************//

    private boolean isViewPagerInit(ViewPager viewPager) {
        return viewPager.getAdapter() != null && viewPager.getAdapter().getCount() > 0;
    }

    //* OnPageChangeListener *********************************************************************//

    @Override
    public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) {

    }

    @Override
    public void onPageSelected(int pos) {
        if (isViewPagerInit(mViewpager)) {
            selectIndicator(pos % mCount);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
