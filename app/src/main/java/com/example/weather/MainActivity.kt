package com.example.weather

import android.Manifest
import android.annotation.SuppressLint

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.ui.AppBarConfiguration
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.models.WeatherViewModel
import com.example.weather.ui.CitiesFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


/**
 * weather: api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=c87aa0a81a65b025ba64901b64347e80
 * api.openweathermap.org/data/2.5/weather?id={city id}&appid={API key}
 * icon url http://openweathermap.org/img/w/01n.png

 */

fun debug(s: String) {
    val stack = Thread.currentThread().stackTrace[3]
    val fullClassName = stack.className
    val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
    val methodName = stack.methodName
    val lineNumber = stack.lineNumber

    Log.d("$className.$methodName():$lineNumber <<", s)
}
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // these exposed to support unit testing
    internal lateinit var citiesFragment: CitiesFragment
    internal val weatherViewModel: WeatherViewModel by viewModels()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        findLastLocation()
    }

    // this is only called after a permission check
    @SuppressLint("MissingPermission")
    private fun findLastLocation(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    weatherViewModel.getWeatherByLoc(location)
                }
            }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),1)

            return
        }
        findLastLocation()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        citiesFragment = CitiesFragment()
        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.fragment_content_main, citiesFragment)
        trans.setTransition(FragmentTransaction.TRANSIT_NONE)
        trans.commit()

        binding.fab.setOnClickListener { view ->
            addWeatherDialogue(view)
        }

        getLastKnownLocation()
    }

    private fun addWeatherDialogue(v:View) {
        val view = layoutInflater.inflate(R.layout.add_city_dialogue, null)

        val diag = AlertDialog.Builder(this)
            .setTitle("Add City")
            .setView(view)
            .setNegativeButton("cancel",null)
            .setPositiveButton("add"){_,_ ->

                val progress = view.findViewById<ProgressBar>(R.id.progress)
                progress.visibility = View.VISIBLE
                val txtInput = view.findViewById<EditText>(R.id.add_city_name)
                val name = txtInput.text.toString()

                weatherViewModel.getWeatherByCityName(name){
                    progress.visibility = View.INVISIBLE
                    if(it != null){
                        citiesFragment.addCity(it)
                    } else {
                        Snackbar.make(v, "Failed to find city \"$name\"", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                }
            }
        diag.show()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}