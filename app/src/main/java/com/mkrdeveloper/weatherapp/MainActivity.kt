package com.mkrdeveloper.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mkrdeveloper.weatherapp.adapters.RvAdapter
import com.mkrdeveloper.weatherapp.databinding.ActivityMainBinding
import com.mkrdeveloper.weatherapp.databinding.BottomSheetBinding
import com.mkrdeveloper.weatherapp.forecastModels.Forecast
import com.mkrdeveloper.weatherapp.forecastModels.WeatherForecastData
import com.mkrdeveloper.weatherapp.models.WeatherData
import com.mkrdeveloper.weatherapp.pillutionModels.Pollution
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val city: String = "poznan"
    private val BASE_URL: String = "https://api.openweathermap.org/data/2.5/"


    private lateinit var binding: ActivityMainBinding
    private lateinit var binding2: BottomSheetBinding
    private lateinit var forecastArray: ArrayList<Forecast>

    private lateinit var dialog: BottomSheetDialog
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var pollutionFragment: PollutionFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setBindings()

        pollutionFragment = PollutionFragment()
        // chPermission()

        fetchLocation()


        // binding.subLayout.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        val pollutionFragment = PollutionFragment()

       /* binding.LinPollution.setOnClickListener {




        }*/




        binding.imgForecast.setOnClickListener {
            openForecast()
            fetchLocation()
        }

    }




    private fun fetchLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }
        task.addOnSuccessListener {


            val geocoder = Geocoder(this, Locale.getDefault())

            val address = geocoder.getFromLocation(it.latitude, it.longitude, 10) as List<Address>



            getPollution(it.latitude, it.longitude)
            getWeather()
            getForecast()

        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getPollution(latitude: Double, longitude: Double) {


        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequest::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            val response: Response<Pollution> =
                api.getPollutionComponents(
                    latitude,
                    longitude,
                    "metric",
                    applicationContext.getString(R.string.api_key)
                )
                    .awaitResponse()

            if (response.isSuccessful) {

                val data = response.body()!!

                withContext(Dispatchers.Main) {
                    val num = data.list[0].main.aqi

                    if (num == 1) binding.tvAirQuality.text = getString(R.string.good)
                    if (num == 2) binding.tvAirQuality.text = getString(R.string.fair)
                    if (num == 3) binding.tvAirQuality.text = getString(R.string.moderate)
                    if (num == 4) binding.tvAirQuality.text = getString(R.string.poor)
                    if (num == 5) binding.tvAirQuality.text = getString(R.string.very_poor)



                    binding.LinPollution.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putDouble("co",data.list[0].components.co)
                        bundle.putDouble("nh3",data.list[0].components.nh3)
                        bundle.putDouble("no",data.list[0].components.no)
                        bundle.putDouble("no2",data.list[0].components.no2)
                        bundle.putDouble("o3",data.list[0].components.o3)
                        bundle.putDouble("pm10",data.list[0].components.pm10)
                        bundle.putDouble("pm2_5",data.list[0].components.pm2_5)
                        bundle.putDouble("so2",data.list[0].components.so2)

                        pollutionFragment.arguments = bundle


                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.frameLayout, pollutionFragment)
                                .addToBackStack(null)
                                .commit()

                        }
                    }

                }

            }
        }
    }

    private fun setBindings() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding2 = BottomSheetBinding.inflate(layoutInflater)

        dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)

        dialog.setContentView(binding2.root)

        setContentView(binding.root)
    }


    @SuppressLint("InflateParams")
    private fun openForecast() {


//val openDialog = layoutInflater.inflate(R.layout.bottom_sheet,null)


        binding2.rvForecast.setHasFixedSize(true)
        binding2.rvForecast.layoutManager =
            GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.show()


    }


    @SuppressLint("SetTextI18n")
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
                    "poznan",
                    "metric",
                    applicationContext.getString(R.string.api_key)
                )
                    .awaitResponse()

            if (response.isSuccessful) {

                val data = response.body()!!

                forecastArray = arrayListOf()



                forecastArray = data.list as ArrayList<Forecast>



                withContext(Dispatchers.Main) {
                    val adapter = RvAdapter(forecastArray)
                    binding2.rvForecast.adapter = adapter
                    binding2.tvSheet.text = "Four days forecast in ${data.city.name}"
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
                        "poznan",
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
                        binding.tvTemp.text = "${data.main.temp.toInt()}??C"
                        binding.tvFeelsLike.text = "Feels Like: ${data.main.feels_like}??C"
                        binding.tvMinTemp.text = "min temp: ${data.main.temp_min}??C"
                        binding.tvMaxTemp.text = "max temp: ${data.main.temp_max}??C"
                        binding.tvHumidity.text = "${data.main.humidity}%"
                        binding.tvPressure.text = "${data.main.pressure} hPa"
                        binding.tvUpdateTime.text =
                            "Last update: ${
                                SimpleDateFormat(
                                    "hh:mm a",
                                    Locale.US
                                    // ).format(System.currentTimeMillis())
                                ).format(data.dt * 1000)
                            }"

                        Log.d("mkrrrrrrrr", data.dt.toString())
                    }


                } else withContext(Dispatchers.Main) { binding.tvError.visibility = View.VISIBLE }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}