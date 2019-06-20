package com.nasduck.lib.anim;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 效果：
 * 切换时，沿中央轴线翻转
 */

public class WindmillTransformer implements ViewPager.PageTransformer {
    private static final float BASE_ROTATION = 180.0f;

    @Override
    public void transformPage(View view, float position) {
        if (position < -1) {

        } else if (position <= 0) {
            view.setTranslationX(-view.getWidth() * position);
            float rotation = (BASE_ROTATION * position);
            view.setRotationY(rotation);

            if (position > -0.5) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.INVISIBLE);
            }
        } else if (position <= 1) {
            view.setTranslationX(-view.getWidth() * position);
            float rotation = (BASE_ROTATION * position);
            view.setRotationY(rotation);

            if (position < 0.5) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }
}
