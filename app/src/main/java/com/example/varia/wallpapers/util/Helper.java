package com.example.varia.wallpapers.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by varia on 03.11.2017.
 */

public class Helper {
    public static boolean isOnline(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        return ni != null && ni.isConnected();
    }

    public static boolean isOnlineShowDialog(Activity activity){
        if (isOnline(activity))
            return true;
        else return false;
    }

    @SuppressLint("NewApi")
    public static void setStatusBarColor(Activity mActivity, int color){
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                mActivity.getWindow().setStatusBarColor(color);
        } catch (Exception e){}
    }

    public static String loadJsonFromAsset(Context context, String name){
        String json = null;
        try{
            InputStream inputStream = context.getAssets().open(name);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return json;
    }

    public static String getDataFromUrl(String url){
        StringBuffer chaine = new StringBuffer("");

        try{
            URL urlConnection = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
            connection.setRequestProperty("User-Agent", "Android");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int status = connection.getResponseCode();
            if ((status != HttpURLConnection.HTTP_OK) && (status == HttpURLConnection.HTTP_MOVED_TEMP)
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER){

                String newUrl = connection.getHeaderField("Location");
                String cookies = connection.getHeaderField("Set-Cookie");

                connection = (HttpURLConnection) new URL(newUrl).openConnection();
                connection.setRequestProperty("Cookie", cookies);
                connection.setRequestProperty("User-Agent", "Android");
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                System.out.println("Redirect to URL : " + newUrl);
            }

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                chaine.append(line);
            }
        } catch (IOException e){}

        return chaine.toString();
    }
}
