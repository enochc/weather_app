package com.example.weather.models

import kotlin.math.roundToInt

class WeatherResponse {
    val id: Int = 0
    val name: String = "none"
    val visibility:Int = 0
    val weather: Array<Weather> = arrayOf(Weather())
    val main: Main = Main()
    val wind:Wind = Wind()
    val list:Array<ListItem> = arrayOf(ListItem())
    val city:City = City()

    val pop:Float
    get(){
        return list[0].pop
    }
    val description:String
    get(){
        return list[0].weather[0].description
    }
    val icon:String
    get(){
        return list[0].weather[0].icon
    }
    val currentTemp:Int
    get(){
        return list[0].main.temp.roundToInt()
    }
    val lowTemp:Int
    get(){
        return list[0].main.temp_min.roundToInt()
    }
    val highTemp:Int
    get(){
        return list[0].main.temp_max.roundToInt()
    }
    val cityName:String
    get(){
        return city.name
    }
    val windSpeed:Int
    get(){
        return list[0].wind.speed.roundToInt()
    }

}
class City {
    val name: String = "none"
    val country:String = "none"
//    "id": 5780993,
//    "name": "Salt Lake City",
//    "coord": {
//        "lat": 40.7608,
//        "lon": -111.8911
//    },
//    "country": "US",
//    "population": 186440,
//    "timezone": -25200,
//    "sunrise": 1614520966,
//    "sunset": 1614561458
}
class ListItem{
    val main:Main = Main()
    val dt:Int = 0
    val weather: Array<Weather> = arrayOf(Weather())
    val wind:Wind = Wind()
    val pop:Float = 0.0F
}

class Wind {
    val speed:Float = 0f
    val deg:Int = 0
}

class Weather {
    val id: Int = 0
    val main: String = ""
    val description: String = ""
    val icon: String = ""
}

class Main {
    val temp: Float = 0f
    val feels_like: Float = 0f
    val temp_min: Float = 0f
    val temp_max: Float = 0f
    val pressure: Int = 0
    val humidity: Int = 0
}