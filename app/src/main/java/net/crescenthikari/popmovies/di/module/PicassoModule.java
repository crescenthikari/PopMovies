package net.crescenthikari.popmovies.di.module;

import android.content.Context;
import android.graphics.Bitmap;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import net.crescenthikari.popmovies.di.AppContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Muhammad Fiqri Muthohar on 8/8/17.
 */
@Module
public class PicassoModule {
    @Provides
    @Singleton
    Picasso providePicasso(@AppContext Context context) {
        return new Picasso.Builder(context)
                .defaultBitmapConfig(Bitmap.Config.RGB_565)
                .memoryCache(new LruCache(100000000))
                .build();
    }
}
