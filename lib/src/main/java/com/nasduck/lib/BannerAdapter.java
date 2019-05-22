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

//	private int mWidth;
//	private int mHeight;

    public BannerAdapter(Drawable... drawables) {
        mDrawables = drawables;
        mType = BannerDataType.TYPE_SOURCE;
    }

    public BannerAdapter(String... urlStrings) {

//		this(-1, -1, urlStrings);

        mUrlStrings = urlStrings;
        mType = BannerDataType.TYPE_INTERNET;
        mBitmapList = new ArrayList<>();
        for (int i = 0; i < mUrlStrings.length; i +=1) {
            mBitmapList.add(null);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadImageFromWebOperations();
            }
        }).start();

    }

//	public BannerAdapter(int width, int height, String... urlStrings) {
//		mWidth = width;
//		mHeight = height;
//
//		mUrlStrings = urlStrings;
//		mType = BannerDataType.TYPE_INTERNET;
//	}


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (mType == BannerDataType.TYPE_SOURCE) {
            imageView.setImageDrawable(mDrawables[position % mDrawables.length]);
        } else {
            if (mBitmapList.get(position % mBitmapList.size()) != null) {
                imageView.setImageBitmap(mBitmapList.get(position % mBitmapList.size()));
            }
        }
        if (container.equals(imageView.getParent())) {
            container.removeView(imageView);
        }
        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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

    public void loadImageFromWebOperations() {
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
}
