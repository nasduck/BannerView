package com.nasduck.lib.indicator;

import android.support.annotation.AnimatorRes;
import android.support.annotation.DrawableRes;

public class IndicatorConfig {

    private int width;

    private int height;

    private int margin;

    @AnimatorRes
    private int animatorResId;

    @AnimatorRes
    private int animatorReverseResId;

    @DrawableRes
    private int backgroundResId;

    @DrawableRes
    private int unselectedBackgroundId;

    public IndicatorConfig() {
        width = -1;
        height = -1;
        margin = -1;
        animatorResId = -1;
        animatorReverseResId = -1;
        backgroundResId = -1;
        unselectedBackgroundId = -1;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public int getAnimatorResId() {
        return animatorResId;
    }

    public void setAnimatorResId(int animatorResId) {
        this.animatorResId = animatorResId;
    }

    public int getAnimatorReverseResId() {
        return animatorReverseResId;
    }

    public void setAnimatorReverseResId(int animatorReverseResId) {
        this.animatorReverseResId = animatorReverseResId;
    }

    public int getBackgroundResId() {
        return backgroundResId;
    }

    public void setBgResId(int backgroundResId) {
        this.backgroundResId = backgroundResId;
    }

    public int getUnselectedBackgroundId() {
        return unselectedBackgroundId;
    }

    public void setUnselectedBgResId(int unselectedBackgroundId) {
        this.unselectedBackgroundId = unselectedBackgroundId;
    }
}
