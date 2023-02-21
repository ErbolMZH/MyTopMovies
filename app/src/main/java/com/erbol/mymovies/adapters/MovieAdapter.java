package com.erbol.mymovies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erbol.mymovies.R;
import com.erbol.mymovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> movies;
    private OnPosterClickListener onPosterClickListener;
    private OnReachEndListener onReachEndListener;
    public MovieAdapter(){
        movies= new ArrayList<>();

    }
 public interface OnPosterClickListener{
        void onPosterClick(int position);

 }
 public interface OnReachEndListener{
        void onReachEnd();
 }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item,parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if(movies.size()>=20 && position==movies.size()-4 && onReachEndListener!=null){
            onReachEndListener.onReachEnd();
        }
        Movie movie=movies.get(position);
        Picasso.get().load(movie.getPosterPath()).into(holder.imageViewSmallPoster);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageViewSmallPoster;
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSmallPoster =itemView.findViewById(R.id.imageViewSmallPoster);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onPosterClickListener!=null){
                    onPosterClickListener.onPosterClick(getAdapterPosition());
                }
            }
        });
        }
    }
    public  void clear(){
        this.movies.clear();
        notifyDataSetChanged();
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
    public void addMovies(List<Movie>movies){
        this.movies.addAll(movies);
        notifyDataSetChanged();

    }

    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener) {
        this.onPosterClickListener = onPosterClickListener;
    }

    public void setOnReachEndListener(OnReachEndListener onReachEndListener) {
        this.onReachEndListener = onReachEndListener;
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
