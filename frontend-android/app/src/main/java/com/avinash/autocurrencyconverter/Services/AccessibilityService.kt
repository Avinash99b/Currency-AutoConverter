package com.avinash.autocurrencyconverter.Services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.avinash.autocurrencyconverter.CurrencyConverter
import com.avinash.autocurrencyconverter.CurrencyExtractor
import com.avinash.autocurrencyconverter.Utils


class AccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.getEventType() === AccessibilityEvent.TYPE_VIEW_LONG_CLICKED) {
            val textList: MutableList<CharSequence>? = event.getText()
            Log.e("Text", textList?.toTypedArray().contentToString())

            val sharedPreferences = Utils.getSharedPrefs(applicationContext)
            textList?.forEach { text ->
                CurrencyExtractor.extract(text.toString()).forEach { match ->
                    Toast.makeText(
                        applicationContext,
                        "Converted ${match.currencyCode} ${match.amount} to ${CurrencyConverter.getDefaultCurrency(applicationContext)} ${
                            CurrencyConverter.convert(
                                sharedPreferences, match.amount.toFloat(), match.currencyCode,
                                CurrencyConverter.getDefaultCurrency(applicationContext)
                            )
                        }", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        CurrencyExtractor.init(applicationContext)
        val info = AccessibilityServiceInfo()
        info.apply {
            // Set the type of events that this service wants to listen to. Others
            // aren't passed to this service.
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK or AccessibilityEvent.TYPE_VIEW_FOCUSED

            // Set the type of feedback your service provides.
            feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL

            // Default services are invoked only if no package-specific services are
            // present for the type of AccessibilityEvent generated. This service is
            // app-specific, so the flag isn't necessary. For a general-purpose
            // service, consider setting the DEFAULT flag.

            // flags = AccessibilityServiceInfo.DEFAULT;

            notificationTimeout = 100
        }

        this.serviceInfo = info
    }

}