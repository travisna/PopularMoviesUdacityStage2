package com.example.android.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by TRAVIS'S COMP on 31/07/2017.
 */

public class FavoritesMovieDbHelper extends SQLiteOpenHelper {

    private static final String TAG = FavoritesMovieDbHelper.class.getSimpleName();
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "favorites_movie.db";

    private static final String CREATE_TABLE_FAVORITES = "create table " + FavoritesMovieContract.FavoritesMoviesColumn.TABLE_FAVORITES
            + "(" + FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_ID + " integer primary key, "
            + FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_TITLE + " text not null,"
            + FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_POSTER + " text, "
            + FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_OVERVIEW + " text, "
            + FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_YEAR + " text, "
            + FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_RATING + " double, "
            + FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_TRAILERS + " text, "
            + FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_REVIEWS + " text );";

    private static final String DB_SCHEMA = CREATE_TABLE_FAVORITES;

    private SQLiteDatabase mDB;

    public FavoritesMovieDbHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.mDB = getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG,"FavoritesDbHelper onCreate");
        db.execSQL(DB_SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "Upgrading database. Existing contents will be lost.[" + oldVersion + "]->[" + newVersion + "]");
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesMovieContract.FavoritesMoviesColumn.TABLE_FAVORITES);
        onCreate(db);
    }
}
