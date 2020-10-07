package com.skailab.nikabuy.services

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://nikabuy-m-v2.azurewebsites.net/api/"
//private const val BASE_URL = "https://2e083c42c204.ngrok.io/api/"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val authInterceptor = Interceptor {chain->
    val newUrl = chain.request().url
        .newBuilder()
        .addQueryParameter("apiKey", "85264347-e30f-4872-877b-96847448eec8-GX2Lm0Ck7qy-S5/fpNoGcmlTNF]38Co")
        .build()

    val newRequest = chain.request()
        .newBuilder()

        .url(newUrl)
        .build()

    chain.proceed(newRequest)
}
private val loggingInterceptor =  HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
private var okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(1, TimeUnit.MINUTES)
    .writeTimeout(30, TimeUnit.MINUTES)
    .addInterceptor(authInterceptor)
    .addInterceptor(loggingInterceptor)
    .build()
val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(okHttpClient)
    .baseUrl(BASE_URL)
   // .addConverterFactory(GsonConverterFactory.create())
    .build()
