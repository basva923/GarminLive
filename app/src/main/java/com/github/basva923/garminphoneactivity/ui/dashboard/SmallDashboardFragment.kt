package com.github.basva923.garminphoneactivity.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.basva923.garminphoneactivity.R
import com.github.basva923.garminphoneactivity.model.LiveTrackProperty
import com.github.basva923.garminphoneactivity.model.Model
import com.github.basva923.garminphoneactivity.model.ModelUpdateReceiver
import com.github.basva923.garminphoneactivity.model.PropertyType

class SmallDashboardFragment : Fragment(), ModelUpdateReceiver {

    private lateinit var propertyTypeTitle: TextView
    private val TAG = "DashboardFragment2"
    private lateinit var root: View

    private val fields = mutableMapOf<LiveTrackProperty, TextView>()
    private val liveTrack = Model.track.liveTrackInfo
    private var propertyTypeIdx = -1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_dashboard2, container, false)

        setupView()
        setupTouchListener()

        Model.modelUpdateReceivers.add(this)
        updateView()
        return root
    }

    private fun setupTouchListener() {
        root.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                rotatePropertyType()
            }
            true
        }
    }

    private fun setupView() {
        fields[LiveTrackProperty.SPEED] = root.findViewById(R.id.dash2_speed)
        fields[LiveTrackProperty.POWER] = root.findViewById(R.id.dash2_power)
        fields[LiveTrackProperty.CADENCE] = root.findViewById(R.id.dash2_cadence)
        fields[LiveTrackProperty.HEART_RATE] = root.findViewById(R.id.dash2_heartrate)

        propertyTypeTitle = root.findViewById(R.id.dash2_title)
        rotatePropertyType()
    }

    private fun rotatePropertyType() {
        propertyTypeIdx = (propertyTypeIdx + 1) % PropertyType.values().size

        propertyTypeTitle.text = PropertyType.values()[propertyTypeIdx].name.toLowerCase().capitalize()
        updateView()
    }

    private fun updateView() {
        for ((prop, text) in fields) {
            text.text = liveTrack.getValueAsString(
                prop,
                PropertyType.values()[propertyTypeIdx]
            ) + " " + prop.unit
        }
    }

    override fun onModelUpdate() {
        activity?.runOnUiThread {
            updateView()
        }
    }
}
