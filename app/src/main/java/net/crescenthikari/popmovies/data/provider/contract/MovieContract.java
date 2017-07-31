package net.crescenthikari.popmovies.data.provider.contract;

import android.database.Cursor;
import android.net.Uri;

import net.crescenthikari.popmovies.data.model.Movie;

import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_BACKDROP_PATH;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_FAVORED;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_ID;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_OVERVIEW;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_POSTER_PATH;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_RELEASE_DATE;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_TITLE;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_VOTE_AVERAGE;
import static net.crescenthikari.popmovies.data.db.table.Movie.COLUMN_MOVIE_VOTE_COUNT;

/**
 * Created by Muhammad Fiqri Muthohar on 7/31/17.
 */

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "net.crescenthikari.popmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.popmovies.movie";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.popmovies.movie";
    private static final String PATH_MOVIES = "movies";
    public static final Uri CONTENT_URI = BASE_CONTENT_URI
            .buildUpon()
            .appendPath(PATH_MOVIES)
            .build();

    public static Uri buildMovieUri(String movieId) {
        return CONTENT_URI
                .buildUpon()
                .appendPath(movieId)
                .build();
    }

    public static Movie fromCursor(Cursor query) {
        Movie movie = new Movie();
        movie.setId(query.getInt(query.getColumnIndex(COLUMN_MOVIE_ID)));
        movie.setTitle(query.getString(query.getColumnIndex(COLUMN_MOVIE_TITLE)));
        movie.setOverview(query.getString(query.getColumnIndex(COLUMN_MOVIE_OVERVIEW)));
        movie.setPosterPath(query.getString(query.getColumnIndex(COLUMN_MOVIE_POSTER_PATH)));
        movie.setVoteAverage(query.getDouble(query.getColumnIndex(COLUMN_MOVIE_VOTE_AVERAGE)));
        movie.setVoteCount(query.getInt(query.getColumnIndex(COLUMN_MOVIE_VOTE_COUNT)));
        movie.setReleaseDate(query.getString(query.getColumnIndex(COLUMN_MOVIE_RELEASE_DATE)));
        movie.setBackdropPath(query.getString(query.getColumnIndex(COLUMN_MOVIE_BACKDROP_PATH)));
        movie.setFavoriteMovie(query.getInt(query.getColumnIndex(COLUMN_MOVIE_FAVORED)) == 1);
        return movie;
    }
}
