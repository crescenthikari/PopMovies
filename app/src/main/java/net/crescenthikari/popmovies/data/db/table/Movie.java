package net.crescenthikari.popmovies.data.db.table;

import android.provider.BaseColumns;

/**
 * Created by Muhammad Fiqri Muthohar on 7/31/17.
 */

public class Movie implements BaseColumns {
    public static final String TABLE_NAME = "movies";
    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_MOVIE_TITLE = "movie_title";
    public static final String COLUMN_MOVIE_OVERVIEW = "movie_overview";
    public static final String COLUMN_MOVIE_VOTE_COUNT = "movie_vote_count";
    public static final String COLUMN_MOVIE_VOTE_AVERAGE = "movie_vote_average";
    public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
    public static final String COLUMN_MOVIE_FAVORED = "movie_favored";
    public static final String COLUMN_MOVIE_POSTER_PATH = "movie_poster_path";
    public static final String COLUMN_MOVIE_BACKDROP_PATH = "movie_backdrop_path";

    public static final String TABLE_CREATE_SCRIPT = "CREATE TABLE " + TABLE_NAME + "("
            + BaseColumns._ID + " INTEGER NOT NULL PRIMARY KEY,"
            + Movie.COLUMN_MOVIE_ID + " TEXT NOT NULL,"
            + Movie.COLUMN_MOVIE_TITLE + " TEXT NOT NULL,"
            + Movie.COLUMN_MOVIE_OVERVIEW + " TEXT,"
            + Movie.COLUMN_MOVIE_VOTE_AVERAGE + " REAL,"
            + Movie.COLUMN_MOVIE_VOTE_COUNT + " INTEGER,"
            + Movie.COLUMN_MOVIE_BACKDROP_PATH + " TEXT,"
            + Movie.COLUMN_MOVIE_POSTER_PATH + " TEXT,"
            + Movie.COLUMN_MOVIE_RELEASE_DATE + " TEXT,"
            + Movie.COLUMN_MOVIE_FAVORED + " INTEGER NOT NULL DEFAULT 0,"
            + "UNIQUE (" + Movie.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE)";
}
