package com.github.basva923.garminphoneactivity.settings

import android.content.Context
import android.util.Log
import com.github.basva923.garminphoneactivity.model.CyclingPosition
import com.github.basva923.garminphoneactivity.model.TrackSurface

object Settings {
    var cyclingPosition = CyclingPosition.DROPS
    var trackSurface = TrackSurface.ASPHALT
    var ftp = 300
    var ftpHeartRate = 180
    var totalMass = 85

    const val id = "Settings"
    const val TAG = "Settings"

    fun save(context: Context) {
        val prefs = context.getSharedPreferences(id, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putInt("cyclingPosition", cyclingPosition.ordinal)
            putInt("trackSurface", trackSurface.ordinal)
            putInt("totalMass", totalMass)
            putInt("FTP", ftp)
            putInt("FTPHeartRate", ftpHeartRate)
            commit()
        }
        Log.d(TAG, "Saved settings")
    }

    fun load(context: Context) {
        val prefs = context.getSharedPreferences(id, Context.MODE_PRIVATE)
        cyclingPosition =
            CyclingPosition.values()[prefs.getInt("cyclingPosition", cyclingPosition.ordinal)]
        trackSurface = TrackSurface.values()[prefs.getInt("trackSurface", trackSurface.ordinal)]
        totalMass = prefs.getInt("totalMass", totalMass)
        ftp = prefs.getInt("FTP", ftp)
        ftpHeartRate = prefs.getInt("FTPHeartRate", ftpHeartRate)
        Log.d(TAG, "Loaded settings")
    }
}