package com.eassychat.retorfit;

import java.io.IOException;

import io.paperdb.Paper;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public interface RetrofitClientInstance {

      String BASE_URL = "https://eassychat.herokuapp.com/eassyChat/api/v1/";

    OkHttpClient OK_HTTP_CLIENT = new OkHttpClient().newBuilder()
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + Paper.book().read(APIConstance.PAPER_TOKEN))
                            .build();
                    return chain.proceed(request);
                }
            }).build();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    Retrofit retrofitwithheader = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OK_HTTP_CLIENT)
            .addConverterFactory(GsonConverterFactory.create())
            .build();



}

