package com.nasduck.lib;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends PagerAdapter {

    private static final String TAG = "BannerAdapter";

    private Drawable[] mDrawables;
    private String[] mUrlStrings;
    private BannerDataType mType;
    private List<Bitmap> mBitmapList;
    private int mRealSize;

    private ImageClickListener mClickListener;

    public BannerAdapter(Drawable[] drawables) {
        mDrawables = drawables;
        mRealSize = mDrawables.length;
        mType = BannerDataType.TYPE_SOURCE;
    }

    public BannerAdapter(String[] urlStrings) {
        mUrlStrings = urlStrings;
        mRealSize = mUrlStrings.length;

        mType = BannerDataType.TYPE_INTERNET;
        mBitmapList = new ArrayList<>();
        for (int i = 0; i < mUrlStrings.length; i += 1) {
            mBitmapList.add(null);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadImageFromWebOperations();
            }
        }).start();

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        int realPosition = position % mRealSize;
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (mType == BannerDataType.TYPE_SOURCE) {
            imageView.setImageDrawable(mDrawables[realPosition]);
        } else {
            if (mBitmapList.get(realPosition) != null) {
                imageView.setImageBitmap(mBitmapList.get(realPosition));
            }
        }
        if (container.equals(imageView.getParent())) {
            container.removeView(imageView);
        }
        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // 图片点击事件

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onImageClick(realPosition);
            }
        });

        return imageView;
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

    /**
     * 通过网络下载图片
     */
    private void loadImageFromWebOperations() {
        for (int i = 0; i < mBitmapList.size(); i += 1) {
            try {
                InputStream is = (InputStream) new URL(mUrlStrings[i]).getContent();
                mBitmapList.set(i, BitmapFactory.decodeStream(is));
            } catch (MalformedURLException mue) {
                Log.e(TAG, "LoadImageFromWebOperations: MalformedURLException: " + mue.getMessage());
            } catch (IOException ioe) {
                Log.e(TAG, "LoadImageFromWebOperations: IOException: " + ioe.getMessage());
            }
        }
    }

    public interface ImageClickListener {
        void onImageClick(int position);
    }

    public void setClickListener(ImageClickListener clickListener) {
        mClickListener = clickListener;
    }
}
