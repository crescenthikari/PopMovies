package net.crescenthikari.popmovies.di.module;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;

import net.crescenthikari.popmovies.di.AppContext;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Muhammad Fiqri Muthohar on 8/8/17.
 */

@Module
public class ApplicationModule {

    @Provides
    @AppContext
    Context provideContext(Application app) {
        return app.getApplicationContext();
    }

    @Provides
    ContentResolver provideContentResolver(Application app) {
        return app.getContentResolver();
    }
}
