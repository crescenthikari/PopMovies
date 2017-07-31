package net.crescenthikari.popmovies.data.repository;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.crescenthikari.popmovies.data.api.TmdbApi;
import net.crescenthikari.popmovies.data.api.response.MovieCollectionResponse;
import net.crescenthikari.popmovies.data.api.response.MovieReviewResponse;
import net.crescenthikari.popmovies.data.api.response.MovieVideoResponse;
import net.crescenthikari.popmovies.data.cache.MoviesCache;
import net.crescenthikari.popmovies.data.model.Movie;
import net.crescenthikari.popmovies.data.model.MovieDetail;
import net.crescenthikari.popmovies.data.model.MovieReview;
import net.crescenthikari.popmovies.data.model.MovieVideo;
import net.crescenthikari.popmovies.data.provider.contract.MovieContract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_BACKDROP_PATH;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_FAVORED;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_ID;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_OVERVIEW;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_POSTER_PATH;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_RELEASE_DATE;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_TITLE;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_VOTE_AVERAGE;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_VOTE_COUNT;
import static net.crescenthikari.popmovies.data.provider.contract.MovieContract.CONTENT_URI;

/**
 * Created by Muhammad Fiqri Muthohar on 7/24/17.
 */

public class MovieRepositoryImpl implements MovieRepository {
    private static final String TAG = "MovieRepositoryImpl";

    private static final int DEFAULT_CACHE_DELAY = 350;

    private static final String[] movieProjection = new String[]{
            COLUMN_MOVIE_ID,
            COLUMN_MOVIE_TITLE,
            COLUMN_MOVIE_OVERVIEW,
            COLUMN_MOVIE_VOTE_COUNT,
            COLUMN_MOVIE_VOTE_AVERAGE,
            COLUMN_MOVIE_RELEASE_DATE,
            COLUMN_MOVIE_FAVORED,
            COLUMN_MOVIE_POSTER_PATH,
            COLUMN_MOVIE_BACKDROP_PATH
    };
    private TmdbApi tmdbApi;
    private MoviesCache moviesCache;
    private ContentResolver contentResolver;

    public MovieRepositoryImpl(TmdbApi tmdbApi,
                               MoviesCache moviesCache,
                               ContentResolver contentResolver) {
        this.tmdbApi = tmdbApi;
        this.moviesCache = moviesCache;
        this.contentResolver = contentResolver;
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
                    public ObservableSource<MovieDetail> apply(@NonNull Response<MovieDetail> response)
                            throws Exception {
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
                    public ObservableSource<List<Movie>> apply(@NonNull Response<MovieCollectionResponse> response)
                            throws Exception {
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
                    public ObservableSource<List<Movie>> apply(@NonNull Response<MovieCollectionResponse> response)
                            throws Exception {
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
                    public ObservableSource<List<Movie>> apply(@NonNull Response<MovieCollectionResponse> response)
                            throws Exception {
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
                    public ObservableSource<List<MovieVideo>> apply(@NonNull Response<MovieVideoResponse> response)
                            throws Exception {
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
                    public ObservableSource<List<MovieReview>> apply(@NonNull Response<MovieReviewResponse> response)
                            throws Exception {
                        return Observable.just(response.body().getReviews());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Movie>> getFavoriteMovies() {
        return Observable.create(
                new ObservableOnSubscribe<List<Movie>>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<List<Movie>> observableEmitter)
                            throws Exception {
                        List<Movie> movies = new ArrayList<>();
                        final Cursor query = contentResolver.query(
                                CONTENT_URI,
                                movieProjection,
                                null,
                                null,
                                null
                        );
                        if (query != null && query.moveToFirst()) {
                            do {
                                movies.add(MovieContract.fromCursor(query));
                            } while (query.moveToNext());
                        }
                        Log.d(TAG, "getFavoriteMovies: " + movies.size());
                        observableEmitter.onNext(movies);
                        observableEmitter.onComplete();
                    }
                }
        );
    }

    @Override
    public Completable addFavoriteMovie(final Movie movie) {
        Log.d(TAG, "addFavoriteMovie: " + movie.getTitle());
        return Completable
                .create(new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull CompletableEmitter completableEmitter)
                            throws Exception {
                        final ContentValues movieValues = getMovieCV(movie);
                        contentResolver.insert(CONTENT_URI, movieValues);
                        completableEmitter.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable removeFavoriteMovie(final String movieId) {
        return Completable
                .create(new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull CompletableEmitter completableEmitter)
                            throws Exception {
                        final String where = String.format("%s=?", COLUMN_MOVIE_ID);
                        final String[] args = new String[]{String.valueOf(movieId)};
                        contentResolver.delete(CONTENT_URI, where, args);
                        completableEmitter.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public Single<Movie> getFavoriteMovie(final String movieId) {
        return Single.create(new SingleOnSubscribe<Movie>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Movie> singleEmitter) throws Exception {
                final String where = String.format("%s=?", COLUMN_MOVIE_ID);
                final String[] args = new String[]{String.valueOf(movieId)};
                final Cursor cursor = contentResolver.query(
                        CONTENT_URI,
                        movieProjection,
                        where,
                        args,
                        null
                );
                if (cursor != null && cursor.getCount() >= 1) {
                    Log.d(TAG, "getFavoriteMovie: cursor count " + cursor.getCount());
                    cursor.moveToFirst();
                    final Movie resultMovie = MovieContract.fromCursor(cursor);
                    singleEmitter.onSuccess(resultMovie);
                } else {
                    singleEmitter.onError(new Throwable("No movies"));
                }
            }
        });
    }

    private ContentValues getMovieCV(Movie movie) {
        final ContentValues values = new ContentValues();
        values.put(COLUMN_MOVIE_ID, movie.getId());
        values.put(COLUMN_MOVIE_TITLE, movie.getTitle());
        values.put(COLUMN_MOVIE_OVERVIEW, movie.getOverview());
        values.put(COLUMN_MOVIE_VOTE_COUNT, movie.getVoteCount());
        values.put(COLUMN_MOVIE_VOTE_AVERAGE, movie.getVoteAverage());
        values.put(COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
        values.put(COLUMN_MOVIE_FAVORED, movie.isFavoriteMovie());
        values.put(COLUMN_MOVIE_POSTER_PATH, movie.getPosterPath());
        values.put(COLUMN_MOVIE_BACKDROP_PATH, movie.getBackdropPath());
        return values;
    }
}
