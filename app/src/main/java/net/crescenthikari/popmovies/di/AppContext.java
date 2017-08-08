package net.crescenthikari.popmovies.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by Muhammad Fiqri Muthohar on 8/8/17.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface AppContext {
}
