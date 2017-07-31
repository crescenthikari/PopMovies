package net.crescenthikari.popmovies;

import android.app.Application;
import android.graphics.Bitmap;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

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
        setupPicassoGlobalConfig();
    }

    private void setupPicassoGlobalConfig() {
        Picasso picasso = new Picasso.Builder(this)
                .defaultBitmapConfig(Bitmap.Config.RGB_565)
                .memoryCache(new LruCache(100000000))
                .build();
        // set the global instance to use this Picasso object
        // all following Picasso (with Picasso.with(Context context) requests will use this Picasso object
        // you can only use the setSingletonInstance() method once!
        try {
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException ignored) {
            // Picasso instance was already set
            // cannot set it after Picasso.with(Context) was already in use
        }
    }

    @Override
    public synchronized MovieRepository getMovieRepository() {
        if (movieRepository == null) {
            movieRepository = new MovieRepositoryImpl(
                    TmdbApiService.open(),
                    new MoviesCacheImpl(),
                    getContentResolver()
            );
        }
        return movieRepository;
    }
}
