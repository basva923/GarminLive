package com.github.basva923.garminphoneactivity.controller

import com.github.basva923.garminphoneactivity.model.TrackSample

abstract class ActivityControl {
    private val updateReceivers = mutableSetOf<ActivityUpdateReceiver>()

    abstract fun start(type: ActivityType)

    abstract fun stop()

    abstract fun pause()

    abstract fun resume()

    fun addUpdateReceiver(receiver: ActivityUpdateReceiver) {
        updateReceivers.add(receiver)
    }

    fun removeUpdateReceiver(receiver: ActivityUpdateReceiver) {
        updateReceivers.remove(receiver)
    }

    protected fun notifyNewSample(sample: TrackSample) {
        updateReceivers.forEach {
            it.onNewSample(sample)
        }
    }
}