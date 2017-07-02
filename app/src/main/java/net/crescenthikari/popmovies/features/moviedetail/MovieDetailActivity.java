package net.crescenthikari.popmovies.features.moviedetail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.crescenthikari.popmovies.R;
import net.crescenthikari.popmovies.api.TmdbApiService;
import net.crescenthikari.popmovies.api.TmdbConstant;
import net.crescenthikari.popmovies.model.MovieDetail;
import net.crescenthikari.popmovies.util.AnimationUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {
    public static final String TAG = "MovieDetailActivity";

    public static final String KEY_MOVIE_ID = "MOVIE_ID";
    public static final String KEY_MOVIE_TITLE = "MOVIE_TITLE";
    public static final String KEY_MOVIE_RATINGS = "MOVIE_RATINGS";
    public static final String KEY_MOVIE_RELEASE_DATE = "MOVIE_RELEASE_DATE";
    public static final String KEY_MOVIE_POSTER_PATH = "MOVIE_POSTER_PATH";
    public static final String KEY_MOVIE_BACKDROP_PATH = "MOVIE_BACKDROP_PATH";
    public static final String KEY_MOVIE_OVERVIEW = "MOVIE_OVERVIEW";

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.75f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.75f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.movie_poster_image)
    ImageView posterImageView;

    @BindView(R.id.movie_backdrop_image)
    ImageView backdropImageView;

    @BindView(R.id.view_backdrop_overlay)
    View viewOverlay;

    @BindView(R.id.movie_title)
    TextView titleView;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitleView;

    @BindView(R.id.movie_overview)
    TextView overviewView;

    @BindView(R.id.movie_release_date)
    TextView releaseDateView;

    @BindView(R.id.movie_ratings)
    TextView ratingView;

    @BindView(R.id.movie_duration)
    TextView durationView;

    @BindView(R.id.movie_tagline)
    TextView taglineView;

    @BindView(R.id.movie_detail_frame)
    FrameLayout detailWrapperView;

    @BindColor(R.color.colorPrimary)
    int colorPrimary;

    @BindColor(R.color.colorPrimaryDark)
    int colorPrimaryDark;

    String movieId;
    String movieTitle;
    String posterPath;
    String backdropPath;
    String movieOverview;
    String movieReleaseDateString;
    Date movieReleaseDate;
    Double movieRatings;
    CompositeDisposable disposables = new CompositeDisposable();
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (backdropImageView != null) {
                backdropImageView.setImageBitmap(bitmap);
            }
            Palette.from(bitmap)
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            if (collapsingToolbar != null) {
                                collapsingToolbar.setContentScrimColor(
                                        palette.getMutedColor(colorPrimary)
                                );
                                collapsingToolbar.setStatusBarScrimColor(
                                        palette.getDarkMutedColor(colorPrimaryDark)
                                );
                            }
                            if (toolbar != null) {
                                toolbar.setBackgroundColor(palette.getMutedColor(colorPrimary));
                                changeToolbarColorAlpha(0);
                            }
                        }
                    });
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            // do nothing
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            // do nothing
        }
    };


    SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyy", Locale.getDefault());

    private boolean isTitleVisible = false;
    private boolean isTitleContainerVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            movieId = getIntent().getStringExtra(KEY_MOVIE_ID);
            movieTitle = getIntent().getStringExtra(KEY_MOVIE_TITLE);
            posterPath = getIntent().getStringExtra(KEY_MOVIE_POSTER_PATH);
            backdropPath = getIntent().getStringExtra(KEY_MOVIE_BACKDROP_PATH);
            movieOverview = getIntent().getStringExtra(KEY_MOVIE_OVERVIEW);
            movieReleaseDateString = getIntent().getStringExtra(KEY_MOVIE_RELEASE_DATE);
            try {
                movieReleaseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        .parse(movieReleaseDateString);
            } catch (Exception e) {
                movieReleaseDate = Calendar.getInstance().getTime();
            }
            movieRatings = getIntent().getDoubleExtra(KEY_MOVIE_RATINGS, 0);
            if (savedInstanceState == null) {
                ViewCompat.setTransitionName(posterImageView, KEY_MOVIE_POSTER_PATH);
                loadMoviePosterImage("w342/");
                supportPostponeEnterTransition();
            } else {
                loadMoviePosterImage("w342/");
            }
        }

        setToolbar();
        appBarLayout.addOnOffsetChangedListener(this);
        titleView.setText(movieTitle);
        overviewView.setText(movieOverview);
        releaseDateView.setText(String.format(
                "Release date : %s",
                dateFormat.format(movieReleaseDate))
        );
        ratingView.setText(String.format(Locale.getDefault(), "%.1f", movieRatings));
        toolbarTitleView.setText(movieTitle);
        loadMovieBackdropImage();
        loadMovieDetails();
    }

    @Override
    protected void onDestroy() {
        if (disposables != null) {
            disposables.dispose();
        }
        super.onDestroy();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    private void loadMoviePosterImage(String width) {
        Picasso picasso = Picasso.with(this);
        picasso.setLoggingEnabled(true);
        picasso.load(TmdbConstant.IMAGE_BASE_URL + width + posterPath)
                .into(posterImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        supportStartPostponedEnterTransition();
                    }
                });
    }

    private void loadMovieBackdropImage() {
        Picasso picasso = Picasso.with(this);
        picasso.setLoggingEnabled(true);
        picasso.load(TmdbConstant.IMAGE_BASE_URL + "w780/" + backdropPath)
                .into(target);
    }

    private void loadMovieDetails() {
        TmdbApiService.open()
                .getMovieDetail(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<MovieDetail>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        disposables.add(disposable);
                    }

                    @Override
                    public void onNext(@NonNull Response<MovieDetail> movieDetailResponse) {
                        MovieDetail detail = movieDetailResponse.body();
                        if (detail != null) {
                            durationView.setText(String.format(
                                    Locale.getDefault(),
                                    "%d minute(s)",
                                    detail.getRuntime())
                            );
                            taglineView.setText(TextUtils.isEmpty(detail.getTagline())
                                    ? "-" : detail.getTagline());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        // do nothing
                    }

                    @Override
                    public void onComplete() {
                        // do nothing
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!isTitleVisible) {
                AnimationUtils.startAlphaAnimation(
                        toolbarTitleView,
                        ALPHA_ANIMATIONS_DURATION,
                        View.VISIBLE
                );
                changeToolbarColorAlpha(255);
                isTitleVisible = true;
            }
        } else {
            if (isTitleVisible) {
                AnimationUtils.startAlphaAnimation(
                        toolbarTitleView,
                        ALPHA_ANIMATIONS_DURATION,
                        View.INVISIBLE
                );
                changeToolbarColorAlpha(0);
                isTitleVisible = false;
            }
        }
    }

    private void changeToolbarColorAlpha(int alpha) {
        if (toolbar.getBackground() != null) {
            toolbar.getBackground().setAlpha(alpha);
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (isTitleContainerVisible) {
                AnimationUtils.startAlphaAnimation(
                        detailWrapperView,
                        ALPHA_ANIMATIONS_DURATION,
                        View.INVISIBLE
                );
                isTitleContainerVisible = false;
            }
        } else {
            if (!isTitleContainerVisible) {
                AnimationUtils.startAlphaAnimation(
                        detailWrapperView,
                        ALPHA_ANIMATIONS_DURATION,
                        View.VISIBLE
                );
                isTitleContainerVisible = true;
            }
        }
    }
}
