package net.crescenthikari.popmovies;

import android.app.Activity;
import android.app.Application;

import com.squareup.picasso.Picasso;

import net.crescenthikari.popmovies.di.component.DaggerApplicationComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * Created by Muhammad Fiqri Muthohar on 6/21/17.
 */

public class PopMoviesApp extends Application
        implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    Picasso picasso;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerApplicationComponent
                .builder()
                .application(this)
                .build()
                .inject(this);
        setupPicassoGlobalConfig();
    }

    private void setupPicassoGlobalConfig() {
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
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
