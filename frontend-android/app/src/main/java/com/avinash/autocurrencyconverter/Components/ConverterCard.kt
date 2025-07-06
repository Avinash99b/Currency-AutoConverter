package com.avinash.autocurrencyconverter.Components

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.util.fastCbrt
import com.avinash.autocurrencyconverter.CurrencyConverter
import com.avinash.autocurrencyconverter.Utils
import com.avinash.autocurrencyconverter.ui.theme.customFontFamily
import kotlinx.coroutines.Runnable

@Composable
fun ConverterCard(modifier: Modifier) {
    val sharedPrefs = LocalContext.current.getSharedPreferences("app", Context.MODE_PRIVATE)
    Card(
        elevation = CardDefaults.cardElevation(12.dp),
        modifier = modifier
            .defaultMinSize(minHeight = 0.3 * LocalConfiguration.current.screenHeightDp.dp)
            .fillMaxWidth(0.9f),
        shape = RoundedCornerShape(28.dp)
    ) {

        Column(modifier= Modifier
            .background(shape = RoundedCornerShape(28.dp), color = Color.White)
            .padding(bottom = 12.dp)) {
            Text(
                "Amount", color = Color(0xFF989898), modifier = Modifier.padding(15.dp)
            )

            val inputValue = remember { mutableStateOf("100") }
            val inputCurrency = remember { mutableStateOf("INR") }
            CurrencyInputRow(inputCurrency, inputValue)


            val outputCurrency = remember { mutableStateOf("INR") }
            val outputValue = remember {
                derivedStateOf {
                    CurrencyConverter.convert(
                        sharedPrefs,
                        inputValue.value.toFloat(),
                        inputCurrency.value,
                        outputCurrency.value
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            CurrencyOutputRow(outputCurrency, outputValue)

            Spacer(modifier = Modifier.height(18.dp))
            DefaultCurrencyRow()


        }
    }
}

interface OnCurrencySelectedListener {
    fun onCurrencySelected(currency: String)
}

fun showCurrencyChangeDialog(
    context: Context, onCurrencySelectedListener: OnCurrencySelectedListener
) {
    val currencyNames = CurrencyConverter.getCurrencyNames(Utils.getSharedPrefs(context))
    var selectedItem = 0
    AlertDialog.Builder(context).setSingleChoiceItems(currencyNames, 0) { dialog, which ->
        selectedItem = which
    }.setPositiveButton("OK") { dialog, _ ->
        Toast.makeText(context, "Selected: ${currencyNames[selectedItem]}", Toast.LENGTH_SHORT)
            .show()
        dialog.dismiss()
        onCurrencySelectedListener.onCurrencySelected(currencyNames[selectedItem])
    }.setNegativeButton("Cancel", null).show()

}

@Composable
fun CurrencyInputRow(
    labelState: MutableState<String>, inputState: MutableState<String>
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            color = Color.Black, text = labelState.value, modifier = Modifier
                .weight(1f)
                .clickable {
                    showCurrencyChangeDialog(context, object : OnCurrencySelectedListener {
                        override fun onCurrencySelected(currency: String) {
                            labelState.value = currency
                        }

                    })
                }, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )

        TextField(
            value = inputState.value,
            onValueChange = { newValue ->
                if (!newValue.isEmpty() || newValue.toFloatOrNull() != null) {
                    inputState.value = newValue
                } else {
                    inputState.value = "0"
                }
            },
            modifier = Modifier
                .weight(2f)
                .height(50.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.End, color = Color.Black, fontWeight = FontWeight.Bold
            ),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xffEFEFEF),
                unfocusedContainerColor = Color(0xffEFEFEF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}


@Composable
fun CurrencyOutputRow(stringState: MutableState<String>, currencyState: State<Float>) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            color = Color.Black,
            text = stringState.value,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    showCurrencyChangeDialog(context, object : OnCurrencySelectedListener {
                        override fun onCurrencySelected(currency: String) {
                            stringState.value = currency
                        }

                    })
                },
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )

        Box(
            modifier = Modifier
                .weight(2f)
                .height(50.dp)
                .background(Color(0xFFEFEFEF), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp), contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = String.format("%.2f", currencyState.value), // formatted properly
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Black, fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun DefaultCurrencyRow() {
    val context = LocalContext.current
    var defaultCurrency by remember {
        mutableStateOf(CurrencyConverter.getDefaultCurrency(context))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            color = Color.Black,
            text = "Default Currency",
            modifier = Modifier
                .weight(1f)
                .clickable {
                    showCurrencyChangeDialog(context, object : OnCurrencySelectedListener {
                        override fun onCurrencySelected(currency: String) {
                            CurrencyConverter.setDefaultCurrency(context, currency)
                            defaultCurrency = currency
                        }

                    })
                },
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )

        Box(
            modifier = Modifier
                .weight(2f)
                .height(50.dp)
                .background(Color(0xFFEFEFEF), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp), contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = defaultCurrency, // formatted properly
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Black, fontWeight = FontWeight.Bold
                )
            )
        }
    }
}


@Preview
@Composable
fun ConverterCardPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        ConverterCard(Modifier.align(Alignment.Center))
    }
}