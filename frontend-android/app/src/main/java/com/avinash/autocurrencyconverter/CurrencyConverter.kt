package com.avinash.autocurrencyconverter

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.avinash.autocurrencyconverter.Objects.CurrencyResponse
import com.google.gson.Gson

class CurrencyConverter {

    companion object{
        @JvmStatic
        fun getRates(sharedPreferences: SharedPreferences):Map<String,String>{
            val gson = Gson()

            val ratesResponse = gson.fromJson(sharedPreferences.getString("rates",""),CurrencyResponse::class.java)

            return ratesResponse?.conversionRates ?: HashMap()
        }

        @JvmStatic
        fun areRatesAvailable(sharedPreferences: SharedPreferences):Boolean{
            if(sharedPreferences.getString("rates","").equals("")){
                return false
            }else{
                if(hasOneHourPassed(sharedPreferences.getLong("last_update_time",1),System.currentTimeMillis()))return false
            }
            return true
        }

        fun getDefaultCurrency(context: Context): String{
            val currency = Utils.getSharedPrefs(context).getString("default_currency","INR")
            return currency ?:""
        }

        fun setDefaultCurrency(context: Context,currency: String){
            Utils.getSharedPrefs(context).edit{
                putString("default_currency",currency)
            }
        }

        @JvmStatic
        fun convert(sharedPreferences: SharedPreferences, sourceAmount:Float,sourceCurrency:String,targetCurrency:String):Float{
            val conversionRates = getRates(sharedPreferences)
            val sourceCurrencyExchangeRate = getExchangeRateByCurrencyName(conversionRates,sourceCurrency)?.toFloat()
            val targetCurrencyExchangeRate = getExchangeRateByCurrencyName(conversionRates,targetCurrency)?.toFloat()

            if(targetCurrencyExchangeRate==null || sourceCurrencyExchangeRate==null)return 0f
            val normalizedCurrency = sourceAmount/sourceCurrencyExchangeRate
            val converted = normalizedCurrency* targetCurrencyExchangeRate

            return converted;
        }

        @JvmStatic
        fun getCurrencyNames(sharedPreferences: SharedPreferences): Array<String>{
            val currencies = getRates(sharedPreferences)

            return  currencies.map { it.key }.toTypedArray()
        }

        private fun getExchangeRateByCurrencyName(conversionRates:Map<String,String>, currency: String): String? {
            return conversionRates[currency];
        }

        private fun hasOneHourPassed(startTime: Long, endTime: Long): Boolean {
            val oneHourInMillis = 60 * 60 * 1000 // 3600000 ms
            return kotlin.math.abs(endTime - startTime) >= oneHourInMillis
        }

    }
}