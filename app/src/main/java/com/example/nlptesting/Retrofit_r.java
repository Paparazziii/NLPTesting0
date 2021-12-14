package com.example.nlptesting;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
public class Retrofit_r {
    private static Retrofit_r retrofit = null;
    public ChatActivity.APIService api;

    public ChatActivity.testService test;

    private static final String testUrl = "http://www.tuling123.com/";

    public static Retrofit_r getClient(String url){
        if (retrofit==null){
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();
            retrofit = new Retrofit_r(client,url);
        }
        return retrofit;
    }

    private  Retrofit_r(OkHttpClient client,String url){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        api = retrofit.create(ChatActivity.APIService.class);
        //test = retrofit.create(ChatActivity.testService.class);
    }
}
