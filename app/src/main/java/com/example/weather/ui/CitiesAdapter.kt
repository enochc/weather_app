package com.example.weather.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.CityListItemBinding
import com.example.weather.models.WeatherResponse
import com.example.weather.models.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class CitiesAdapter(
    private val city_map: ArrayList<Pair<Int, WeatherResponse?>>,
    val context: Context
) : RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {

    var deg = context.resources.getString(R.string.degrees)
    var weatherViewModel: WeatherViewModel? = null

    fun addOne(weatherResponse: WeatherResponse) {
        city_map.add(Pair(weatherResponse.id, weatherResponse))
        notifyItemInserted(city_map.size - 1)
    }

    fun deleteItem(position: Int) {
        city_map.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        val binding = CityListItemBinding.inflate(inflator, parent, false)
        return ViewHolder(binding)

    }

    private fun getCityResponse(position: Int, response: (wr: WeatherResponse) -> Unit) {
        var city = city_map[position]
        val cityId = city.first
        if (city.second == null) {
            weatherViewModel!!.getWeather(cityId) {
                city = city.copy(second = it)
                response(it)
            }
        } else {
            response(city.second!!)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getCityResponse(position) {
            holder.view.cityItem.setOnClickListener {
                holder.weatherResponse?.let {
                    weatherViewModel?.weather?.postValue(it)
                }
            }
            holder.weatherResponse = it
            holder.view.cityName.text = it.cityName
            holder.view.currentTemp.text = String.format(deg, it.currentTemp)
            weatherViewModel!!.loadIcon(it.icon, holder.view.icon)
        }
    }


    override fun getItemCount(): Int {
        return city_map.size
    }

    class ViewHolder(val view: CityListItemBinding) : RecyclerView.ViewHolder(view.root) {
        var weatherResponse: WeatherResponse? = null
    }
}


