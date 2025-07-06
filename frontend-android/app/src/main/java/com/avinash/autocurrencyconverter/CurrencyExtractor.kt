package com.avinash.autocurrencyconverter

import android.content.Context
import org.json.JSONObject
import java.util.regex.Pattern


data class CurrencyMatch(val currencyCode: String, val amount: String)

object CurrencyExtractor {
    private lateinit var symbolToCurrency: Map<String, String>
    private lateinit var currencyRegex: Pattern
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return  // Only initialize once

        val jsonStream = context.resources.openRawResource(R.raw.currency_logo_mappings)
        val jsonString = jsonStream.bufferedReader().use { it.readText() }

        val codeToSymbol = JSONObject(jsonString)
        val tempMap = mutableMapOf<String, String>()
        val regexParts = mutableListOf<String>()

        // Reverse map: symbol → currency code
        codeToSymbol.keys().forEach { code ->
            val symbol = codeToSymbol.getString(code).trim()
            if (symbol.isNotBlank()) {
                tempMap[symbol] = code
                regexParts.add(Regex.escape(symbol))
            }
        }

        symbolToCurrency = tempMap

        // Updated regex to support numbers like ₹55,118.00
        val combinedRegex = """(${regexParts.joinToString("|")})\s?([0-9]{1,3}(?:,[0-9]{3})*(?:\.[0-9]+)?|[0-9]+(?:\.[0-9]+)?)"""
        currencyRegex = Pattern.compile(combinedRegex)

        initialized = true
    }

    fun extract(text: String): List<CurrencyMatch> {
        if (!initialized) throw IllegalStateException("CurrencyExtractor must be initialized first")

        val matches = mutableListOf<CurrencyMatch>()
        val matcher = currencyRegex.matcher(text)

        while (matcher.find()) {
            val symbol = matcher.group(1).trim()
            val rawAmount = matcher.group(2)
            val amount = rawAmount.replace(",", "")

            val code = symbolToCurrency[symbol] ?: "UNKNOWN"
            matches.add(CurrencyMatch(code, amount))
        }

        return matches
    }
}