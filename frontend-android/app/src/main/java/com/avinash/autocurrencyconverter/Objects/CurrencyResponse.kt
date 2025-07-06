package com.avinash.autocurrencyconverter.Objects

import com.google.gson.annotations.SerializedName

data class CurrencyResponse(
    @SerializedName("conversion_rates")
    val conversionRates: Map<String, String>
)
