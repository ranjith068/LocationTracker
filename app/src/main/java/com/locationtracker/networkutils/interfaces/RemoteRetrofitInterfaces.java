package com.locationtracker.networkutils.interfaces;


import com.locationtracker.models.DurationModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by rajesh on 29/4/17.
 */

public interface RemoteRetrofitInterfaces {

    @GET("json?")
    Call<DurationModel> getDuration(@Query("origins") String origins,@Query("destinations") String destinations,@Query("key") String key);
}
