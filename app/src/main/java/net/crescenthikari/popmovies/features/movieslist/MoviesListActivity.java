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

import net.crescenthikari.popmovies.R;
import net.crescenthikari.popmovies.api.TmdbApiService;
import net.crescenthikari.popmovies.api.response.MovieCollectionResponse;
import net.crescenthikari.popmovies.features.moviedetail.MovieDetailActivity;
import net.crescenthikari.popmovies.features.movieslist.adapter.MovieCollectionAdapter;
import net.crescenthikari.popmovies.features.movieslist.contract.OnMovieItemClickCallback;
import net.crescenthikari.popmovies.model.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MoviesListActivity extends AppCompatActivity implements OnMovieItemClickCallback {
    public static final String TAG = "MainActivity";

    public static final int SECTION_NOW_PLAYING = 0;
    public static final int SECTION_POPULARITY = 1;
    public static final int SECTION_RATING = 2;

    public static final String KEY_SECTION = "KEY_SECTION";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.bottom_nav)
    BottomNavigationView bottomNavView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.movies_list_view)
    RecyclerView moviesListView;

    GridLayoutManager moviesLayoutAdapter;

    private CompositeDisposable disposables = new CompositeDisposable();
    private Disposable lastDisposable;

    private MovieCollectionAdapter movieCollectionAdapter;

    private int currentSection = SECTION_NOW_PLAYING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            currentSection = savedInstanceState.getInt(KEY_SECTION, SECTION_NOW_PLAYING);
        }
        setupViews();
        retrieveMovies();
    }

    private void retrieveMovies() {
        if (currentSection == SECTION_NOW_PLAYING) {
            retrieveNowPlayingMovies();
        } else if (currentSection == SECTION_POPULARITY) {
            retrievePopularMovies();
        } else if (currentSection == SECTION_RATING) {
            retrieveHighestRatedMovies();
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
                    retrieveNowPlayingMovies();
                } else if (id == R.id.action_sort_popularity) {
                    currentSection = SECTION_POPULARITY;
                    retrievePopularMovies();
                } else if (id == R.id.action_sort_ratings) {
                    currentSection = SECTION_RATING;
                    retrieveHighestRatedMovies();
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

    private void retrieveNowPlayingMovies() {
        subscribeMovieCollectionResponse(getNowPlayingMovies());
    }

    private void retrieveHighestRatedMovies() {
        subscribeMovieCollectionResponse(getHighestRatedMovies());
    }

    private void retrievePopularMovies() {
        subscribeMovieCollectionResponse(getPopularMovies());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private Observable<Response<MovieCollectionResponse>> getNowPlayingMovies() {
        return TmdbApiService.open()
                .getNowPlayingMovies(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Response<MovieCollectionResponse>> getPopularMovies() {
        return TmdbApiService.open()
                .getMostPopularMovies(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Response<MovieCollectionResponse>> getHighestRatedMovies() {
        return TmdbApiService.open()
                .getHighestRatedMovies(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void subscribeMovieCollectionResponse(Observable<Response<MovieCollectionResponse>> observable) {
        hideMovieList();
        showProgress();
        if (disposables.size() == 1 && lastDisposable != null) {
            lastDisposable.dispose();
            disposables.remove(lastDisposable);
        }
        lastDisposable = observable
                .subscribe(new Consumer<Response<MovieCollectionResponse>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Response<MovieCollectionResponse> listResponse)
                            throws Exception {
                        if (listResponse != null) {
                            MovieCollectionResponse body = listResponse.body();
                            if (body != null
                                    && body.getMovies() != null
                                    && !body.getMovies().isEmpty()) {
                                movieCollectionAdapter.replaceMovieList(body.getMovies());
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable)
                            throws Exception {
                        Log.e(TAG, "accept: " + throwable.getMessage());
                        // do nothing
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
        detailIntent.putExtra(MovieDetailActivity.KEY_MOVIE_POSTER_PATH, movie.getPosterPath());
        detailIntent.putExtra(MovieDetailActivity.KEY_MOVIE_BACKDROP_PATH, movie.getBackdropPath());
        detailIntent.putExtra(MovieDetailActivity.KEY_MOVIE_OVERVIEW, movie.getOverview());
        ActivityCompat.startActivity(this, detailIntent, options.toBundle());
    }
}
