package com.avinash.autocurrencyconverter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionServices
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.avinash.autocurrencyconverter.Components.ConverterCard
import com.avinash.autocurrencyconverter.ui.theme.AutoCurrencyConverterTheme
import com.avinash.autocurrencyconverter.ui.theme.SemiTransparent
import com.avinash.autocurrencyconverter.ui.theme.customFontFamily


class MainActivity : ComponentActivity() {

    val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->

            if (!granted) {
                Toast.makeText(
                    applicationContext,
                    "Notification Permission is required for this to work",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            AutoCurrencyConverterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        RequestManager.loadExchangeRates(
            applicationContext
        ) { Toast.makeText(this@MainActivity, "Loaded Yo", Toast.LENGTH_SHORT).show() }
        //Ask Notification Permission
        activityResultLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        if (!isAccessibilityEnabled(applicationContext)) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }
//TODO:For OCR
//        if (!Settings.canDrawOverlays(applicationContext)) {
//            val intent = Intent(
//                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                Uri.parse("package:${applicationContext.packageName}")
//            )
//            startActivity(intent)
//        }
//
//        startForegroundService(Intent(applicationContext, OverlayService::class.java))
    }
}


fun isAccessibilityEnabled(context: Context): Boolean {
    var accessibilityEnabled = 0
    val LIGHTFLOW_ACCESSIBILITY_SERVICE =
        "com.avinash.autocurrencyconverter.Services.AccessibilityService"
    val accessibilityFound = false
    val LOGTAG = "Log"
    try {
        accessibilityEnabled =
            Settings.Secure.getInt(context.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        Log.d(LOGTAG, "ACCESSIBILITY: $accessibilityEnabled")
    } catch (e: SettingNotFoundException) {
        Log.d(LOGTAG, "Error finding setting, default accessibility to not found: " + e.message)
    }

    val mStringColonSplitter = SimpleStringSplitter(':')

    if (accessibilityEnabled == 1) {
        Log.d(LOGTAG, "***ACCESSIBILIY IS ENABLED***: ")

        val settingValue = Settings.Secure.getString(
            context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        Log.d(LOGTAG, "Setting: $settingValue")
        if (settingValue != null) {
            val splitter = mStringColonSplitter
            splitter.setString(settingValue)
            while (splitter.hasNext()) {
                val accessabilityService = splitter.next()
                Log.d(LOGTAG, "Setting: $accessabilityService")
                if (accessabilityService.contains(
                        LIGHTFLOW_ACCESSIBILITY_SERVICE, ignoreCase = true
                    )
                ) {
                    Log.d(LOGTAG, "We've found the correct setting - accessibility is switched on!")
                    return true
                }
            }
        }

        Log.d(LOGTAG, "***END***")
    } else {
        Log.d(LOGTAG, "***ACCESSIBILIY IS DISABLED***")
    }
    return accessibilityFound
}

@Composable
fun HomeScreen(modifier: Modifier) {

    Box(modifier= Modifier.fillMaxSize().background(Color.White))
    val circleSize = LocalConfiguration.current.screenWidthDp.toFloat()*1.3
    Box(modifier = Modifier.wrapContentSize(unbounded = true)
        .offset(x = -(circleSize / 2).dp, y = (-circleSize/1.5).dp)) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(2 * circleSize.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE6EDFB), // top - light purple/blue
                            Color(0xFFF8FAFF)  // bottom - very light grey
                        ),
                    ), shape = CircleShape
                )
        )
    }


    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Currency Converter",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp),
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            fontFamily = customFontFamily,
            fontWeight = FontWeight.Bold,
            color = Color(0xff1F2261),
        )

        Text(
            text = "Check live rates, set rate alerts, receive notifications and more.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            fontFamily = customFontFamily,
            color = Color(0xff808080),
        )

    }

    Box(modifier = Modifier.fillMaxSize()) {

        ConverterCard(modifier = Modifier.align(Alignment.Center))
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AutoCurrencyConverterTheme {
        HomeScreen(Modifier)
    }
}