package net.crescenthikari.popmovies.features.moviedetail.contract;

import net.crescenthikari.popmovies.data.model.MovieVideo;

/**
 * Created by Muhammad Fiqri Muthohar on 7/31/17.
 */

public interface VideoOnClickCallback {
    void openVideo(MovieVideo video);
}
