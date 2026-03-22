package com.btl_ptit.hotelbooking.data.remote;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {

    // keys are managed in local.properties
    private static final String BASE_URL = com.btl_ptit.hotelbooking.BuildConfig.SUPABASE_URL;
    private static final String API_KEY = com.btl_ptit.hotelbooking.BuildConfig.SUPABASE_KEY;

    private static volatile Retrofit retrofit;
    private static final OkHttpClient client = createClient();

    private static OkHttpClient createClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {

                    Request original = chain.request();
                    // 1. Lấy Header Authorization mà bạn truyền từ LoginActivity (@Header)
                    String authHeader = original.header("Authorization");

                    Request.Builder builder = original.newBuilder()
                            .header("apikey", API_KEY) // Luôn phải có
                            .header("Content-Type", "application/json");

                    // 2. NẾU ở Activity có truyền Bearer, thì ép nó vào Request cuối cùng
                    if (authHeader != null) {
                        builder.header("Authorization", authHeader);
                    }

                    Request request = builder.build();

                    // DEBUG: In ra để xem Header có thực sự tồn tại trước khi gửi đi không
                    System.out.println("DEBUG APIKEY: " + request.header("apikey"));
                    System.out.println("DEBUG AUTH: " + request.header("Authorization"));

                    return chain.proceed(request);
                })
                .build();
    }

    private static Retrofit getRetrofit() {
        if(retrofit == null) {
            synchronized (SupabaseClient.class) {
                if(retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static <T> T createService(Class<T> serviceClass) {
        return getRetrofit().create(serviceClass);
    }


}


