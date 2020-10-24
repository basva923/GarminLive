package com.github.basva923.garminphoneactivity.model

class Track {

    private val _samples = mutableListOf<TrackSample>()
    private var _totalDistance = 0

    val samples
        get() = _samples

    val liveTrackInfo = LiveTrackInfo(this)

    fun getLastSample(): TrackSample? {
        return _samples.lastOrNull()
    }

    fun addSample(sample: TrackSample) {
        _samples.add(sample)

        liveTrackInfo.update()
    }

}
