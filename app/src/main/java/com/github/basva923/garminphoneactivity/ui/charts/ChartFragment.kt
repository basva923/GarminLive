package com.github.basva923.garminphoneactivity.ui.charts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.basva923.garminphoneactivity.R
import com.github.basva923.garminphoneactivity.model.LiveTrackProperty
import com.github.basva923.garminphoneactivity.model.Model
import com.github.basva923.garminphoneactivity.model.ModelUpdateReceiver
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.math.min


class ChartFragment : Fragment(), ModelUpdateReceiver {

    private val TAG = "DashboardFragment2"
    private lateinit var root: View

    private val fields = mutableMapOf<LiveTrackProperty, GraphView>()
    private val liveTrack = Model.track.liveTrackInfo

    private val minValues = mapOf(
        LiveTrackProperty.SPEED to 25.0,
        LiveTrackProperty.POWER to 0.0,
        LiveTrackProperty.CADENCE to 40.0,
        LiveTrackProperty.HEART_RATE to 80.0
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_charts, container, false)

        setupView()

        Model.modelUpdateReceivers.add(this)
        updateView()
        return root
    }


    private fun setupView() {
        fields[LiveTrackProperty.SPEED] = root.findViewById(R.id.charts_speed)
        fields[LiveTrackProperty.POWER] = root.findViewById(R.id.charts_power)
        fields[LiveTrackProperty.CADENCE] = root.findViewById(R.id.charts_cadence)
        fields[LiveTrackProperty.HEART_RATE] = root.findViewById(R.id.charts_heart_rate)
    }


    private fun updateView() {
        Log.d(TAG, "Updating view")
        for ((prop, chart) in fields) {
            chart.title = prop.quantity.capitalize()
            val series = LineGraphSeries<DataPoint>(
                liveTrack.getAvg30SProp(prop).mapIndexed { index, d ->
                    DataPoint(index.toDouble(), d)
                }.toTypedArray()
            )
            chart.removeAllSeries()
            chart.addSeries(series)
            chart.viewport.isXAxisBoundsManual = true
            chart.viewport.isYAxisBoundsManual = true
            chart.viewport.maxXAxisSize = series.highestValueX
            chart.viewport.setMinY(min(series.lowestValueY, minValues[prop]!!))
            chart.viewport.setMaxY(series.highestValueY * 1.2)

        }
    }

    override fun onModelUpdate() {
        activity?.runOnUiThread {
            updateView()
        }
    }
}
