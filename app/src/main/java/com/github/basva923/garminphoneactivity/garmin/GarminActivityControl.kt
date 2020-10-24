package com.github.basva923.garminphoneactivity.garmin

import com.github.basva923.garminphoneactivity.controller.ActivityControl
import com.github.basva923.garminphoneactivity.controller.ActivityType
import com.github.basva923.garminphoneactivity.model.TrackSample

class GarminActivityControl(private val _connection: GarminConnection) : ActivityControl(),
    GarminMessageReceiver {

    init {
        _connection.messageReceivers.add(this)
    }

    override fun start(type: ActivityType) {
        _connection.sendMessage(
            GarminMessage(
                "start", mapOf(
                    "type" to type.name
                )
            )
        )
    }

    override fun stop() {
        _connection.sendMessage(GarminMessage("stop", mapOf()))
    }

    override fun pause() {
        _connection.sendMessage(GarminMessage("pause", mapOf()))
    }

    override fun resume() {
        _connection.sendMessage(GarminMessage("resume", mapOf()))
    }

    override fun onMessage(message: GarminMessage) {
        if (message.command == "updateLocation") {
            notifyNewSample(TrackSample.fromDict(message.params))
        }
    }

}