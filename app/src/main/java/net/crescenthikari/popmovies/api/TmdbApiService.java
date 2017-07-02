package net.crescenthikari.popmovies.api;

import com.google.gson.Gson;

import net.crescenthikari.popmovies.BuildConfig;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static net.crescenthikari.popmovies.api.TmdbConstant.API_BASE_URL;

/**
 * Created by Muhammad Fiqri Muthohar on 6/21/17.
 */

public class TmdbApiService {
    private static final String BASE_URL = API_BASE_URL;
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;

    private static Gson gson;
    private static OkHttpClient client;

    private static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    private static File getCacheDirectory() {
        File dir = new File("cache");
        return dir;
    }

    private static OkHttpClient getClient() {
        if (client == null) {
            final long cacheSize = 25 * 1024L * 1024; // 25 MB Cache
            Cache cache = new Cache(getCacheDirectory(), cacheSize);
            final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            HttpUrl url = request.url().newBuilder()
                                    .addQueryParameter("api_key", API_KEY)
                                    .build();
                            request = request.newBuilder().url(url).build();

                            return chain.proceed(request);
                        }
                    }).build();
        }
        return client;
    }

    public static TmdbApi open() {
        OkHttpClient client = getClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(TmdbApi.class);
    }
}
