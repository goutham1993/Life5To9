package com.journal.life5to9.data.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.journal.life5to9.BuildConfig;
import com.journal.life5to9.data.remote.InsightsApi;
import com.journal.life5to9.data.remote.InsightsException;
import com.journal.life5to9.data.remote.dto.InsightsRequest;
import com.journal.life5to9.data.remote.dto.InsightsResponse;
import com.journal.life5to9.data.remote.dto.InsightsResponseDeserializer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InsightsRepository {
    private static final String TAG = "InsightsRepository";
    private static final int CONNECT_TIMEOUT_SECONDS = 30;
    private static final int READ_TIMEOUT_SECONDS = 60;
    private static final int WRITE_TIMEOUT_SECONDS = 60;

    private final InsightsApi api;

    public InsightsRepository() {
        this(createApi());
    }

    InsightsRepository(InsightsApi api) {
        this.api = api;
    }

    private static InsightsApi createApi() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BASIC
                : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(InsightsResponse.class, new InsightsResponseDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.INSIGHTS_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(InsightsApi.class);
    }

    public InsightsResponse fetchInsights(InsightsRequest request) throws InsightsException {
        try {
            Response<InsightsResponse> response = api.getInsights(request).execute();
            if (!response.isSuccessful()) {
                Log.w(TAG, "Insights request failed with HTTP " + response.code());
                throw new InsightsException("Insights service returned an error (" + response.code() + ").");
            }

            InsightsResponse body = response.body();
            if (body == null || !body.hasContent()) {
                throw new InsightsException("Insights service returned an empty response.");
            }
            return body;
        } catch (InsightsException e) {
            throw e;
        } catch (IOException e) {
            Log.w(TAG, "Insights request failed", e);
            throw new InsightsException(
                    "Couldn't reach the insights service. Check that you're on the same network.",
                    e
            );
        } catch (Exception e) {
            Log.e(TAG, "Unexpected insights error", e);
            throw new InsightsException("Something went wrong while generating insights.", e);
        }
    }
}
