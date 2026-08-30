package com.journal.life5to9.data.remote;

import com.journal.life5to9.data.remote.dto.InsightsRequest;
import com.journal.life5to9.data.remote.dto.InsightsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface InsightsApi {
    @POST("api/v1/insights")
    Call<InsightsResponse> getInsights(@Body InsightsRequest request);
}
