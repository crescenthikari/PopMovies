package net.crescenthikari.popmovies.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.crescenthikari.popmovies.data.db.table.Movie;

/**
 * Created by Muhammad Fiqri Muthohar on 7/31/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "popmovie.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Movie.TABLE_CREATE_SCRIPT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
