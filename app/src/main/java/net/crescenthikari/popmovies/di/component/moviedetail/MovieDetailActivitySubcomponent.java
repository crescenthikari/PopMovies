package net.crescenthikari.popmovies.di.component.moviedetail;

import net.crescenthikari.popmovies.features.moviedetail.MovieDetailActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Muhammad Fiqri Muthohar on 8/8/17.
 */

@Subcomponent
public interface MovieDetailActivitySubcomponent extends AndroidInjector<MovieDetailActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<MovieDetailActivity> {
    }
}
