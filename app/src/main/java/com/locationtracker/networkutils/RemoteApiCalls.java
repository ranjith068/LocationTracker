package com.locationtracker.networkutils;

import android.annotation.SuppressLint;
import android.util.Log;


import com.locationtracker.models.DurationModel;
import com.locationtracker.networkutils.interfaces.OnRetrofitResult;
import com.locationtracker.networkutils.interfaces.RemoteRetrofitInterfaces;
import com.locationtracker.utils.AppConstant;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rajesh on 29/4/17.
 */


//AIzaSyBubk9N9V4CHeYo53t2h7PhZVTkumnug90

@SuppressLint("LongLogTag")
public final class RemoteApiCalls {
    private static final String TAG = "RemoteApiCalls";

    public static final class Builder {
        RemoteRetrofitInterfaces mService;
        Retrofit mRetrofit;

        OnRetrofitResult OnRetrofitResultInterface;

        public Builder remoteApiCall(OnRetrofitResult OnRetrofitResultInterface) {


            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS).addInterceptor(interceptor)
                    .build();

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(AppConstant.HOST).addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            mService = mRetrofit.create(RemoteRetrofitInterfaces.class);
            this.OnRetrofitResultInterface = OnRetrofitResultInterface;
            return this;
        }


        /*login Info*/
        public void durationCall(String origin,String destination) {




            Call<DurationModel> api = mService.getDuration(origin,destination,AppConstant.DISTANCEMATRIXKEY);


            api.enqueue(new Callback<DurationModel>() {
                @Override
                public void onResponse(Call<DurationModel> responseCall, Response<DurationModel> response) {

//                    Log.d("response" ,response.body().toString());

                    if (response.body().status.equals("OK")) {
                        if(response.body().rows!=null||response.body().rows.size()>0) {
                            if(response.body().rows.get(0).elements.size()>0) {
//                                response.body().rows.get(0).elements.get(0).duration.text
                                OnRetrofitResultInterface.onDurationResult(true, response);
                            }else{
                                OnRetrofitResultInterface.onDurationResult(false, null);

                            }
                        }else{
                            OnRetrofitResultInterface.onDurationResult(false, null);

                        }
                    } else {
                        OnRetrofitResultInterface.onDurationResult(false, null);


                    }


                }

                @Override
                public void onFailure(Call<DurationModel> responseCall, Throwable t) {
                    t.printStackTrace();
                    OnRetrofitResultInterface.onDurationResult(false, null);

                }


            });
        }
    }



}