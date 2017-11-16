package com.example.varia.wallpapers.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.varia.wallpapers.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by varia on 14.11.2017.
 */

public class ImageAdapter extends ArrayAdapter<TumblrItem> {
    private ArrayList<TumblrItem> listData;
    private LayoutInflater layoutInflater;
    private Context mContext;

    public ImageAdapter(Context context, Integer someInt, ArrayList<TumblrItem> listData){
        super(context, someInt, listData);
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Nullable
    @Override
    public TumblrItem getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        View view = convertView;

        if (view == null){
            view = layoutInflater.inflate(R.layout.fragment_row, parent, false);
            holder = new ViewHolder();
            assert view != null;
            holder.imageView = (ImageView) view.findViewById(R.id.image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Picasso.with(mContext).load(listData.get(position).getUrl())
                .placeholder(R.drawable.placeholder).fit().centerCrop().into(holder.imageView);

        return view;
    }

    static class ViewHolder{
        ImageView imageView;
    }
}
