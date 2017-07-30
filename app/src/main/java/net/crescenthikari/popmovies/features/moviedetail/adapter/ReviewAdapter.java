package net.crescenthikari.popmovies.features.moviedetail.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.crescenthikari.popmovies.R;
import net.crescenthikari.popmovies.data.model.MovieReview;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Muhammad Fiqri Muthohar on 7/31/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    List<MovieReview> reviews;

    public ReviewAdapter() {
        reviews = new ArrayList<>();
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_review, null);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewHolder reviewHolder, int pos) {
        reviewHolder.bindViews(reviews.get(pos));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void clearData() {
        reviews.clear();
        notifyDataSetChanged();
    }

    public void addReviews(List<MovieReview> reviewList) {
        reviews.addAll(reviewList);
        notifyDataSetChanged();
    }

    public void clearThenAddReviews(List<MovieReview> reviewList) {
        reviews.clear();
        reviews.addAll(reviewList);
        notifyDataSetChanged();
    }

    class ReviewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.review_author)
        TextView authorView;

        @BindView(R.id.review_content)
        TextView contentView;

        ReviewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindViews(MovieReview review) {
            authorView.setText(review.getAuthor());
            contentView.setText(review.getContent());
        }
    }
}
