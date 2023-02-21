package com.erbol.mymovies;

import static com.erbol.mymovies.R.*;
import static com.erbol.mymovies.R.string.add_to_favourite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.erbol.mymovies.adapters.ReviewAdapter;
import com.erbol.mymovies.adapters.TrailerAdapter;
import com.erbol.mymovies.data.FavouriteMovie;
import com.erbol.mymovies.data.MainViewModel;
import com.erbol.mymovies.data.Movie;
import com.erbol.mymovies.data.Reviews;
import com.erbol.mymovies.data.Trailer;
import com.erbol.mymovies.utils.JSONUtils;
import com.erbol.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private ImageView imageViewAddToFavourite;
    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;



    private  int id;
    private FavouriteMovie favouriteMovie;
    private Movie movie;
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
    private MainViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_detail);
        imageViewBigPoster=findViewById(R.id.imageViewBigPoster);
        textViewTitle=findViewById(R.id.textViewTitle);
        textViewOriginalTitle=findViewById(R.id.textViewOriginalTitle);
        textViewRating=findViewById(R.id.textViewRating);
        textViewReleaseDate=findViewById(R.id.textViewReleaseDate);
        textViewOverview=findViewById(R.id.textViewOverview);
        imageViewAddToFavourite =findViewById(R.id.imageViewAddToFavourite);
        Intent intent= getIntent();
        if(intent!=null && intent.hasExtra("id")){
            id = intent.getIntExtra("id",-1);

        }else {
            finish();
        }
        viewModel= ViewModelProviders.of(this).get(MainViewModel.class);
         movie=viewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewOverview.setText(movie.getOverview());
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        setFavouriteMovie();

        recyclerViewTrailers=findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews=findViewById(R.id.recyclerViewReviews);
        reviewAdapter = new ReviewAdapter();
        trailerAdapter= new TrailerAdapter();
        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);

            }
        });
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewTrailers.setAdapter(trailerAdapter);
        JSONObject jsonObjectTrailers= NetworkUtils.getJSONForVideos(movie.getId());
        JSONObject jsonObjectReviews= NetworkUtils.getJSONForReviews(movie.getId());
        ArrayList<Trailer> trailers= JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
        ArrayList<Reviews> reviews=JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        reviewAdapter.setReviews(reviews);
        trailerAdapter.setTrailers(trailers);




    }

    public void onClickChangeFavourite(View view) {

        if(favouriteMovie==null){
            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(this, add_to_favourite,Toast.LENGTH_SHORT).show();
        }else{
            viewModel.deleteFavouriteMovie(favouriteMovie);

            Toast.makeText(this, string.remove_from_favourite,Toast.LENGTH_SHORT).show();
        }
        setFavouriteMovie();
    }
    private void setFavouriteMovie(){
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if(favouriteMovie ==null){
            imageViewAddToFavourite.setImageResource(drawable.favourite_add_to);
        }else {
            imageViewAddToFavourite.setImageResource(drawable.favourite_remove);
        }
    }
}