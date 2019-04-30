package com.nasduck.lib;


import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class BannerAdapter extends PagerAdapter {

    private Drawable[] mDrawables;
    private String[] mUrlStrings;
    private BannerDataType mType;

    private int mWidth;
    private int mHeight;

    public BannerAdapter(Drawable... drawables) {
        mDrawables = drawables;
        mType = BannerDataType.TYPE_SOURCE;
    }

    public BannerAdapter(String... urlStrings) {
        this(-1, -1, urlStrings);
    }

    public BannerAdapter(int width, int height, String... urlStrings) {
        mWidth = width;
        mHeight = height;

        mUrlStrings = urlStrings;
        mType = BannerDataType.TYPE_INTERENET;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (mType == BannerDataType.TYPE_SOURCE) {
            imageView.setImageDrawable(mDrawables[position % mDrawables.length]);
        } else {
            getImageByPicasso(mUrlStrings[position % mUrlStrings.length], imageView);
        }
        if (container.equals(imageView.getParent())) {
            container.removeView(imageView);
        }
        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return imageView;
    }

    private void getImageByPicasso(String urlString, ImageView imageView) {
        if (mWidth > 0 && mHeight > 0) {
            Picasso.get()
                    .load(urlString)
                    .resize(mWidth, mHeight)
                    .into(imageView);
        } else {
            Picasso.get()
                    .load(urlString)
                    .into(imageView);
        }
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
