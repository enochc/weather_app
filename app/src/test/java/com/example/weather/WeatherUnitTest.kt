package com.example.weather

import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit



@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class WeatherUnitTest {

    private val lock: CountDownLatch = CountDownLatch(1)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    /**
     * This unit test requires a third party api and internet access to succeed
     * which doesn't really seem fair, but it works. It's primarily an example of how to build a
     * unit test that acts as an implementation test and can manage coroutine lifetimes and scopes
     */
    @Test
    fun useRunBlockingTest() = runBlockingTest {
        launch(Dispatchers.Main) {  // Will be launched in the mainThreadSurrogate dispatcher
            /**
             * Launch this here so callbacks that run in Dispatchers.Main will get picked up
             * updating elements in the views still throws exceptions but the tests run.
             */
        }

        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.STARTED);    // Moves the activity state to State.STARTED.

        scenario.onActivity { activity ->
            // Wait a second to fetch the initial weather data
            // I don't like using arbitrary timers but it works as a drop in solution.
            // Long term I would prefer to build some kind of synchronous test case
            // possibly by implementing a CountDownLatch at least
            Thread.sleep(1000)

            var responseName = ""
            var count = activity.citiesFragment.citiesAdaptor.itemCount
            assertEquals(count, 3)

            // add a city by name, I just happen to use Orem because that's where I'm living.
            activity.weatherViewModel.getWeatherByCityName("orem"){
                println(".... got a response: $it")
                responseName = it!!.cityName
                lock.countDown()
            }

            // wait at least 4 seconds for the callback from adding the new city, then verify
            val done = lock.await(4000, TimeUnit.MILLISECONDS)
            println(".... hey hey hye $done : $count")

            // Verify that the latest response was the city Orem, it shoudl be capitalized now
            // due to the api response data
            assertEquals(responseName,"Orem")

        }
    }
}