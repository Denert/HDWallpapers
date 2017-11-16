package com.example.varia.wallpapers.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.varia.wallpapers.MainActivity;
import com.example.varia.wallpapers.R;
import com.example.varia.wallpapers.inherit.PermissionsFragment;
import com.example.varia.wallpapers.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by varia on 14.11.2017.
 */

public class WallpapersFragment extends Fragment implements PermissionsFragment {

    ArrayList<TumblrItem> tumblrItems;
    private ImageAdapter imageAdapter = null;

    Activity mActivity;

    private GridView gridView;
    private LinearLayout linearLayout;

    RelativeLayout pDialog;

    String perpage = "25";
    Integer curpage = 0;
    Integer total_posts;

    String baseUrl;

    Boolean initialLoad = true;
    Boolean isLoading = true;

    SwipeRefreshLayout mSwipeRefreshLayout;

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_wallpapers, container, false);
        setHasOptionsMenu(true);

        String username = this.getArguments().getStringArray(MainActivity.FRAGMENT_DATA)[0];
        baseUrl = "https://"+username+".tumblr.com/api/read/json?type=photo&num=" + perpage + "&start=";

        gridView = (GridView) linearLayout.findViewById(R.id.gridview);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startImagePagerActivity(i);
            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {}

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (imageAdapter == null)
                    return;
                if (imageAdapter.getCount() == 0)
                    return;

                int l = i1 + i;
                if (l >= i2 && !isLoading && (curpage * Integer.parseInt(perpage)) <= total_posts){
                    isLoading = true;
                    new InitialLoadGridView().execute(baseUrl);
                }
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) linearLayout.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_green_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoading){
                    initialLoad = true;
                    isLoading = true;
                    curpage = 1;
                    tumblrItems.clear();
                    gridView.setAdapter(null);
                    new InitialLoadGridView().execute(baseUrl);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 4000);
            }
        });

        return linearLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();

        new InitialLoadGridView().execute(baseUrl);
    }

    private void startImagePagerActivity(int position){
        Intent intent = new Intent(mActivity, TumblrPagerActivity.class);

        ArrayList<TumblrItem> underlying = new ArrayList<>();
        for (int i = 0; i < imageAdapter.getCount(); i++)
            underlying.add(imageAdapter.getItem(i));

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.Extra.IMAGES, underlying);
        intent.putExtras(bundle);
        intent.putExtra(Constants.Extra.IMAGE_POSITION, position);
        startActivity(intent);
    }

    public void updateList() {
        if (initialLoad){
            imageAdapter = new ImageAdapter(mActivity, 0, tumblrItems);
            gridView.setAdapter(imageAdapter);
            initialLoad = false;
        } else {
            imageAdapter.addAll(tumblrItems);
            imageAdapter.notifyDataSetChanged();
        }
        isLoading = false;
    }

    @Override
    public String[] requiredPermissions() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
        };
    }

    private class InitialLoadGridView extends AsyncTask<String, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (initialLoad)
                pDialog = (RelativeLayout) linearLayout.findViewById(R.id.progressBarHolder);
        }

        @Override
        protected Void doInBackground(String... strings) {

            String getUrl = strings[0];
            getUrl = getUrl + Integer.toString((curpage) * Integer.parseInt(perpage));
            curpage++;

            String jsonString = Helper.getDataFromUrl(getUrl);
            System.out.println("Return: " + jsonString);
            JSONObject json = null;

            try{
                jsonString = jsonString.replace("var tumblr_api_read = ", "");
                json = new JSONObject(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<TumblrItem> images = new ArrayList<>();

            try{
                String success = json.getString("posts-total");
                total_posts = Integer.parseInt(success);

                if (0 < Integer.parseInt(success)){
                    JSONArray products = json.getJSONArray("posts");

                    for(int i = 0; i < products.length(); i++){
                        JSONObject c = products.getJSONObject(i);

                        String id = c.getString("id");
                        String link = c.getString("url");
                        String url;
                        try{
                            url = c.getString("photo-url-1280");
                        } catch (JSONException e){
                            try {
                                url = c.getString("photo-url-500");
                            } catch (JSONException ex){
                                try{
                                    url = c.getString("photo-url-250");
                                } catch (JSONException l){
                                    url = null;
                                }
                            }
                        }

                        if (url != null){
                            TumblrItem item = new TumblrItem(id, link, url);
                            images.add(item);
                        }
                    }

                    tumblrItems = images;
                } else {
                    Log.v("INFO", "No items found");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(tumblrItems != null)
                updateList();
            if(pDialog.getVisibility() == View.VISIBLE){
                pDialog.setVisibility(View.GONE);
                Helper.revealView(gridView, linearLayout);
            }
        }
    }
}
