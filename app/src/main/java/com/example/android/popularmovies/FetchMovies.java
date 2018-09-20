package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by KOMP on 04/07/2017.
 */

public class FetchMovies extends AsyncTask<String,Void,String> {

    @Override
    protected String doInBackground(String... strings) {


        if (strings.length==0)
            return  null;

        String sortOrder = strings[0];


        Uri builtUri = Uri.parse(APIHelper.api_url+sortOrder).buildUpon()
                .appendQueryParameter("api_key",APIHelper.key)
                .build();

        String response;
        try {
            response = getJson(builtUri);
            return response;
        }
        catch (Exception e){
            /*MainActivity.toast.setText("Connection Error");
            MainActivity.toast.setDuration(Toast.LENGTH_SHORT);
            MainActivity.toast.show();*/

            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(String s) {
        if(s!=null){
            loadInfo(s);
        }
        else {
            try {
                MainActivity.toast.setText("No Internet Connection");
                MainActivity.toast.setDuration(Toast.LENGTH_SHORT);
                MainActivity.toast.show();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public static void loadInfo(String jsonString){
        MainActivity.imagesArrayList.clear();
        MainActivity.moviesArrayList.clear();
        //MainActivity.gridView.setAdapter(null);

        try{
            if(jsonString!=null){
                JSONObject moviesJsonObject= new JSONObject(jsonString);
                JSONArray moviesJsonArray = moviesJsonObject.getJSONArray("results");

                for(int i=0;i<moviesJsonArray.length();i++){
                    JSONObject movies= moviesJsonArray.getJSONObject(i);
                    Movies moviesObject= new Movies();

                    moviesObject.setTitle(movies.getString("title"));
                    moviesObject.setId(movies.getInt("id"));
                    moviesObject.setBackdropPath(movies.getString("backdrop_path"));
                    moviesObject.setOriginal_title(movies.getString("original_title"));
                    moviesObject.setVote_average(movies.getString("vote_average"));
                    moviesObject.setImagePath(movies.getString("poster_path"));


                    if(movies.getString("overview")=="null"){
                        moviesObject.setOverview("There is no overview yet.");
                    }
                    else{
                        moviesObject.setOverview(movies.getString("overview"));
                    }

                    if(movies.getString("release_date")=="null"){
                        moviesObject.setRelease_date("Release date is not yet known");
                    }
                    else{
                        moviesObject.setRelease_date(movies.getString("release_date"));
                    }

                    if(movies.getString("poster_path")=="null"){
                        MainActivity.imagesArrayList.add(APIHelper.image_not_found);
                        moviesObject.setImagePath(APIHelper.image_not_found);
                    }
                    else{
                        MainActivity.imagesArrayList.add(APIHelper.image_url+ APIHelper.image_size+movies.getString("poster_path"));
                    }

                    MainActivity.moviesArrayList.add(moviesObject);
                    MainActivity.imageAdapter.notifyDataSetChanged();

                }
                //MainActivity.gridView.setAdapter(MainActivity.imageAdapter);

            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static String getJson(Uri builtUri){
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
        }catch (IOException e){
            e.printStackTrace();
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

        return moviesJson;
    }
}
