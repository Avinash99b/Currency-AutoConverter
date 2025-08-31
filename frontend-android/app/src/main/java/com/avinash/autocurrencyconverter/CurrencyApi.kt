package com.avinash.autocurrencyconverter

import com.avinash.autocurrencyconverter.Objects.CurrencyResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object CurrencyApi {

    val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val BASE_URL = "https://currency-converter-backend-xaha.onrender.com/"

    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val currencyEndpoint by lazy {
        retrofit.create(CurrencyEndpoint::class.java)
    }

    interface CurrencyEndpoint {
        @GET("getRates.php")
        fun getConversionRates(): Call<CurrencyResponse>

        @GET("cronJob.php")
        fun runCronJob(): Call<String>
    }
}