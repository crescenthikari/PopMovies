package net.crescenthikari.popmovies.data.repository;

import net.crescenthikari.popmovies.data.model.Movie;
import net.crescenthikari.popmovies.data.model.MovieDetail;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Muhammad Fiqri Muthohar on 7/24/17.
 */

public interface MovieRepository {
    Observable<MovieDetail> getMovieDetail(String movieId);

    Observable<List<Movie>> getNowPlayingMovies(int page);

    Observable<List<Movie>> getMostPopularMovies(int page);

    Observable<List<Movie>> getHighestRatedMovies(int page);
}