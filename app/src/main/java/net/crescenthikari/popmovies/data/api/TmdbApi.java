package net.crescenthikari.popmovies.data.api;

import net.crescenthikari.popmovies.data.api.response.MovieCollectionResponse;
import net.crescenthikari.popmovies.data.api.response.MovieReviewResponse;
import net.crescenthikari.popmovies.data.api.response.MovieVideoResponse;
import net.crescenthikari.popmovies.data.model.MovieDetail;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Muhammad Fiqri Muthohar on 6/21/17.
 */

public interface TmdbApi {
    @GET("movie/{movie_id}")
    Observable<Response<MovieDetail>> getMovieDetail(@Path("movie_id") String movieId);

    @GET("movie/now_playing")
    Observable<Response<MovieCollectionResponse>> getNowPlayingMovies(@Query("page") int page);

    @GET("movie/popular")
    Observable<Response<MovieCollectionResponse>> getMostPopularMovies(@Query("page") int page);

    @GET("movie/top_rated")
    Observable<Response<MovieCollectionResponse>> getHighestRatedMovies(@Query("page") int page);

    @GET("movie/{movie_id}/videos")
    Observable<Response<MovieVideoResponse>> getMovieVideos(@Path("movie_id") String movieId);

    @GET("movie/{movie_id}/reviews")
    Observable<Response<MovieReviewResponse>> getMovieReviews(@Path("movie_id") String movieId, @Query("page") int page);
}
