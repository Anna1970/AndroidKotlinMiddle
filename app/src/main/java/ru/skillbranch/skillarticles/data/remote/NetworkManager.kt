package ru.skillbranch.skillarticles.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.skillbranch.skillarticles.AppConfig
import ru.skillbranch.skillarticles.data.JsonConverter
import ru.skillbranch.skillarticles.data.remote.interceptors.ErrorStatusInterceptor
import ru.skillbranch.skillarticles.data.remote.interceptors.NetworkStatusInterceptor
import ru.skillbranch.skillarticles.data.remote.interceptors.TokenAuthenticator
import java.util.concurrent.TimeUnit

object NetworkManager {
    val api : RestService by lazy {
        //client
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient().newBuilder()
            .readTimeout(2, TimeUnit.SECONDS) //socket timeout (GET)
            .writeTimeout(5, TimeUnit.SECONDS) //socket timeout (Post, Put, etc)
            .authenticator(TokenAuthenticator())        // refresh token if response code == 401
            .addInterceptor(NetworkStatusInterceptor()) //interceptor network status
            .addInterceptor(logging) //intercept req/res for logging
            .addInterceptor(ErrorStatusInterceptor()) //intercept status errors
            .build()

        //json converter
        val moshi = Moshi.Builder()
            .add(JsonConverter.DateAdapter()) //convert long timestamp to Date
            .add(KotlinJsonAdapterFactory()) //convert json to class be reflection
            .build()

        //retrofit
        val retrofit = Retrofit.Builder()
            .client(client) // set http client
            .addConverterFactory(MoshiConverterFactory.create(moshi)) //set json converter/parser
            .baseUrl(AppConfig.BASE_URL)
            .build()

        retrofit.create(RestService::class.java)
    }
}