package net.crescenthikari.popmovies.features.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.crescenthikari.popmovies.R;
import net.crescenthikari.popmovies.features.movieslist.MoviesListActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        startActivity(new Intent(this, MoviesListActivity.class));
        finish();
    }
}
