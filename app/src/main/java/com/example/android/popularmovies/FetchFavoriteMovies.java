package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by TRAVIS'S COMP on 03/08/2017.
 */

public class FetchFavoriteMovies extends AsyncTask<Void,Void,ArrayList<Movies>> {
    private final ContentResolver contentResolver;

    public FetchFavoriteMovies(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }


    @Override
    protected ArrayList<Movies> doInBackground(Void... params) {
        MainActivity.imagesArrayList.clear();
        MainActivity.moviesArrayList.clear();
        ArrayList<Movies> moviesList = new ArrayList<>();

        String[] projection = new String[]{FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_ID,
                FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_POSTER};
        final Cursor cursor = contentResolver.query(
                FavoritesMovieProvider.CONTENT_URI,
                projection,
                null,
                null,
                null);
        if(cursor.getCount()!=0){
            while (cursor.moveToNext()){
                Movies movies = new Movies();
                movies.setId(cursor.getInt(0));
                movies.setImagePath(APIHelper.image_url+ APIHelper.image_size+cursor.getString(1));

                moviesList.add(movies);
            }
        }
        cursor.close();
        return moviesList;
    }

    @Override
    protected void onPostExecute(ArrayList<Movies> movies) {
        super.onPostExecute(movies);
        if(movies!=null){
            for(Movies res : movies){
                MainActivity.moviesArrayList.add(res);
                MainActivity.imagesArrayList.add(res.getImagePath());
            }
            MainActivity.imageAdapter.notifyDataSetChanged();

        }
    }
}

