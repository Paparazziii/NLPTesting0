package com.example.nlptesting;

import java.net.HttpURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static final String Base_URL = "https://language.cs.ucdavis.edu/";
    private static String testURL = "https://reqres.in";


    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(Base_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC);

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static <Decode> Decode createService(Class<Decode> serviceClass){
//        if(!httpClient.interceptors().contains(loggingInterceptor)){
  //          httpClient.addInterceptor(loggingInterceptor);
            builder.client(httpClient.build());
    //    }
        return retrofit.create(serviceClass);
    }
}
