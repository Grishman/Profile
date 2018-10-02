package com.grishman.profiletest.network

import com.grishman.profiletest.model.CardsResponse
import com.grishman.profiletest.model.ProfileResponse
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


/**
 * API interface for Openpay backend
 */
interface OpenpayService {

    @GET("profile.json")
    fun getProfileInfo(): Observable<ProfileResponse>

    @GET("cards.json")
    fun getCards(): Observable<CardsResponse>

    companion object {
        fun create(): OpenpayService {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .baseUrl("https://s3-ap-southeast-2.amazonaws.com/openpay-mobile-test/")
                    .build()

            return retrofit.create(OpenpayService::class.java)
        }
    }

}