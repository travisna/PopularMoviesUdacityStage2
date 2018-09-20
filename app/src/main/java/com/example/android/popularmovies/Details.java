package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Details extends AppCompatActivity {

    public static Movies movies;
    public static Intent intent;
    public static TextView title,year,rate,overview;
    public static ImageView image;

    public static List<Trailers> trailerData;
    public static List<Reviews> reviewData;
    public static LinearLayout trailersListView;
    public static LinearLayout reviewsListView;
    public static Button favoriteButton;

    private static JSONObject trailerObj;
    private static JSONObject reviewObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment,new PlaceHolderFragment())
                    .commit();
        }

    }





    public static class PlaceHolderFragment extends Fragment{
        public PlaceHolderFragment(){}


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
            View rootView=inflater.inflate(R.layout.details_fragment,container,false);
            WindowManager windowManager= (WindowManager) rootView.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            initComponents(rootView);
            setValues(rootView);
            return rootView;
        }

        public void initComponents (View rootView){
            movies = new Movies();
            Details.intent = getActivity().getIntent();
            int id = intent.getIntExtra("movie_id",0);
            int position = intent.getIntExtra("movie_position",0);
            movies = MainActivity.moviesArrayList.get(position);
            title = (TextView) rootView.findViewById(R.id.movies_title);
            year = (TextView) rootView.findViewById(R.id.movies_year);
            rate = (TextView) rootView.findViewById(R.id.movies_rating);
            image = (ImageView) rootView.findViewById(R.id.movies_images);
            overview = (TextView) rootView.findViewById(R.id.movies_overview);
            favoriteButton = (Button) rootView.findViewById(R.id.buttonFavorite);
            trailersListView = (LinearLayout) rootView.findViewById(R.id.trailer_list);
            reviewsListView = (LinearLayout) rootView.findViewById(R.id.review_list);

            String sortOrder = intent.getStringExtra("sortOrder");

            if(sortOrder.equals(getString(R.string.pref_sort_favorite)))
                fetchFavoritesMovies(id);
            else
                fetchMoviesData(id);
        }

        public boolean isInternetAvailable() {
            try {
                InetAddress ipAddr = InetAddress.getByName("google.com");
                return !ipAddr.equals("");

            } catch (Exception e) {
                return false;
            }

        }

        public void setValues(View rootView){
            final int position = intent.getIntExtra("movie_position",0);

            title.setText(movies.getOriginal_title());
            year.setText(movies.getRelease_date().substring(0,4));
            title.setVisibility(View.VISIBLE);
            rate.setText(movies.getVote_average()+"/10");
            overview.setText(movies.getOverview());

            final String movie_images_url;
            if(movies.getImagePath()== APIHelper.image_not_found){
                movie_images_url= APIHelper.image_not_found;

            }
            else {
                movie_images_url = APIHelper.image_url + APIHelper.image_size + "/" + movies.getImagePath();
            }

            Picasso.with(rootView.getContext())
                    .load(movie_images_url)
                    .error(R.drawable.error_loading)
                    .into(image);
            image.setVisibility(View.VISIBLE);


            Uri movieUri = Uri.parse(FavoritesMovieProvider.CONTENT_URI + "/" + movies.getId());
            String[] projection = new String[] {FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_ID};
            final boolean isFavorite = isMovieFavorite(movieUri,projection);
            if(isFavorite)
                favoriteButton.setText(R.string.remove_favorite_label);
            else
                favoriteButton.setText(R.string.mark_favorite_label);
            favoriteButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    if(!isFavorite){
                        ContentValues cv = new ContentValues();
                        cv.put(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_ID,movies.getId());
                        cv.put(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_OVERVIEW,movies.getOverview());
                        cv.put(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_POSTER,movies.getImagePath());
                        cv.put(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_RATING,movies.getVote_average());
                        cv.put(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_TITLE,movies.getOriginal_title());
                        cv.put(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_YEAR,movies.getRelease_date());

                        if(trailerObj.length()>0){
                            cv.put(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_TRAILERS,trailerObj.toString());
                        }
                        else
                            cv.put(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_TRAILERS,"");

                        if(reviewObj.length()>0){
                            cv.put(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_REVIEWS,reviewObj.toString());
                        }
                        else
                            cv.put(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_REVIEWS,"");

                        Uri uri = getActivity().getContentResolver().insert(FavoritesMovieProvider.CONTENT_URI,cv);

                        if(uri!=null){
                            favoriteButton.setText(R.string.remove_favorite_label);
                        }

                    }
                    else {
                        favoriteButton.setText(R.string.mark_favorite_label);
                        Uri deleteUri = Uri.parse(FavoritesMovieProvider.CONTENT_URI + "/" + movies.getId());
                        int count = getActivity().getContentResolver().delete(deleteUri,null,null);

                        MainActivity.imageAdapter.remove(position);
                        MainActivity.imageAdapter.notifyDataSetChanged();
                    }
                }
            });
        }


        private boolean isMovieFavorite(Uri uri, String[] projection){
            Cursor cursor = getActivity().getContentResolver().query(uri,projection,null,null,null);
            if(cursor.getCount()==0)
                return false;
            else
                return true;
        }


        public void populateTrailersList(final List<Trailers> trailer){
            for(final Trailers trailer_ : trailer){
                View view = getActivity().getLayoutInflater().inflate(R.layout.trailer_list,null);
                TextView textViewTrailer = (TextView) view.findViewById(R.id.trailer_list_title);
                textViewTrailer.setText(trailer_.getName());

                view.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        String url = trailer_.getKey();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });

                ImageButton shareButton = (ImageButton) view.findViewById(R.id.shareButton);

                shareButton.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        String url = trailer_.getKey();
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_SUBJECT, "Trailer");
                        share.putExtra(Intent.EXTRA_TEXT,trailer_.getKey());
                        startActivity(Intent.createChooser(share, "Share link!"));
                    }
                });
                trailersListView.addView(view);
            }
            return;
        }

        public void populateReviewList(final List<Reviews> reviewsList ){
            for (final Reviews review : reviewsList) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.review_list, null);
                TextView author = (TextView) view.findViewById(R.id.reviewerTextView);
                author.setText("Review by : "+review.getReviewer());
                TextView content = (TextView) view.findViewById(R.id.review_content);
                content.setText(review.getReviewContent());
                reviewsListView.addView(view);
            }
            return;
        }

        public void fetchFavoritesMovies(int movieId){
            String[] projection = new String[]{FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_ID,
                    FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_POSTER,
                    FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_OVERVIEW,
                    FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_RATING,
                    FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_YEAR,
                    FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_TITLE,
                    FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_REVIEWS,
                    FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_TRAILERS
            };

            Uri uri = Uri.parse(FavoritesMovieProvider.CONTENT_URI+"/"+movieId);
            Cursor cursor = getActivity().getContentResolver().query(
                    uri,
                    projection,
                    null,
                    null,
                    null);

            if(cursor!=null&&cursor.moveToFirst()) {
                cursor.moveToFirst();

                movies = new Movies();
                movies.setId(movieId);
                movies.setOriginal_title(cursor.getString(cursor.getColumnIndex(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_TITLE)));
                movies.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_POSTER)));
                movies.setOverview(cursor.getString(cursor.getColumnIndex(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_OVERVIEW)));
                movies.setRelease_date(cursor.getString(cursor.getColumnIndex(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_YEAR)));
                movies.setVote_average(cursor.getString(cursor.getColumnIndex(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_RATING)));

                try {
                    JSONObject movieTrailer = new JSONObject(cursor.getString(cursor.getColumnIndex(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_TRAILERS)));
                    JSONObject movieReview = new JSONObject(cursor.getString(cursor.getColumnIndex(FavoritesMovieContract.FavoritesMoviesColumn.COL_MOVIE_REVIEWS)));

                    parseTrailerObj(movieTrailer);
                    populateTrailersList(trailerData);

                    JSONArray reviewArray = movieReview.getJSONArray("results");
                    reviewData = new ArrayList<Reviews>();
                    for (int i = 0; i < reviewArray.length(); i++) {
                        JSONObject reviewsObj = reviewArray.getJSONObject(i);

                        String reviewer = reviewsObj.getString("author");
                        String content = reviewsObj.getString("content");
                        Reviews reviews = new Reviews();
                        reviews.setReviewer(reviewer);
                        reviews.setReviewContent(content);

                        reviewData.add(reviews);
                    }
                    populateReviewList(reviewData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cursor.close();
            }
        }


        public void fetchMoviesData(int movieId){
            FetchTrailer fetchTrailer = new FetchTrailer();
            fetchTrailer.execute(movieId);

            FetchReviews fetchReviews = new FetchReviews();
            fetchReviews.execute(movieId);
        }

        public void parseTrailerObj(JSONObject obj){
            trailerData = new ArrayList<Trailers>();
            try{
                JSONArray trailersResult = obj.getJSONArray("results");

                for(int i = 0 ; i < trailersResult.length() ; i++){
                    JSONObject trailerDetails = trailersResult.getJSONObject(i);
                    String trailerUri = "http://www.youtube.com/watch?v="+trailerDetails.getString("key");
                    Trailers trailers = new Trailers();

                    trailers.setKey(trailerUri);
                    trailers.setName(trailerDetails.getString("name"));

                    trailerData.add(trailers);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        public class FetchTrailer extends AsyncTask<Integer,Void,JSONObject> {
            @Override
            protected JSONObject doInBackground(Integer... params) {
                Uri builtUri = Uri.parse(APIHelper.api_url+ params[0] + "/videos").buildUpon()
                        .appendQueryParameter("api_key",APIHelper.key)
                        .build();

                JSONObject jsonObject = getJson(builtUri);
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                if (jsonObject != null) {
                    trailerObj = jsonObject;
                    parseTrailerObj(jsonObject);

                    populateTrailersList(trailerData);
                }
            }
        }

        public class FetchReviews extends AsyncTask<Integer,Void,JSONObject>{

            @Override
            protected JSONObject doInBackground(Integer... params) {
                Uri builtUri = Uri.parse(APIHelper.api_url+ params[0] + "/reviews").buildUpon()
                        .appendQueryParameter("api_key",APIHelper.key)
                        .build();

                JSONObject jsonObject = getJson(builtUri);
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                reviewObj = jsonObject;

                if(jsonObject!=null){
                    reviewData = new ArrayList<Reviews>();

                    try{
                        JSONArray reviewsArray = jsonObject.getJSONArray("results");
                        for(int i = 0 ; i < reviewsArray.length() ; i++){
                            JSONObject reviewsObj = reviewsArray.getJSONObject(i);

                            String reviewer = reviewsObj.getString("author");
                            String content = reviewsObj.getString("content");
                            Reviews reviews = new Reviews();
                            reviews.setReviewer(reviewer);
                            reviews.setReviewContent(content);

                            reviewData.add(reviews);
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    populateReviewList(reviewData);
                }
            }
        }


        //////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////
        public static JSONObject getJson(Uri builtUri){
        InputStream inputStream;
        StringBuffer stringBuffer;
        HttpURLConnection urlConnection=null;
        BufferedReader bufferedReader=null;
        String moviesJson=null;

        try{
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            stringBuffer = new StringBuffer();

            if(inputStream == null){
                return null;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line= bufferedReader.readLine())!=null){
                stringBuffer.append(line+"\n");
            }

            if(stringBuffer.length()==0){
                return null;
            }

            moviesJson=stringBuffer.toString();
            JSONObject jsonObject = new JSONObject(moviesJson);
            return jsonObject;

        }catch (IOException | JSONException e){
            e.printStackTrace();
            return null;
        }finally {
            if (urlConnection!=null)
                urlConnection.disconnect();

            if (bufferedReader!=null){
                try{
                    bufferedReader.close();
                }catch (final IOException e){

                }
            }
        }
    }

    }


}
