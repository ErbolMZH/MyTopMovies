package com.erbol.mymovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.erbol.mymovies.adapters.MovieAdapter;
import com.erbol.mymovies.data.MainViewModel;
import com.erbol.mymovies.data.Movie;
import com.erbol.mymovies.utils.JSONUtils;
import com.erbol.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {
    private Switch switchSort;
    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private TextView textViewTopRated;
    private TextView textViewPopularity;
    private MainViewModel viewModel;
    private ProgressBar progressBarLoading;
    private static final int LOADER_ID=133;
    private LoaderManager loaderManager;
    private static boolean isLoading =false;

    private static int page=1;
    private static int methodOfSort;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent intentToFavourite= new Intent(this, FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loaderManager=LoaderManager.getInstance(this);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        recyclerViewPosters=findViewById(R.id.recyclerViewPosters);
        progressBarLoading=findViewById(R.id.progressBarLoading);
        switchSort=findViewById(R.id.switchSort);
        textViewPopularity=findViewById(R.id.textViewPopularity);
        textViewTopRated=findViewById(R.id.textViewTopRated);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this,2));
        movieAdapter= new MovieAdapter();
//        JSONObject jsonObject=NetworkUtils.getJSONFromNetwork(NetworkUtils.POPULARITY,1);
//        ArrayList<Movie>movies= JSONUtils.getMoviesFromJSON(jsonObject);
//        movieAdapter.setMovies(movies);
        recyclerViewPosters.setAdapter(movieAdapter);
        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                page=1;
              setMethodOfSort(isChecked);
            }
        });
        switchSort.setChecked(false);
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent= new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("id",movie.getId());
                startActivity(intent);
            }
        });
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if(!isLoading) {
                    downloadData(methodOfSort,page);
                }
            }
        });

        LiveData<List<Movie>> moviesFromLiveData= viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if(page==1){
                    movieAdapter.setMovies(movies);
                }
            }
        });
    }

    public void onClickSetPopularity(View view) {
        setMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        setMethodOfSort(true);
        switchSort.setChecked(true);
    }
    private void setMethodOfSort(boolean isTopRated){

        if (isTopRated){
            textViewTopRated.setTextColor(getResources().getColor(R.color.pink));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white));
            methodOfSort= NetworkUtils.TOP_RATE;
        }else {
            methodOfSort= NetworkUtils.POPULARITY;
            textViewPopularity.setTextColor(getResources().getColor(R.color.pink));
            textViewTopRated.setTextColor(getResources().getColor(R.color.white));

        }
        downloadData(methodOfSort,page);
    }
    private void downloadData(int methodOfSort,int page){
        URL url =NetworkUtils.buildURL(methodOfSort,page);
        Bundle bundle= new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID,bundle,this);


    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        NetworkUtils.JSONLoader jsonLoader= new NetworkUtils.JSONLoader(this, args);
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.onStartLoadingListener() {
            @Override
            public void onStartLoading() {
                progressBarLoading.setVisibility(View.VISIBLE);
                isLoading=true;
            }
        });

        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
        ArrayList<Movie>movies= JSONUtils.getMoviesFromJSON(data);
        if(movies !=null && !movies.isEmpty()){
            if(page==1) {
                viewModel.deleteAllMovies();
                movieAdapter.clear();
            }
            for(Movie movie:movies){
                viewModel.insertMovie(movie);

            }
            movieAdapter.addMovies(movies);
            page++;
        }
        isLoading=false;
        progressBarLoading.setVisibility(View.INVISIBLE);
        loaderManager.destroyLoader(LOADER_ID);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}