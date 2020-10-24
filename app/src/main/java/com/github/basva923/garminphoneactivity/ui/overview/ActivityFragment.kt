package com.github.basva923.garminphoneactivity.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.github.basva923.garminphoneactivity.R
import com.github.basva923.garminphoneactivity.controller.ActivityController
import com.github.basva923.garminphoneactivity.controller.ActivityStatus
import com.github.basva923.garminphoneactivity.controller.ActivityType
import com.github.basva923.garminphoneactivity.controller.Controllers

class ActivityFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var _pauseResumeButton: Button
    private lateinit var _startStopButton: Button
    private lateinit var _spinner: Spinner
    private lateinit var _root: View

    private var _activityType = ActivityType.CYCLING
    private lateinit var _activityController: ActivityController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_overview, container, false)

        _root = root
        setupActivity()
        setupSpinner()
        setupButton()
        return root
    }

    private fun setupActivity() {
        _activityController = Controllers.activityController!!
    }

    private fun setupButton() {
        _startStopButton = _root.findViewById(R.id.startStopButton)
        _pauseResumeButton = _root.findViewById(R.id.pauseResumeButton)

        updateButtons()

        _startStopButton.setOnClickListener {
            if (_activityController.status == ActivityStatus.BEFORE_START)
                _activityController.start(_activityType)
            else
                _activityController.stop()
            updateButtons()
        }
        _pauseResumeButton.setOnClickListener {
            if (_activityController.status == ActivityStatus.RUNNING)
                _activityController.pause()
            else
                _activityController.resume()
            updateButtons()
        }
    }

    private fun updateButtons() {
        if (_activityController.status == ActivityStatus.BEFORE_START || _activityController.status == ActivityStatus.PAUSED)
            _startStopButton.visibility = VISIBLE
        else
            _startStopButton.visibility = INVISIBLE

        if (_activityController.status == ActivityStatus.BEFORE_START || _activityController.status == ActivityStatus.STOPPED)
            _pauseResumeButton.visibility = INVISIBLE
        else
            _pauseResumeButton.visibility = VISIBLE


        if (_activityController.status == ActivityStatus.BEFORE_START)
            _startStopButton.text = "Start"
        else
            _startStopButton.text = "Stop"

        if (_activityController.status == ActivityStatus.PAUSED)
            _pauseResumeButton.text = "Resume"
        else
            _pauseResumeButton.text = "Pause"

        _spinner.isEnabled = _activityController.status == ActivityStatus.BEFORE_START
    }

    private fun setupSpinner() {
        _spinner = _root.findViewById(R.id.activity_type_spinner)
        _spinner.onItemSelectedListener = this
        val dataAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            ActivityType.values().map { it.name })
        _spinner.adapter = dataAdapter
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        _activityType = ActivityType.values()[position]
    }
}
