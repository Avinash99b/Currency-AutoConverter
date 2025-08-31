package com.avinash.autocurrencyconverter

import android.content.Context
import android.content.SharedPreferences

object Utils {
    @JvmStatic
    fun getSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences("app", Context.MODE_PRIVATE)
    }
}