package com.github.basva923.garminphoneactivity.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.basva923.garminphoneactivity.R
import com.github.basva923.garminphoneactivity.model.*
import com.github.basva923.garminphoneactivity.settings.Settings

class TimerDashboardFragment : Fragment(), ModelUpdateReceiver {

    private val TAG = "TimerDashboard"
    private lateinit var root: View
    private lateinit var time: TextView

    private val fields = mutableMapOf<LiveTrackProperty, TextView>()
    private val avgFields = mutableMapOf<LiveTrackProperty, TextView>()
    private val liveTrack = Model.track.liveTrackInfo
    private val lapTimer = LapTimer(liveTrack)

    private lateinit var powerBar: ProgressBar
    private lateinit var heartRateBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_timer, container, false)
        time = root.findViewById(R.id.timer_time)


        setupView()
        setupTouchListener()

        Model.modelUpdateReceivers.add(this)
        Model.modelUpdateReceivers.add(lapTimer)
        updateView()
        return root
    }

    private fun setupTouchListener() {
        root.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                lapTimer.resetTimer()
            }
            true
        }
    }

    override fun onStart() {
        super.onStart()

        Model.modelUpdateReceivers.add(this)
        Model.modelUpdateReceivers.add(lapTimer)
    }

    override fun onStop() {
        Model.modelUpdateReceivers.remove(this)
        Model.modelUpdateReceivers.remove(lapTimer)
        super.onStop()
    }

    private fun setupView() {
        fields[LiveTrackProperty.SPEED] = root.findViewById(R.id.timer_speed)
        fields[LiveTrackProperty.POWER] = root.findViewById(R.id.timer_power)
        fields[LiveTrackProperty.CADENCE] = root.findViewById(R.id.timer_cadence)
        fields[LiveTrackProperty.HEART_RATE] = root.findViewById(R.id.timer_heartrate)

        avgFields[LiveTrackProperty.SPEED] = root.findViewById(R.id.timer_speed_avg)
        avgFields[LiveTrackProperty.POWER] = root.findViewById(R.id.timer_power_avg)
        avgFields[LiveTrackProperty.CADENCE] = root.findViewById(R.id.timer_cadence_avg)
        avgFields[LiveTrackProperty.HEART_RATE] = root.findViewById(R.id.timer_heartrate_avg)

        avgFields[LiveTrackProperty.POWER_ZONE] = root.findViewById(R.id.timer_power_avg_zone)
        avgFields[LiveTrackProperty.HEART_RATE_ZONE] =
            root.findViewById(R.id.timer_heartrate_avg_zone)

        powerBar = root.findViewById(R.id.timer_power_bar)
        heartRateBar = root.findViewById(R.id.timer_heart_rate_bar)
        powerBar.progress = 100
        heartRateBar.progress = 100
    }


    @SuppressLint("SetTextI18n")
    private fun updateView() {
        val secs = lapTimer.getTime()
        time.text = "${(secs / 60).toInt()}:${(secs % 60).toInt()}"

        for (prop in fields.keys) {
            fields[prop]!!.text = liveTrack.getValueAsString(
                prop,
                PropertyType.CURRENT
            )
            avgFields[prop]!!.text = lapTimer.getAvgPropertyAsString(prop)
        }

        avgFields[LiveTrackProperty.POWER_ZONE]!!.text =
            "Z" + lapTimer.getAvgPropertyAsString(LiveTrackProperty.POWER_ZONE)
        avgFields[LiveTrackProperty.HEART_RATE_ZONE]!!.text =
            "Z" + lapTimer.getAvgPropertyAsString(LiveTrackProperty.HEART_RATE_ZONE)

        powerBar.setProgress(
            (liveTrack.getValue(
                PropertyType.CURRENT,
                LiveTrackProperty.POWER
            ) / Settings.ftp * 50).toInt(), true
        )
        heartRateBar.setProgress(
            (liveTrack.getValue(
                PropertyType.CURRENT,
                LiveTrackProperty.HEART_RATE
            ) / Settings.ftpHeartRate * 75).toInt(), true
        )
    }

    override fun onModelUpdate() {
        activity?.runOnUiThread {
            updateView()
        }
    }
}
