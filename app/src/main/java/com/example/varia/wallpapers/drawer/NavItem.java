package com.example.varia.wallpapers.drawer;

import android.app.Fragment;
import android.content.Context;

import java.io.Serializable;

/**
 * Created by varia on 02.11.2017.
 */

public class NavItem implements Serializable {
    private String mText;
    private int mTextResource;
    private String[] mData;
    private Class<? extends Fragment> mFragment;

    public String categoryImageUrl;

    public NavItem(String text, Class<? extends Fragment> fragment, String[] data) {
        this.mText = text;
        this.mData = data;
        this.mFragment = fragment;
    }

    public String getText(Context context) {
        if (mText != null)
            return mText;
        else return context.getResources().getString(mTextResource);
    }

    public String[] getData() {
        return mData;
    }

    public Class<? extends Fragment> getFragment() {
        return mFragment;
    }

    public void setCategoryImageUrl(String url){
        this.categoryImageUrl = url;
    }
}
