package com.mkrdeveloper.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mkrdeveloper.weatherapp.adapters.RvAdapter
import com.mkrdeveloper.weatherapp.databinding.ActivityMainBinding
import com.mkrdeveloper.weatherapp.databinding.BottomSheetBinding
import com.mkrdeveloper.weatherapp.forecastModels.Forecast
import com.mkrdeveloper.weatherapp.forecastModels.WeatherForecastData
import com.mkrdeveloper.weatherapp.models.WeatherData
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val CITY: String = "poznan"
    private val BASE_URL: String = "https://api.openweathermap.org/data/2.5/"


    private lateinit var binding: ActivityMainBinding
    private lateinit var binding2: BottomSheetBinding
    lateinit var forecastArray : ArrayList<Forecast>

    lateinit var dialog : BottomSheetDialog

    //val API : String = "bef83524693fb6717b1bc60c2a919263"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding2 = BottomSheetBinding.inflate(layoutInflater)

        dialog = BottomSheetDialog(this,R.style.BottomSheetTheme)

        dialog.setContentView(binding2.root)

        setContentView(binding.root)

        binding.subLayout.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        forecastArray = arrayListOf()



        getWeather()
        getForecast()

        binding.imgForecast.setOnClickListener {
            openForecast()
        }

    }

    @SuppressLint("InflateParams")
    private fun openForecast() {


        //val openDialog = layoutInflater.inflate(R.layout.bottom_sheet,null)


        binding2.rvForecast.setHasFixedSize(true)
        binding2.rvForecast.layoutManager = GridLayoutManager(this,1, RecyclerView.HORIZONTAL,false)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.show()


    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun getForecast() {


        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequest::class.java)



        GlobalScope.launch(Dispatchers.IO) {

            val response: Response<WeatherForecastData> =
                api.getForecastWeather(
                    CITY,
                    "metric",
                    applicationContext.getString(R.string.api_key)
                )
                    .awaitResponse()

            if (response.isSuccessful) {

                val data = response.body()!!

                forecastArray = data.list as ArrayList<Forecast>



                withContext(Dispatchers.Main) {
                 val adapter = RvAdapter(forecastArray)
                    binding2.rvForecast.adapter = adapter

                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    private fun getWeather() {
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequest::class.java)

        GlobalScope.launch(Dispatchers.IO) {


            try {
                val response: Response<WeatherData> =
                    api.getCurrentWeather(
                        CITY,
                        "metric",
                        applicationContext.getString(R.string.api_key)
                    )
                        .awaitResponse()





                if (response.isSuccessful) {

                    val data = response.body()!!
                    Log.d("mkrrrrrrrr", data.toString())


                    withContext(Dispatchers.Main) {


                        binding.progressBar.visibility = View.GONE
                        binding.subLayout.visibility = View.VISIBLE

                        val iconId = data.weather[0].icon

                        val imageUrl = "https://openweathermap.org/img/w/$iconId.png"

                        Picasso.get().load(imageUrl).into(binding.imgWeather)

                        binding.tvSunset.text =
                            SimpleDateFormat(
                                "hh:mm a",
                                Locale.ENGLISH
                            ).format(data.sys.sunset * 1000)

                        binding.tvSunrise.text =
                            SimpleDateFormat(
                                "hh:mm a",
                                Locale.ENGLISH
                            ).format(data.sys.sunrise * 1000)


                        binding.tvStatus.text = data.weather[0].description
                        binding.tvWind.text = "${data.wind.speed} Km/h"
                        binding.tvLocation.text = "${data.name}\n${data.sys.country}"
                        binding.tvTemp.text = "${data.main.temp.toInt()}°C"
                        binding.tvMinTemp.text = "min temp: ${data.main.temp_min}°C"
                        binding.tvMaxTemp.text = "max temp: ${data.main.temp_max}°C"
                        binding.tvHumidity.text = "${data.main.humidity}%"
                        binding.tvPressure.text = "${data.main.pressure} hPa"
                        binding.tvUpdateTime.text =
                            "Last update: ${SimpleDateFormat(
                                "hh:mm a",
                                Locale.US
                                // ).format(System.currentTimeMillis())
                            ).format(data.dt * 1000)}"

                        Log.d("mkrrrrrrrr", data.dt.toString())
                    }


                } else withContext(Dispatchers.Main) { binding.tvError.visibility = View.VISIBLE }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}