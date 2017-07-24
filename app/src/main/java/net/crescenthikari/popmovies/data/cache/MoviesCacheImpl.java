package net.crescenthikari.popmovies.data.cache;

import net.crescenthikari.popmovies.data.model.Movie;
import net.crescenthikari.popmovies.data.model.MovieDetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Muhammad Fiqri Muthohar on 7/24/17.
 */

public class MoviesCacheImpl implements MoviesCache {

    Map<String, MovieDetail> movieDetailCache;
    Map<Integer, List<Movie>> nowPlayingMoviesCache;
    Map<Integer, List<Movie>> highestRAtedMoviesCache;
    Map<Integer, List<Movie>> mostPopularMoviesCache;

    public MoviesCacheImpl() {
        movieDetailCache = new HashMap<>();
        nowPlayingMoviesCache = new HashMap<>();
        highestRAtedMoviesCache = new HashMap<>();
        mostPopularMoviesCache = new HashMap<>();
    }

    @Override
    public MovieDetail getMovieDetail(String movieId) {
        return movieDetailCache.get(movieId);
    }

    @Override
    public void putMovieDetail(String movieId, MovieDetail detail) {
        if (detail != null) {
            movieDetailCache.put(movieId, detail);
        }
    }

    @Override
    public List<Movie> getNowPlayingMovies(int page) {
        return nowPlayingMoviesCache.get(page);
    }

    @Override
    public void putNowPlayingMovies(int page, List<Movie> movies) {
        if (movies != null && !movies.isEmpty()) {
            nowPlayingMoviesCache.put(page, movies);
        }
    }

    @Override
    public List<Movie> getHighestRatedMovies(int page) {
        return highestRAtedMoviesCache.get(page);
    }

    @Override
    public void putHighestRatedMovies(int page, List<Movie> movies) {
        if (movies != null && !movies.isEmpty()) {
            highestRAtedMoviesCache.put(page, movies);
        }
    }

    @Override
    public List<Movie> getMostPopularMovies(int page) {
        return mostPopularMoviesCache.get(page);
    }

    @Override
    public void putMostPopularMovies(int page, List<Movie> movies) {
        if (movies != null && !movies.isEmpty()) {
            mostPopularMoviesCache.put(page, movies);
        }
    }
}
