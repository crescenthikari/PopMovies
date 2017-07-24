package net.crescenthikari.popmovies.data;

import net.crescenthikari.popmovies.data.repository.MovieRepository;

/**
 * Created by Muhammad Fiqri Muthohar on 7/24/17.
 */

public interface DataManager {
    MovieRepository getMovieRepository();
}
