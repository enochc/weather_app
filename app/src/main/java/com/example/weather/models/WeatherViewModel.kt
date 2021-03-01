package com.example.weather.models

import android.app.Application
import android.location.Location
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.weather.util.RetrofitBuilder
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import retrofit2.HttpException

class WeatherViewModel(val context: Application) : AndroidViewModel(context) {

    val weather: MutableLiveData<WeatherResponse> = MutableLiveData()

    // updates the primary weather data
    // used by primary get location on load
    fun getWeatherByLoc(location: Location) {
        GlobalScope.launch { // launch a new coroutine in background and continue
            val weatherResponse = RetrofitBuilder.weatherService(context)
                .getWeatherByLoc(location.latitude, location.longitude)

            withContext(Dispatchers.Main) { // back to the ui thread up update views
                weather.postValue(weatherResponse)
            }
        }
    }

    fun loadIcon(icon: String, view: ImageView) {
        val url = "http://openweathermap.org/img/w/$icon.png"
        Picasso.with(context).load(url).into(view)
    }

    // takes a return function that accepts the location on the main ui thread
    // used by the list adaptor
    fun getWeather(cityId: Int, response: (wr: WeatherResponse) -> Unit) {
        GlobalScope.launch { // launch a new coroutine in background and continue
            val resp = RetrofitBuilder.weatherService(context).getWeather(cityId)
            withContext(Dispatchers.Main) {
                response(resp)
            }
        }
    }


    // takes a return funtion that accepts the location on the main ui thread
    // used by the list adaptor
    fun getWeatherByCityName(cityName: String, response: (wr: WeatherResponse?) -> Unit) {
        GlobalScope.launch { // launch a new coroutine in background and continue
            try {
                val resp = RetrofitBuilder.weatherService(context).getWeatherByCity(cityName)
                withContext(Dispatchers.Main) {
                    response(resp)
                }
            } catch (e: HttpException) {
                response(null)
            }
        }
    }
}


