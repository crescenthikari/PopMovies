package net.crescenthikari.popmovies.features.movieslist.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.crescenthikari.popmovies.R;
import net.crescenthikari.popmovies.features.movieslist.contract.OnMovieItemClickCallback;
import net.crescenthikari.popmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.crescenthikari.popmovies.api.TmdbConstant.IMAGE_BASE_URL;
import static net.crescenthikari.popmovies.model.MoviePosterConstant.BACKDROP_SIZE;
import static net.crescenthikari.popmovies.model.MoviePosterConstant.POSTER_SIZE;

/**
 * Created by Muhammad Fiqri Muthohar on 6/22/17.
 */

public class MovieCollectionAdapter
        extends RecyclerView.Adapter<MovieCollectionAdapter.MovieViewHolder> {

    OnMovieItemClickCallback movieItemClickCallback;
    private List<Movie> movies;

    public MovieCollectionAdapter() {
        movies = new ArrayList<>();
    }

    @Override
    public MovieCollectionAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_movie, null);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieCollectionAdapter.MovieViewHolder holder, int pos) {
        holder.setData(movies.get(pos));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovieItemClickCallback(OnMovieItemClickCallback callback) {
        movieItemClickCallback = callback;
    }

    public void removeMovieItemClickCallback() {
        movieItemClickCallback = null;
    }

    public void replaceMovieList(List<Movie> movieList) {
        movies.clear();
        movies.addAll(movieList);
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_poster_image)
        ImageView posterImageView;

        @BindView(R.id.movie_title)
        TextView titleView;

        Target picassoTarget;

        Palette.PaletteAsyncListener asyncListener;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int pos = getAdapterPosition();
                    Movie movie = movies.get(pos);
                    if (movie != null && movieItemClickCallback != null) {
                        movieItemClickCallback.onMovieItemClicked(posterImageView, movie);
                    }
                }
            });
            asyncListener = new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch textSwatch = palette.getVibrantSwatch();
                    if (textSwatch == null) {
                        return;
                    }
                    titleView.setBackgroundColor(textSwatch.getRgb());
                    titleView.setTextColor(textSwatch.getTitleTextColor());
                }
            };
            picassoTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    setPosterBitmap(bitmap);
                    Palette.from(bitmap)
                            .generate(asyncListener);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    setPosterDrawable(errorDrawable);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    setPosterDrawable(placeHolderDrawable);
                }
            };
        }

        void setData(Movie movie) {
            titleView.setText(movie.getTitle());
            Picasso
                    .with(itemView.getContext())
                    .load(IMAGE_BASE_URL + POSTER_SIZE + movie.getPosterPath())
                    .placeholder(R.drawable.ic_local_movies_blue_grey_400_48dp)
                    .error(R.drawable.ic_local_movies_blue_grey_400_48dp)
                    .into(picassoTarget);
            // prefetch movie backdrop image to improve image load time in movie detail view
            Picasso.with(itemView.getContext())
                    .load(IMAGE_BASE_URL + BACKDROP_SIZE + movie.getBackdropPath())
                    .fetch();
        }

        void setPosterDrawable(Drawable drawable) {
            if (posterImageView != null) {
                posterImageView.setScaleType(ImageView.ScaleType.CENTER);
                posterImageView.setImageDrawable(drawable);
            }
        }

        void setPosterBitmap(Bitmap bitmap) {
            if (posterImageView != null) {
                posterImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                posterImageView.setImageBitmap(bitmap);
            }
        }
    }
}
