package net.crescenthikari.popmovies.data.cache;

import net.crescenthikari.popmovies.data.model.Movie;
import net.crescenthikari.popmovies.data.model.MovieDetail;

import java.util.List;

/**
 * Created by Muhammad Fiqri Muthohar on 7/24/17.
 */

public interface MoviesCache {
    MovieDetail getMovieDetail(String movieId);

    void putMovieDetail(String movieId, MovieDetail detail);

    List<Movie> getNowPlayingMovies(int page);

    void putNowPlayingMovies(int page, List<Movie> movies);

    List<Movie> getHighestRatedMovies(int page);

    void putHighestRatedMovies(int page, List<Movie> movies);

    List<Movie> getMostPopularMovies(int page);

    void putMostPopularMovies(int page, List<Movie> movies);
}
