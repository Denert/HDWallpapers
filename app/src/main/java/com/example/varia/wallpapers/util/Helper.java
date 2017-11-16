package com.example.varia.wallpapers.util;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.example.varia.wallpapers.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


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
        } catch (IOException e){
            e.printStackTrace();
        }

        return chaine.toString();
    }

    public static void admobLoader(Context c, Resources res, View AdmobView){
        String adId = res.getString(R.string.admob_banner_id);
        if (!adId.equals("")){
            AdView adView = (AdView) AdmobView;
            adView.setVisibility(View.VISIBLE);

            AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
            adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            adView.loadAd(adRequestBuilder.build());
        }
    }

    @SuppressLint("NewApi")
    public static void revealView(View toBeRevealed, View frame){
        if (ViewCompat.isAttachedToWindow(toBeRevealed)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                int cx = (frame.getLeft() + frame.getRight()) / 2;
                int cy = (frame.getTop() + frame.getBottom()) / 2;

                int finalRadius = Math.max(frame.getWidth(), frame.getHeight());

                Animator anim = ViewAnimationUtils.createCircularReveal(toBeRevealed,
                        cx, cy, 0, finalRadius);

                toBeRevealed.setVisibility(View.VISIBLE);
                anim.start();
            } else {
                toBeRevealed.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void updateAndroidSecurityProvider(Activity callingActivity){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            try{
                ProviderInstaller.installIfNeeded(callingActivity);
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
            }
        }
    }
}
