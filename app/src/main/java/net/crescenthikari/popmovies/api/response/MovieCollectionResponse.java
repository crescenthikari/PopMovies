package net.crescenthikari.popmovies.api.response;

import com.google.gson.annotations.SerializedName;

import net.crescenthikari.popmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhammad Fiqri Muthohar on 6/22/17.
 */

public class MovieCollectionResponse {
    @SerializedName("page")
    private int page;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("results")
    private List<Movie> movies = new ArrayList<>();

    public int getPage() {
        return page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
