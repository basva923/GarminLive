package com.github.basva923.garminphoneactivity.garmin

interface GarminMessageReceiver {
    fun onMessage(message: GarminMessage)
}
