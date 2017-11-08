package com.example.varia.wallpapers;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.example.varia.wallpapers.drawer.NavItem;
import com.example.varia.wallpapers.drawer.SimpleMenu;
import com.example.varia.wallpapers.drawer.SimpleSubMenu;
import com.example.varia.wallpapers.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by varia on 03.11.2017.
 */

public class ConfigParser extends AsyncTask<Void, Void, Void> {

    private String souceLocation;
    private Activity context;
    private SimpleMenu menu;
    private CallBack callBack;
    private boolean facedException;
    private static JSONArray jsonMenu = null;

    private static String CACHE_FILE = "menuCache.srl";
    final long MAX_FILE_AGE = 60 * 60 * 24 * 1;

    public ConfigParser(String souceLocation, SimpleMenu menu, Activity context, CallBack callBack) {
        this.souceLocation = souceLocation;
        this.context = context;
        this.menu = menu;
        this.callBack = callBack;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (jsonMenu == null)
            try{
                if(souceLocation.contains("http")){
                    jsonMenu = getJSONFromCache();
                    if (getJSONFromCache() == null){
                        Log.v("INFO", "Loading menu config from url.");
                        String jsonStr = Helper.getDataFromUrl(souceLocation);
                        jsonMenu = new JSONArray(jsonStr);
                        saveJSONToCache(jsonStr);
                    } else {
                        Log.v("INFO", "Loading menu config from url");
                    }
                } else {
                    String jsonStr = Helper.loadJsonFromAsset(context, souceLocation);
                    jsonMenu = new JSONArray(jsonStr);
                }
            } catch (JSONException e){
                e.printStackTrace();;
            }

        if (jsonMenu != null) {
            final JSONArray jsonMenuFinal = jsonMenu;

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        SimpleSubMenu subMenu = null;

                        for (int i = 0; i < jsonMenuFinal.length(); i++){
                            JSONObject jsonMenuItem = jsonMenuFinal.getJSONObject(i);
                            String menuTitle = jsonMenuItem.getString("title");
                            int menuDrawableResource = 0;

                            if (jsonMenuItem.has("drawable") &&
                                    jsonMenuItem.getString("drawable") != null &&
                                    !jsonMenuItem.getString("drawable").isEmpty() &&
                                    !jsonMenuItem.getString("drawable").equals("0"))
                                menuDrawableResource = getDrawableByName(jsonMenuItem
                                        .getString("drawable"));

                            if (jsonMenuItem.has("submenu")
                                    && jsonMenuItem.getString("submenu") != null
                                    && !jsonMenuItem.getString("submenu").isEmpty()){
                                String menuSubMenu = jsonMenuItem.getString("submenu");

                                if(subMenu == null || !subMenu.getSubMenuTitle().equals(menuSubMenu))
                                    subMenu = new SimpleSubMenu(menu, menuSubMenu);
                            } else {
                                subMenu = null;
                            }

                            boolean requiresIap = false;
                            if (jsonMenuItem.has("iap") &&
                                    jsonMenuItem.getBoolean("iap"))
                                requiresIap = true;

                            List<NavItem> menuTabs = new ArrayList<NavItem>();
                            JSONArray jsonTabs = jsonMenuItem.getJSONArray("tabs");

                            for (int j = 0; j < jsonTabs.length(); j++){
                                JSONObject jsonTab = jsonTabs.getJSONObject(j);
                                menuTabs.add(navItemFromJSON(jsonTab));
                            }

                            if (subMenu != null)
                                subMenu.add(menuTitle, menuDrawableResource, menuTabs, requiresIap);
                            else menu.add(menuTitle, menuDrawableResource, menuTabs, requiresIap);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("INFO", "JSON was invalid");
                        facedException = true;
                    }
                }
            });
        } else {
            Log.e("INFO", "JSON Could not be retrieved");
            facedException = true;
        }
        return null;
    }

    public static NavItem navItemFromJSON(JSONObject jsonTab) throws JSONException{
        String tabTitle = jsonTab.getString("title");

        Class<? extends Fragment> tabClass = null;

        JSONArray args = jsonTab.getJSONArray("arguments");
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < args.length(); i++) {
            list.add(args.getString(i));
        }

        NavItem item = new NavItem(tabTitle, tabClass, list.toArray(new String[0]));

        if (jsonTab.has("image") && jsonTab.getString("image") != null &&
                !jsonTab.getString("image").isEmpty()){
            item.setCategoryImageUrl(jsonTab.getString("image"));
        }

        return item;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (callBack != null)
            callBack.configLoaded(facedException);
    }

    public int getDrawableByName(String name){
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        return resourceId;
    }

    public interface CallBack {
        void configLoaded(boolean success);
    }

    public void saveJSONToCache(String json){
        try{
            ObjectOutput out = null;
            out = new ObjectOutputStream(new FileOutputStream(
                    new File(context.getCacheDir(), "") + CACHE_FILE));
            out.writeObject(json);
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private JSONArray getJSONFromCache(){
        try{
            ObjectInputStream in = null;
            File cacheFile = new File(new File(context.getCacheDir(), "") + CACHE_FILE);
            in = new ObjectInputStream(new FileInputStream(cacheFile));
            String jsonArrayRaw = (String) in.readObject();
            in.close();

            if(cacheFile.lastModified() + MAX_FILE_AGE > System.currentTimeMillis())
                return new JSONArray(jsonArrayRaw);
            else return null;
        } catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }

        return null;
    }
}
