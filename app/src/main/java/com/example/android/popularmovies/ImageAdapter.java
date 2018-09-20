package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by KOMP on 04/07/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context mContext){
        this.mContext=mContext;
    }
    @Override
    public int getCount() {
        return MainActivity.imagesArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView images;

        if(view==null){
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            images = (ImageView) inflater.inflate(R.layout.movie_item,viewGroup,false);
        }
        else
            images= (ImageView) view;

        Picasso.with(mContext)
                .load(MainActivity.imagesArrayList.get(i))
                .placeholder(R.drawable.loading)
                .error(R.drawable.error_loading)
                .into(images);

        return images;
    }


    public void remove(int position){
        MainActivity.imagesArrayList.remove(position);
    }
}
