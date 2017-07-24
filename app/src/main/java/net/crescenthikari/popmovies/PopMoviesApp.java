package net.crescenthikari.popmovies;

import android.app.Application;

import net.crescenthikari.popmovies.data.DataManager;
import net.crescenthikari.popmovies.data.api.TmdbApiService;
import net.crescenthikari.popmovies.data.cache.MoviesCacheImpl;
import net.crescenthikari.popmovies.data.repository.MovieRepository;
import net.crescenthikari.popmovies.data.repository.MovieRepositoryImpl;

/**
 * Created by Muhammad Fiqri Muthohar on 6/21/17.
 */

public class PopMoviesApp extends Application
        implements DataManager {

    private MovieRepository movieRepository;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public synchronized MovieRepository getMovieRepository() {
        if (movieRepository == null) {
            movieRepository = new MovieRepositoryImpl(
                    TmdbApiService.open(),
                    new MoviesCacheImpl()
            );
        }
        return movieRepository;
    }
}
