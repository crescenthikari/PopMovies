package net.crescenthikari.popmovies.features.moviedetail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.crescenthikari.popmovies.R;
import net.crescenthikari.popmovies.data.DataManager;
import net.crescenthikari.popmovies.data.model.MovieDetail;
import net.crescenthikari.popmovies.data.model.MovieReview;
import net.crescenthikari.popmovies.data.model.MovieVideo;
import net.crescenthikari.popmovies.data.repository.MovieRepository;
import net.crescenthikari.popmovies.features.moviedetail.adapter.ReviewAdapter;
import net.crescenthikari.popmovies.features.moviedetail.adapter.VideoAdapter;
import net.crescenthikari.popmovies.features.moviedetail.contract.VideoOnClickCallback;
import net.crescenthikari.popmovies.util.AnimationUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.plaidapp.util.ColorUtils;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static net.crescenthikari.popmovies.data.api.TmdbConstant.IMAGE_BASE_URL;
import static net.crescenthikari.popmovies.data.model.MoviePosterConstant.BACKDROP_SIZE;
import static net.crescenthikari.popmovies.data.model.MoviePosterConstant.POSTER_SIZE;

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

    private static final float SCRIM_ADJUSTMENT = 0.075f;

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

    @BindView(R.id.videos_rv)
    RecyclerView videosRv;

    @BindView(R.id.reviews_rv)
    RecyclerView reviewsRv;

    @BindView(R.id.movie_favorite_button)
    FloatingActionButton favoriteFab;

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

    VideoAdapter videoAdapter;
    ReviewAdapter reviewAdapter;

    VideoOnClickCallback videoOnClickCallback;

    MovieRepository movieRepository;
    CompositeDisposable disposables = new CompositeDisposable();
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            if (backdropImageView != null) {
                backdropImageView.setImageBitmap(bitmap);
            }

            final int twentyFourDip = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    24,
                    MovieDetailActivity.this.getResources().getDisplayMetrics()
            );
            Palette.from(bitmap)
                    .maximumColorCount(3)
                    .clearFilters() /* by default palette ignore certain hues
                        (e.g. pure black/white) but we don't want this. */
                    .setRegion(0, 0, bitmap.getWidth() - 1, twentyFourDip) /* - 1 to work around
                        https://code.google.com/p/android/issues/detail?id=191013 */
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            boolean isDark;
                            @ColorUtils.Lightness int lightness = ColorUtils.isDark(palette);
                            if (lightness == ColorUtils.LIGHTNESS_UNKNOWN) {
                                isDark = ColorUtils.isDark(
                                        bitmap,
                                        bitmap.getWidth() / 2,
                                        0
                                );
                            } else {
                                isDark = lightness == ColorUtils.IS_DARK;
                            }

                            if (!isDark) { // make back icon dark on light images
                                try {
                                    toolbar.getNavigationIcon()
                                            .setColorFilter(
                                                    ContextCompat.getColor(
                                                            MovieDetailActivity.this,
                                                            R.color.darkIconColor
                                                    ),
                                                    PorterDuff.Mode.MULTIPLY
                                            );
                                } catch (Exception e) {
                                    Log.e(TAG, "onGenerated: error", e);
                                }
                            }
                        }
                    });
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
                supportPostponeEnterTransition();
                ViewCompat.setTransitionName(posterImageView, KEY_MOVIE_POSTER_PATH);
                loadMoviePosterImage(POSTER_SIZE);
            } else {
                loadMoviePosterImage(POSTER_SIZE);
            }
        }

        setupToolbar();
        setupAdapters();
        setupViews();
        setupMovieRepository();
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

    public void setupAdapters() {
        videoAdapter = new VideoAdapter();
        videoOnClickCallback = new VideoOnClickCallback() {
            @Override
            public void openVideo(MovieVideo video) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/watch?v=" + video.getKey())
                ));
            }
        };
        videoAdapter.setVideoOnClickCallback(videoOnClickCallback);
        reviewAdapter = new ReviewAdapter();
    }

    private void setupViews() {
        appBarLayout.addOnOffsetChangedListener(this);
        titleView.setText(movieTitle);
        overviewView.setText(movieOverview);
        releaseDateView.setText(String.format(
                "Release date : %s",
                dateFormat.format(movieReleaseDate))
        );
        ratingView.setText(String.format(Locale.getDefault(), "%.1f", movieRatings));
        toolbarTitleView.setText(movieTitle);
        setupVideosRv();
        setupReviewsRv();
    }

    private void setupReviewsRv() {
        reviewsRv.setLayoutManager(new LinearLayoutManager(this));
        reviewsRv.setItemAnimator(new DefaultItemAnimator());
        reviewsRv.setAdapter(reviewAdapter);
    }

    private void setupVideosRv() {
        videosRv.setLayoutManager(new LinearLayoutManager(this));
        videosRv.setItemAnimator(new DefaultItemAnimator());
        videosRv.setAdapter(videoAdapter);
    }

    private void setupMovieRepository() {
        movieRepository = ((DataManager) getApplication()).getMovieRepository();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    private void loadMoviePosterImage(String width) {
        Picasso picasso = Picasso.with(this);
        picasso.setLoggingEnabled(true);
        picasso.load(IMAGE_BASE_URL + width + posterPath)
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
        picasso.load(IMAGE_BASE_URL + BACKDROP_SIZE + backdropPath)
                .into(target);
    }

    private void loadMovieDetails() {
        movieRepository.getMovieDetail(movieId)
                .subscribe(new Observer<MovieDetail>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        disposables.add(disposable);
                    }

                    @Override
                    public void onNext(@NonNull MovieDetail detail) {
                        durationView.setText(String.format(
                                Locale.getDefault(),
                                "%d minute(s)",
                                detail.getRuntime())
                        );
                        taglineView.setText(TextUtils.isEmpty(detail.getTagline())
                                ? "-" : detail.getTagline());
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
        movieRepository.getMovieReviews(movieId, 1)
                .subscribe(new Observer<List<MovieReview>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        disposables.add(disposable);
                    }

                    @Override
                    public void onNext(@NonNull List<MovieReview> reviews) {
                        reviewAdapter.clearThenAddReviews(reviews);
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

        movieRepository.getMovieVideos(movieId)
                .subscribe(new Observer<List<MovieVideo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        disposables.add(disposable);
                    }

                    @Override
                    public void onNext(@NonNull List<MovieVideo> videos) {
                        videoAdapter.addVideos(videos);
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
        // code from https://github.com/saulmm/CoordinatorBehaviorExample
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }


    // modified code from https://github.com/saulmm/CoordinatorBehaviorExample
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

    // modified code from https://github.com/saulmm/CoordinatorBehaviorExample
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
