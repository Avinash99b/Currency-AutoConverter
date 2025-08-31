package com.avinash.autocurrencyconverter

import android.content.Context
import android.util.Log
import com.avinash.autocurrencyconverter.Objects.CurrencyResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.edit

class RequestManager {
    companion object {
        @JvmStatic
        fun loadExchangeRates(context: Context, runnable: Runnable) {
            CurrencyApi.currencyEndpoint.getConversionRates()
                .enqueue(object : Callback<CurrencyResponse> {
                    override fun onResponse(
                        call: Call<CurrencyResponse>, response: Response<CurrencyResponse>
                    ) {
                        if (response.isSuccessful) {
                            val sharedPreferences = Utils.getSharedPrefs(context)
                            val gson = Gson()
                            sharedPreferences.edit {
                                putString(
                                    "rates", gson.toJson(response.body()))
                            }
                        } else {
                            Log.e("Request Failed", response.raw().body?.string() ?: "failed again")
                        }

                        context.run { runnable }
                    }

                    override fun onFailure(
                        call: Call<CurrencyResponse?>, t: Throwable
                    ) {
                        Log.e("Failed", call.toString())
                        t.printStackTrace()
                    }

                })
        }
    }
}