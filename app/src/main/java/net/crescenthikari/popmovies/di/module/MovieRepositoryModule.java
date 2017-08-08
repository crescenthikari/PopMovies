package net.crescenthikari.popmovies.di.module;

import android.content.ContentResolver;

import net.crescenthikari.popmovies.data.api.TmdbApi;
import net.crescenthikari.popmovies.data.cache.MoviesCache;
import net.crescenthikari.popmovies.data.cache.MoviesCacheImpl;
import net.crescenthikari.popmovies.data.repository.MovieRepository;
import net.crescenthikari.popmovies.data.repository.MovieRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Muhammad Fiqri Muthohar on 8/8/17.
 */

@Module
public class MovieRepositoryModule {
    @Provides
    @Singleton
    MovieRepository provideMovieRepository(TmdbApi tmdbApi,
                                           MoviesCache moviesCache,
                                           ContentResolver contentResolver) {
        return new MovieRepositoryImpl(tmdbApi, moviesCache, contentResolver);
    }

    @Provides
    @Singleton
    MoviesCache provideMoviesCache() {
        return new MoviesCacheImpl();
    }
}
