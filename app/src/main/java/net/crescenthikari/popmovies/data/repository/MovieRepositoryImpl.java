package net.crescenthikari.popmovies.data.repository;

import net.crescenthikari.popmovies.data.api.TmdbApi;
import net.crescenthikari.popmovies.data.api.response.MovieCollectionResponse;
import net.crescenthikari.popmovies.data.api.response.MovieReviewResponse;
import net.crescenthikari.popmovies.data.api.response.MovieVideoResponse;
import net.crescenthikari.popmovies.data.cache.MoviesCache;
import net.crescenthikari.popmovies.data.model.Movie;
import net.crescenthikari.popmovies.data.model.MovieDetail;
import net.crescenthikari.popmovies.data.model.MovieReview;
import net.crescenthikari.popmovies.data.model.MovieVideo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by Muhammad Fiqri Muthohar on 7/24/17.
 */

public class MovieRepositoryImpl implements MovieRepository {

    private static final int DEFAULT_CACHE_DELAY = 350;

    private TmdbApi tmdbApi;
    private MoviesCache moviesCache;

    public MovieRepositoryImpl(TmdbApi tmdbApi, MoviesCache moviesCache) {
        this.tmdbApi = tmdbApi;
        this.moviesCache = moviesCache;
    }

    @Override
    public Observable<MovieDetail> getMovieDetail(final String movieId) {
        MovieDetail detail = moviesCache.getMovieDetail(movieId);
        if (detail != null) {
            return Observable.just(detail)
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return tmdbApi
                .getMovieDetail(movieId)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Response<MovieDetail>, ObservableSource<MovieDetail>>() {
                    @Override
                    public ObservableSource<MovieDetail> apply(@NonNull Response<MovieDetail> response) throws Exception {
                        moviesCache.putMovieDetail(movieId, response.body());
                        return Observable.just(response.body());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Movie>> getNowPlayingMovies(final int page) {
        List<Movie> movies = moviesCache.getNowPlayingMovies(page);
        if (movies != null) {
            return Observable.just(movies)
                    .delay(DEFAULT_CACHE_DELAY, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return tmdbApi
                .getNowPlayingMovies(page)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Response<MovieCollectionResponse>, ObservableSource<List<Movie>>>() {
                    @Override
                    public ObservableSource<List<Movie>> apply(@NonNull Response<MovieCollectionResponse> response) throws Exception {
                        moviesCache.putNowPlayingMovies(page, response.body().getMovies());
                        return Observable.just(response.body().getMovies());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Movie>> getMostPopularMovies(final int page) {
        List<Movie> movies = moviesCache.getMostPopularMovies(page);
        if (movies != null) {
            return Observable.just(movies)
                    .delay(DEFAULT_CACHE_DELAY, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return tmdbApi
                .getMostPopularMovies(page)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Response<MovieCollectionResponse>, ObservableSource<List<Movie>>>() {
                    @Override
                    public ObservableSource<List<Movie>> apply(@NonNull Response<MovieCollectionResponse> response) throws Exception {
                        moviesCache.putMostPopularMovies(page, response.body().getMovies());
                        return Observable.just(response.body().getMovies());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Movie>> getHighestRatedMovies(final int page) {
        List<Movie> movies = moviesCache.getHighestRatedMovies(page);
        if (movies != null) {
            return Observable.just(movies)
                    .delay(DEFAULT_CACHE_DELAY, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return tmdbApi
                .getHighestRatedMovies(page)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Response<MovieCollectionResponse>, ObservableSource<List<Movie>>>() {
                    @Override
                    public ObservableSource<List<Movie>> apply(@NonNull Response<MovieCollectionResponse> response) throws Exception {
                        moviesCache.putHighestRatedMovies(page, response.body().getMovies());
                        return Observable.just(response.body().getMovies());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<MovieVideo>> getMovieVideos(String movieId) {
        return tmdbApi
                .getMovieVideos(movieId)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Response<MovieVideoResponse>, ObservableSource<List<MovieVideo>>>() {
                    @Override
                    public ObservableSource<List<MovieVideo>> apply(@NonNull Response<MovieVideoResponse> response) throws Exception {
                        return Observable.just(response.body().getVideos());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<MovieReview>> getMovieReviews(String movieId, int page) {
        return tmdbApi
                .getMovieReviews(movieId, page)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Response<MovieReviewResponse>, ObservableSource<List<MovieReview>>>() {
                    @Override
                    public ObservableSource<List<MovieReview>> apply(@NonNull Response<MovieReviewResponse> response) throws Exception {
                        return Observable.just(response.body().getReviews());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }
}
