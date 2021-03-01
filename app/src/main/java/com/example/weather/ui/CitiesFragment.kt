package com.example.weather.ui

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.FragmentCitiesBinding
import com.example.weather.models.WeatherResponse
import com.example.weather.models.WeatherViewModel


/**
 * slc = 5780993
 * nyc = 5128581
 * san fran = 5391959
 */
class CitiesFragment : Fragment() {

    private var _binding: FragmentCitiesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val cityIds = arrayListOf<Pair<Int, WeatherResponse?>>(
        Pair(5780993, null),
        Pair(5128581, null),
        Pair(5391959, null)
    )
    val weatherViewModel: WeatherViewModel by activityViewModels()
    lateinit var citiesAdaptor: CitiesAdapter


    fun addCity(weather: WeatherResponse) {
        citiesAdaptor.addOne(weather)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCitiesBinding.inflate(inflater, container, false)
        citiesAdaptor = CitiesAdapter(cityIds, requireContext())

        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        citiesAdaptor.weatherViewModel = weatherViewModel

        val swipeHandler = object : SwipeToDeleteCallback(citiesAdaptor) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                citiesAdaptor.deleteItem(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.cityList)

        binding.cityList.adapter = citiesAdaptor
        citiesAdaptor.notifyDataSetChanged()

        weatherViewModel.weather.observe(viewLifecycleOwner, Observer {
            binding.cityName.text = it.cityName
            binding.desc.text = it.description
            var value = it.highTemp
            var label = resources.getString(R.string.high)
            binding.high.text = String.format(label, value)

            value = it.lowTemp
            label = resources.getString(R.string.low)
            binding.low.text = String.format(label, value)

            value = it.currentTemp
            label = resources.getString(R.string.current)
            binding.current.text = String.format(label, value)

            value = it.windSpeed
            label = resources.getString(R.string.wind)
            binding.wind.text = String.format(label, value)

            val percentPrecipitation = it.pop
            label = resources.getString(R.string.pop)
            binding.pop.text = String.format(label, percentPrecipitation)

            weatherViewModel.loadIcon(it.icon, binding.mainIcon)
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

/*
 * Copied code from: https://github.com/kitek/android-rv-swipe-delete
 */
abstract
class SwipeToDeleteCallback(val adapter: CitiesAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
    private val deleteIcon = ContextCompat.getDrawable(
        adapter.context,
        R.drawable.ic_baseline_delete_forever_24
    )!!
    private val intrinsicWidth = deleteIcon.intrinsicWidth
    private val intrinsicHeight = deleteIcon.intrinsicHeight
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        adapter.deleteItem(position)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        // Calculate position of delete icon
        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft = itemView.left + deleteIconMargin
        val deleteIconRight = itemView.left + deleteIconMargin + intrinsicWidth
        val deleteIconBottom = deleteIconTop + intrinsicHeight

        // Draw the delete icon
        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteIcon.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}