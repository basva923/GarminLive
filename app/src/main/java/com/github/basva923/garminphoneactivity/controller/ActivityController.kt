package com.github.basva923.garminphoneactivity.controller

import com.github.basva923.garminphoneactivity.model.Model
import com.github.basva923.garminphoneactivity.model.Track
import com.github.basva923.garminphoneactivity.model.TrackSample

class ActivityController(private val _track: Track, private val control: ActivityControl) :
    ActivityUpdateReceiver {

    private var _status: ActivityStatus = ActivityStatus.BEFORE_START

    init {
        control.addUpdateReceiver(this)
    }

    val status
        get() = _status

    fun start(type: ActivityType) {
        control.start(type)
        _status = ActivityStatus.RUNNING
    }

    fun stop() {
        control.stop()
        _status = ActivityStatus.STOPPED
    }

    fun pause() {
        control.pause()
        _status = ActivityStatus.PAUSED
    }

    fun resume() {
        control.resume()
        _status = ActivityStatus.RUNNING
    }

    override fun onNewSample(sample: TrackSample) {
        _track.addSample(sample)
        Model.modelChanged()
    }

}