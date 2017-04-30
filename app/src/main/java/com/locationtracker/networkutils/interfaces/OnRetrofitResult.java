package com.locationtracker.networkutils.interfaces;


import com.locationtracker.models.DurationModel;

import java.util.List;

import retrofit2.Response;

/**
 * Created by rajesh on 29/4/17.
 */

public interface OnRetrofitResult {
    public void onDurationResult(boolean result, Response<DurationModel> code);
}
