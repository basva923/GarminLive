package com.github.basva923.garminphoneactivity.controller

import com.github.basva923.garminphoneactivity.model.TrackSample

interface ActivityUpdateReceiver {
    fun onNewSample(sample: TrackSample)
}