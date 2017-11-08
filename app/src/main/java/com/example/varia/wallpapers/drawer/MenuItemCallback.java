package com.example.varia.wallpapers.drawer;

import android.view.MenuItem;

import java.util.List;

/**
 * Created by varia on 02.11.2017.
 */

public interface MenuItemCallback {
    void menuItemClicked(List<NavItem> action, MenuItem item, boolean requiresPurchase);
}
