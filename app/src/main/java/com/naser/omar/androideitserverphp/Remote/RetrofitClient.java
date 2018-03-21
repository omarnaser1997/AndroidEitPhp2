package com.naser.omar.androideitserverphp.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by OmarNasser on 2/19/2018.
 */

public class RetrofitClient {

    private static Retrofit retrofit =null;
    public static  Retrofit getRetrofit(String baseUrl)
    {
        if (retrofit == null)
        {
            retrofit =new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

        }
        return retrofit;
    }

}
