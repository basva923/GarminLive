package com.github.basva923.garminphoneactivity.model

data class TrackSample(
    var millisSinceStart: Int,
    var speed: Double,
    var power: Double,
    var heartRate: Int?,
    var latitude: Double,
    var longitude: Double,
    var distance: Double,
    var altitude: Double,
    val cadence: Int?
) {


    companion object {
        fun fromDict(string: Map<String, Any?>): TrackSample {

            return TrackSample(
                if (string["time"] != null) string["time"] as Int else 0,
                if (string["speed"] != null) (string["speed"] as Float).toDouble() else 0.0,
                if (string["power"] != null) (string["power"] as Float).toDouble() else 0.0,
                if (string["heartRate"] != null) string["heartRate"] as Int else null,
                if (string["latitude"] != null) (string["latitude"] as Double).toDouble() else 0.0,
                if (string["longitude"] != null) (string["longitude"] as Double).toDouble() else 0.0,
                if (string["distance"] != null) (string["distance"] as Float).toDouble() else 0.0,
                if (string["altitude"] != null) (string["altitude"] as Float).toDouble() else 0.0,
                if (string["cadence"] != null) string["cadence"] as Int else null
            )
        }
    }

    override fun toString(): String {
        return "TrackSample(time=$millisSinceStart, speed=$speed, heartRate=$heartRate, distance=$distance, altitude=$altitude, cadence=$cadence)"
    }


}
