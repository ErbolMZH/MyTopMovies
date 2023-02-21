package com.erbol.mymovies.adapters;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erbol.mymovies.R;
import com.erbol.mymovies.data.Reviews;

import java.sql.Array;
import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private ArrayList<Reviews> reviews;
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent,false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Reviews review=reviews.get(position);
        holder.textViewContent.setText(review.getContent());
        holder.textViewAuthor.setText(review.getAuthor());

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setReviews(ArrayList<Reviews> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    class  ReviewViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewAuthor;
        private TextView textViewContent;
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAuthor=itemView.findViewById(R.id.textViewAuthor);
            textViewContent=itemView.findViewById(R.id.textViewContent);


        }
    }
}
