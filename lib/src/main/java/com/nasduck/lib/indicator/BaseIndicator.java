package com.nasduck.lib.indicator;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.nasduck.lib.R;

public class BaseIndicator extends LinearLayout {

    private final static int DEFAULT_INDICATOR_WIDTH = 6;

    protected int mMargin = -1;
    protected int mWidth = -1;
    protected int mHeight = -1;

    protected int mBgResId; // 选中背景
    protected int mUnselectedBgResId; // 未选中背景

    // 对应动画
    protected Animator mAnimatorOut;
    protected Animator mAnimatorIn;
    protected Animator mImmediateAnimatorOut;
    protected Animator mImmediateAnimatorIn;

    protected int mLastPosition;

    public BaseIndicator(Context context) {
        super(context);
        initView(context, null);
    }

    public BaseIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public BaseIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private IndicatorConfig initAttr(Context context, AttributeSet attrs) {

        IndicatorConfig config = new IndicatorConfig();

        if (attrs == null) {
            return config;
        }

        @SuppressLint("CustomViewStyleable")
		TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.PenguinIndicator);

        config.setWidth(typedArray.getDimensionPixelSize(R.styleable.PenguinIndicator_pi_width, -1));
        config.setHeight(typedArray.getDimensionPixelSize(R.styleable.PenguinIndicator_pi_height, -1));
        config.setMargin(typedArray.getDimensionPixelSize(R.styleable.PenguinIndicator_pi_margin, -1));
        config.setAnimatorResId(typedArray.getResourceId(R.styleable.PenguinIndicator_pi_animator,
                R.anim.scale_with_alpha));
        config.setAnimatorReverseResId(typedArray.getResourceId(R.styleable.PenguinIndicator_pi_animator_reverse, -1));
        config.setBgResId(typedArray.getResourceId(R.styleable.PenguinIndicator_pi_drawable,
                        R.drawable.round_white));
        config.setUnselectedBgResId(typedArray.getResourceId(R.styleable.PenguinIndicator_pi_drawable_unselected,
                config.getBackgroundResId()));
        typedArray.recycle();

        return config;
    }

    private void initView(Context context, AttributeSet attrs) {
        mLastPosition = -1;
        IndicatorConfig config = initAttr(context, attrs);
        initWithConfig(config);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    public void initWithConfig(IndicatorConfig config) {
        int miniSize = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_INDICATOR_WIDTH, getResources().getDisplayMetrics()) + 0.5f);

        // todo < miniSize 吧
        mWidth = (config.getWidth() < 0) ? miniSize : config.getWidth();
        mHeight = (config.getHeight() < 0) ? miniSize : config.getHeight();
        mMargin = (config.getMargin() < 0) ? miniSize : config.getMargin();

        mAnimatorOut = createAnimatorOut(config);
        mImmediateAnimatorOut = createAnimatorOut(config);
        mImmediateAnimatorOut.setDuration(0);

        mAnimatorIn = createAnimatorIn(config);
        mImmediateAnimatorIn = createAnimatorIn(config);
        mImmediateAnimatorIn.setDuration(0);

        mBgResId =
                (config.getBackgroundResId() == -1) ? R.drawable.round_white : config.getBackgroundResId();
        mUnselectedBgResId =
                (config.getUnselectedBackgroundId() == -1) ? config.getBackgroundResId() : config.getUnselectedBackgroundId();
    }

    protected Animator createAnimatorOut(IndicatorConfig config) {
        return AnimatorInflater.loadAnimator(getContext(), config.getAnimatorResId());
    }

    protected Animator createAnimatorIn(IndicatorConfig config) {
        Animator animatorIn;
        if (config.getAnimatorReverseResId() == -1) { // 用户未设置反向动画
            // 则将 animatorResId 反向处理
            animatorIn = AnimatorInflater.loadAnimator(getContext(), config.getAnimatorResId());
            animatorIn.setInterpolator(new ReverseInterpolator());
        } else {
            animatorIn = AnimatorInflater.loadAnimator(getContext(), config.getAnimatorReverseResId());
        }
        return animatorIn;
    }

    /**
     * Indicator 数量, 当前选中
     *
     * @param count
     * @param currentPosition
     */
    protected void createIndicators(int count, int currentPosition) {
        for (int i = 0; i < count; i++) {
            if (currentPosition == i) {
                addIndicator(mBgResId, mImmediateAnimatorOut);
            } else {
                addIndicator(mUnselectedBgResId, mImmediateAnimatorIn);
            }
        }
    }

    /**
     * 添加 Indicator
     *
     * @param backgroundDrawableId
     * @param animator
     */
    protected void addIndicator(@DrawableRes int backgroundDrawableId,
                                Animator animator) {

        // Stop animation
        if (animator.isRunning()) {
            animator.cancel();
        }

        // Create an indicator view
        View indicator = new View(getContext());
        indicator.setBackgroundResource(backgroundDrawableId);
        addView(indicator, mWidth, mHeight);

        LayoutParams lp = (LayoutParams) indicator.getLayoutParams();
        lp.leftMargin = mMargin;
        lp.rightMargin = mMargin;
        indicator.setLayoutParams(lp);

        // Start Animation
        animator.setTarget(indicator);
        animator.start();
    }

    protected void selectIndicator(int pos) {

        if (mAnimatorIn.isRunning()) {
            mAnimatorIn.cancel();
        }

        if (mAnimatorOut.isRunning()) {
            mAnimatorOut.cancel();
        }

        // Restore last indicator
        View currentIndicator = getChildAt(mLastPosition);
        if (mLastPosition >= 0 && currentIndicator != null) {
            currentIndicator.setBackgroundResource(mUnselectedBgResId);
            mAnimatorIn.setTarget(currentIndicator);
            mAnimatorIn.start();
        }

        // Animate current selected indicator
        View selectedIndicator = getChildAt(pos);
        if (selectedIndicator != null) {
            selectedIndicator.setBackgroundResource(mBgResId);
            mAnimatorOut.setTarget(selectedIndicator);
            mAnimatorOut.start();
        }

        mLastPosition = pos;
    }

    public static class ReverseInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float value) {
            return Math.abs(1.0f - value);
        }

    }
}
