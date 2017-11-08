package com.example.varia.wallpapers;

import com.example.varia.wallpapers.drawer.SimpleMenu;

/**
 * Created by varia on 03.11.2017.
 */

public class Config {
    public static String CONFIG_URL = "";

    public static final boolean HIDE_DRAWER = false;

    public static boolean USE_HARDCODED_CONFIG = false;

    public static void configureMenu(SimpleMenu menu, ConfigParser.CallBack callBack){
        callBack.configLoaded(false);
    }
}
