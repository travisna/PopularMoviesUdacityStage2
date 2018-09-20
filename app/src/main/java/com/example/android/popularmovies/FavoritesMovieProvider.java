package com.example.android.popularmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.sql.SQLException;

/**
 * Created by TRAVIS'S COMP on 31/07/2017.
 */

public class FavoritesMovieProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.popularmovies.favoritesmovieprovider";
    static final String URL = "content://" + PROVIDER_NAME + "/favorites";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final int FAVORITES = 10;
    static final int FAVORITES_ID = 11;
    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"favorites",FAVORITES);
        uriMatcher.addURI(PROVIDER_NAME, "favorites/#", FAVORITES_ID);
    }

    private SQLiteDatabase favoriteDB;


    @Override
    public boolean onCreate() {
        Context context = getContext();
        FavoritesMovieDbHelper dbHelper = new FavoritesMovieDbHelper(context);
        favoriteDB = dbHelper.getWritableDatabase();
        return (favoriteDB == null) ? false : true;
    }


    @Override
    public Cursor query(Uri uri,  String[] projection, String selection,String[] selectionArgs,  String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(FavoritesMovieContract.FavoritesMoviesColumn.TABLE_FAVORITES);

        switch (uriMatcher.match(uri)){
            case FAVORITES_ID:
                qb.appendWhere(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_ID + "=" + uri.getLastPathSegment());
                break;
        }

        Cursor cursor = qb.query(
                favoriteDB,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.setNotificationUri(getContext().getContentResolver(),CONTENT_URI);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert( Uri uri, ContentValues values) {
        long rowID = favoriteDB.insert(FavoritesMovieContract.FavoritesMoviesColumn.TABLE_FAVORITES, "", values);

        if(rowID>0){
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        try {
            throw new SQLException("Failed to add new row into "+uri);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int delete( Uri uri,  String selection, String[] selectionArgs) {
        int count=0;

        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                count = favoriteDB.delete(FavoritesMovieContract.FavoritesMoviesColumn.TABLE_FAVORITES, selection, selectionArgs);
                break;
            case FAVORITES_ID:
                String id = uri.getPathSegments().get(1);
                count = favoriteDB.delete(FavoritesMovieContract.FavoritesMoviesColumn.TABLE_FAVORITES,FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update( Uri uri,  ContentValues values,  String selection, String[] selectionArgs) {
        return 0;
    }
}
