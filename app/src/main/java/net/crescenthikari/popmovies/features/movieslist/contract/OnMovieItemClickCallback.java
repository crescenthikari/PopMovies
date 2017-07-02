package net.crescenthikari.popmovies.features.movieslist.contract;

import android.widget.ImageView;

import net.crescenthikari.popmovies.model.Movie;

/**
 * Created by Muhammad Fiqri Muthohar on 6/22/17.
 */

public interface OnMovieItemClickCallback {
    void onMovieItemClicked(ImageView posterView, Movie movie);
}
