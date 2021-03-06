package net.crescenthikari.popmovies.data.repository;

import net.crescenthikari.popmovies.data.model.Movie;
import net.crescenthikari.popmovies.data.model.MovieDetail;
import net.crescenthikari.popmovies.data.model.MovieReview;
import net.crescenthikari.popmovies.data.model.MovieVideo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by Muhammad Fiqri Muthohar on 7/24/17.
 */

public interface MovieRepository {
    Observable<MovieDetail> getMovieDetail(String movieId);

    Observable<List<Movie>> getNowPlayingMovies(int page);

    Observable<List<Movie>> getMostPopularMovies(int page);

    Observable<List<Movie>> getHighestRatedMovies(int page);

    Observable<List<MovieVideo>> getMovieVideos(String movieId);

    Observable<List<MovieReview>> getMovieReviews(String movieId, int page);

    Observable<List<Movie>> getFavoriteMovies();

    Completable addFavoriteMovie(Movie movie);

    Completable removeFavoriteMovie(String movieId);

    Single<Movie> getFavoriteMovie(String movieId);
}
