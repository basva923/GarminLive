package com.github.basva923.garminphoneactivity.model

class LapTimer(private val liveTrackInfo: LiveTrackInfo) : ModelUpdateReceiver {

    private var startTime = 0.0
    private var startDistance = 0.0
    private var counter = 0

    private val sumProperties = mutableMapOf(
        LiveTrackProperty.POWER to 0.0,
        LiveTrackProperty.SPEED to 0.0,
        LiveTrackProperty.CADENCE to 0.0,
        LiveTrackProperty.HEART_RATE to 0.0
    )

    @Synchronized
    fun getAvgProperty(property: LiveTrackProperty): Double {
        return when (property) {
            LiveTrackProperty.TIME -> getTime()
            LiveTrackProperty.DISTANCE -> getDistance()
            in sumProperties -> sumProperties[property]!! / counter
            LiveTrackProperty.HEART_RATE_ZONE -> liveTrackInfo.hrZoneFinder.valueToZone(
                sumProperties[LiveTrackProperty.HEART_RATE]!! / counter
            ).toDouble()
            LiveTrackProperty.POWER_ZONE -> liveTrackInfo.powerZoneFinder.valueToZone(
                sumProperties[LiveTrackProperty.POWER]!! / counter
            ).toDouble()
            else -> liveTrackInfo.getValue(PropertyType.CURRENT, property)
        }
    }

    fun getAvgPropertyAsString(property: LiveTrackProperty): String {
        return String.format("%.${property.precision}f", getAvgProperty(property))
    }

    fun getTime() =
        liveTrackInfo.getValue(PropertyType.CURRENT, LiveTrackProperty.TIME) - startTime

    private fun getDistance() =
        liveTrackInfo.getValue(PropertyType.CURRENT, LiveTrackProperty.DISTANCE) - startDistance

    @Synchronized
    fun resetTimer() {
        startTime = liveTrackInfo.getValue(PropertyType.CURRENT, LiveTrackProperty.TIME)
        startDistance = liveTrackInfo.getValue(PropertyType.CURRENT, LiveTrackProperty.DISTANCE)
        counter = 0

        for (prop in sumProperties.keys)
            sumProperties[prop] = 0.0
    }

    @Synchronized
    override fun onModelUpdate() {
        counter += 1
        for (prop in sumProperties.keys)
            sumProperties[prop] =
                sumProperties[prop]!! + liveTrackInfo.getValue(PropertyType.CURRENT, prop)
    }
}