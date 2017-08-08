package net.crescenthikari.popmovies.di.module;

import net.crescenthikari.popmovies.features.moviedetail.MovieDetailActivity;
import net.crescenthikari.popmovies.features.movieslist.MoviesListActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Muhammad Fiqri Muthohar on 8/8/17.
 */

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract MoviesListActivity contributeMoviesListActivityInjector();

    @ContributesAndroidInjector
    abstract MovieDetailActivity contributeMovieDetailActivityInjector();
}
