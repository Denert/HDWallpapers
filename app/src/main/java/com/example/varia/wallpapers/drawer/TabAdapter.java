package com.example.varia.wallpapers.drawer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.varia.wallpapers.MainActivity;

import java.util.List;

/**
 * Created by varia on 15.11.2017.
 */

public class TabAdapter extends FragmentStatePagerAdapter {

    List<NavItem> actions;
    Context context;
    private Fragment mCurrentFragment;

    public TabAdapter(FragmentManager fm, List<NavItem> actions, Context context){
        super(fm);
        this.actions = actions;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragmentFromAction(actions.get(position));
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return actions.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if(getCurrentFragment() != object)
            mCurrentFragment = ((Fragment) object);
        super.setPrimaryItem(container, position, object);
    }

    public Fragment getCurrentFragment(){
        return mCurrentFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return actions.get(position).getText(context);
    }

    public static Fragment fragmentFromAction(NavItem action){
        try{
            Fragment fragment = action.getFragment().newInstance();

            Bundle args = new Bundle();
            args.putStringArray(MainActivity.FRAGMENT_DATA, action.getData());

            fragment.setArguments(args);

            return fragment;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }
}
