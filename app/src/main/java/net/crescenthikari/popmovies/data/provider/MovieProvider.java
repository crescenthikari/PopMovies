package net.crescenthikari.popmovies.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import net.crescenthikari.popmovies.data.db.DatabaseHelper;
import net.crescenthikari.popmovies.data.db.table.Movie;
import net.crescenthikari.popmovies.data.provider.contract.MovieContract;

import static net.crescenthikari.popmovies.data.provider.contract.MovieContract.CONTENT_AUTHORITY;

/**
 * Created by Muhammad Fiqri Muthohar on 7/31/17.
 */

public class MovieProvider extends ContentProvider {
    private static final String TAG = "MovieProvider";

    private static final int MOVIES = 100;
    private static final int MOVIES_ID = 101;
    private static final String MOVIES_PATH = "movies";
    private static final String MOVIES_ID_PATH = "movies/*";
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    private DatabaseHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, MOVIES_PATH, MOVIES);
        matcher.addURI(authority, MOVIES_ID_PATH, MOVIES_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);

        Cursor query;
        if (match == MOVIES) {
            query = getFavoredMovies(projection, selection, selectionArgs, sortOrder, db);
        } else {
            throw new UnsupportedOperationException("Unknown query uri: " + uri);
        }
        query.setNotificationUri(getContext().getContentResolver(), uri);
        return query;
    }

    private Cursor getFavoredMovies(@Nullable String[] projection,
                                    @Nullable String selection,
                                    @Nullable String[] selectionArgs,
                                    @Nullable String sortOrder,
                                    SQLiteDatabase db) {
        Cursor query;
        query = db.query(Movie.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        return query;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        Uri insertUri;
        if (match == MOVIES) {
            long id = db.insertOrThrow(Movie.TABLE_NAME, null, values);
            insertUri = MovieContract.buildMovieUri(String.valueOf(id));
        } else {
            throw new UnsupportedOperationException("Unknown insert uri: " + uri);
        }
        notifyContentResolver(uri);
        return insertUri;
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int retrieveValue;
        if (match == MOVIES) {
            retrieveValue = db.delete(
                    Movie.TABLE_NAME,
                    selection,
                    selectionArgs
            );
        } else {
            throw new UnsupportedOperationException("Unknown delete uri: " + uri);
        }
        notifyContentResolver(uri);
        return retrieveValue;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int retrieveValue;
        if (match == MOVIES) {
            retrieveValue = db.update(
                    Movie.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
            );
        } else {
            throw new UnsupportedOperationException("Unknown update uri: " + uri);
        }
        notifyContentResolver(uri);
        return retrieveValue;
    }

    private void notifyContentResolver(Uri uri) {
        try {
            getContext()
                    .getContentResolver()
                    .notifyChange(uri, null);
        } catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }
    }
}
