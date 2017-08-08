package net.crescenthikari.popmovies.di.component;

import android.app.Application;

import net.crescenthikari.popmovies.PopMoviesApp;
import net.crescenthikari.popmovies.di.module.ActivityBuilder;
import net.crescenthikari.popmovies.di.module.ApiModule;
import net.crescenthikari.popmovies.di.module.ApplicationModule;
import net.crescenthikari.popmovies.di.module.MovieRepositoryModule;
import net.crescenthikari.popmovies.di.module.PicassoModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by Muhammad Fiqri Muthohar on 8/8/17.
 */
@Singleton
@Component(modules = {
        ApplicationModule.class,
        AndroidSupportInjectionModule.class,
        ActivityBuilder.class,
        PicassoModule.class,
        MovieRepositoryModule.class,
        ApiModule.class
})
public interface ApplicationComponent {
    void inject(PopMoviesApp application);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        ApplicationComponent build();
    }
}
