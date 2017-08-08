package net.crescenthikari.popmovies.di.component.movielist;

import net.crescenthikari.popmovies.features.movieslist.MoviesListActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Muhammad Fiqri Muthohar on 8/8/17.
 */

@Subcomponent
public interface MoviesListActivitySubcomponent extends AndroidInjector<MoviesListActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<MoviesListActivity> {
    }
}
