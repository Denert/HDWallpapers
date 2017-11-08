package com.example.varia.wallpapers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.varia.wallpapers.drawer.MenuItemCallback;
import com.example.varia.wallpapers.drawer.NavItem;
import com.example.varia.wallpapers.drawer.SimpleMenu;
import com.example.varia.wallpapers.util.DisableViewPager;
import com.example.varia.wallpapers.util.Helper;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MenuItemCallback, ConfigParser.CallBack {

    private Toolbar mToolbar;
    private TabLayout tabLayout;
    private DisableViewPager viewPager;
    private NavigationView navigationView;
    private static SimpleMenu menu;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    public static String FRAGMENT_DATA = "transaction_data";
    public static String FRAGMENT_CLASS = "transaction_target";

    public static boolean TABLET_LAYOUT = true;

    List<NavItem> queueItem;
    MenuItem queueMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this, getString(R.string.admob_app_id));//инифиализация рекламы

        if (useTabletMenu()){
            setContentView(R.layout.activity_main_tablet);
            Helper.setStatusBarColor(MainActivity.this,
                    ContextCompat.getColor(this, R.color.myPrimaryDarkColor));
        } else
            setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (!useTabletMenu()){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } else getSupportActionBar().setDisplayShowHomeEnabled(false);

        if (!useTabletMenu()){
            drawer = (DrawerLayout) findViewById(R.id.drawer);
            toggle = new ActionBarDrawerToggle(this, drawer, mToolbar,
                    R.string.drawer_open, R.string.drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (DisableViewPager) findViewById(R.id.viewpager);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(FRAGMENT_CLASS)){//не разобрался
            try{
                Class<? extends Fragment> fragmentClass = (Class<? extends Fragment>) getIntent()
                        .getExtras().getSerializable(FRAGMENT_CLASS);
                if (fragmentClass != null){
                    String[] extra = getIntent().getExtras().getStringArray(FRAGMENT_DATA);
                    finish();
                }
            } catch (Exception e){}
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        menu = new SimpleMenu(navigationView.getMenu(), this);
        if (Config.USE_HARDCODED_CONFIG){//не понял
            Config.configureMenu(menu, this);
        } else if (!Config.CONFIG_URL.isEmpty() && Config.CONFIG_URL.contains("http"))
            new ConfigParser(Config.CONFIG_URL, menu, this, this).execute();
        else
            new ConfigParser("config.json", menu, this, this).execute();
        tabLayout.setupWithViewPager(viewPager);

        if(!useTabletMenu()){
            drawer.setStatusBarBackgroundColor(
                    ContextCompat.getColor(this, R.color.myPrimaryDarkColor));
        }

        applyDrawerLocks();
    }

    @Override
    public void menuItemClicked(List<NavItem> action, MenuItem item, boolean requiresPurchase) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean openOnStart = preferences.getBoolean("menuOpenOnStart", false);
        if(drawer != null)
            if (openOnStart && !useTabletMenu()) {
                drawer.openDrawer(GravityCompat.START);
            } else {
                drawer.closeDrawer(GravityCompat.START);
            }

        if(item != null){
            for(MenuItem menuItem : menu.getMenuItems())
                menuItem.setChecked(false);
            item.setChecked(true);
        }

    }

    @Override
    public void configLoaded(boolean success) {
        if (success || menu.getFirstMenuItem() == null){
            if (Helper.isOnlineShowDialog(MainActivity.this))
                Toast.makeText(this, R.string.invalid_configuration, Toast.LENGTH_LONG).show();
        } else {
            menuItemClicked(menu.getFirstMenuItem().getValue(), menu.getFirstMenuItem().getKey(),
                    false);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 1:
                boolean foundfalse = false;
                for (int i = 0; i < grantResults.length; i++){
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        foundfalse = true;
                }
                if (!foundfalse)
                    menuItemClicked(queueItem, queueMenuItem, false);
                else Toast.makeText(MainActivity.this, getResources()
                        .getString(R.string.permissions_required), Toast.LENGTH_SHORT).show();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        @SuppressLint("RestrictedApi") List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null)
            for (Fragment fragment : fragments)
                if(fragment != null)
                    fragment.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
    }

    public boolean useTabletMenu(){
        return (getResources().getBoolean(R.bool.isWideTablet) && TABLET_LAYOUT);
    }

    public void applyDrawerLocks(){
        if(drawer == null){
            if(Config.HIDE_DRAWER)
                navigationView.setVisibility(View.GONE);
            return;
        }

        if(Config.HIDE_DRAWER){
            toggle.setDrawerIndicatorEnabled(false);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
}
