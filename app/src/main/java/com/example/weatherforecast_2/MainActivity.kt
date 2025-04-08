package com.example.weatherforecast_2

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val apiKey = "3a40caaed30624dd3ed13790e371b4bd"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val main = findViewById<ConstraintLayout>(R.id.main)
        val editCity = findViewById<EditText>(R.id.edt_City)
        val tvCity = findViewById<TextView>(R.id.tv_City)
        val tvTemperature = findViewById<TextView>(R.id.tv_Temperature)
        val tvDescription = findViewById<TextView>(R.id.tv_Description)
        val btnGetWeather = findViewById<Button>(R.id.btn_get_weather)

        editCity.inputType = InputType.TYPE_CLASS_TEXT

        val inputFilter = InputFilter { source, start, end, dist, dstart, dend ->
            if (source.matches(Regex("[a-zA-Zа-яА-Я -]"))) source else ""
        }

        editCity.filters = arrayOf(
            InputFilter.LengthFilter(20),
            inputFilter
        )

        btnGetWeather.setOnClickListener {
            val city = editCity.text.toString().trim()
            if (city.isEmpty()) {
                Toast.makeText(this, "Введите город", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val weather = withContext(Dispatchers.IO) {
                        RetrofitClient.weatherApi.getWeather(city, apiKey)
                    }
                    editCity.setText("")
                    tvCity.text = "${weather.name}"
                    tvTemperature.text = "${weather.main.temp}°"
                    tvDescription.text = "${weather.weather[0].description}"
                    if (tvDescription.text.toString().contains("cloud", ignoreCase = true)) {
                        main.setBackgroundResource(R.drawable.cloudy)
                    }
                    if (tvDescription.text.toString().contains("sky", ignoreCase = true)) {
                        main.setBackgroundResource(R.drawable.clear_sky)
                    }
                    if (tvDescription.text.toString().contains("rain", ignoreCase = true)) {
                        main.setBackgroundResource(R.drawable.rain)
                    }
                    if (tvDescription.text.toString().contains("snow", ignoreCase = true)) {
                        main.setBackgroundResource(R.drawable.snow)
                    }
                    if (tvDescription.text.toString().contains("sun", ignoreCase = true)) {
                        main.setBackgroundResource(R.drawable.sun)
                    }
                } catch (e:Exception) {
                    Toast.makeText(this@MainActivity, "Такого города не найдено", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}