package com.xxmassdeveloper.mpchartexample.Pro;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit = null;
    private static String BASE_URL="http://196.202.24.127:8080/";

    private static final Object LOCK = new Object();

    public static void clear() {
        synchronized (LOCK) {
            retrofit = null;
        }
    }
    public static MyServicesInterface getService() {


        if (retrofit == null) {

//            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(40, TimeUnit.SECONDS)
                    .writeTimeout(40, TimeUnit.SECONDS)
                    .build();

            System.out.println("retrofit instance will be created");
            System.out.println(BASE_URL);
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        } else {
            if (!retrofit.baseUrl().equals(BASE_URL)) {
                System.out.println("retrofit instance was created");

                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        return retrofit.create(MyServicesInterface.class);
    }

}
