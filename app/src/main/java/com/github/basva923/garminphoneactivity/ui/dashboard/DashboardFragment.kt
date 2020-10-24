package com.github.basva923.garminphoneactivity.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.basva923.garminphoneactivity.R
import com.github.basva923.garminphoneactivity.model.LiveTrackProperty
import com.github.basva923.garminphoneactivity.model.Model
import com.github.basva923.garminphoneactivity.model.ModelUpdateReceiver

class DashboardFragment : Fragment(), ModelUpdateReceiver {
    private val TAG = "DashboardFragment"
    private lateinit var root: View
    private val fields = mutableMapOf<LiveTrackProperty, TextView>()
    private val liveTrack = Model.track.liveTrackInfo
    private val propertySequence = listOf(
        LiveTrackProperty.TIME,
        LiveTrackProperty.SPEED,
        LiveTrackProperty.DISTANCE,
        LiveTrackProperty.HEART_RATE,
        LiveTrackProperty.ALTITUDE,
        LiveTrackProperty.CADENCE,
        LiveTrackProperty.POWER
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setupView()

        Model.modelUpdateReceivers.add(this)
        updateView()
        return root
    }

    private fun setupView() {
        val table = root.findViewById<TableLayout>(R.id.dashboard_table)
        for (i in 0 until propertySequence.size / 2) {
            val first = propertySequence[2 * i]
            val second = propertySequence[2 * i + 1]

            table.addView(createQuantityRow(first, second))
            table.addView(createValueRow(first, second))
        }

        if (propertySequence.size % 2 != 0) {
            val prop = propertySequence.last()
            table.addView(createQuantityRow(prop, null))
            table.addView(createValueRow(prop, null))
        }
    }

    private fun createValueRow(first: LiveTrackProperty, second: LiveTrackProperty?): TableRow {
        val row = TableRow(activity)

        val firstValueT = TextView(activity)
        firstValueT.setTextAppearance(R.style.TextAppearance_AppCompat_Display1)
        firstValueT.gravity = Gravity.START
        row.addView(firstValueT)
        fields[first] = firstValueT

        if (second != null) {
            val secondValueT = TextView(activity)
            secondValueT.setTextAppearance(R.style.TextAppearance_AppCompat_Display1)
            secondValueT.gravity = Gravity.END
            fields[second] = secondValueT
            row.addView(secondValueT)
        }

        return row
    }

    private fun createQuantityRow(first: LiveTrackProperty, second: LiveTrackProperty?): TableRow {
        val row = TableRow(activity)

        val firstT = TextView(activity)
        firstT.text = first.quantity.capitalize()
        row.addView(firstT)

        if (second != null) {
            val secondT = TextView(activity)
            secondT.text = second.quantity.capitalize()
            row.addView(secondT)
        }
        return row
    }

    private fun updateView() {
        Log.d(TAG, "Updating view")
        for ((prop, text) in fields) {
            text.text = liveTrack.getValueAsString(prop) + " " + prop.unit
        }
    }

    override fun onModelUpdate() {
        activity?.runOnUiThread {
            updateView()
        }
    }
}
