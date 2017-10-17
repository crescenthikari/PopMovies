package net.crescenthikari.popmovies.features.movieslist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.crescenthikari.popmovies.R;
import net.crescenthikari.popmovies.data.model.Movie;
import net.crescenthikari.popmovies.data.repository.MovieRepository;
import net.crescenthikari.popmovies.features.moviedetail.MovieDetailActivity;
import net.crescenthikari.popmovies.features.movieslist.adapter.MovieCollectionAdapter;
import net.crescenthikari.popmovies.features.movieslist.contract.OnMovieItemClickCallback;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class MoviesListActivity extends AppCompatActivity implements OnMovieItemClickCallback {
    public static final String TAG = "MainActivity";

    public static final int SECTION_NOW_PLAYING = 0;
    public static final int SECTION_POPULARITY = 1;
    public static final int SECTION_RATING = 2;
    public static final int SECTION_FAVORITE = 3;

    public static final String KEY_SECTION = "KEY_SECTION";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.bottom_nav)
    BottomNavigationView bottomNavView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.movies_list_view)
    RecyclerView moviesListView;

    @BindView(R.id.movies_list_message)
    TextView errorMessageField;

    GridLayoutManager moviesLayoutAdapter;

    @Inject
    MovieRepository movieRepository;

    private CompositeDisposable disposables = new CompositeDisposable();
    private Disposable lastDisposable;

    private MovieCollectionAdapter movieCollectionAdapter;

    private int currentSection = SECTION_NOW_PLAYING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            currentSection = savedInstanceState.getInt(KEY_SECTION, SECTION_NOW_PLAYING);
        }
        setupViews();
        getMovies();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // reload favorite movie section on resume
        if (currentSection == SECTION_FAVORITE) {
            getFavoriteMovies();
        }
    }

    private void getMovies() {
        if (currentSection == SECTION_NOW_PLAYING) {
            getNowPlayingMovies();
        } else if (currentSection == SECTION_POPULARITY) {
            getMostPopularMovies();
        } else if (currentSection == SECTION_RATING) {
            getHighestRatedMovies();
        } else if (currentSection == SECTION_FAVORITE) {
            getFavoriteMovies();
        }
    }

    @Override
    protected void onDestroy() {
        if (disposables != null) {
            disposables.dispose();
        }
        if (movieCollectionAdapter != null) {
            movieCollectionAdapter.removeMovieItemClickCallback();
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SECTION, currentSection);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            currentSection = savedInstanceState.getInt(KEY_SECTION, SECTION_NOW_PLAYING);
        }
    }

    private void setupViews() {
        setupToolbar();
        setupBottomNav();
        setupMoviesListView();
    }

    private void setupMoviesListView() {
        moviesLayoutAdapter = new GridLayoutManager(
                this,
                getResources().getInteger(R.integer.grid_size)
        );
        movieCollectionAdapter = new MovieCollectionAdapter();
        movieCollectionAdapter.setMovieItemClickCallback(this);
        moviesListView.setItemAnimator(new DefaultItemAnimator());
        moviesListView.setLayoutManager(moviesLayoutAdapter);
        moviesListView.setAdapter(movieCollectionAdapter);
    }

    private void setupBottomNav() {
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.action_now_playing) {
                    currentSection = SECTION_NOW_PLAYING;
                    getNowPlayingMovies();
                } else if (id == R.id.action_sort_popularity) {
                    currentSection = SECTION_POPULARITY;
                    getMostPopularMovies();
                } else if (id == R.id.action_sort_ratings) {
                    currentSection = SECTION_RATING;
                    getHighestRatedMovies();
                } else if (id == R.id.action_favorited) {
                    currentSection = SECTION_FAVORITE;
                    getFavoriteMovies();
                }
                moviesLayoutAdapter.scrollToPositionWithOffset(0, 0);
                return true;
            }
        });
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void showMovieList() {
        moviesListView.setVisibility(View.VISIBLE);
    }

    private void hideMovieList() {
        moviesListView.setVisibility(View.GONE);
    }

    private void showErrorMessage(String errorMessage) {
        errorMessageField.setText(errorMessage);
        errorMessageField.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage() {
        errorMessageField.setText("");
        errorMessageField.setVisibility(View.GONE);
    }

    private void getNowPlayingMovies() {
        subscribeMovieCollectionResponse(movieRepository.getNowPlayingMovies(1));
    }

    private void getHighestRatedMovies() {
        subscribeMovieCollectionResponse(movieRepository.getHighestRatedMovies(1));
    }

    private void getMostPopularMovies() {
        subscribeMovieCollectionResponse(movieRepository.getMostPopularMovies(1));
    }

    private void getFavoriteMovies() {
        subscribeMovieCollectionResponse(movieRepository.getFavoriteMovies());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void subscribeMovieCollectionResponse(Observable<List<Movie>> observable) {
        hideMovieList();
        hideErrorMessage();
        showProgress();
        if (disposables.size() == 1 && lastDisposable != null) {
            lastDisposable.dispose();
            disposables.remove(lastDisposable);
        }
        lastDisposable = observable
                .subscribe(new Consumer<List<Movie>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<Movie> movies)
                            throws Exception {
                        movieCollectionAdapter.replaceMovieList(movies);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable)
                            throws Exception {
                        Log.e(TAG, "accept: " + throwable.getMessage());
                        hideProgress();
                        if (throwable instanceof IOException) {
                            showErrorMessage(getString(R.string.network_error_message));
                        }
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        hideProgress();
                        showMovieList();
                    }
                });
        disposables.add(lastDisposable);
    }

    @Override
    public void onMovieItemClicked(ImageView posterView, Movie movie) {
        goToMovieDetailPage(posterView, movie);
    }

    private void goToMovieDetailPage(ImageView posterView, Movie movie) {
        Intent detailIntent = new Intent(this, MovieDetailActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                posterView,
                MovieDetailActivity.KEY_MOVIE_POSTER_PATH
        );
        detailIntent.putExtra(MovieDetailActivity.KEY_MOVIE_ID, String.valueOf(movie.getId()));
        detailIntent.putExtra(MovieDetailActivity.KEY_MOVIE_TITLE, movie.getTitle());
        detailIntent.putExtra(MovieDetailActivity.KEY_MOVIE_RELEASE_DATE, movie.getReleaseDate());
        detailIntent.putExtra(MovieDetailActivity.KEY_MOVIE_RATINGS, movie.getVoteAverage());
        detailIntent.putExtra(MovieDetailActivity.KEY_MOVIE_VOTE_COUNT, movie.getVoteCount());
        detailIntent.putExtra(MovieDetailActivity.KEY_MOVIE_POSTER_PATH, movie.getPosterPath());
        detailIntent.putExtra(MovieDetailActivity.KEY_MOVIE_BACKDROP_PATH, movie.getBackdropPath());
        detailIntent.putExtra(MovieDetailActivity.KEY_MOVIE_OVERVIEW, movie.getOverview());
        detailIntent.putExtra(MovieDetailActivity.KEY_MOVIE_FAVORITE, movie.isFavoriteMovie());
        ActivityCompat.startActivity(this, detailIntent, options.toBundle());
    }
}
