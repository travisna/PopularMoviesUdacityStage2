package com.example.android.popularmovies;

import android.provider.BaseColumns;

/**
 * Created by TRAVIS'S COMP on 31/07/2017.
 */

public class FavoritesMovieContract {

    private FavoritesMovieContract() {}

    public static abstract class FavoritesMoviesColumn implements BaseColumns{
        public static final String TABLE_FAVORITES = "favorites_table";
        public static final String COL_MOVIE_ID = "movie_id";
        public static final String COL_MOVIE_TITLE = "movie_title";
        public static final String COL_MOVIE_OVERVIEW = "movie_overview";
        public static final String COL_MOVIE_YEAR = "movie_year";
        public static final String COL_MOVIE_RATING = "movie_rating";
        public static final String COL_MOVIE_TRAILERS = "movie_trailers";
        public static final String COL_MOVIE_REVIEWS = "movie_reviews";
        public static final String COL_MOVIE_POSTER = "movie_poster";
    }
}
