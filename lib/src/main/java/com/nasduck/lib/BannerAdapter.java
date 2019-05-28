package com.nasduck.lib;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
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
        mClickListener = null;
    }

    public BannerAdapter( String[] urlStrings) {
        mUrlStrings = urlStrings;
        mRealSize = mUrlStrings.length;
        mType = BannerDataType.TYPE_INTERNET;
        mClickListener = null;

        mBitmapList = new ArrayList<>();
        for (int i = 0; i < mUrlStrings.length; i +=1) {
            mBitmapList.add(null);
        }

        new Thread(this::loadImageFromWebOperations).start();

    }

    void setPlaceholder(Drawable drawable) {
        for (int i = 0; i < mBitmapList.size(); i+=1) {
            mBitmapList.set(i, drawableToBitmap(drawable));
        }
    }

    void setPlaceholder(Bitmap bitmap) {
        for (int i = 0; i < mBitmapList.size(); i+=1) {
            mBitmapList.set(i, bitmap);
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        int realPosition = position % mRealSize;
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (mType == BannerDataType.TYPE_SOURCE) {
            imageView.setImageDrawable(mDrawables[realPosition]);
        } else {
            imageView.setImageBitmap(mBitmapList.get(realPosition));
        }
        if (container.equals(imageView.getParent())) {
            container.removeView(imageView);
        }
        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // 图片点击事件
        if (mClickListener != null) {
            imageView.setOnClickListener(v -> mClickListener.onImageClick(realPosition));
        }

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

    void setClickListener(ImageClickListener clickListener) {
        mClickListener = clickListener;
    }


    private Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
